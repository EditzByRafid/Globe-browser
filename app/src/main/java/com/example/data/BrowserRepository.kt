package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BrowserRepository(private val database: GlobeDatabase) {

    val allTabs: Flow<List<TabItem>> = database.tabDao().getAllTabs().map { list ->
        list.map { entity ->
            TabItem(
                id = entity.id,
                title = entity.title,
                url = entity.url,
                isIncognito = entity.isIncognito,
                lastAccessed = entity.lastAccessed
            )
        }
    }

    val allBookmarks: Flow<List<BookmarkItem>> = database.bookmarkDao().getAllBookmarks().map { list ->
        list.map { entity ->
            BookmarkItem(
                id = entity.id,
                title = entity.title,
                url = entity.url,
                iconEmoji = entity.iconEmoji,
                category = entity.category
            )
        }
    }

    val allHistory: Flow<List<HistoryItem>> = database.historyDao().getAllHistory().map { list ->
        list.map { entity ->
            HistoryItem(
                id = entity.id,
                title = entity.title,
                url = entity.url,
                timestamp = entity.timestamp,
                visitCount = entity.visitCount
            )
        }
    }

    val blockedTrackers: Flow<List<BlockedTracker>> = database.blockedTrackerDao().getRecentBlocked().map { list ->
        list.map { entity ->
            BlockedTracker(
                id = entity.id,
                domain = entity.domain,
                category = entity.category,
                timestamp = entity.timestamp
            )
        }
    }

    val totalBlockedCount: Flow<Int> = database.blockedTrackerDao().getTotalBlockedCount()

    val allExtensions: Flow<List<ExtensionItem>> = database.extensionDao().getAllExtensions().map { list ->
        list.map { entity ->
            ExtensionItem(
                id = entity.id,
                name = entity.name,
                version = entity.version,
                description = entity.description,
                isEnabled = entity.isEnabled,
                permissions = entity.permissionsCsv.split(",").filter { it.isNotBlank() },
                scriptCode = entity.scriptCode
            )
        }
    }

    val allAccounts: Flow<List<UserAccountItem>> = database.userAccountDao().getAllAccounts().map { list ->
        list.map { entity ->
            UserAccountItem(
                id = entity.id,
                username = entity.username,
                email = entity.email,
                fullName = entity.fullName,
                avatarColorHex = entity.avatarColorHex,
                role = entity.role,
                isCurrent = entity.isCurrent
            )
        }
    }

    suspend fun saveTab(tab: TabItem, position: Int) = withContext(Dispatchers.IO) {
        database.tabDao().insertTab(
            TabEntity(
                id = tab.id,
                title = tab.title,
                url = tab.url,
                isIncognito = tab.isIncognito,
                lastAccessed = tab.lastAccessed,
                position = position
            )
        )
    }

    suspend fun deleteTab(id: String) = withContext(Dispatchers.IO) {
        database.tabDao().deleteTab(id)
    }

    suspend fun clearTabs() = withContext(Dispatchers.IO) {
        database.tabDao().deleteAllTabs()
    }

    suspend fun addBookmark(title: String, url: String, emoji: String = "🔖", category: String = "Favorites") = withContext(Dispatchers.IO) {
        database.bookmarkDao().insertBookmark(
            BookmarkEntity(title = title, url = url, iconEmoji = emoji, category = category)
        )
    }

    suspend fun deleteBookmark(id: Long) = withContext(Dispatchers.IO) {
        database.bookmarkDao().deleteBookmark(id)
    }

    suspend fun addHistory(title: String, url: String) = withContext(Dispatchers.IO) {
        if (url.startsWith("globe://") || url.isBlank()) return@withContext
        database.historyDao().insertHistory(
            HistoryEntity(title = title.ifBlank { url }, url = url, timestamp = System.currentTimeMillis())
        )
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        database.historyDao().clearHistory()
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        database.historyDao().deleteHistoryItem(id)
    }

    suspend fun logBlockedTracker(domain: String, category: String) = withContext(Dispatchers.IO) {
        database.blockedTrackerDao().insertBlocked(
            BlockedTrackerEntity(domain = domain, category = category)
        )
    }

    suspend fun clearBlockedLogs() = withContext(Dispatchers.IO) {
        database.blockedTrackerDao().clearBlockedLogs()
    }

    suspend fun toggleExtension(id: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        database.extensionDao().toggleExtension(id, enabled)
    }

    suspend fun addExtension(ext: ExtensionItem) = withContext(Dispatchers.IO) {
        database.extensionDao().insertExtension(
            ExtensionEntity(
                id = ext.id,
                name = ext.name,
                version = ext.version,
                description = ext.description,
                isEnabled = ext.isEnabled,
                permissionsCsv = ext.permissions.joinToString(","),
                scriptCode = ext.scriptCode
            )
        )
    }

    suspend fun deleteExtension(id: String) = withContext(Dispatchers.IO) {
        database.extensionDao().deleteExtension(id)
    }

    suspend fun switchAccount(id: Long) = withContext(Dispatchers.IO) {
        database.userAccountDao().clearCurrentAccount()
        database.userAccountDao().setCurrentAccount(id)
    }

    suspend fun addAccount(account: UserAccountItem) = withContext(Dispatchers.IO) {
        val count = database.userAccountDao().getAccountCount()
        if (count >= 700) return@withContext // Capacity limit of 700 accounts

        database.userAccountDao().insertAccount(
            UserAccountEntity(
                id = if (account.id > 0) account.id else 0,
                username = account.username,
                email = account.email,
                fullName = account.fullName,
                avatarColorHex = account.avatarColorHex,
                role = account.role,
                isCurrent = account.isCurrent
            )
        )
    }

    suspend fun deleteAccount(id: Long) = withContext(Dispatchers.IO) {
        database.userAccountDao().deleteAccount(id)
    }

    suspend fun searchAccounts(query: String): List<UserAccountItem> = withContext(Dispatchers.IO) {
        database.userAccountDao().searchAccounts(query).map {
            UserAccountItem(
                id = it.id,
                username = it.username,
                email = it.email,
                fullName = it.fullName,
                avatarColorHex = it.avatarColorHex,
                role = it.role,
                isCurrent = it.isCurrent
            )
        }
    }

    suspend fun panicWipe() = withContext(Dispatchers.IO) {
        database.tabDao().deleteAllTabs()
        database.historyDao().clearHistory()
        database.blockedTrackerDao().clearBlockedLogs()
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existingCount = database.userAccountDao().getAccountCount()
        if (existingCount == 0) {
            // Seed private user account into 700-capacity storage database
            val primaryAccount = UserAccountEntity(
                id = 1,
                username = "my_profile",
                email = "user@gmail.com",
                fullName = "Personal Profile (Google)",
                avatarColorHex = "#1A73E8",
                role = "Primary Account",
                isCurrent = true
            )
            database.userAccountDao().insertAccount(primaryAccount)
        } else if (existingCount > 100) {
            // Clean up any previously seeded dummy crowd data so the user's private database is pristine
            val current = database.userAccountDao().getCurrentAccount()
            database.userAccountDao().deleteAllAccounts()
            val primaryAccount = UserAccountEntity(
                id = 1,
                username = current?.username ?: "my_profile",
                email = current?.email ?: "user@gmail.com",
                fullName = current?.fullName ?: "Personal Profile (Google)",
                avatarColorHex = current?.avatarColorHex ?: "#1A73E8",
                role = "Primary Account",
                isCurrent = true
            )
            database.userAccountDao().insertAccount(primaryAccount)
        }

        // Seed default bookmarks if empty
        val defaultBookmarks = listOf(
            BookmarkEntity(title = "Google Search", url = "https://www.google.com", iconEmoji = "🔍", category = "Search"),
            BookmarkEntity(title = "Google Maps", url = "https://maps.google.com", iconEmoji = "🗺️", category = "Navigation"),
            BookmarkEntity(title = "YouTube", url = "https://www.youtube.com", iconEmoji = "▶️", category = "Media"),
            BookmarkEntity(title = "Wikipedia", url = "https://www.wikipedia.org", iconEmoji = "📚", category = "Knowledge"),
            BookmarkEntity(title = "GitHub", url = "https://github.com", iconEmoji = "💻", category = "Development"),
            BookmarkEntity(title = "Reddit", url = "https://www.reddit.com", iconEmoji = "💬", category = "Social"),
            BookmarkEntity(title = "Hacker News", url = "https://news.ycombinator.com", iconEmoji = "📰", category = "News")
        )
        defaultBookmarks.forEach { database.bookmarkDao().insertBookmark(it) }

        // Seed 205+ built-in active and customizable extensions
        val extCount = database.extensionDao().getExtensionCount()
        if (extCount < 200) {
            val allExtensions = ExtensionSeeder.generateAllExtensions()
            database.extensionDao().insertExtensions(allExtensions)
        }
    }
}
