package com.starshas.flickrapp.utils

import org.jsoup.Jsoup

object HtmlFormatter {
    fun removeImageTagsFromHtml(htmlContent: String): String {
        val document = Jsoup.parse(htmlContent)
        val imageTag = "img"
        val paragraphTag = "p"
        document.select(imageTag).remove()
        document.select(paragraphTag).forEach {
            if (it.text().trim().isEmpty()) {
                it.remove()
            }
        }
        return document.body().html()
    }
}
