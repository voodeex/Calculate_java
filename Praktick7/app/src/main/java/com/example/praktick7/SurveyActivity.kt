package com.example.praktick7

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

abstract class SurveyActivity : AppCompatActivity() {
    protected val model: SurveyViewModel by lazy {
        val repository = (application as SurveyApplication).repository
        ViewModelProvider(this, SurveyViewModel.factory(repository))[SurveyViewModel::class.java]
    }

    protected fun showScreen(root: View) {
        enableEdgeToEdge()
        setContentView(root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safe = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime(),
            )
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
            insets
        }
    }

    protected fun hideKeyboard() {
        val focused = currentFocus ?: return
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(focused.windowToken, 0)
    }
}
