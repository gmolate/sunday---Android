import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CachedMoonData(
    @PrimaryKey val id: Int,
    // ...otros campos...
)
