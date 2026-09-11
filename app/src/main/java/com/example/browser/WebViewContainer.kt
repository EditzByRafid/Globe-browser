package com.example.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BrowserSettings
import com.example.model.ExtensionItem
import com.example.model.TabItem
import java.io.ByteArrayInputStream

private val AD_TRACKER_DOMAINS = setOf(
    "doubleclick.net", "google-analytics.com", "adservice.google.com",
    "taboola.com", "outbrain.com", "adroll.com", "criteo.com",
    "facebook.com/tr", "scorecardresearch.com", "popads.net",
    "trafficjunky.com", "adnxs.com", "advertising.com", "hotjar.com",
    "clarity.ms", "amazon-adsystem.com", "rubiconproject.com", "pubmatic.com"
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    tab: TabItem,
    browserSettings: BrowserSettings,
    activeExtensions: List<ExtensionItem>,
    onUrlChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onLoadingChange: (Boolean, Int) -> Unit,
    onNavigationStateChange: (Boolean, Boolean) -> Unit,
    onTrackerBlocked: (String, String) -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loadError by remember(tab.url) { mutableStateOf<String?>(null) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (tab.url.startsWith("globe://") || tab.url.isBlank()) {
            // Internal globe:// page - rendered by Compose (e.g. New Tab Page)
            return@Box
        }

        if (loadError != null) {
            // Friendly error page when site cannot be embedded or network error occurs
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GppMaybe,
                    contentDescription = "Security or Network Alert",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Unable to load page",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = loadError ?: "This website may be restricting embedded display or your device is offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            loadError = null
                            webViewInstance?.reload()
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry")
                    }
                    OutlinedButton(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tab.url))
                                context.startActivity(browserIntent)
                            } catch (_: Exception) {}
                        }
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open External")
                    }
                }
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        settings.cacheMode = WebSettings.LOAD_DEFAULT

                        if (browserSettings.desktopMode) {
                            settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 GlobeBrowser/1.0"
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val targetUri = request?.url ?: return false
                                val scheme = targetUri.scheme ?: ""
                                if (scheme != "http" && scheme != "https") {
                                    return try {
                                        val intent = Intent(Intent.ACTION_VIEW, targetUri)
                                        ctx.startActivity(intent)
                                        true
                                    } catch (e: Exception) {
                                        true
                                    }
                                }
                                return false
                            }

                            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                                val reqUrl = request?.url?.toString() ?: return null
                                val host = request.url?.host ?: ""

                                // Ad & Tracker Blocker check
                                if (browserSettings.trackerBlockEnabled || browserSettings.adBlockEnabled) {
                                    for (adDomain in AD_TRACKER_DOMAINS) {
                                        if (host.contains(adDomain, ignoreCase = true) || reqUrl.contains(adDomain, ignoreCase = true)) {
                                            val category = if (adDomain.contains("analytic") || adDomain.contains("clarity") || adDomain.contains("hotjar")) "Tracker" else "Ad Server"
                                            onTrackerBlocked(host.ifBlank { adDomain }, category)
                                            // Return blank response to block network request
                                            return WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
                                        }
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                onLoadingChange(true, 10)
                                if (url != null) onUrlChange(url)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                onLoadingChange(false, 100)
                                onNavigationStateChange(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                                if (url != null) onUrlChange(url)

                                // Inject active extensions (e.g. Dark Reader CSS, Custom CSS)
                                for (ext in activeExtensions) {
                                    if (ext.isEnabled && ext.scriptCode.isNotBlank()) {
                                        view?.evaluateJavascript(ext.scriptCode, null)
                                    }
                                }
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    loadError = "Failed to load page: ${error?.description ?: "Network error"}"
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                onLoadingChange(newProgress < 100, newProgress)
                                onNavigationStateChange(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (!title.isNullOrBlank()) {
                                    onTitleChange(title)
                                }
                            }
                        }

                        webViewInstance = this
                        onWebViewCreated(this)
                        loadUrl(tab.url)
                    }
                },
                update = { wv ->
                    webViewInstance = wv
                    if (wv.url != tab.url && !tab.url.startsWith("globe://") && tab.url.isNotBlank()) {
                        wv.loadUrl(tab.url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
