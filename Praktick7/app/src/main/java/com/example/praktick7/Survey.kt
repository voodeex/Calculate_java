package com.example.praktick7

import android.content.Intent
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Patterns
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

const val EXTRA_SURVEY = "com.example.praktick7.SURVEY"
const val FIELD_COUNT = 5

data class Survey(
    val name: String = "",
    val birth: String = "",
    val email: String = "",
    val phone: String = "",
    val topic: Int = 0,
    val contactAllowed: Boolean = false,
    val step: Int = 0,
) : Parcelable {
    val hasDraft get() = name.isNotBlank() || birth.isNotBlank() || email.isNotBlank() ||
        phone.isNotBlank() || topic != 0 || contactAllowed
    val filledCount get() = listOf(
        name.isNotBlank(), validBirth(birth), validEmail(email), validPhone(phone), topic in 1..3,
    ).count { it }
    val complete get() = filledCount == FIELD_COUNT

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(birth)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeInt(topic)
        parcel.writeInt(if (contactAllowed) 1 else 0)
        parcel.writeInt(step)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Survey> {
            override fun createFromParcel(parcel: Parcel) = Survey(
                parcel.readString().orEmpty(), parcel.readString().orEmpty(),
                parcel.readString().orEmpty(), parcel.readString().orEmpty(),
                parcel.readInt(), parcel.readInt() == 1, parcel.readInt(),
            )

            override fun newArray(size: Int) = arrayOfNulls<Survey>(size)
        }
    }
}

@Suppress("DEPRECATION")
fun Intent.readSurvey(): Survey? = if (Build.VERSION.SDK_INT >= 33) {
    getParcelableExtra(EXTRA_SURVEY, Survey::class.java)
} else {
    getParcelableExtra(EXTRA_SURVEY)
}

fun formattedDate(input: CharSequence): String {
    val digits = input.filter { it in '0'..'9' }.take(8)
    return listOf(digits.take(2), digits.drop(2).take(2), digits.drop(4))
        .filter { it.isNotEmpty() }.joinToString(".")
}

fun validBirth(value: String): Boolean {
    if (!value.matches(Regex("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}"))) return false
    val (day, month, year) = value.split(".").map(String::toInt)
    if (year == 0) return false
    val date = GregorianCalendar().apply {
        gregorianChange = Date(Long.MIN_VALUE)
        clear()
        isLenient = false
        set(year, month - 1, day)
    }
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }
    return try {
        date.timeInMillis <= today.timeInMillis
    } catch (_: IllegalArgumentException) {
        false
    }
}

fun validEmail(value: String) = Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()

fun validPhone(value: String): Boolean {
    val phone = value.trim()
    return phone.matches(Regex("[+]?[0-9 ()-]+")) &&
        phone.count { it in '0'..'9' } in 10..15
}
