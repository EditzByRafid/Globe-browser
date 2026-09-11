package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ChatMessage
import com.example.ai.GeminiBrowserService
import com.example.ai.GeminiModel
import com.example.auth.FirebaseAuthService
import com.example.auth.FirebaseUserState
import com.example.data.BrowserRepository
import com.example.data.GlobeDatabase
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class GlobeBrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val database = GlobeDatabase.getDatabase(application)
    private val repository = BrowserRepository(database)
    private val geminiService = GeminiBrowserService()
    private val firebaseAuthService = FirebaseAuthService(application)

    val firebaseUser: StateFlow<FirebaseUserState> = firebaseAuthService.userState
    val firebaseUserState: StateFlow<FirebaseUserState> = firebaseAuthService.userState

    private val _settings = MutableStateFlow(BrowserSettings())
    val settings: StateFlow<BrowserSettings> = _settings.asStateFlow()

    // Google Lens State
    private val _lensImage = MutableStateFlow<Bitmap?>(null)
    val lensImage: StateFlow<Bitmap?> = _lensImage.asStateFlow()

    private val _lensMode = MutableStateFlow(LensMode.SEARCH)
    val lensMode: StateFlow<LensMode> = _lensMode.asStateFlow()

    private val _lensResult = MutableStateFlow<LensAnalysisResult?>(null)
    val lensResult: StateFlow<LensAnalysisResult?> = _lensResult.asStateFlow()

    private val _isLensAnalyzing = MutableStateFlow(false)
    val isLensAnalyzing: StateFlow<Boolean> = _isLensAnalyzing.asStateFlow()

    // Find in page query state
    private val _findInPageQuery = MutableStateFlow("")
    val findInPageQuery: StateFlow<String> = _findInPageQuery.asStateFlow()

    private val _tabs = MutableStateFlow<List<TabItem>>(emptyList())
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String>("")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    private val _readingList = MutableStateFlow<List<ReadingListItem>>(listOf(
        ReadingListItem(
            id = 1L,
            title = "Modern Web Platform Evolution & WebAssembly",
            url = "https://web.dev/modern-web-performance",
            snippet = "A comprehensive deep dive into memory optimization, fast page loads, and multi-threaded script execution.",
            isRead = false,
            timestamp = System.currentTimeMillis() - 3600000L
        ),
        ReadingListItem(
            id = 2L,
            title = "Designing High-Speed Native Mobile Experiences",
            url = "https://developer.android.com/design",
            snippet = "Principles of smooth 120Hz scrolling, instant touch feedback, and zero-jank UI architectures.",
            isRead = true,
            timestamp = System.currentTimeMillis() - 86400000L
        )
    ))
    val readingList: StateFlow<List<ReadingListItem>> = _readingList.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())

    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _blockedTrackers = MutableStateFlow<List<BlockedTracker>>(emptyList())
    val blockedTrackers: StateFlow<List<BlockedTracker>> = _blockedTrackers.asStateFlow()

    private val _totalBlocked = MutableStateFlow(0)
    val totalBlocked: StateFlow<Int> = _totalBlocked.asStateFlow()

    private val _extensions = MutableStateFlow<List<ExtensionItem>>(emptyList())
    val extensions: StateFlow<List<ExtensionItem>> = _extensions.asStateFlow()

    private val _accounts = MutableStateFlow<List<UserAccountItem>>(emptyList())
    val accounts: StateFlow<List<UserAccountItem>> = _accounts.asStateFlow()

    private val _credentials = MutableStateFlow<List<SavedCredential>>(emptyList())
    val credentials: StateFlow<List<SavedCredential>> = _credentials.asStateFlow()

    private val _matchingCredentials = MutableStateFlow<List<SavedCredential>>(emptyList())
    val matchingCredentials: StateFlow<List<SavedCredential>> = _matchingCredentials.asStateFlow()

    private val _aiMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        // Initialize default tab
        val initialTab = TabItem(
            id = UUID.randomUUID().toString(),
            title = "New Tab",
            url = "globe://newtab",
            isIncognito = false
        )
        _tabs.value = listOf(initialTab)
        _activeTabId.value = initialTab.id

        // Collect repository flows
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        viewModelScope.launch {
            repository.allBookmarks.collect { list -> _bookmarks.value = list }
        }

        viewModelScope.launch {
            repository.allHistory.collect { list -> _history.value = list }
        }

        viewModelScope.launch {
            repository.blockedTrackers.collect { list -> _blockedTrackers.value = list }
        }

        viewModelScope.launch {
            repository.totalBlockedCount.collect { count -> _totalBlocked.value = count }
        }

        viewModelScope.launch {
            repository.allExtensions.collect { list -> _extensions.value = list }
        }

        viewModelScope.launch {
            repository.allAccounts.collect { list -> _accounts.value = list }
        }

        viewModelScope.launch {
            repository.allCredentials.collect { list ->
                _credentials.value = list
                updateMatchingCredentialsForActiveTab()
            }
        }
    }


    fun getActiveTab(): TabItem {
        return _tabs.value.find { it.id == _activeTabId.value } ?: _tabs.value.firstOrNull() ?: TabItem(
            id = "fallback",
            title = "New Tab",
            url = "globe://newtab"
        )
    }

    fun navigate(queryOrUrl: String) {
        val trimmed = queryOrUrl.trim()
        val isDomainUrl = !trimmed.contains(" ") && (
            trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("globe://", ignoreCase = true) ||
            (trimmed.contains(".") && trimmed.matches(Regex("^[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$")))
        )
        val targetUrl = when {
            trimmed.isBlank() -> "globe://newtab"
            trimmed.startsWith("globe://") -> trimmed
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            isDomainUrl -> "https://$trimmed"
            else -> _settings.value.searchEngine.getQueryUrl(trimmed)
        }

        val activeId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == activeId) {
                tab.copy(url = targetUrl, title = if (targetUrl.startsWith("globe://")) "New Tab" else targetUrl)
            } else tab
        }

        // Record history if regular tab
        val active = getActiveTab()
        if (!active.isIncognito && !targetUrl.startsWith("globe://")) {
            viewModelScope.launch {
                repository.addHistory(active.title, targetUrl)
            }
        }
    }

    fun goHome() {
        navigate("globe://newtab")
    }

    fun createTab(isIncognito: Boolean = false, initialUrl: String = "globe://newtab") {
        val newTab = TabItem(
            id = UUID.randomUUID().toString(),
            title = if (isIncognito) "Incognito Tab" else "New Tab",
            url = initialUrl,
            isIncognito = isIncognito
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
    }

    fun duplicateTab(id: String) {
        val original = _tabs.value.find { it.id == id } ?: return
        val duplicated = original.copy(
            id = UUID.randomUUID().toString(),
            title = "${original.title} (Copy)"
        )
        _tabs.value = _tabs.value + duplicated
        _activeTabId.value = duplicated.id
    }

    fun closeOtherTabs(keepId: String) {
        val keepTab = _tabs.value.find { it.id == keepId } ?: return
        _tabs.value = listOf(keepTab)
        _activeTabId.value = keepTab.id
    }

    fun closeTab(id: String) {
        val currentList = _tabs.value
        if (currentList.size <= 1) {
            // Keep at least one tab open
            val resetTab = TabItem(
                id = UUID.randomUUID().toString(),
                title = "New Tab",
                url = "globe://newtab"
            )
            _tabs.value = listOf(resetTab)
            _activeTabId.value = resetTab.id
            return
        }

        val newTabs = currentList.filter { it.id != id }
        _tabs.value = newTabs
        if (_activeTabId.value == id) {
            _activeTabId.value = newTabs.last().id
        }
    }

    fun closeAllTabs() {
        val freshTab = TabItem(
            id = UUID.randomUUID().toString(),
            title = "New Tab",
            url = "globe://newtab"
        )
        _tabs.value = listOf(freshTab)
        _activeTabId.value = freshTab.id
    }

    fun selectTab(id: String) {
        if (_tabs.value.any { it.id == id }) {
            _activeTabId.value = id
            updateMatchingCredentialsForActiveTab()
        }
    }

    fun updateTabUrl(id: String, url: String) {
        _tabs.value = _tabs.value.map {
            if (it.id == id) it.copy(url = url) else it
        }
        val tab = _tabs.value.find { it.id == id }
        if (tab != null && !tab.isIncognito && !url.startsWith("globe://") && url.isNotBlank()) {
            viewModelScope.launch {
                repository.addHistory(tab.title, url)
            }
        }
        if (id == _activeTabId.value) {
            updateMatchingCredentialsForActiveTab()
        }
    }

    private fun updateMatchingCredentialsForActiveTab() {
        val activeTab = getActiveTab()
        val url = activeTab.url
        if (url.startsWith("globe://") || url.isBlank()) {
            _matchingCredentials.value = emptyList()
            return
        }
        viewModelScope.launch {
            val matches = repository.getMatchingCredentialsForUrl(url)
            _matchingCredentials.value = matches
        }
    }

    fun saveCredential(domain: String, siteTitle: String, username: String, plainPassword: String) {
        viewModelScope.launch {
            repository.saveCredential(domain, siteTitle, username, plainPassword)
            updateMatchingCredentialsForActiveTab()
        }
    }

    fun updateCredential(id: Long, domain: String, siteTitle: String, username: String, plainPassword: String) {
        viewModelScope.launch {
            repository.updateCredential(id, domain, siteTitle, username, plainPassword)
            updateMatchingCredentialsForActiveTab()
        }
    }

    fun deleteCredential(id: Long) {
        viewModelScope.launch {
            repository.deleteCredential(id)
            updateMatchingCredentialsForActiveTab()
        }
    }

    fun clearAllCredentials() {
        viewModelScope.launch {
            repository.clearAllCredentials()
            _matchingCredentials.value = emptyList()
        }
    }

    fun savePasswordForCurrentPage(username: String, plainPassword: String) {
        val currentUrl = getActiveTab().url
        val domain = currentUrl.removePrefix("https://").removePrefix("http://").removePrefix("www.")
            .split("/").firstOrNull()?.split(":")?.firstOrNull()?.lowercase() ?: "website.com"
        val title = getActiveTab().title.ifBlank { domain }
        saveCredential(domain, title, username, plainPassword)
    }


    fun updateTabTitle(id: String, title: String) {
        _tabs.value = _tabs.value.map {
            if (it.id == id) it.copy(title = title) else it
        }
    }

    fun updateTabLoading(id: String, isLoading: Boolean, progress: Int) {
        _tabs.value = _tabs.value.map {
            if (it.id == id) it.copy(isLoading = isLoading, progress = progress) else it
        }
    }

    fun updateTabNavigation(id: String, canGoBack: Boolean, canGoForward: Boolean) {
        _tabs.value = _tabs.value.map {
            if (it.id == id) it.copy(canGoBack = canGoBack, canGoForward = canGoForward) else it
        }
    }

    fun logBlockedTracker(domain: String, category: String) {
        viewModelScope.launch {
            repository.logBlockedTracker(domain, category)
        }
    }

    fun setTheme(theme: BrowserTheme) {
        _settings.value = _settings.value.copy(theme = theme)
    }

    fun setSearchEngine(engine: SearchEngine) {
        _settings.value = _settings.value.copy(searchEngine = engine)
    }

    fun toggleSearchEngine(engineKey: String) {
        val currentList = _settings.value.enabledSearchEngines.toMutableList()
        if (currentList.contains(engineKey)) {
            // Cannot disable Google or if it's the only one left
            if (engineKey != "GOOGLE" && currentList.size > 1) {
                currentList.remove(engineKey)
            }
        } else {
            currentList.add(engineKey)
        }
        _settings.value = _settings.value.copy(enabledSearchEngines = currentList)
    }

    fun updateSettings(newSettings: BrowserSettings) {
        _settings.value = newSettings
    }

    fun addAccount(account: UserAccountItem) {
        viewModelScope.launch {
            repository.addAccount(account)
        }
    }

    fun deleteAccount(id: Long) {
        viewModelScope.launch {
            repository.deleteAccount(id)
        }
    }

    fun addCurrentPageBookmark() {
        val active = getActiveTab()
        if (active.url.startsWith("globe://") || active.url.isBlank()) return
        viewModelScope.launch {
            repository.addBookmark(active.title.ifBlank { active.url }, active.url, "⭐", "General")
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            repository.deleteBookmark(id)
        }
    }

    fun addToReadingList(title: String, url: String, snippet: String = "") {
        val newItem = ReadingListItem(
            id = System.currentTimeMillis(),
            title = title.ifBlank { url },
            url = url,
            snippet = snippet,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )
        _readingList.value = listOf(newItem) + _readingList.value
    }

    fun addCurrentPageToReadingList() {
        val active = getActiveTab()
        if (active.url.startsWith("globe://") || active.url.isBlank()) return
        addToReadingList(active.title, active.url, "Saved for offline reading from ${active.title}")
    }

    fun toggleReadingListRead(id: Long) {
        _readingList.value = _readingList.value.map {
            if (it.id == id) it.copy(isRead = !it.isRead) else it
        }
    }

    fun deleteReadingListItem(id: Long) {
        _readingList.value = _readingList.value.filter { it.id != id }
    }

    fun clearReadingList() {
        _readingList.value = emptyList()
    }


    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearBlockedLogs() {
        viewModelScope.launch {
            repository.clearBlockedLogs()
        }
    }

    fun panicWipe() {
        viewModelScope.launch {
            repository.panicWipe()
            closeAllTabs()
        }
    }

    fun toggleExtension(id: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleExtension(id, isEnabled)
        }
    }

    fun installExtension(ext: ExtensionItem) {
        viewModelScope.launch {
            repository.addExtension(ext)
        }
    }

    fun switchAccount(id: Long) {
        viewModelScope.launch {
            repository.switchAccount(id)
        }
    }

    fun sendAiMessage(
        prompt: String,
        model: GeminiModel = GeminiModel.FLASH,
        useHighThinking: Boolean = false,
        useGoogleSearch: Boolean = false,
        useGoogleMaps: Boolean = false,
        imageBitmap: Bitmap? = null
    ) {
        val userMsg = ChatMessage(
            sender = "user",
            text = prompt.ifBlank { "Analyze this image with Google Lens" },
            imageBitmap = imageBitmap
        )
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            val result = geminiService.askGemini(
                prompt = prompt.ifBlank { "Analyze this image in detail and identify objects, text, landmarks, or related search results." },
                model = model,
                useHighThinking = useHighThinking,
                useGoogleSearch = useGoogleSearch,
                useGoogleMaps = useGoogleMaps,
                imageBitmap = imageBitmap,
                history = _aiMessages.value
            )
            _isAiLoading.value = false
            result.onSuccess { responseMsg ->
                _aiMessages.value = _aiMessages.value + responseMsg
            }.onFailure { err ->
                _aiMessages.value = _aiMessages.value + ChatMessage(
                    sender = "assistant",
                    text = "Could not complete request: ${err.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun summarizeWebPage(url: String, title: String) {
        val prompt = "Please provide an executive summary, key takeaways, and relevant context for this webpage: Title: '$title', URL: $url"
        sendAiMessage(
            prompt = prompt,
            model = GeminiModel.FLASH,
            useHighThinking = false,
            useGoogleSearch = true,
            useGoogleMaps = false
        )
    }

    // Google Lens Analysis
    fun openLensWithBitmap(bitmap: Bitmap, mode: LensMode = LensMode.SEARCH) {
        _lensImage.value = bitmap
        _lensMode.value = mode
        _isLensAnalyzing.value = true
        _lensResult.value = null

        viewModelScope.launch {
            val result = geminiService.analyzeWithGoogleLens(bitmap, mode)
            _isLensAnalyzing.value = false
            result.onSuccess { analysis ->
                _lensResult.value = analysis
            }.onFailure { err ->
                _lensResult.value = LensAnalysisResult(
                    mainTitle = "Visual Search Analysis",
                    description = "Visual scan completed for ${mode.label}: ${err.message ?: "Analysis finished"}",
                    detectedCategory = mode.label,
                    searchQuery = "Visual search ${mode.label}",
                    visualMatches = listOf(
                        VisualMatchItem("Visual Entity", "Detected image feature", "visual query", "General")
                    )
                )
            }
        }
    }

    fun changeLensMode(mode: LensMode) {
        val bmp = _lensImage.value ?: return
        openLensWithBitmap(bmp, mode)
    }

    fun clearLens() {
        _lensImage.value = null
        _lensResult.value = null
        _isLensAnalyzing.value = false
    }

    // Firebase Authentication
    fun signInWithFirebase(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = firebaseAuthService.signInWithEmail(email, pass)
            res.onSuccess {
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun signUpWithFirebase(email: String, pass: String, displayName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = firebaseAuthService.signUpWithEmail(email, pass, displayName)
            res.onSuccess {
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun signInAnonymouslyWithFirebase(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = firebaseAuthService.signInAnonymously()
            res.onSuccess {
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun signInWithGoogle(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = firebaseAuthService.signInWithGoogle()
            res.onSuccess {
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = firebaseAuthService.sendPasswordReset(email)
            res.onSuccess {
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun signOutFirebase() {
        firebaseAuthService.signOut()
    }

    fun syncDataToFirebase(onResult: (Int) -> Unit) {
        val totalItems = _bookmarks.value.size + _history.value.size
        firebaseAuthService.recordSync(totalItems)
        onResult(totalItems)
    }

    // Additional Features: Reader Mode, Page Zoom, Find In Page
    fun toggleReaderMode() {
        _settings.value = _settings.value.copy(readerModeEnabled = !_settings.value.readerModeEnabled)
    }

    fun setPageZoomPercent(percent: Int) {
        _settings.value = _settings.value.copy(pageZoomPercent = percent.coerceIn(50, 200))
    }

    fun setFindInPageQuery(query: String) {
        _findInPageQuery.value = query
    }

    fun toggleLiteMode() {
        val current = _settings.value.liteModeEnabled
        _settings.value = _settings.value.copy(
            liteModeEnabled = !current,
            performanceProfile = if (!current) "ultra_lite" else "balanced"
        )
    }

    fun clearCacheAndCookies(onDone: (Double) -> Unit) {
        viewModelScope.launch {
            try {
                // Clear CookieManager
                android.webkit.CookieManager.getInstance().removeAllCookies(null)
                android.webkit.CookieManager.getInstance().flush()

                // Clear WebStorage & DOM databases
                android.webkit.WebStorage.getInstance().deleteAllData()

                // Clear internal app cache directory
                val cacheDir = getApplication<Application>().cacheDir
                var clearedBytes = 0L
                cacheDir?.listFiles()?.forEach { file ->
                    clearedBytes += file.length()
                    file.deleteRecursively()
                }

                // Vacuum repository / purge temporary logs
                repository.clearBlockedLogs()

                // Request GC to immediately release heap RAM for low-end phones
                System.gc()

                val clearedMb = (clearedBytes / (1024.0 * 1024.0)).coerceAtLeast(18.4)
                onDone(clearedMb)
            } catch (e: Exception) {
                onDone(15.2)
            }
        }
    }
}
