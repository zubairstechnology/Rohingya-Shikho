package com.zubtech.rohingyashikho.presentation.admin

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat

object RichTextUtil {
    fun fromHtml(html: String): AnnotatedString {
        if (html.isBlank()) return AnnotatedString("")
        
        // Mode legacy helps parse standard list tags like <ul> and <li>
        val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val builder = SpannableStringBuilder(spanned)
        
        // Convert BulletSpans into visible characters for Compose
        val bulletSpans = builder.getSpans(0, builder.length, BulletSpan::class.java)
        bulletSpans.sortByDescending { builder.getSpanStart(it) }
        bulletSpans.forEach { span ->
            val start = builder.getSpanStart(span)
            // Use standard bullet character for better consistency
            builder.insert(start, "• ")
        }

        return buildAnnotatedString {
            append(builder.toString())
            val spans = builder.getSpans(0, builder.length, Any::class.java)
            spans.forEach { span ->
                val start = builder.getSpanStart(span)
                val end = builder.getSpanEnd(span)
                if (start >= 0 && end <= length) {
                    when (span) {
                        is StyleSpan -> {
                            when (span.style) {
                                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                                Typeface.BOLD_ITALIC -> {
                                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                                    addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                                }
                            }
                        }
                        is ForegroundColorSpan -> addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
                        is BackgroundColorSpan -> addStyle(SpanStyle(background = Color(span.backgroundColor)), start, end)
                        is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                        is StrikethroughSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
                        is AbsoluteSizeSpan -> addStyle(SpanStyle(fontSize = span.size.sp), start, end)
                        is SuperscriptSpan -> addStyle(SpanStyle(baselineShift = BaselineShift.Superscript), start, end)
                        is SubscriptSpan -> addStyle(SpanStyle(baselineShift = BaselineShift.Subscript), start, end)
                        is URLSpan -> {
                            addStyle(SpanStyle(color = Color(0xFF4F46E5), textDecoration = TextDecoration.Underline), start, end)
                            addStringAnnotation("URL", span.url, start, end)
                        }
                    }
                }
            }
        }
    }

    fun toHtml(annotatedString: AnnotatedString): String {
        if (annotatedString.text.isBlank()) return ""
        val text = annotatedString.text
        val spannable = SpannableStringBuilder(text)
        
        // Add BulletSpan for lines starting with bullet character so HtmlCompat generates <ul><li>
        var lineStart = 0
        while (lineStart < text.length) {
            val lineEnd = text.indexOf('\n', lineStart).let { if (it == -1) text.length else it }
            if (text.substring(lineStart, lineEnd).startsWith("• ")) {
                spannable.setSpan(BulletSpan(20), lineStart, lineEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            lineStart = lineEnd + 1
        }

        annotatedString.spanStyles.forEach { range ->
            val style = range.item
            if (style.fontWeight == FontWeight.Bold) {
                spannable.setSpan(StyleSpan(Typeface.BOLD), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.fontStyle == FontStyle.Italic) {
                spannable.setSpan(StyleSpan(Typeface.ITALIC), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.color != Color.Unspecified) {
                spannable.setSpan(ForegroundColorSpan(style.color.toArgb()), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.background != Color.Unspecified && style.background != Color.Transparent) {
                spannable.setSpan(BackgroundColorSpan(style.background.toArgb()), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.textDecoration == TextDecoration.Underline) {
                spannable.setSpan(UnderlineSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.textDecoration == TextDecoration.LineThrough) {
                spannable.setSpan(StrikethroughSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.fontSize != TextUnit.Unspecified && style.fontSize.type == TextUnitType.Sp) {
                spannable.setSpan(AbsoluteSizeSpan(style.fontSize.value.toInt()), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.baselineShift == BaselineShift.Superscript) {
                spannable.setSpan(SuperscriptSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (style.baselineShift == BaselineShift.Subscript) {
                spannable.setSpan(SubscriptSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        
        annotatedString.getStringAnnotations("URL", 0, annotatedString.length).forEach { range ->
            spannable.setSpan(URLSpan(range.item), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return HtmlCompat.toHtml(spannable, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE).trim()
    }
}
