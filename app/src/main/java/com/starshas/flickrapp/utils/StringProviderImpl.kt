package com.starshas.flickrapp.utils

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StringProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getHttpErrorMessage(@StringRes resId: Int, httpCode: Int, message: String): String {
        val errorMessageTemplate = context.getString(resId)
        return String.format(errorMessageTemplate, httpCode, message)
    }
}
