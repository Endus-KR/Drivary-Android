package com.endus.drivary.presentation.logbook

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Environment
import android.util.TypedValue
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.FileProvider
import java.io.File

/**
 * 운행기록부 CSV를 앱 캐시에 저장하고 안드로이드 공유 시트로 보낸다.
 * 파일은 `res/xml/file_paths.xml`의 `logbook/` 캐시 폴더에 두고 FileProvider로 넘긴다.
 *
 * 차량(Android Automotive)에는 CSV를 받을 앱(메일·파일 등)이 없는 경우가 많다.
 * 그때는 앱 전용 문서 폴더에 저장하고 내용을 미리보기로 보여 준다.
 * (실제 사용자 전달은 이후 메일 발송으로 대체할 예정)
 */
fun Context.shareLogbookCsv(fileName: String, content: String) {
    val dir = File(cacheDir, SHARE_DIR).apply { mkdirs() }
    val file = File(dir, fileName).apply { writeText(content, Charsets.UTF_8) }
    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, fileName.removeSuffix(".csv"))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        // 받을 앱이 없으면 ActivityNotFoundException이 난다 (선택 창을 쓰면 "앱 없음" 화면만 뜨므로 직접 띄운다)
        startActivity(send)
    } catch (e: ActivityNotFoundException) {
        val saved = saveToDocuments(fileName, content)
        showCsvPreview(fileName, content, saved)
    }
}

/** 앱 전용 문서 폴더에 저장한다. 개발 확인용으로 adb pull로 꺼낼 수 있다 */
private fun Context.saveToDocuments(fileName: String, content: String): File {
    val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: filesDir
    return File(dir, fileName).apply { writeText(content, Charsets.UTF_8) }
}

/** 만든 CSV 내용을 차량 화면에서 읽을 수 있게 보여 준다 */
private fun Context.showCsvPreview(fileName: String, content: String, saved: File) {
    val text = TextView(this).apply {
        // BOM은 화면에 보일 필요가 없다
        setText(content.removePrefix(BOM).replace(",", " | "))
        typeface = Typeface.MONOSPACE
        setTextSize(TypedValue.COMPLEX_UNIT_SP, PREVIEW_TEXT_SIZE_SP)
        val padding = (24 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)
        setTextIsSelectable(true)
    }
    val scroll = ScrollView(this).apply {
        addView(HorizontalScrollView(this@showCsvPreview).apply { addView(text) })
    }
    AlertDialog.Builder(this)
        .setTitle("$fileName 을(를) 만들었어요")
        .setView(scroll)
        .setPositiveButton("확인", null)
        .show()
    // 저장 위치는 개발 확인용이라 로그로만 남긴다
    timber.log.Timber.i("운행기록부 저장: ${saved.absolutePath}")
}

private const val SHARE_DIR = "logbook"
private const val BOM = "﻿"
private const val PREVIEW_TEXT_SIZE_SP = 20f
