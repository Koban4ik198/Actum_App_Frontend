package com.actum.app.screens

fun isPhoneValid(phone: String): Boolean {
    val cleaned = phone.trim()
    return cleaned.matches(Regex("""^\+?[0-9]{10,15}$"""))
}

fun isNotBlankMin(text: String, min: Int = 2): Boolean {
    return text.trim().length >= min
}