package com.ahmedmolawale.lokalize.utils

fun String.isBlankOrEmpty(): Boolean {
    return isEmpty() || isBlank()
}