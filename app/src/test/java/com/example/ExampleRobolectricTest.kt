package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.HtmlUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Güven Geziyor", appName)
    }

    @Test
    fun `html utils decodes entities and cleans tags`() {
        val rawHtml = "<p>Ankara&lt;Çanakkale,Biga ve Düğün</p>"
        val cleaned = HtmlUtils.cleanTitle(rawHtml)
        assertEquals("Ankara<Çanakkale,Biga ve Düğün", cleaned)
    }

    @Test
    fun `detect city identifies cities correctly`() {
        val city = HtmlUtils.detectCity("Ankara-Konya Arası YHT Maceramız", "Konya hızlı tren")
        assertEquals("Konya", city)
    }
}
