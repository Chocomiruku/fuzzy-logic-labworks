package com.chocomiruku.fuzzylogiclabworks

import kotlin.math.roundToInt


fun getDegreeOfBelongingTrapezoid(fuzzySetPoints: List<Float>) : Float {
    val pointA = fuzzySetPoints[0]
    val pointB = fuzzySetPoints[1]
    val pointC = fuzzySetPoints[2]
    val pointD = fuzzySetPoints[3]
    val valueX = fuzzySetPoints[4]

    if (valueX <= pointA || valueX >= pointD)
    {
        return 0F
    }
    else if (valueX in pointA..pointB)
    {
        return ((1F - (pointB - valueX) / (pointB - pointA)) * 1000F).roundToInt() / 1000F
    }
    else if (valueX in pointB..pointC)
    {
        return 1F
    }
    else
    {
        return ((1F - (valueX - pointC) / (pointD - pointC)) * 1000F).roundToInt() / 1000F
    }
}

fun getDegreeOfBelongingTriangle(fuzzySetPoints: List<Float>) : Float {
    val pointA = fuzzySetPoints[0]
    val pointB = fuzzySetPoints[1]
    val pointC = fuzzySetPoints[2]

    return when (val valueX = fuzzySetPoints[3]) {
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