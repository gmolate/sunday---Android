import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Date

@Entity(tableName = "cached_uv_data")
data class CachedUvData(
    @PrimaryKey val id: Int = 0,
    val uvIndex: Float,
    val uvLevel: String,
    val location: String,
    val timestamp: Date
)

@Dao
interface CachedUvDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: CachedUvData)

    @Query("SELECT * FROM cached_uv_data WHERE id = :id")
    suspend fun getById(id: Int): CachedUvData?

    @Query("SELECT * FROM cached_uv_data ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): CachedUvData?

    @Query("DELETE FROM cached_uv_data")
    suspend fun deleteAll()
}
