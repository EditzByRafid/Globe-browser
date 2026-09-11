package com.example.model

enum class SearchEngine(val displayName: String, val searchUrl: String, val homeUrl: String, val iconEmoji: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com", "🔍"),
    BING("Bing", "https://www.bing.com/search?q=", "https://www.bing.com", "🟦"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com", "🦆"),
    YAHOO("Yahoo", "https://search.yahoo.com/search?q=", "https://search.yahoo.com", "🟣"),
    ECOSIA("Ecosia", "https://www.ecosia.org/search?q=", "https://www.ecosia.org", "🌳"),
    BRAVE("Brave Search", "https://search.brave.com/search?q=", "https://search.brave.com", "🦁"),
    STARTPAGE("Startpage", "https://www.startpage.com/do/dsearch?query=", "https://www.startpage.com", "🛡️"),
    YANDEX("Yandex", "https://yandex.com/search/?text=", "https://yandex.com", "🔴");

    fun getQueryUrl(query: String): String {
        return searchUrl + java.net.URLEncoder.encode(query, "UTF-8")
    }
}

enum class BrowserTheme(val label: String, val description: String) {
    LIGHT("Light Mode", "Clean Google Chrome light styling"),
    DARK("Dark Mode", "Google Chrome dark styling"),
    MIDNIGHT("Midnight Mode", "OLED pitch black with high contrast")
}

enum class ToolbarPosition {
    TOP,    // Chrome / Desktop style
    BOTTOM  // Safari / Modern Mobile style
}

data class TabItem(
    val id: String,
    val title: String,
    val url: String,
    val isIncognito: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val favicon: String? = null,
    val lastAccessed: Long = System.currentTimeMillis()
)

data class BookmarkItem(
    val id: Long = 0,
    val title: String,
    val url: String,
    val iconEmoji: String = "🌐",
    val category: String = "General"
)

data class ReadingListItem(
    val id: Long = 0,
    val title: String,
    val url: String,
    val snippet: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)


data class HistoryItem(
    val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val visitCount: Int = 1
)

data class BlockedTracker(
    val id: Long = 0,
    val domain: String,
    val category: String, // "Ad Server", "Tracker", "Fingerprinter", "Telemetry"
    val timestamp: Long = System.currentTimeMillis()
)

data class ExtensionItem(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val isEnabled: Boolean,
    val permissions: List<String>,
    val scriptCode: String
)

data class FileLabItem(
    val id: String,
    val filename: String,
    val extension: String,
    val content: String,
    val isModified: Boolean = false
)

data class UserAccountItem(
    val id: Long = 0,
    val username: String,
    val email: String,
    val fullName: String,
    val avatarColorHex: String,
    val role: String,
    val isCurrent: Boolean = false
)

data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val category: String,
    val url: String,
    val icon: String = "📰"
)

data class BrowserSettings(
    val reduceMotion: Boolean = false,
    val theme: BrowserTheme = BrowserTheme.DARK,
    val toolbarPosition: ToolbarPosition = ToolbarPosition.TOP, // Default Chrome top bar
    val searchEngine: SearchEngine = SearchEngine.GOOGLE, // Default Google
    val enabledSearchEngines: List<String> = listOf("GOOGLE", "BING", "DUCKDUCKGO", "YAHOO", "ECOSIA", "BRAVE", "STARTPAGE", "YANDEX"),
    val homePageUrl: String = "https://www.google.com",
    val adBlockEnabled: Boolean = true,
    val trackerBlockEnabled: Boolean = true,
    val cookieBlockSimulation: Boolean = true,
    val fingerprintProtection: Boolean = true,
    val desktopMode: Boolean = false,
    val tabLimitWarning: Int = 15,
    val highThinkingAi: Boolean = true,
    val showHorizontalTabStrip: Boolean = false,
    val readerModeEnabled: Boolean = false,
    val pageZoomPercent: Int = 100,
    val isFirstLaunchSetupDone: Boolean = false,
    val destinationRegion: String = "Global",
    val appLanguage: String = "English (US)",
    val performanceProfile: String = "Ultra Smooth (Auto 90Hz/120Hz)",
    val liteModeEnabled: Boolean = false,
    val autoClearCacheOnExit: Boolean = false,
    val buttonNavigationEnabled: Boolean = true, // Bottom navigation bar compatibility for one-handed operation
    val autofillEnabled: Boolean = true, // Auto-detect and autofill credentials on login forms
    val backButtonHistoryFirst: Boolean = true // System / button back navigates browser history before home
)

data class SavedCredential(
    val id: Long = 0,
    val domain: String,
    val siteTitle: String,
    val username: String,
    val encryptedPassword: String,
    val iv: String,
    val decryptedPassword: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis()
)


enum class LensMode(val label: String, val iconEmoji: String) {
    SEARCH("Search", "🔍"),
    TEXT_OCR("Text (OCR)", "📝"),
    SHOPPING("Shopping", "🛍️"),
    PLACES("Places", "📍")
}

data class VisualMatchItem(
    val title: String,
    val subtitle: String,
    val query: String,
    val category: String,
    val iconEmoji: String = "🔍"
)

data class LensAnalysisResult(
    val mainTitle: String,
    val description: String,
    val detectedCategory: String,
    val searchQuery: String,
    val extractedText: String? = null,
    val visualMatches: List<VisualMatchItem> = emptyList(),
    val shoppingMatches: List<VisualMatchItem> = emptyList(),
    val relatedQuestions: List<String> = emptyList()
)

enum class DownloadStatus {
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

enum class DownloadCategory(val label: String, val icon: String) {
    ALL("All", "📁"),
    DOCUMENTS("Documents", "📄"),
    IMAGES("Images", "🖼️"),
    VIDEOS("Videos", "🎬"),
    AUDIO("Audio", "🎵"),
    ARCHIVES("Archives", "📦"),
    OTHERS("Other", "💾")
}

data class DownloadItem(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val totalSizeBytes: Long,
    val downloadedBytes: Long,
    val speedBytesPerSec: Long = 0,
    val status: DownloadStatus = DownloadStatus.COMPLETED,
    val category: DownloadCategory = DownloadCategory.DOCUMENTS,
    val timestamp: Long = System.currentTimeMillis(),
    val mimeType: String = "application/octet-stream",
    val threadsCount: Int = 8
)


