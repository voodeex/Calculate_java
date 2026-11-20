package com.example.praktick7

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.praktick7.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.launch

class WelcomeActivity : SurveyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        showScreen(binding.root)
        binding.btnStart.isEnabled = false
        binding.btnStart.setOnClickListener {
            val draft = model.draft.value ?: return@setOnClickListener
            startActivity(Intent(this, StepActivity::class.java).putExtra(EXTRA_SURVEY, draft))
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    model.draft.collect { draft ->
                        binding.btnStart.isEnabled = draft != null
                        binding.btnStart.setText(if (draft?.hasDraft == true) R.string.resume else R.string.start)
                    }
                }
                launch { model.storageError.collect { binding.storageStatus.isVisible = it } }
            }
        }
    }
}
