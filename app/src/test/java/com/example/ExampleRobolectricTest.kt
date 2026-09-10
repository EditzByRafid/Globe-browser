package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Globe Browser", appName)
  }

  @Test
  fun `verify search engine query url generation`() {
    val googleQuery = com.example.model.SearchEngine.GOOGLE.getQueryUrl("test search")
    assertEquals("https://www.google.com/search?q=test+search", googleQuery)

    val ddgQuery = com.example.model.SearchEngine.DUCKDUCKGO.getQueryUrl("privacy")
    assertEquals("https://duckduckgo.com/?q=privacy", ddgQuery)
  }

  @Test
  fun `verify settings defaults and low end mode toggle`() {
    val defaultSettings = com.example.model.BrowserSettings()
    assertEquals(true, defaultSettings.liquidGlassEnabled)
    assertEquals(false, defaultSettings.lowEndModeEnabled)
    assertEquals(com.example.model.ToolbarPosition.BOTTOM, defaultSettings.toolbarPosition)
    assertEquals(com.example.model.SearchEngine.GOOGLE, defaultSettings.searchEngine)
    assertEquals(false, defaultSettings.readerModeEnabled)
    assertEquals(100, defaultSettings.pageZoomPercent)

    val lowEndSettings = defaultSettings.copy(lowEndModeEnabled = true, liquidGlassEnabled = false)
    assertEquals(true, lowEndSettings.lowEndModeEnabled)
    assertEquals(false, lowEndSettings.liquidGlassEnabled)
  }

  @Test
  fun `verify Google Lens modes exist and have labels`() {
    val searchMode = com.example.model.LensMode.SEARCH
    val textMode = com.example.model.LensMode.TEXT_OCR
    val shopMode = com.example.model.LensMode.SHOPPING
    val placesMode = com.example.model.LensMode.PLACES

    assertEquals("Search", searchMode.label)
    assertEquals("Text OCR", textMode.label)
    assertEquals("Shopping", shopMode.label)
    assertEquals("Places", placesMode.label)
  }

  @Test
  fun `verify Firebase anonymous state initialization`() {
    val anonState = com.example.auth.FirebaseUserState(
        isLoggedIn = true,
        isAnonymous = true,
        email = null,
        displayName = "Guest Explorer",
        uid = "anon_123"
    )
    assertEquals(true, anonState.isLoggedIn)
    assertEquals(true, anonState.isAnonymous)
    assertEquals("Guest Explorer", anonState.displayName)
  }
}
