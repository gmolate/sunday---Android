import androidx.room.TypeConverter
import java.util.Date
import androidx.room.Entity
import androidx.room.PrimaryKey

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
