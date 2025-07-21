import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cached_moon_data")
data class CachedMoonData(
    @PrimaryKey val id: Int = 0,
    val moonPhase: String,
    val illumination: Float,
    val moonrise: String?,
    val moonset: String?,
    val timestamp: Date
)

