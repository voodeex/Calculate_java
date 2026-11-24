package com.example.httplab10

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val root = ScrollView(this)
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (24 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        root.addView(form)
        setContentView(root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safe = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
            insets
        }

        form.addView(TextView(this).apply {
            text = "Альбомы пользователя"
            textSize = 28f
        })
        val user = EditText(this).apply {
            id = R.id.user_number
            hint = "Номер пользователя, например 1"
            inputType = InputType.TYPE_CLASS_NUMBER
            setSingleLine()
        }
        val load = Button(this).apply { text = "Загрузить альбомы" }
        val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            isIndeterminate = true
            visibility = View.GONE
            contentDescription = "Загрузка"
        }
        val status = TextView(this).apply {
            textSize = 16f
            accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        }
        val albums = TextView(this).apply { textSize = 17f; setTextIsSelectable(true) }
        listOf(user, load, progress, status, albums).forEach { form.addView(it) }

        status.text = savedInstanceState?.getCharSequence("status") ?: ""
        albums.text = savedInstanceState?.getCharSequence("albums") ?: ""
        uiState = { status.text to albums.text }
        load.setOnClickListener {
            val id = user.text.toString().trim().toIntOrNull()
            if (id == null || id <= 0) {
                user.error = "Введите положительный целый номер"
                status.text = "Введите положительный целый номер."
                albums.text = ""
                return@setOnClickListener
            }
            getSystemService(InputMethodManager::class.java)
                .hideSoftInputFromWindow(user.windowToken, 0)
            user.error = null
            load.isEnabled = false
            progress.visibility = View.VISIBLE
            status.text = "Загрузка…"
            albums.text = ""
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) { loadAlbums(id) }
                    val label = "HTTP ${response.code} ${response.reason}".trim()
                    status.text = when {
                        response.code == 404 -> "$label\nЗапрошенный ресурс не найден."
                        response.code !in 200..299 -> "$label\nСервер отклонил запрос."
                        response.titles == null -> "$label\nНекорректный JSON в ответе."
                        else -> "$label\nАльбомов: ${response.titles.size}"
                    }
                    response.titles?.let { titles ->
                        albums.text = if (titles.isEmpty()) "У этого пользователя нет альбомов."
                        else titles.mapIndexed { index, title -> "${index + 1}. $title" }
                            .joinToString("\n\n")
                    }
                } catch (_: IOException) {
                    status.text = "Не удалось выполнить запрос. Проверьте подключение и повторите."
                } finally {
                    progress.visibility = View.GONE
                    load.isEnabled = true
                }
            }
        }
    }

    private var uiState: (() -> Pair<CharSequence, CharSequence>)? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        uiState?.invoke()?.let { (status, albums) ->
            if (status.toString() != "Загрузка…") {
                outState.putCharSequence("status", status)
                outState.putCharSequence("albums", albums)
            }
        }
    }

    private fun loadAlbums(userId: Int): AlbumResponse {
        val url = URL("https://jsonplaceholder.typicode.com/albums?userId=$userId")
        val connection = url.openConnection() as HttpsURLConnection
        try {
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.instanceFollowRedirects = false
            val code = connection.responseCode
            val reason = connection.responseMessage.orEmpty()
            if (code !in 200..299) return AlbumResponse(code, reason, null)
            if (code == 204) return AlbumResponse(code, reason, emptyList())

            val raw = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val titles = try {
                val json = JSONArray(raw)
                List(json.length()) { index -> json.getJSONObject(index).getString("title") }
            } catch (_: JSONException) {
                null
            }
            return AlbumResponse(code, reason, titles)
        } finally {
            connection.disconnect()
        }
    }

    private data class AlbumResponse(val code: Int, val reason: String, val titles: List<String>?)
}
