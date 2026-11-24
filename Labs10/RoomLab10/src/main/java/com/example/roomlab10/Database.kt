package com.example.roomlab10

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Entity(tableName = "masters")
data class Master(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val experience: Int)

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(
        entity = Master::class, parentColumns = ["id"], childColumns = ["masterId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["masterId", "cost"])],
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val masterId: Long,
    val car: String,
    val cost: Double,
    val date: LocalDate,
)

class DateConverters {
    @TypeConverter
    fun encode(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun decode(days: Long): LocalDate = LocalDate.ofEpochDay(days)
}

interface BaseDao<T> {
    @Insert suspend fun insert(item: T): Long
    @Insert suspend fun insertAll(items: List<T>): List<Long>
    @Update suspend fun update(item: T): Int
    @Delete suspend fun delete(item: T): Int
}

@Dao
interface MasterDao : BaseDao<Master> {
    @Query("SELECT * FROM masters ORDER BY id") suspend fun getAll(): List<Master>
    @Query("SELECT * FROM masters WHERE id = :id") suspend fun byId(id: Long): Master?
}

data class OrderDetails(val id: Long, val master: String, val car: String, val cost: Double)

@Dao
interface OrderDao : BaseDao<Order> {
    @Query("SELECT * FROM orders ORDER BY id") suspend fun getAll(): List<Order>
    @Query("SELECT * FROM orders WHERE id = :id") suspend fun byId(id: Long): Order?
    @Query("SELECT COUNT(*) FROM orders WHERE masterId = :masterId")
    fun observeCount(masterId: Long): Flow<Int>
    @Query("SELECT * FROM orders WHERE masterId = :masterId AND cost > :minimum ORDER BY cost DESC, id")
    suspend fun above(masterId: Long, minimum: Double): List<Order>
    @Query("SELECT COALESCE(SUM(cost), 0.0) FROM orders WHERE masterId = :masterId")
    suspend fun revenue(masterId: Long): Double
    @Query(
        """SELECT orders.id, masters.name AS master, orders.car, orders.cost
            FROM orders JOIN masters ON orders.masterId = masters.id
            WHERE orders.masterId = :masterId ORDER BY orders.id""",
    )
    suspend fun details(masterId: Long): List<OrderDetails>
    @Query("SELECT COUNT(*) FROM orders") suspend fun count(): Int
}

@Database(entities = [Master::class, Order::class], version = 1, exportSchema = true)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun masters(): MasterDao
    abstract fun orders(): OrderDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "autoservice.db",
            ).build().also { instance = it }
        }
    }
}
