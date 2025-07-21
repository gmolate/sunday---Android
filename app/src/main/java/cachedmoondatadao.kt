import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedMoonDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: CachedMoonData)

    @Query("SELECT * FROM cached_moon_data WHERE id = :id")
    suspend fun getById(id: Int): CachedMoonData?

    @Query("SELECT * FROM cached_moon_data ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): CachedMoonData?

    @Query("DELETE FROM cached_moon_data")
    suspend fun deleteAll()
}
