package com.starshas.flickrapp.utils

import androidx.annotation.StringRes

interface StringProvider {
    fun getString(@StringRes resId: Int): String
    fun getHttpErrorMessage(@StringRes resId: Int, httpCode: Int, message: String): String
}
