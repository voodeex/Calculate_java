package com.example.praktick7

import android.app.Application
import android.content.Context
import android.util.AtomicFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class SurveyApplication : Application() {
    val repository by lazy { SurveyRepository(this) }
}

class SurveyRepository(context: Context) {
    private val file = AtomicFile(File(context.applicationContext.filesDir, "survey-draft.json"))
    private val writer = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pending = Channel<Survey>(Channel.CONFLATED)
    private val mutableDraft = MutableStateFlow<Survey?>(null)
    private val mutableError = MutableStateFlow(false)
    val draft = mutableDraft.asStateFlow()
    val storageError = mutableError.asStateFlow()

    init {
        writer.launch {
            mutableDraft.value = read()
            for (survey in pending) {
                mutableError.value = !write(survey)
            }
        }
    }

    // На диске сохраняется последний снимок. Записи проходят последовательно.
    fun save(survey: Survey) {
        if (mutableDraft.value == survey) return
        mutableDraft.value = survey
        pending.trySend(survey)
    }

    private fun read(): Survey {
        return try {
            val json = JSONObject(file.openRead().bufferedReader(Charsets.UTF_8).use { it.readText() })
            Survey(
                name = json.optString("name"),
                birth = json.optString("birth"),
                email = json.optString("email"),
                phone = json.optString("phone"),
                topic = json.optInt("topic").coerceIn(0, 3),
                contactAllowed = json.optBoolean("contactAllowed"),
                step = json.optInt("step").coerceIn(0, 2),
            )
        } catch (_: FileNotFoundException) {
            Survey()
        } catch (_: IOException) {
            mutableError.value = true
            Survey()
        } catch (_: JSONException) {
            mutableError.value = true
            Survey()
        }
    }

    private fun write(survey: Survey): Boolean {
        val data = JSONObject()
            .put("name", survey.name)
            .put("birth", survey.birth)
            .put("email", survey.email)
            .put("phone", survey.phone)
            .put("topic", survey.topic)
            .put("contactAllowed", survey.contactAllowed)
            .put("step", survey.step)
            .toString().toByteArray(Charsets.UTF_8)

        val stream = try {
            file.startWrite()
        } catch (_: IOException) {
            return false
        }
        return try {
            stream.write(data)
            file.finishWrite(stream)
            true
        } catch (_: IOException) {
            file.failWrite(stream)
            false
        }
    }
}

class SurveyViewModel(private val repository: SurveyRepository) : ViewModel() {
    val draft = repository.draft
    val storageError = repository.storageError

    fun save(survey: Survey) = repository.save(survey)

    companion object {
        fun factory(repository: SurveyRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SurveyViewModel(repository) as T
            }
    }
}
