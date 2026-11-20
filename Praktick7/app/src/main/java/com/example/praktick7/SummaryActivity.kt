package com.example.praktick7

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import com.example.praktick7.databinding.ActivitySummaryBinding
import com.google.android.material.snackbar.Snackbar

class SummaryActivity : SurveyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val survey = intent.readSurvey()
        if (survey == null || !survey.complete) {
            finish()
            return
        }

        val binding = ActivitySummaryBinding.inflate(layoutInflater)
        showScreen(binding.root)
        val name = survey.name.trim()
        val email = survey.email.trim()
        val phone = survey.phone.trim()
        val topic = resources.getStringArray(R.array.topics)[survey.topic]
        val contact = getString(if (survey.contactAllowed) R.string.yes else R.string.no)
        binding.valueName.text = name
        binding.valueBirth.text = survey.birth
        binding.valueEmail.text = email
        binding.valuePhone.text = phone
        binding.valueTopic.text = topic
        binding.valueContact.text = contact
        binding.btnEdit.setOnClickListener { finish() }
        binding.btnShare.setOnClickListener {
            val content = getString(R.string.share_body, name, survey.birth, email, phone, topic, contact)
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
            }
            try {
                startActivity(Intent.createChooser(send, getString(R.string.share)))
            } catch (_: ActivityNotFoundException) {
                Snackbar.make(binding.root, R.string.share_error, Snackbar.LENGTH_LONG).show()
            } catch (_: SecurityException) {
                Snackbar.make(binding.root, R.string.share_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
