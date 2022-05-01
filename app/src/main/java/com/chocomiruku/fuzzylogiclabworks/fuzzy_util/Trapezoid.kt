package com.chocomiruku.fuzzylogiclabworks.fuzzy_util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trapezoid(
    override val a: Float,
    override val b: Float,
    override val c: Float,
    val d: Float,
    override val name: String,
) : Parcelable, LinguisticVariable