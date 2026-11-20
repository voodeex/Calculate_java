package com.example.praktick7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.praktick7.databinding.ActivityStepBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StepActivity : SurveyActivity() {
    private lateinit var binding: ActivityStepBinding
    private var rendering = true
    private var formattingDate = false
    private var survey = Survey()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepBinding.inflate(layoutInflater)
        showScreen(binding.root)
        binding.btnNext.isEnabled = false
        binding.btnBack.setOnClickListener { previousStep() }
        binding.btnNext.setOnClickListener { nextStep() }
        onBackPressedDispatcher.addCallback(this) { previousStep() }
        watchInputs()

        lifecycleScope.launch {
            val stored = model.draft.filterNotNull().first()
            val restored = savedInstanceState?.readDraft() ?: intent.readSurvey() ?: stored
            showStep(restored)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.storageError.collect { binding.storageStatus.isVisible = it }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.readSurvey()?.let(::showStep)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (!rendering) outState.putParcelable(EXTRA_SURVEY, survey)
        super.onSaveInstanceState(outState)
    }

    @Suppress("DEPRECATION")
    private fun Bundle.readDraft(): Survey? = getParcelable(EXTRA_SURVEY)

    private fun watchInputs() = with(binding) {
        inputName.doAfterTextChanged {
            if (!rendering) {
                layoutName.error = null
                update(survey.copy(name = it.toString()))
            }
        }
        // Первый тип наблюдателя вводит разделители в дате и сохраняет позицию курсора.
        inputBirth.doAfterTextChanged { editable ->
            if (rendering || formattingDate || editable == null) return@doAfterTextChanged
            val formatted = formattedDate(editable)
            if (formatted != editable.toString()) {
                val digitsBefore = editable.take(inputBirth.selectionStart.coerceAtLeast(0))
                    .count { it in '0'..'9' }
                formattingDate = true
                editable.replace(0, editable.length, formatted)
                var index = 0
                var digits = 0
                while (index < formatted.length && digits < digitsBefore) {
                    if (formatted[index] != '.') digits++
                    index++
                }
                inputBirth.setSelection(index)
                formattingDate = false
            }
            layoutBirth.error = if (formatted.length == 10 && !validBirth(formatted))
                getString(R.string.invalid_birth) else null
            update(survey.copy(birth = formatted))
        }
        // Контактные поля проверяются сразу после изменения и обновляют общий прогресс.
        inputEmail.doAfterTextChanged {
            if (!rendering) {
                val value = it.toString()
                layoutEmail.error = if (value.isNotBlank() && !validEmail(value))
                    getString(R.string.invalid_email) else null
                update(survey.copy(email = value))
            }
        }
        inputPhone.doAfterTextChanged {
            if (!rendering) {
                val value = it.toString()
                layoutPhone.error = if (value.isNotBlank() && !validPhone(value))
                    getString(R.string.invalid_phone) else null
                update(survey.copy(phone = value))
            }
        }
        topic.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!rendering && position == topic.selectedItemPosition) update(survey.copy(topic = position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        contactAllowed.setOnCheckedChangeListener { _, checked ->
            if (!rendering) update(survey.copy(contactAllowed = checked))
        }
    }

    private fun showStep(draft: Survey) = with(binding) {
        rendering = true
        survey = draft
        val step = draft.step
        content.requestFocus()
        root.scrollTo(0, 0)
        personal.isVisible = step == 0
        contacts.isVisible = step == 1
        preferences.isVisible = step == 2
        btnBack.isVisible = step > 0
        btnNext.isEnabled = true
        btnNext.setText(if (step == 2) R.string.finish else R.string.next)
        stepCount.text = getString(R.string.step_count, step + 1, 3)
        stepTitle.setText(when (step) {
            0 -> R.string.personal_title
            1 -> R.string.contacts_title
            else -> R.string.survey_title
        })

        inputName.setText(draft.name)
        inputBirth.setText(draft.birth)
        inputEmail.setText(draft.email)
        inputPhone.setText(draft.phone)
        topic.setSelection(draft.topic)
        contactAllowed.isChecked = draft.contactAllowed
        layoutName.error = null
        layoutBirth.error = if (draft.birth.length == 10 && !validBirth(draft.birth))
            getString(R.string.invalid_birth) else null
        layoutEmail.error = if (draft.email.isNotBlank() && !validEmail(draft.email))
            getString(R.string.invalid_email) else null
        layoutPhone.error = if (draft.phone.isNotBlank() && !validPhone(draft.phone))
            getString(R.string.invalid_phone) else null
        labelTopic.error = null
        rendering = false
        updateProgress()
    }

    private fun update(draft: Survey) {
        survey = draft
        if (draft.topic != 0) binding.labelTopic.error = null
        model.save(draft)
        updateProgress()
    }

    private fun updateProgress() {
        val filled = survey.filledCount
        binding.progress.progress = filled * 100 / FIELD_COUNT
        binding.progressLabel.text = getString(R.string.steps, filled, FIELD_COUNT)
    }

    private fun previousStep() {
        if (rendering || survey.step == 0) finish()
        else navigateTo(survey.step - 1)
    }

    private fun nextStep() {
        when (survey.step) {
            0 -> {
                val valid = validatePersonal()
                if (valid) navigateTo(1)
            }
            1 -> {
                val valid = validateContacts()
                if (valid) navigateTo(2)
            }
            2 -> {
                if (survey.topic == 0) {
                    binding.labelTopic.error = getString(R.string.missing_topic)
                    binding.topic.requestFocus()
                    return
                }
                hideKeyboard()
                startActivity(Intent(this, SummaryActivity::class.java).putExtra(EXTRA_SURVEY, survey))
            }
        }
    }

    private fun validatePersonal(): Boolean = with(binding) {
        val nameValid = survey.name.isNotBlank()
        val birthValid = validBirth(survey.birth)
        layoutName.error = if (nameValid) null else getString(R.string.missing_name)
        layoutBirth.error = if (birthValid) null else getString(R.string.invalid_birth)
        if (!nameValid) inputName.requestFocus()
        else if (!birthValid) inputBirth.requestFocus()
        nameValid && birthValid
    }

    private fun validateContacts(): Boolean = with(binding) {
        val emailValid = validEmail(survey.email)
        val phoneValid = validPhone(survey.phone)
        layoutEmail.error = if (emailValid) null else getString(R.string.invalid_email)
        layoutPhone.error = if (phoneValid) null else getString(R.string.invalid_phone)
        if (!emailValid) inputEmail.requestFocus()
        else if (!phoneValid) inputPhone.requestFocus()
        emailValid && phoneValid
    }

    private fun navigateTo(step: Int) {
        hideKeyboard()
        val next = survey.copy(step = step)
        model.save(next)
        // Новый шаг получает Parcelable через явный Intent и обрабатывается в onNewIntent.
        startActivity(Intent(this, StepActivity::class.java).putExtra(EXTRA_SURVEY, next))
    }
}
