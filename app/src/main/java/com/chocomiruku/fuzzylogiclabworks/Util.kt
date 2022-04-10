package com.chocomiruku.fuzzylogiclabworks

import kotlin.math.roundToInt


fun getDegreeOfBelongingTrapezoid(fuzzySetPoints: List<Float>, valueX: Float): Float {
    val pointA = fuzzySetPoints[0]
    val pointB = fuzzySetPoints[1]
    val pointC = fuzzySetPoints[2]
    val pointD = fuzzySetPoints[3]

    return when (valueX) {
        in pointA..pointB -> {
            ((1F - (pointB - valueX) / (pointB - pointA)) * 1000F).roundToInt() / 1000F
        }
        in pointB..pointC -> {
            1F
        }
        in pointC..pointD -> {
            return ((1F - (valueX - pointC) / (pointD - pointC)) * 1000F).roundToInt() / 1000F
        }
        else -> {
            0F
        }
    }
}

fun getDegreeOfBelongingTriangle(fuzzySetPoints: List<Float>, valueX: Float): Float {
    val pointA = fuzzySetPoints[0]
    val pointB = fuzzySetPoints[1]
    val pointC = fuzzySetPoints[2]

    return when (valueX) {
        in pointA..pointB -> {
            ((1F - (pointB - valueX) / (pointB - pointA)) * 1000F).roundToInt() / 1000F
        }
        in pointB..pointC -> {
            ((1F - (valueX - pointB) / (pointC - pointB)) * 1000F).roundToInt() / 1000F
        }
        else -> {
            0F
        }
    }
}