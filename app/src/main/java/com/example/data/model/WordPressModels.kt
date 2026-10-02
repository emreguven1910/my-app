package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.regex.Pattern

@JsonClass(generateAdapter = true)
data class WpPostDto(
    @Json(name = "id") val id: Int,
    @Json(name = "date") val date: String = "",
    @Json(name = "slug") val slug: String = "",
    @Json(name = "link") val link: String = "",
    @Json(name = "title") val title: WpRenderedText = WpRenderedText(),
    @Json(name = "content") val content: WpRenderedText = WpRenderedText(),
    @Json(name = "excerpt") val excerpt: WpRenderedText? = null,
    @Json(name = "categories") val categories: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WpRenderedText(
    @Json(name = "rendered") val rendered: String = ""
)

object HtmlUtils {
    private val TAG_REGEX = Pattern.compile("<[^>]*>")

    fun decodeAndCleanHtml(html: String): String {
        if (html.isBlank()) return ""
        // Replace paragraph tags with newlines
        var text = html
            .replace("</p>", "\n\n")
            .replace("<br>", "\n")
            .replace("<br/>", "\n")
            .replace("<br />", "\n")

        // Strip any remaining html tags
        text = TAG_REGEX.matcher(text).replaceAll("")

        // Decode common HTML entities
        return text
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&#8217;", "'")
            .replace("&#8216;", "'")
            .replace("&#8220;", "\"")
            .replace("&#8221;", "\"")
            .replace("&#8211;", "–")
            .replace("&#8212;", "—")
            .replace("&hellip;", "…")
            .replace("&nbsp;", " ")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    fun cleanTitle(raw: String): String {
        return decodeAndCleanHtml(raw)
    }

    fun calculateReadTime(text: String): Int {
        val wordCount = text.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        return (wordCount / 180).coerceAtLeast(1)
    }

    fun detectCity(title: String, content: String): String {
        val full = "$title $content".lowercase()
        return when {
            full.contains("çanakkale") || full.contains("biga") -> "Çanakkale / Biga"
            full.contains("konya") || full.contains("yht") -> "Konya"
            full.contains("bursa") || full.contains("narlı") -> "Bursa / Narlı"
            full.contains("istanbul") || full.contains("istabul") -> "İstanbul"
            full.contains("ankara") -> "Ankara"
            else -> "Türkiye Gezisi"
        }
    }
}
