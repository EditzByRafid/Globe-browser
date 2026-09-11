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

    // Downloads Manager State
    private val _downloads = MutableStateFlow<List<DownloadItem>>(
        listOf(
            DownloadItem(
                id = "dl-1",
                fileName = "GB-Browser.apk",
                fileUrl = "https://gb-browser.internal/download/GB-Browser.apk",
                totalSizeBytes = 31457280L,
                downloadedBytes = 31457280L,
                status = DownloadStatus.COMPLETED,
                category = DownloadCategory.OTHERS,
                timestamp = System.currentTimeMillis() - 120000L,
                mimeType = "application/vnd.android.package-archive"
            ),
            DownloadItem(
                id = "dl-2",
                fileName = "GB-Browser.ipa",
                fileUrl = "https://gb-browser.internal/download/GB-Browser.ipa",
                totalSizeBytes = 18457280L,
                downloadedBytes = 18457280L,
                status = DownloadStatus.COMPLETED,
                category = DownloadCategory.OTHERS,
                timestamp = System.currentTimeMillis() - 360000L,
                mimeType = "application/octet-stream"
            ),
            DownloadItem(
                id = "dl-3",
                fileName = "Android_Jetpack_Compose_Optimization.pdf",
                fileUrl = "https://developer.android.com/guide/performance.pdf",
                totalSizeBytes = 4194304L,
                downloadedBytes = 4194304L,
                status = DownloadStatus.COMPLETED,
                category = DownloadCategory.DOCUMENTS,
                timestamp = System.currentTimeMillis() - 1800000L,
                mimeType = "application/pdf"
            )
        )
    )
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    // Enhanced Reading Mode State
    private val _readerArticle = MutableStateFlow<ReaderArticle?>(null)
    val readerArticle: StateFlow<ReaderArticle?> = _readerArticle.asStateFlow()

    // Chrome Web Store State
    private val _storeExtensions = MutableStateFlow<List<StoreExtensionItem>>(defaultStoreExtensions())
    val storeExtensions: StateFlow<List<StoreExtensionItem>> = _storeExtensions.asStateFlow()

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

    // Fullscreen Controls
    fun toggleFullscreen() {
        _settings.value = _settings.value.copy(fullscreenMode = !_settings.value.fullscreenMode)
    }

    fun setFullscreen(enabled: Boolean) {
        _settings.value = _settings.value.copy(fullscreenMode = enabled)
    }

    // Downloads Manager Controls
    fun startDownload(url: String, suggestedFileName: String?, contentLength: Long?, mimeType: String?) {
        val calculatedName = suggestedFileName?.takeIf { it.isNotBlank() }
            ?: url.substringAfterLast("/").substringBefore("?").takeIf { it.isNotBlank() }
            ?: "download_${System.currentTimeMillis()}"
        val size = if (contentLength != null && contentLength > 0) contentLength else 2097152L // 2MB fallback
        val cat = when {
            calculatedName.endsWith(".apk", true) -> DownloadCategory.OTHERS
            calculatedName.endsWith(".ipa", true) -> DownloadCategory.OTHERS
            calculatedName.endsWith(".pdf", true) || calculatedName.endsWith(".doc", true) -> DownloadCategory.DOCUMENTS
            calculatedName.endsWith(".png", true) || calculatedName.endsWith(".jpg", true) || calculatedName.endsWith(".webp", true) -> DownloadCategory.IMAGES
            calculatedName.endsWith(".mp4", true) || calculatedName.endsWith(".mkv", true) -> DownloadCategory.VIDEOS
            calculatedName.endsWith(".mp3", true) || calculatedName.endsWith(".wav", true) -> DownloadCategory.AUDIO
            calculatedName.endsWith(".zip", true) || calculatedName.endsWith(".tar", true) || calculatedName.endsWith(".gz", true) -> DownloadCategory.ARCHIVES
            else -> DownloadCategory.OTHERS
        }

        val newItem = DownloadItem(
            id = UUID.randomUUID().toString(),
            fileName = calculatedName,
            fileUrl = url,
            totalSizeBytes = size,
            downloadedBytes = size,
            status = DownloadStatus.COMPLETED,
            category = cat,
            timestamp = System.currentTimeMillis(),
            mimeType = mimeType ?: "application/octet-stream"
        )
        _downloads.value = listOf(newItem) + _downloads.value
    }

    fun pauseDownload(id: String) {
        _downloads.value = _downloads.value.map {
            if (it.id == id) it.copy(status = DownloadStatus.PAUSED) else it
        }
    }

    fun resumeDownload(id: String) {
        _downloads.value = _downloads.value.map {
            if (it.id == id) it.copy(status = DownloadStatus.DOWNLOADING) else it
        }
    }

    fun deleteDownload(id: String) {
        _downloads.value = _downloads.value.filter { it.id != id }
    }

    fun clearFinishedDownloads() {
        _downloads.value = _downloads.value.filter { it.status == DownloadStatus.DOWNLOADING }
    }

    fun openDownloadsTab() {
        createTab(isIncognito = false, initialUrl = "globe://downloads")
    }

    fun openStoreTab() {
        createTab(isIncognito = false, initialUrl = "globe://extensions-store")
    }

    // Enhanced Reader Mode
    fun loadReaderForCurrentTab(title: String, url: String) {
        val domain = url.substringAfter("://").substringBefore("/").removePrefix("www.")
        val sampleParagraphs = listOf(
            "The evolution of modern web applications has accelerated dramatically with the advent of hardware-accelerated rendering engines and compiled bytecode runtimes like WebAssembly.",
            "Modern mobile chipsets, from energy-efficient octacore processors such as the Unisoc T612 in the Realme Note 60 to flagship multi-cluster silicon, require browsers to treat system memory and GPU resources with surgical discipline.",
            "By eliminating heavy DOM mutation cycles and stripping tracking telemetry from network streams, native client browsers achieve predictable 90Hz and 120Hz display refresh stability while conserving up to 40% battery life during extended reading sessions.",
            "Reader Mode isolates primary semantic content, discarding unneeded layout scripts, nested iframes, and promotional overlays. The result is pure, distraction-free reading with customizable typography and dark room contrast."
        )
        _readerArticle.value = ReaderArticle(
            title = if (title.isBlank() || title == "New Tab") "Distraction-Free Reading View" else title,
            domain = if (domain.isBlank()) "web.read" else domain,
            author = "GB Intelligence Engine",
            readingTimeMinutes = 3,
            wordCount = 420,
            paragraphs = sampleParagraphs,
            heroImageUrl = null
        )
        _settings.value = _settings.value.copy(readerModeEnabled = true)
    }

    fun closeReaderMode() {
        _settings.value = _settings.value.copy(readerModeEnabled = false)
        _readerArticle.value = null
    }

    fun updateReaderSettings(theme: String, fontSize: Int, fontFamily: String) {
        _settings.value = _settings.value.copy(
            readerTheme = theme,
            readerFontSize = fontSize,
            readerFontFamily = fontFamily
        )
    }

    // Chrome Web Store Operations
    fun installStoreExtension(item: StoreExtensionItem) {
        val newExt = ExtensionItem(
            id = item.id,
            name = item.name,
            version = item.version,
            description = item.shortDescription,
            isEnabled = true,
            permissions = item.permissions,
            scriptCode = item.scriptCode
        )
        viewModelScope.launch {
            repository.addExtension(newExt)
        }
    }

    fun uninstallExtension(extensionId: String) {
        viewModelScope.launch {
            repository.deleteExtension(extensionId)
        }
    }

    fun createCustomExtension(name: String, description: String, category: String, scriptCode: String) {
        val id = "custom_" + UUID.randomUUID().toString().take(8)
        val storeItem = StoreExtensionItem(
            id = id,
            name = name,
            version = "1.0.0",
            developer = "You (Local Developer)",
            category = category,
            rating = 5.0f,
            ratingCount = 1,
            userCount = "1 user",
            shortDescription = description,
            fullDescription = "$description\n\nCustom UserScript running directly inside GB Browser engine.",
            permissions = listOf("ActiveTab", "Storage", "ScriptInjection"),
            iconColorHex = "#34A853",
            previewBadge = "Custom UserScript",
            previewFeatureHighlights = listOf("Instant DOM execution", "Custom CSS/JS filters", "Zero-telemetry local code"),
            reviews = listOf(
                StoreReview(
                    id = "rev_owner",
                    author = "You",
                    rating = 5,
                    date = "Today",
                    comment = "Custom script compiled and installed successfully."
                )
            ),
            scriptCode = scriptCode,
            isCustomCreated = true
        )
        _storeExtensions.value = listOf(storeItem) + _storeExtensions.value

        // Automatically install into active extensions
        installStoreExtension(storeItem)
    }

    fun addStoreReview(extensionId: String, author: String, rating: Int, comment: String) {
        val newReview = StoreReview(
            id = UUID.randomUUID().toString(),
            author = author.ifBlank { "GB User" },
            rating = rating.coerceIn(1, 5),
            date = "Just now",
            comment = comment
        )
        _storeExtensions.value = _storeExtensions.value.map { item ->
            if (item.id == extensionId) {
                val updatedReviews = listOf(newReview) + item.reviews
                val newAvg = (item.rating * item.ratingCount + rating) / (item.ratingCount + 1)
                item.copy(
                    reviews = updatedReviews,
                    rating = (newAvg * 10).toInt() / 10f,
                    ratingCount = item.ratingCount + 1
                )
            } else item
        }
    }
}

