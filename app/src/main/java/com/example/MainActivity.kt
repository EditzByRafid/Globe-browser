package com.example

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.browser.WebViewContainer
import com.example.model.LensMode
import com.example.model.ToolbarPosition
import com.example.ui.components.*
import com.example.ui.theme.GlobeBrowserTheme
import com.example.util.ImageUtils
import com.example.viewmodel.GlobeBrowserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val browserViewModel: GlobeBrowserViewModel = viewModel()
            val settings by browserViewModel.settings.collectAsState()
            val tabs by browserViewModel.tabs.collectAsState()
            val activeTabId by browserViewModel.activeTabId.collectAsState()
            val bookmarks by browserViewModel.bookmarks.collectAsState()
            val readingList by browserViewModel.readingList.collectAsState()
            val history by browserViewModel.history.collectAsState()

            val blockedTrackers by browserViewModel.blockedTrackers.collectAsState()
            val totalBlocked by browserViewModel.totalBlocked.collectAsState()
            val extensions by browserViewModel.extensions.collectAsState()
            val accounts by browserViewModel.accounts.collectAsState()
            val credentials by browserViewModel.credentials.collectAsState()
            val matchingCredentials by browserViewModel.matchingCredentials.collectAsState()
            val downloads by browserViewModel.downloads.collectAsState()
            val readerArticle by browserViewModel.readerArticle.collectAsState()
            val storeExtensions by browserViewModel.storeExtensions.collectAsState()
            val lensImage by browserViewModel.lensImage.collectAsState()
            val lensMode by browserViewModel.lensMode.collectAsState()
            val isLensAnalyzing by browserViewModel.isLensAnalyzing.collectAsState()
            val lensResult by browserViewModel.lensResult.collectAsState()

            val activeTab = remember(tabs, activeTabId) {
                browserViewModel.getActiveTab()
            }

            var webViewRef by remember { mutableStateOf<WebView?>(null) }
            val snackbarHostState = remember { SnackbarHostState() }
            val context = LocalContext.current

            // Sheet Visibilities
            var showSearchOverlay by remember { mutableStateOf(false) }
            var showTabsOverview by remember { mutableStateOf(false) }
            var showGoogleLens by remember { mutableStateOf(false) }
            var showFileLab by remember { mutableStateOf(false) }
            var showDownloadsSheet by remember { mutableStateOf(false) }
            var showPrivacyDashboard by remember { mutableStateOf(false) }
            var showExtensions by remember { mutableStateOf(false) }
            var showAccounts by remember { mutableStateOf(false) }
            var showBookmarksHistory by remember { mutableStateOf(false) }
            var bookmarksHistoryInitialTab by remember { mutableStateOf(0) }
            var showSettings by remember { mutableStateOf(false) }
            var showBrowserMenu by remember { mutableStateOf(false) }
            var showPasswordManagerSheet by remember { mutableStateOf(false) }
            var showGoogleMaps by remember { mutableStateOf(false) }
            var showSplashScreen by remember { mutableStateOf(true) }

            // Photo / Camera Activity Launchers for Google Lens Visual Search
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap ->
                if (bitmap != null) {
                    browserViewModel.openLensWithBitmap(bitmap, LensMode.SEARCH)
                    showGoogleLens = true
                }
            }

            val photoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    val bmp = ImageUtils.uriToBitmap(context, uri)
                    if (bmp != null) {
                        browserViewModel.openLensWithBitmap(bmp, LensMode.SEARCH)
                        showGoogleLens = true
                    } else {
                        Toast.makeText(context, "Could not load selected image", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Hardware Back Button Handler
            // User requested: "When i click back button i just go back to the home of the app." / Button navigation compatibility
            BackHandler(enabled = true) {
                if (showDownloadsSheet) {
                    showDownloadsSheet = false
                } else if (settings.readerModeEnabled) {
                    browserViewModel.closeReaderMode()
                } else if (settings.fullscreenMode) {
                    browserViewModel.setFullscreen(false)
                } else if (showPasswordManagerSheet) {
                    showPasswordManagerSheet = false
                } else if (showSearchOverlay) {
                    showSearchOverlay = false
                } else if (showTabsOverview) {
                    showTabsOverview = false
                } else if (showGoogleLens) {
                    showGoogleLens = false
                } else if (showFileLab) {
                    showFileLab = false
                } else if (showPrivacyDashboard) {
                    showPrivacyDashboard = false
                } else if (showExtensions) {
                    showExtensions = false
                } else if (showAccounts) {
                    showAccounts = false
                } else if (showBookmarksHistory) {
                    showBookmarksHistory = false
                } else if (showSettings) {
                    showSettings = false
                } else if (showBrowserMenu) {
                    showBrowserMenu = false
                } else if (showGoogleMaps) {
                    showGoogleMaps = false
                } else if (!activeTab.url.startsWith("globe://") && activeTab.url.isNotBlank()) {
                    if (settings.backButtonHistoryFirst && webViewRef?.canGoBack() == true) {
                        webViewRef?.goBack()
                    } else {
                        browserViewModel.goHome()
                    }
                } else {
                    finish()
                }
            }

            GlobeBrowserTheme(theme = settings.theme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!settings.isFirstLaunchSetupDone) {
                        // Phone Reset / First-Time Setup Wizard Screen
                        SetupWizardScreen(
                            initialSettings = settings,
                            onComplete = { completedSettings ->
                                browserViewModel.updateSettings(
                                    completedSettings.copy(isFirstLaunchSetupDone = true)
                                )
                                showSplashScreen = false
                            }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = MaterialTheme.colorScheme.background,
                            topBar = {
                                if (!settings.fullscreenMode && settings.toolbarPosition == ToolbarPosition.TOP) {
                                    OmniboxBar(
                                        tab = activeTab,
                                        settings = settings,
                                        tabCount = tabs.size,
                                        matchingCredentialsCount = matchingCredentials.size,
                                        onNavigate = { query -> browserViewModel.navigate(query) },
                                        onBack = {
                                            if (settings.backButtonHistoryFirst && webViewRef?.canGoBack() == true) {
                                                webViewRef?.goBack()
                                            } else {
                                                browserViewModel.goHome()
                                            }
                                        },
                                        onForward = { webViewRef?.goForward() },
                                        onReload = {
                                            if (activeTab.isLoading) webViewRef?.stopLoading()
                                            else webViewRef?.reload()
                                        },
                                        onHome = { browserViewModel.goHome() },
                                        onOpenTabs = { showTabsOverview = true },
                                        onOpenSearchOverlay = { showSearchOverlay = true },
                                        onOpenLens = { showGoogleLens = true },
                                        onOpenMenu = { showBrowserMenu = true },
                                        onOpenVault = { showPasswordManagerSheet = true },
                                        modifier = Modifier
                                            .statusBarsPadding()
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            },
                            bottomBar = {
                                if (!settings.fullscreenMode) {
                                    if (settings.toolbarPosition == ToolbarPosition.BOTTOM) {
                                        OmniboxBar(
                                            tab = activeTab,
                                            settings = settings,
                                            tabCount = tabs.size,
                                            matchingCredentialsCount = matchingCredentials.size,
                                            onNavigate = { query -> browserViewModel.navigate(query) },
                                            onBack = {
                                                if (settings.backButtonHistoryFirst && webViewRef?.canGoBack() == true) {
                                                    webViewRef?.goBack()
                                                } else {
                                                    browserViewModel.goHome()
                                                }
                                            },
                                            onForward = { webViewRef?.goForward() },
                                            onReload = {
                                                if (activeTab.isLoading) webViewRef?.stopLoading()
                                                else webViewRef?.reload()
                                            },
                                            onHome = { browserViewModel.goHome() },
                                            onOpenTabs = { showTabsOverview = true },
                                            onOpenSearchOverlay = { showSearchOverlay = true },
                                            onOpenLens = { showGoogleLens = true },
                                            onOpenMenu = { showBrowserMenu = true },
                                            onOpenVault = { showPasswordManagerSheet = true },
                                            modifier = Modifier
                                                .navigationBarsPadding()
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    } else if (settings.buttonNavigationEnabled) {
                                        BottomNavBar(
                                            tab = activeTab,
                                            tabsCount = tabs.size,
                                            matchingCredentialsCount = matchingCredentials.size,
                                            onBack = {
                                                if (settings.backButtonHistoryFirst && webViewRef?.canGoBack() == true) {
                                                    webViewRef?.goBack()
                                                } else {
                                                    browserViewModel.goHome()
                                                }
                                            },
                                            onForward = { webViewRef?.goForward() },
                                            onHome = { browserViewModel.goHome() },
                                            onOpenVault = { showPasswordManagerSheet = true },
                                            onOpenTabs = { showTabsOverview = true },
                                            onOpenMenu = { showBrowserMenu = true }
                                        )
                                    }
                                }
                            },
                            snackbarHost = { SnackbarHost(snackbarHostState) }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (settings.fullscreenMode) PaddingValues(0.dp) else innerPadding)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                if (settings.readerModeEnabled && readerArticle != null) {
                                    // Full Distraction-Free Reading Mode
                                    ReaderModeView(
                                        article = readerArticle!!,
                                        settings = settings,
                                        onUpdateSettings = { s -> browserViewModel.updateSettings(s) },
                                        onClose = { browserViewModel.closeReaderMode() }
                                    )
                                } else if (activeTab.url == "globe://extensions-store" || activeTab.url == "globe://webstore") {
                                    // Real Chrome Web Store with Preview, Reviews, Settings, and Create
                                    ExtensionStoreView(
                                        storeExtensions = storeExtensions,
                                        installedExtensions = extensions,
                                        onInstallExtension = { item -> browserViewModel.installStoreExtension(item) },
                                        onUninstallExtension = { id -> browserViewModel.uninstallExtension(id) },
                                        onCreateCustomExtension = { name, desc, cat, script ->
                                            browserViewModel.createCustomExtension(name, desc, cat, script)
                                        },
                                        onAddReview = { id, author, rating, comment ->
                                            browserViewModel.addStoreReview(id, author, rating, comment)
                                        },
                                        onCloseTab = { browserViewModel.closeTab(activeTab.id) }
                                    )
                                } else if (activeTab.url == "globe://downloads") {
                                    // Downloads Manager Page
                                    DownloadsManagerSheet(
                                        downloads = downloads,
                                        onPauseDownload = { id -> browserViewModel.pauseDownload(id) },
                                        onResumeDownload = { id -> browserViewModel.resumeDownload(id) },
                                        onDeleteDownload = { id -> browserViewModel.deleteDownload(id) },
                                        onClearFinishedDownloads = { browserViewModel.clearFinishedDownloads() },
                                        onDismiss = { browserViewModel.goHome() }
                                    )
                                } else if (activeTab.url.startsWith("globe://") || activeTab.url.isBlank()) {
                                    // Render New Tab Page (Speed Dial)
                                    NewTabPage(
                                        settings = settings,
                                        bookmarks = bookmarks,
                                        history = history,
                                        totalBlocked = totalBlocked,
                                        onNavigate = { url -> browserViewModel.navigate(url) },
                                        onSetTheme = { theme -> browserViewModel.setTheme(theme) },
                                        onSelectSearchEngine = { engine -> browserViewModel.updateSettings(settings.copy(searchEngine = engine)) },
                                        onOpenSearchOverlay = { showSearchOverlay = true },
                                        onOpenMaps = { showGoogleMaps = true },
                                        onOpenLens = { showGoogleLens = true },
                                        onPanicWipe = {
                                            browserViewModel.panicWipe()
                                            Toast.makeText(context, "Panic Wipe complete: All data purged", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    // Render Active Web Page inside WebViewContainer
                                    WebViewContainer(
                                        tab = activeTab,
                                        browserSettings = settings,
                                        activeExtensions = extensions.filter { it.isEnabled },
                                        matchingCredentials = matchingCredentials,
                                        onUrlChange = { newUrl -> browserViewModel.updateTabUrl(activeTab.id, newUrl) },
                                        onTitleChange = { newTitle -> browserViewModel.updateTabTitle(activeTab.id, newTitle) },
                                        onLoadingChange = { loading, prog -> browserViewModel.updateTabLoading(activeTab.id, loading, prog) },
                                        onNavigationStateChange = { canBack, canFwd -> browserViewModel.updateTabNavigation(activeTab.id, canBack, canFwd) },
                                        onTrackerBlocked = { domain, cat -> browserViewModel.logBlockedTracker(domain, cat) },
                                        onWebViewCreated = { wv -> webViewRef = wv },
                                        onOpenPasswordManager = { showPasswordManagerSheet = true },
                                        onDownloadRequested = { url, contentDisposition, contentLength, mimeType ->
                                            browserViewModel.startDownload(url, contentDisposition, contentLength, mimeType)
                                            showDownloadsSheet = true
                                            Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }

                                // Floating Exit Fullscreen pill when in fullscreen mode
                                if (settings.fullscreenMode) {
                                    FloatingActionButton(
                                        onClick = { browserViewModel.toggleFullscreen() },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp)
                                            .size(46.dp),
                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ) {
                                        Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen", modifier = Modifier.size(22.dp))
                                    }
                                }
                            }

                            // Fullscreen Search Overlay (User can type comfortably and manage history)
                            if (showSearchOverlay) {
                                SearchOverlay(
                                    initialQuery = if (activeTab.url.startsWith("globe://")) "" else activeTab.url,
                                    settings = settings,
                                    history = history,
                                    onNavigate = { query ->
                                        browserViewModel.navigate(query)
                                        showSearchOverlay = false
                                    },
                                    onSelectSearchEngine = { engine ->
                                        browserViewModel.updateSettings(settings.copy(searchEngine = engine))
                                    },
                                    onDeleteHistoryItem = { id ->
                                        browserViewModel.deleteHistoryItem(id)
                                    },
                                    onClearAllHistory = {
                                        browserViewModel.clearHistory()
                                    },
                                    onOpenLens = {
                                        showSearchOverlay = false
                                        showGoogleLens = true
                                    },
                                    onDismiss = { showSearchOverlay = false }
                                )
                            }

                            // Sheets & Dialog Overlays
                            if (showTabsOverview) {
                                TabsOverviewSheet(
                                    tabs = tabs,
                                    activeTabId = activeTabId,
                                    settings = settings,
                                    onSelectTab = { id ->
                                        browserViewModel.selectTab(id)
                                        showTabsOverview = false
                                    },
                                    onCloseTab = { id -> browserViewModel.closeTab(id) },
                                    onDuplicateTab = { id -> browserViewModel.duplicateTab(id) },
                                    onCloseOtherTabs = { id -> browserViewModel.closeOtherTabs(id) },
                                    onNewTab = { incognito ->
                                        browserViewModel.createTab(incognito)
                                        showTabsOverview = false
                                    },
                                    onCloseAllTabs = {
                                        browserViewModel.closeAllTabs()
                                        showTabsOverview = false
                                    },
                                    onDismiss = { showTabsOverview = false }
                                )
                            }

                            if (showGoogleLens) {
                                GoogleLensSheet(
                                    settings = settings,
                                    lensImage = lensImage,
                                    isAnalyzing = isLensAnalyzing,
                                    analysisResult = lensResult,
                                    activeMode = lensMode,
                                    onChangeMode = { mode -> browserViewModel.changeLensMode(mode) },
                                    onTakePhoto = { cameraLauncher.launch(null) },
                                    onPickImage = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onCaptureWebPage = {
                                        webViewRef?.let { wv ->
                                            val bmp = ImageUtils.captureWebView(wv)
                                            if (bmp != null) {
                                                browserViewModel.openLensWithBitmap(bmp, LensMode.SEARCH)
                                            } else {
                                                Toast.makeText(context, "Could not capture page", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onSelectSampleImage = { sampleType ->
                                        val bmp = ImageUtils.createSampleBitmap(sampleType)
                                        val mode = when (sampleType) {
                                            "sample_text" -> LensMode.TEXT_OCR
                                            "sample_device" -> LensMode.SHOPPING
                                            "sample_landmark" -> LensMode.PLACES
                                            else -> LensMode.SEARCH
                                        }
                                        browserViewModel.openLensWithBitmap(bmp, mode)
                                    },
                                    onPerformGoogleSearch = { query ->
                                        browserViewModel.navigate(query)
                                        showGoogleLens = false
                                    },
                                    onDismiss = { showGoogleLens = false }
                                )
                            }

                            if (showFileLab) {
                                FileLabSheet(
                                    browserSettings = settings,
                                    onDismiss = { showFileLab = false }
                                )
                            }

                            if (showPrivacyDashboard) {
                                PrivacyDashboardSheet(
                                    settings = settings,
                                    blockedList = blockedTrackers,
                                    totalBlocked = totalBlocked,
                                    onUpdateSettings = { s -> browserViewModel.updateSettings(s) },
                                    onClearHistory = { browserViewModel.clearHistory() },
                                    onClearBlockedLogs = { browserViewModel.clearBlockedLogs() },
                                    onPanicWipe = {
                                        browserViewModel.panicWipe()
                                        Toast.makeText(context, "Panic Wipe: Cleared tabs and browsing data", Toast.LENGTH_SHORT).show()
                                    },
                                    onDismiss = { showPrivacyDashboard = false }
                                )
                            }

                            if (showExtensions) {
                                ExtensionsSheet(
                                    settings = settings,
                                    extensions = extensions,
                                    onToggleExtension = { id, en -> browserViewModel.toggleExtension(id, en) },
                                    onInstallSample = { ext -> browserViewModel.installExtension(ext) },
                                    onDismiss = { showExtensions = false }
                                )
                            }

                            if (showAccounts) {
                                AccountsSheet(
                                    settings = settings,
                                    accounts = accounts,
                                    onSwitchAccount = { id ->
                                        browserViewModel.switchAccount(id)
                                        Toast.makeText(context, "Switched active profile", Toast.LENGTH_SHORT).show()
                                    },
                                    onAddAccount = { account ->
                                        browserViewModel.addAccount(account)
                                        Toast.makeText(context, "Account added to vault", Toast.LENGTH_SHORT).show()
                                    },
                                    onDeleteAccount = { id ->
                                        browserViewModel.deleteAccount(id)
                                        Toast.makeText(context, "Account removed", Toast.LENGTH_SHORT).show()
                                    },
                                    onDismiss = { showAccounts = false }
                                )
                            }

                            if (showBookmarksHistory) {
                                BookmarksHistorySheet(
                                    initialTab = bookmarksHistoryInitialTab,
                                    settings = settings,
                                    bookmarks = bookmarks,
                                    readingList = readingList,
                                    history = history,
                                    onOpenUrl = { url -> browserViewModel.navigate(url) },
                                    onDeleteBookmark = { id -> browserViewModel.deleteBookmark(id) },
                                    onToggleReadingListRead = { id -> browserViewModel.toggleReadingListRead(id) },
                                    onDeleteReadingListItem = { id -> browserViewModel.deleteReadingListItem(id) },
                                    onClearHistory = { browserViewModel.clearHistory() },
                                    onDismiss = { showBookmarksHistory = false }
                                )
                            }


                            if (showSettings) {
                                SettingsSheet(
                                    settings = settings,
                                    onUpdateSettings = { s -> browserViewModel.updateSettings(s) },
                                    savedPasswordsCount = credentials.size,
                                    onOpenPasswordVault = { showPasswordManagerSheet = true },
                                    onClearAllPasswords = { browserViewModel.clearAllCredentials() },
                                    onToggleSearchEngine = { engineName -> browserViewModel.toggleSearchEngine(engineName) },
                                    onClearCacheAndCookies = { onDone ->
                                        browserViewModel.clearCacheAndCookies(onDone)
                                    },
                                    onClearAllData = {
                                        browserViewModel.panicWipe()
                                        Toast.makeText(context, "Cleared all browsing data & cache", Toast.LENGTH_SHORT).show()
                                    },
                                    onDismiss = { showSettings = false }
                                )
                            }

                            if (showGoogleMaps) {
                                GoogleMapsSheet(
                                    onNavigateWeb = { url -> browserViewModel.navigate(url) },
                                    onDismiss = { showGoogleMaps = false }
                                )
                            }

                            if (showBrowserMenu) {
                                BrowserMenuSheet(
                                    tab = activeTab,
                                    settings = settings,
                                    onNewTab = { incognito -> browserViewModel.createTab(incognito) },
                                    onForward = {
                                        webViewRef?.goForward()
                                    },
                                    onReload = {
                                        if (activeTab.isLoading) webViewRef?.stopLoading()
                                        else webViewRef?.reload()
                                    },
                                    onDuplicateTab = {
                                        browserViewModel.duplicateTab(activeTab.id)
                                        Toast.makeText(context, "Tab duplicated", Toast.LENGTH_SHORT).show()
                                    },
                                    onOpenLens = { showGoogleLens = true },
                                    onToggleReaderMode = {
                                        if (settings.readerModeEnabled) {
                                            browserViewModel.closeReaderMode()
                                        } else {
                                            browserViewModel.loadReaderForCurrentTab(activeTab.title, activeTab.url)
                                        }
                                    },
                                    onFindInPage = {
                                        showSearchOverlay = true
                                    },
                                    onAddBookmark = {
                                        browserViewModel.addCurrentPageBookmark()
                                        Toast.makeText(context, "Page added to bookmarks", Toast.LENGTH_SHORT).show()
                                    },
                                    onAddToReadingList = {
                                        browserViewModel.addCurrentPageToReadingList()
                                        Toast.makeText(context, "Saved to Reading List for offline", Toast.LENGTH_SHORT).show()
                                    },
                                    onOpenBookmarks = {
                                        bookmarksHistoryInitialTab = 0
                                        showBookmarksHistory = true
                                    },
                                    onOpenReadingList = {
                                        bookmarksHistoryInitialTab = 1
                                        showBookmarksHistory = true
                                    },
                                    onOpenHistory = {
                                        bookmarksHistoryInitialTab = 2
                                        showBookmarksHistory = true
                                    },
                                    onOpenFileLab = { showFileLab = true },
                                    onOpenDownloads = { showDownloadsSheet = true },
                                    onOpenExtensions = { showExtensions = true },
                                    onOpenStoreTab = { browserViewModel.openStoreTab() },
                                    onOpenAccounts = { showAccounts = true },
                                    onOpenPasswordVault = { showPasswordManagerSheet = true },
                                    onOpenPrivacy = { showPrivacyDashboard = true },
                                    onOpenSettings = { showSettings = true },
                                    onOpenMaps = { showGoogleMaps = true },
                                    onToggleDesktopMode = {
                                        val current = settings.desktopMode
                                        browserViewModel.updateSettings(settings.copy(desktopMode = !current))
                                        webViewRef?.reload()
                                        Toast.makeText(context, if (!current) "Requesting desktop site" else "Requesting mobile site", Toast.LENGTH_SHORT).show()
                                    },
                                    onToggleFullscreen = { browserViewModel.toggleFullscreen() },
                                    onShare = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, activeTab.url)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Webpage"))
                                    },
                                    onDismiss = { showBrowserMenu = false }
                                )
                            }

                            if (showDownloadsSheet) {
                                DownloadsManagerSheet(
                                    downloads = downloads,
                                    onPauseDownload = { id -> browserViewModel.pauseDownload(id) },
                                    onResumeDownload = { id -> browserViewModel.resumeDownload(id) },
                                    onDeleteDownload = { id -> browserViewModel.deleteDownload(id) },
                                    onClearFinishedDownloads = { browserViewModel.clearFinishedDownloads() },
                                    onDismiss = { showDownloadsSheet = false }
                                )
                            }

                            if (showSplashScreen) {
                                GoogleSplashScreen(
                                    theme = settings.theme,
                                    onDismiss = { showSplashScreen = false }
                                )
                            }

                            if (showPasswordManagerSheet) {
                                PasswordManagerSheet(
                                    settings = settings,
                                    credentials = credentials,
                                    currentSiteUrl = activeTab.url,
                                    onSaveCredential = { dom, tit, user, pass ->
                                        browserViewModel.saveCredential(dom, tit, user, pass)
                                    },
                                    onUpdateCredential = { id, dom, tit, user, pass ->
                                        browserViewModel.updateCredential(id, dom, tit, user, pass)
                                    },
                                    onDeleteCredential = { id ->
                                        browserViewModel.deleteCredential(id)
                                    },
                                    onClearAllCredentials = {
                                        browserViewModel.clearAllCredentials()
                                    },
                                    onToggleAutofill = { enabled ->
                                        browserViewModel.updateSettings(settings.copy(autofillEnabled = enabled))
                                    },
                                    onDismiss = { showPasswordManagerSheet = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
