package com.example.model

enum class SearchEngine(val displayName: String, val searchUrl: String, val homeUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
    BING("Bing", "https://www.bing.com/search?q=", "https://www.bing.com"),
    BRAVE("Brave Search", "https://search.brave.com/search?q=", "https://search.brave.com");

    fun getQueryUrl(query: String): String {
        return searchUrl + java.net.URLEncoder.encode(query, "UTF-8")
    }
}

enum class BrowserTheme(val label: String) {
    ELECTRIC_BLUE("Electric Cyan (Globe)"),
    CHROME_LIGHT("Google Chrome Light"),
    CHROME_DARK("Google Chrome Dark"),
    OPERA_GX("Opera GX Crimson"),
    EMERALD_CYBER("Matrix Emerald"),
    OLED_BLACK("OLED Pitch Black")
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
    val liquidGlassEnabled: Boolean = true,
    val lowEndModeEnabled: Boolean = false, // Optimized for Realme Note 60 & low-end devices
    val reduceMotion: Boolean = false,
    val theme: BrowserTheme = BrowserTheme.ELECTRIC_BLUE,
    val toolbarPosition: ToolbarPosition = ToolbarPosition.BOTTOM,
    val searchEngine: SearchEngine = SearchEngine.GOOGLE,
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
    val pageZoomPercent: Int = 100
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

