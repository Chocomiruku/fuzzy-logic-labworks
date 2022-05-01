package com.chocomiruku.fuzzylogiclabworks

import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.LinguisticVariable
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Result
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Trapezoid
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Triangle
import kotlin.math.roundToInt


fun getDegreeOfBelongingTrapezoid(trapezoid: Trapezoid, valueX: Float): Float {
    val pointA = trapezoid.a
    val pointB = trapezoid.b
    val pointC = trapezoid.c
    val pointD = trapezoid.d

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

fun getDegreeOfBelongingTriangle(triangle: Triangle, valueX: Float): Float {
    val pointA = triangle.a
    val pointB = triangle.b
    val pointC = triangle.c

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

fun getDegreeOfBelonging(
    linguisticVariables: List<LinguisticVariable>,
    valueX: Float
): Result {
    val lingVariableWithMaxDegree =
        linguisticVariables.maxByOrNull {
            when (it) {
                is Trapezoid -> getDegreeOfBelongingTrapezoid(it, valueX)
                else -> {
                    getDegreeOfBelongingTriangle(it as Triangle, valueX)
                }
            }
        }

    val degreeOfBelonging = when (lingVariableWithMaxDegree) {
        is Trapezoid -> {
            getDegreeOfBelongingTrapezoid(lingVariableWithMaxDegree, valueX)
        }
        else -> {
            getDegreeOfBelongingTriangle(lingVariableWithMaxDegree as Triangle, valueX)
        }
    }

    return Result(valueX, degreeOfBelonging, lingVariableWithMaxDegree.name)
}