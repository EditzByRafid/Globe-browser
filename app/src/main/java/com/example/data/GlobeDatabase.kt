package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val isIncognito: Boolean,
    val lastAccessed: Long,
    val position: Int
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val iconEmoji: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val visitCount: Int = 1
)

@Entity(tableName = "blocked_trackers")
data class BlockedTrackerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val domain: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "extensions")
data class ExtensionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: String,
    val description: String,
    val isEnabled: Boolean,
    val permissionsCsv: String,
    val scriptCode: String
)

@Entity(tableName = "accounts")
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val email: String,
    val fullName: String,
    val avatarColorHex: String,
    val role: String,
    val isCurrent: Boolean = false
)

@Dao
interface TabDao {
    @Query("SELECT * FROM tabs ORDER BY position ASC")
    fun getAllTabs(): Flow<List<TabEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTab(tab: TabEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTabs(tabs: List<TabEntity>)

    @Query("DELETE FROM tabs WHERE id = :id")
    suspend fun deleteTab(id: String)

    @Query("DELETE FROM tabs")
    suspend fun deleteAllTabs()
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY id DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: Long)

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAllBookmarks()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}

@Dao
interface BlockedTrackerDao {
    @Query("SELECT * FROM blocked_trackers ORDER BY timestamp DESC LIMIT 200")
    fun getRecentBlocked(): Flow<List<BlockedTrackerEntity>>

    @Query("SELECT COUNT(*) FROM blocked_trackers")
    fun getTotalBlockedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocked(tracker: BlockedTrackerEntity)

    @Query("DELETE FROM blocked_trackers")
    suspend fun clearBlockedLogs()
}

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions")
    fun getAllExtensions(): Flow<List<ExtensionEntity>>

    @Query("SELECT COUNT(*) FROM extensions")
    suspend fun getExtensionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtension(extension: ExtensionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtensions(extensions: List<ExtensionEntity>)

    @Query("UPDATE extensions SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleExtension(id: String, enabled: Boolean)

    @Query("DELETE FROM extensions WHERE id = :id")
    suspend fun deleteExtension(id: String)
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountCount(): Int

    @Query("SELECT * FROM accounts WHERE username LIKE '%' || :query || '%' OR fullName LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchAccounts(query: String): List<UserAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<UserAccountEntity>)

    @Query("UPDATE accounts SET isCurrent = 0")
    suspend fun clearCurrentAccount()

    @Query("UPDATE accounts SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrentAccount(id: Long)

    @Query("SELECT * FROM accounts WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentAccount(): UserAccountEntity?
}

@Database(
    entities = [
        TabEntity::class,
        BookmarkEntity::class,
        HistoryEntity::class,
        BlockedTrackerEntity::class,
        ExtensionEntity::class,
        UserAccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GlobeDatabase : RoomDatabase() {
    abstract fun tabDao(): TabDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun blockedTrackerDao(): BlockedTrackerDao
    abstract fun extensionDao(): ExtensionDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: GlobeDatabase? = null

        fun getDatabase(context: Context): GlobeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GlobeDatabase::class.java,
                    "globe_browser.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
