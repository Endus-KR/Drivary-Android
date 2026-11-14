package com.endus.drivary.presentation.logbook

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

/**
 * 운행기록부 CSV를 앱 캐시에 저장하고 안드로이드 공유 시트로 보낸다.
 * 파일은 `res/xml/file_paths.xml`의 `logbook/` 캐시 폴더에 두고 FileProvider로 넘긴다.
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
    startActivity(Intent.createChooser(send, "운행기록부 보내기"))
}

private const val SHARE_DIR = "logbook"
