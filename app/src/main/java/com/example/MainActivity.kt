package com.example

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
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
            val history by browserViewModel.history.collectAsState()
            val blockedTrackers by browserViewModel.blockedTrackers.collectAsState()
            val totalBlocked by browserViewModel.totalBlocked.collectAsState()
            val extensions by browserViewModel.extensions.collectAsState()
            val accounts by browserViewModel.accounts.collectAsState()
            val aiMessages by browserViewModel.aiMessages.collectAsState()
            val isAiLoading by browserViewModel.isAiLoading.collectAsState()
            val lensImage by browserViewModel.lensImage.collectAsState()
            val lensMode by browserViewModel.lensMode.collectAsState()
            val isLensAnalyzing by browserViewModel.isLensAnalyzing.collectAsState()
            val lensResult by browserViewModel.lensResult.collectAsState()
            val firebaseUserState by browserViewModel.firebaseUserState.collectAsState()
            val findInPageQuery by browserViewModel.findInPageQuery.collectAsState()

            val activeTab = remember(tabs, activeTabId) {
                browserViewModel.getActiveTab()
            }

            var webViewRef by remember { mutableStateOf<WebView?>(null) }
            val snackbarHostState = remember { SnackbarHostState() }
            val context = LocalContext.current

            // Sheet Visibilities
            var showTabsOverview by remember { mutableStateOf(false) }
            var showGeminiAi by remember { mutableStateOf(false) }
            var showGoogleLens by remember { mutableStateOf(false) }
            var showFirebaseLogin by remember { mutableStateOf(false) }
            var showFileLab by remember { mutableStateOf(false) }
            var showPrivacyDashboard by remember { mutableStateOf(false) }
            var showExtensions by remember { mutableStateOf(false) }
            var showAccounts by remember { mutableStateOf(false) }
            var showBookmarksHistory by remember { mutableStateOf(false) }
            var bookmarksHistoryInitialTab by remember { mutableStateOf(0) }
            var showSettings by remember { mutableStateOf(false) }
            var showBrowserMenu by remember { mutableStateOf(false) }
            var showGoogleMaps by remember { mutableStateOf(false) }
            var showSplashScreen by remember { mutableStateOf(true) }

            // Automatic Google Login on App Launch
            LaunchedEffect(Unit) {
                browserViewModel.signInWithGoogle { _, _ -> }
            }

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

            // Handle hardware / system back navigation
            BackHandler(enabled = true) {
                if (showTabsOverview) {
                    showTabsOverview = false
                } else if (showGoogleLens) {
                    showGoogleLens = false
                } else if (showFirebaseLogin) {
                    showFirebaseLogin = false
                } else if (showGeminiAi) {
                    showGeminiAi = false
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
                } else if (webViewRef?.canGoBack() == true) {
                    webViewRef?.goBack()
                } else if (!activeTab.url.startsWith("globe://") && activeTab.url.isNotBlank()) {
                    browserViewModel.goHome()
                } else {
                    finish()
                }
            }

            GlobeBrowserTheme(theme = settings.theme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (settings.toolbarPosition == ToolbarPosition.TOP) {
                            OmniboxBar(
                                tab = activeTab,
                                settings = settings,
                                tabCount = tabs.size,
                                onNavigate = { query -> browserViewModel.navigate(query) },
                                onBack = {
                                    if (webViewRef?.canGoBack() == true) webViewRef?.goBack()
                                    else browserViewModel.goHome()
                                },
                                onForward = { webViewRef?.goForward() },
                                onReload = {
                                    if (activeTab.isLoading) webViewRef?.stopLoading()
                                    else webViewRef?.reload()
                                },
                                onHome = { browserViewModel.goHome() },
                                onOpenTabs = { showTabsOverview = true },
                                onOpenAi = { showGeminiAi = true },
                                onOpenLens = { showGoogleLens = true },
                                onOpenMenu = { showBrowserMenu = true },
                                modifier = Modifier
                                    .statusBarsPadding()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    },
                    bottomBar = {
                        if (settings.toolbarPosition == ToolbarPosition.BOTTOM) {
                            OmniboxBar(
                                tab = activeTab,
                                settings = settings,
                                tabCount = tabs.size,
                                onNavigate = { query -> browserViewModel.navigate(query) },
                                onBack = {
                                    if (webViewRef?.canGoBack() == true) webViewRef?.goBack()
                                    else browserViewModel.goHome()
                                },
                                onForward = { webViewRef?.goForward() },
                                onReload = {
                                    if (activeTab.isLoading) webViewRef?.stopLoading()
                                    else webViewRef?.reload()
                                },
                                onHome = { browserViewModel.goHome() },
                                onOpenTabs = { showTabsOverview = true },
                                onOpenAi = { showGeminiAi = true },
                                onOpenLens = { showGoogleLens = true },
                                onOpenMenu = { showBrowserMenu = true },
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        if (activeTab.url.startsWith("globe://") || activeTab.url.isBlank()) {
                            // Render New Tab Page (Speed Dial)
                            NewTabPage(
                                settings = settings,
                                bookmarks = bookmarks,
                                history = history,
                                totalBlocked = totalBlocked,
                                onNavigate = { url -> browserViewModel.navigate(url) },
                                onSetTheme = { theme -> browserViewModel.setTheme(theme) },
                                onSelectSearchEngine = { engine -> browserViewModel.updateSettings(settings.copy(searchEngine = engine)) },
                                onOpenAiWithPrompt = { prompt ->
                                    showGeminiAi = true
                                    browserViewModel.sendAiMessage(prompt)
                                },
                                onOpenMaps = { showGoogleMaps = true },
                                onOpenLens = { showGoogleLens = true },
                                onOpenAuth = { showFirebaseLogin = true },
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
                                onUrlChange = { newUrl -> browserViewModel.updateTabUrl(activeTab.id, newUrl) },
                                onTitleChange = { newTitle -> browserViewModel.updateTabTitle(activeTab.id, newTitle) },
                                onLoadingChange = { loading, prog -> browserViewModel.updateTabLoading(activeTab.id, loading, prog) },
                                onNavigationStateChange = { canBack, canFwd -> browserViewModel.updateTabNavigation(activeTab.id, canBack, canFwd) },
                                onTrackerBlocked = { domain, cat -> browserViewModel.logBlockedTracker(domain, cat) },
                                onWebViewCreated = { wv -> webViewRef = wv }
                            )
                        }
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

                    if (showFirebaseLogin) {
                        FirebaseLoginSheet(
                            settings = settings,
                            userState = firebaseUserState,
                            onSignIn = { email, pass, onResult ->
                                browserViewModel.signInWithFirebase(email, pass, onResult)
                            },
                            onSignUp = { email, pass, name, onResult ->
                                browserViewModel.signUpWithFirebase(email, pass, name, onResult)
                            },
                            onSignInAnonymously = { onResult ->
                                browserViewModel.signInAnonymouslyWithFirebase(onResult)
                            },
                            onSignInWithGoogle = { onResult ->
                                browserViewModel.signInWithGoogle(onResult)
                            },
                            onSendPasswordReset = { email, onResult ->
                                browserViewModel.sendPasswordReset(email, onResult)
                            },
                            onSignOut = { browserViewModel.signOutFirebase() },
                            onSyncData = { onDone -> browserViewModel.syncDataToFirebase(onDone) },
                            onDismiss = { showFirebaseLogin = false }
                        )
                    }

                    if (showGeminiAi) {
                        GeminiAiSheet(
                            currentTab = activeTab,
                            messages = aiMessages,
                            isLoading = isAiLoading,
                            settings = settings,
                            onSendMessage = { prompt, model, thinking, search, maps, img ->
                                browserViewModel.sendAiMessage(prompt, model, thinking, search, maps, img)
                            },
                            onSummarizePage = { url, title ->
                                browserViewModel.summarizeWebPage(url, title)
                            },
                            onDismiss = { showGeminiAi = false }
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
                            history = history,
                            onOpenUrl = { url -> browserViewModel.navigate(url) },
                            onDeleteBookmark = { id -> browserViewModel.deleteBookmark(id) },
                            onClearHistory = { browserViewModel.clearHistory() },
                            onDismiss = { showBookmarksHistory = false }
                        )
                    }

                    if (showSettings) {
                        SettingsSheet(
                            settings = settings,
                            onUpdateSettings = { s -> browserViewModel.updateSettings(s) },
                            onToggleSearchEngine = { engineName -> browserViewModel.toggleSearchEngine(engineName) },
                            onClearAllData = {
                                browserViewModel.panicWipe()
                                Toast.makeText(context, "Cleared all browsing data & cache", Toast.LENGTH_SHORT).show()
                            },
                            onOpenFirebaseAccount = { showFirebaseLogin = true },
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
                            onDuplicateTab = {
                                browserViewModel.duplicateTab(activeTab.id)
                                Toast.makeText(context, "Tab duplicated", Toast.LENGTH_SHORT).show()
                            },
                            onOpenLens = { showGoogleLens = true },
                            onOpenFirebase = { showFirebaseLogin = true },
                            onToggleReaderMode = {
                                browserViewModel.toggleReaderMode()
                                Toast.makeText(context, if (settings.readerModeEnabled) "Reader mode turned off" else "Reader mode enabled", Toast.LENGTH_SHORT).show()
                            },
                            onFindInPage = {
                                Toast.makeText(context, "Use search bar to search content", Toast.LENGTH_SHORT).show()
                            },
                            onAddBookmark = {
                                browserViewModel.addCurrentPageBookmark()
                                Toast.makeText(context, "Page added to bookmarks", Toast.LENGTH_SHORT).show()
                            },
                            onOpenBookmarks = {
                                bookmarksHistoryInitialTab = 0
                                showBookmarksHistory = true
                            },
                            onOpenHistory = {
                                bookmarksHistoryInitialTab = 1
                                showBookmarksHistory = true
                            },
                            onOpenFileLab = { showFileLab = true },
                            onOpenExtensions = { showExtensions = true },
                            onOpenAccounts = { showAccounts = true },
                            onOpenPrivacy = { showPrivacyDashboard = true },
                            onOpenSettings = { showSettings = true },
                            onOpenMaps = { showGoogleMaps = true },
                            onToggleDesktopMode = {
                                val current = settings.desktopMode
                                browserViewModel.updateSettings(settings.copy(desktopMode = !current))
                                webViewRef?.reload()
                                Toast.makeText(context, if (!current) "Requesting desktop site" else "Requesting mobile site", Toast.LENGTH_SHORT).show()
                            },
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

                    if (showSplashScreen) {
                        GoogleSplashScreen(
                            theme = settings.theme,
                            onDismiss = { showSplashScreen = false }
                        )
                    }
                }
            }
        }
    }
}
}