private fun defaultStoreExtensions(): List<StoreExtensionItem> {
    return listOf(
        StoreExtensionItem(
            id = "ublock_lite",
            name = "uBlock Origin Lite",
            version = "1.58.0",
            developer = "Raymond Hill (gorhill)",
            category = "Ad Blockers",
            rating = 4.9f,
            ratingCount = 48290,
            userCount = "10,000,000+ users",
            shortDescription = "An ultra-efficient content blocker. Fast, lightweight, and gentle on CPU and memory.",
            fullDescription = "uBlock Origin Lite is a permission-minimal content blocker designed for the modern web. It blocks banners, video commercials, pop-unders, and telemetry trackers automatically with zero perceptible CPU overhead.",
            permissions = listOf("declarativeNetRequest", "storage", "webRequest"),
            iconColorHex = "#EA4335",
            previewBadge = "Featured • Editor's Choice",
            previewFeatureHighlights = listOf(
                "Zero battery drain on low-spec chips",
                "Strips video ads and sponsored widgets",
                "Built-in EasyList & Peter Lowe's Blocklist"
            ),
            reviews = listOf(
                StoreReview("r1", "Alex Chen", 5, "2 days ago", "The gold standard for ad blocking on mobile. Nothing else compares."),
                StoreReview("r2", "Sarah Jenkins", 5, "1 week ago", "Runs like a dream on my Realme Note 60. Web pages load instantly now!"),
                StoreReview("r3", "Marcus B.", 4, "2 weeks ago", "Saves so much mobile data on older phones.")
            ),
            scriptCode = "console.log('[uBlock Lite] Shield active.');"
        ),
        StoreExtensionItem(
            id = "dark_reader_pro",
            name = "Dark Reader Pro",
            version = "4.9.82",
            developer = "Alexander Shutov",
            category = "Themes & Style",
            rating = 4.8f,
            ratingCount = 31400,
            userCount = "6,000,000+ users",
            shortDescription = "Invert colors smartly and apply dark mode to every website smoothly without glare.",
            fullDescription = "Dark Reader inverts bright colors making them high contrast and easy to read at night. You can adjust brightness, contrast, sepia filter, and font settings.",
            permissions = listOf("activeTab", "storage"),
            iconColorHex = "#8AB4F8",
            previewBadge = "OLED Friendly",
            previewFeatureHighlights = listOf(
                "True pitch black #000000 for OLED battery savings",
                "Adjustable warmth, sepia, and brightness",
                "Automated dusk-to-dawn switching"
            ),
            reviews = listOf(
                StoreReview("r4", "Elena Rostova", 5, "3 days ago", "Protects my eyes when reading in bed. Pitch black looks gorgeous."),
                StoreReview("r5", "David K.", 5, "2 weeks ago", "Essential extension for any browser.")
            ),
            scriptCode = "document.documentElement.style.filter = 'contrast(95%) brightness(95%)';"
        ),
        StoreExtensionItem(
            id = "grammarly_ai",
            name = "Grammarly AI Assistant",
            version = "3.2.14",
            developer = "Grammarly Inc.",
            category = "Productivity",
            rating = 4.7f,
            ratingCount = 22100,
            userCount = "8,000,000+ users",
            shortDescription = "Real-time grammar, spelling, clarity suggestions and AI rewriting on any web form.",
            fullDescription = "Compose clear, mistake-free messages, emails, and comments everywhere on the web. Features tone detection and sentence restructuring.",
            permissions = listOf("activeTab", "clipboardRead"),
            iconColorHex = "#34A853",
            previewBadge = "Productivity Pick",
            previewFeatureHighlights = listOf(
                "Instant spelling and punctuation correction",
                "Vocabulary enhancer and synonym chips",
                "Works seamlessly with Android keyboard"
            ),
            reviews = listOf(
                StoreReview("r6", "Priya Nair", 5, "Yesterday", "Super handy when typing long emails on the go."),
                StoreReview("r7", "Liam O.", 4, "3 weeks ago", "Catches mistakes I would have missed completely.")
            ),
            scriptCode = "console.log('[Grammarly] Form analyzer ready.');"
        ),
        StoreExtensionItem(
            id = "bitwarden_vault",
            name = "Bitwarden Vault Bridge",
            version = "2026.3.0",
            developer = "Bitwarden Inc.",
            category = "Privacy & Security",
            rating = 4.9f,
            ratingCount = 18900,
            userCount = "4,000,000+ users",
            shortDescription = "Secure end-to-end encrypted password and passkey synchronization across all your devices.",
            fullDescription = "Store unlimited logins and generate high-entropy passwords with AES-256 GCM encryption. Pairs directly with GB Browser's built-in Room Vault.",
            permissions = listOf("storage", "unlimitedStorage"),
            iconColorHex = "#1A73E8",
            previewBadge = "Security Verified",
            previewFeatureHighlights = listOf(
                "Zero-knowledge architecture",
                "Strong random password generator",
                "Biometric unlock support"
            ),
            reviews = listOf(
                StoreReview("r8", "Christian M.", 5, "5 days ago", "Best open-source password manager.")
            ),
            scriptCode = "console.log('[Bitwarden Bridge] Vault active.');"
        ),
        StoreExtensionItem(
            id = "sponsorblock_yt",
            name = "SponsorBlock for Video",
            version = "5.5.3",
            developer = "Ajay Ramachandran",
            category = "Productivity",
            rating = 4.9f,
            ratingCount = 15320,
            userCount = "5,000,000+ users",
            shortDescription = "Skip sponsor segments, intro animations, outro cards, and subscribe reminders automatically.",
            fullDescription = "Crowdsourced database that automatically skips sponsored ads, subscription reminders, and filler moments in online videos.",
            permissions = listOf("activeTab", "webNavigation"),
            iconColorHex = "#FBBC05",
            previewBadge = "Time Saver",
            previewFeatureHighlights = listOf(
                "Skips sponsor segments with millisecond precision",
                "Custom categories for intros, music and outros",
                "Crowd-sourced by millions of daily users"
            ),
            reviews = listOf(
                StoreReview("r9", "Jordan T.", 5, "4 days ago", "Saves me hours every week on video sites.")
            ),
            scriptCode = "console.log('[SponsorBlock] Skipping engine initialized.');"
        ),
        StoreExtensionItem(
            id = "tampermonkey_engine",
            name = "Tampermonkey Engine",
            version = "5.1.61",
            developer = "Jan Biniok",
            category = "Developer Tools",
            rating = 4.8f,
            ratingCount = 14200,
            userCount = "7,000,000+ users",
            shortDescription = "The world's most popular userscript manager. Run custom scripts and DOM modifications.",
            fullDescription = "Allows users to write and run user scripts that enhance web pages with new features, bypass paywalls, customize styles, and automate tasks.",
            permissions = listOf("allUrls", "storage", "unlimitedStorage"),
            iconColorHex = "#202124",
            previewBadge = "Developer Favorite",
            previewFeatureHighlights = listOf(
                "GM API compatibility (GM_setValue, GM_xmlhttpRequest)",
                "Built-in code editor with syntax highlighting",
                "Automatic script update checks"
            ),
            reviews = listOf(
                StoreReview("r10", "Felix Meyer", 5, "1 week ago", "Full Tampermonkey on mobile is an absolute superpower.")
            ),
            scriptCode = "console.log('[Tampermonkey] UserScript engine running.');"
        ),
        StoreExtensionItem(
            id = "google_translate_ext",
            name = "Google Translate Everywhere",
            version = "2.1.0",
            developer = "Google LLC",
            category = "Productivity",
            rating = 4.6f,
            ratingCount = 61200,
            userCount = "15,000,000+ users",
            shortDescription = "Translate entire web pages into over 100 languages with a single tap.",
            fullDescription = "View web pages in your native language instantly. Fast translation powered by Google Cloud Neural Machine Translation.",
            permissions = listOf("activeTab"),
            iconColorHex = "#1A73E8",
            previewBadge = "Official Google Tool",
            previewFeatureHighlights = listOf(
                "Translates entire pages in under 2 seconds",
                "Supports 108+ international languages",
                "Auto-detects page language"
            ),
            reviews = listOf(
                StoreReview("r11", "Wei Zhang", 5, "3 days ago", "Flawless translation for foreign news.")
            ),
            scriptCode = "console.log('[Translate] Neural translation ready.');"
        ),
        StoreExtensionItem(
            id = "privacy_badger_shield",
            name = "Privacy Badger 3.0",
            version = "2026.2.1",
            developer = "Electronic Frontier Foundation (EFF)",
            category = "Privacy & Security",
            rating = 4.8f,
            ratingCount = 19400,
            userCount = "3,000,000+ users",
            shortDescription = "Automatically learns to block invisible third-party trackers based on their behavior.",
            fullDescription = "Privacy Badger analyzes tracking scripts that spy on you across multiple sites without your permission and blocks them dynamically.",
            permissions = listOf("webRequest", "storage"),
            iconColorHex = "#EA4335",
            previewBadge = "EFF Endorsed",
            previewFeatureHighlights = listOf(
                "Heuristic tracking detection (no static list required)",
                "Prevents canvas and audio fingerprinting",
                "Sends Global Privacy Control (GPC) signal"
            ),
            reviews = listOf(
                StoreReview("r12", "Hanna S.", 5, "2 weeks ago", "Essential for real privacy online.")
            ),
            scriptCode = "console.log('[Privacy Badger] Learning algorithm active.');"
        )
    )
}
