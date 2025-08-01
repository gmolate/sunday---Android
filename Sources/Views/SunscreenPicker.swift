import SwiftUI

struct SunscreenPicker: View {
    @Binding var selection: SunscreenLevel
    @Environment(\.dismiss) var dismiss
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationView {
            List {
                ForEach(SunscreenLevel.allCases, id: \.self) { level in
                    Button(action: {
                        selection = level
                        dismiss()
                    }) {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(level.description)
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                Text(sunscreenDetail(for: level))
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            if selection == level {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.blue)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Sunscreen")
            .navigationBarItems(trailing: Button("Done") { dismiss() })
            .preferredColorScheme(.dark)
        }
        .presentationBackground(Color(UIColor.systemBackground).opacity(0.99))
    }
    
    private func sunscreenDetail(for level: SunscreenLevel) -> String {
        switch level {
        case .none: return "100% UV passes through"
        case .spf15: return "Blocks ~93% of UV rays"
        case .spf30: return "Blocks ~97% of UV rays"
        case .spf50: return "Blocks ~98% of UV rays"
        case .spf100: return "Blocks ~99% of UV rays"
        }
    }
}