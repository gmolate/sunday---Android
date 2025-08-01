import Foundation
import CoreLocation
import WidgetKit
import Combine
import UIKit

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var location: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus = .notDetermined
    @Published var isUpdatingLocation = false
    @Published var showLocationDeniedAlert = false
    @Published var locationServicesEnabled = true
    @Published var locationName: String = "" {
        didSet {
            // Share location name with widget
            sharedDefaults?.set(locationName, forKey: "locationName")
            // Force synchronize to ensure widget gets updated data
            sharedDefaults?.synchronize()
            // Trigger widget update
            WidgetCenter.shared.reloadAllTimelines()
        }
    }
    
    private let manager = CLLocationManager()
    private let geocoder = CLGeocoder()
    private let sharedDefaults = UserDefaults(suiteName: "group.sunday.widget")
    
    // Geocoding cache
    private var geocodeCache: [String: String] = [:]
    private let geocodeCacheRadius: CLLocationDistance = 1000 // 1km radius for cache
    private var lastGeocodedLocation: CLLocation?
    
    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        manager.distanceFilter = 500
        manager.pausesLocationUpdatesAutomatically = true
        manager.activityType = .fitness
        
        // Try to restore last known location from UserDefaults
        if let savedLat = UserDefaults.standard.object(forKey: "lastKnownLatitude") as? Double,
           let savedLon = UserDefaults.standard.object(forKey: "lastKnownLongitude") as? Double,
           let savedAlt = UserDefaults.standard.object(forKey: "lastKnownAltitude") as? Double {
            location = CLLocation(
                coordinate: CLLocationCoordinate2D(latitude: savedLat, longitude: savedLon),
                altitude: savedAlt,
                horizontalAccuracy: 100,
                verticalAccuracy: 50,
                timestamp: Date()
            )
            
            // Also restore location name
            if let savedName = UserDefaults.standard.string(forKey: "lastKnownLocationName") {
                locationName = savedName
            }
        }
    }
    
    func requestPermission() {
        // Check authorization status first to avoid blocking UI
        authorizationStatus = manager.authorizationStatus
        
        switch authorizationStatus {
        case .notDetermined:
            // Only request authorization if not determined
            // The actual request will be handled asynchronously
            manager.requestWhenInUseAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            // Already authorized, start updating location
            startUpdatingLocation()
        case .denied, .restricted:
            // Handle denial through the locationManagerDidChangeAuthorization callback
            break
        @unknown default:
            break
        }
    }
    
    func startUpdatingLocation() {
        guard !isUpdatingLocation else { return }
        isUpdatingLocation = true
        manager.startUpdatingLocation()
    }
    
    func stopUpdatingLocation() {
        guard isUpdatingLocation else { return }
        isUpdatingLocation = false
        manager.stopUpdatingLocation()
    }
    
    func startSignificantLocationChanges() {
        // Use significant location changes for battery efficiency
        manager.startMonitoringSignificantLocationChanges()
    }
    
    func stopSignificantLocationChanges() {
        manager.stopMonitoringSignificantLocationChanges()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let newLocation = locations.last else { return }
        location = newLocation
        
        // Save location for offline use
        UserDefaults.standard.set(newLocation.coordinate.latitude, forKey: "lastKnownLatitude")
        UserDefaults.standard.set(newLocation.coordinate.longitude, forKey: "lastKnownLongitude")
        UserDefaults.standard.set(newLocation.altitude, forKey: "lastKnownAltitude")
        
        // Check if we need to reverse geocode (use cache if location is close enough)
        if let lastLocation = lastGeocodedLocation,
           newLocation.distance(from: lastLocation) < geocodeCacheRadius,
           let cachedName = getCachedLocationName(for: newLocation) {
            // Use cached name
            locationName = cachedName
        } else {
            // Reverse geocode to get location name
            geocoder.reverseGeocodeLocation(newLocation) { [weak self] placemarks, error in
                guard let self = self,
                      let placemark = placemarks?.first else { return }
                
                DispatchQueue.main.async {
                    // Prefer neighborhood, then locality, then administrative area
                    if let neighborhood = placemark.subLocality {
                        self.locationName = neighborhood
                    } else if let city = placemark.locality {
                        self.locationName = city
                    } else if let area = placemark.administrativeArea {
                        self.locationName = area
                    } else {
                        self.locationName = ""
                    }
                    
                    // Cache the result
                    if !self.locationName.isEmpty {
                        self.cacheLocationName(self.locationName, for: newLocation)
                        self.lastGeocodedLocation = newLocation
                        
                        // Save location name for offline use
                        UserDefaults.standard.set(self.locationName, forKey: "lastKnownLocationName")
                    }
                }
            }
        }
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        authorizationStatus = manager.authorizationStatus
        
        // Determine location services status based on authorization
        // This avoids the synchronous call that can block UI
        switch authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways:
            locationServicesEnabled = true
            startUpdatingLocation()
            showLocationDeniedAlert = false
        case .denied:
            locationServicesEnabled = true // Services are enabled, just denied for this app
            stopUpdatingLocation()
            // Only show alert if we haven't shown it before in this session
            if !UserDefaults.standard.bool(forKey: "hasShownLocationDeniedAlert") {
                showLocationDeniedAlert = true
                UserDefaults.standard.set(true, forKey: "hasShownLocationDeniedAlert")
            }
        case .restricted:
            locationServicesEnabled = false // Restricted usually means services are disabled
            stopUpdatingLocation()
            // Only show alert if we haven't shown it before in this session
            if !UserDefaults.standard.bool(forKey: "hasShownLocationDeniedAlert") {
                showLocationDeniedAlert = true
                UserDefaults.standard.set(true, forKey: "hasShownLocationDeniedAlert")
            }
        case .notDetermined:
            locationServicesEnabled = true // Assume enabled until proven otherwise
            // Will be prompted when we request authorization
            break
        @unknown default:
            break
        }
    }
    
    func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
    
    func resetLocationDeniedAlert() {
        // Reset the flag when app becomes active again
        UserDefaults.standard.set(false, forKey: "hasShownLocationDeniedAlert")
    }
    
    var locationDeniedMessage: String {
        if !locationServicesEnabled {
            return "Location Services are disabled. Please enable Location Services in Settings > Privacy & Security > Location Services."
        } else {
            return "Sun Day needs your location to determine UV levels and calculate vitamin D production. Please enable location access in Settings."
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        // Silent error handling - errors are handled by checking location status
    }
    
    // MARK: - Geocoding Cache
    
    private func getCachedLocationName(for location: CLLocation) -> String? {
        let key = geocodeCacheKey(for: location)
        return geocodeCache[key]
    }
    
    private func cacheLocationName(_ name: String, for location: CLLocation) {
        let key = geocodeCacheKey(for: location)
        geocodeCache[key] = name
        
        // Limit cache size to prevent memory issues
        if geocodeCache.count > 50 {
            // Remove oldest entries (simple FIFO)
            if let firstKey = geocodeCache.keys.first {
                geocodeCache.removeValue(forKey: firstKey)
            }
        }
    }
    
    private func geocodeCacheKey(for location: CLLocation) -> String {
        // Round to ~100m precision for cache key
        let lat = round(location.coordinate.latitude * 1000) / 1000
        let lon = round(location.coordinate.longitude * 1000) / 1000
        return "\(lat),\(lon)"
    }
}
