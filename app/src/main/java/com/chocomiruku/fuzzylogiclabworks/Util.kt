package com.chocomiruku.fuzzylogiclabworks

import java.lang.Exception


fun findIntersection(trapezoids: List<List<Float>>) : List<Pair<Float, Float>> {
    val sortedTrapezoids = trapezoids.sortedBy { it.first() }

    val xValueFirst = sortedTrapezoids[0]
    val xValuesSecond = sortedTrapezoids[1]
    val xValuesThird = if (sortedTrapezoids.size > 2) sortedTrapezoids[2] else null
    val d1 = xValueFirst[3]
    val a2 = xValuesSecond[0]
    val d2 = xValuesSecond[3]

    xValuesThird?.let {
        val a3 = xValuesThird[0]

        if (a2 < d1 && a3 < d1) {
            return findIntersectionOfThree(sortedTrapezoids)
        }
        else if (a2 < d1) {
            return findIntersectionOfTwo(xValueFirst, xValuesSecond)
        }
        else if (a3 < d1) {
            return findIntersectionOfTwo(xValueFirst, xValuesThird)
        }
        else if (a3 < d2) {
            return findIntersectionOfTwo(xValuesSecond, xValuesThird)
        }
        else throw Exception("Sets don't intersect")
    } ?: run {
        if (a2 < d1) {
            return findIntersectionOfTwo(xValueFirst, xValuesSecond)
        }
        else throw Exception("Sets don't intersect")
    }
}

fun findIntersectionOfThree(trapezoids: List<List<Float>>) : List<Pair<Float, Float>> {
    val xValueFirst = trapezoids[0]
    val xValuesSecond = trapezoids[1]
    val xValuesThird = trapezoids[2]

    val intersectionPoints = findIntersectionOfTwo(xValueFirst, xValuesSecond)
    val xValuesIntersection = intersectionPoints.map { it.first }

    return findIntersectionOfTwo(xValuesIntersection, xValuesThird)
}

fun findIntersectionOfTwo(xValuesFirst: List<Float>, xValuesSecond: List<Float>) : List<Pair<Float, Float>> {
    val c1 = xValuesFirst[2]
    val b2 = xValuesSecond[1]

    return if (b2 >= c1) {
        findTriangleIntersection(xValuesFirst, xValuesSecond)
    }
    else findTrapezoidIntersection(xValuesFirst, xValuesSecond)
}

fun findTriangleIntersection(xValuesFirst: List<Float>, xValuesSecond: List<Float>) : List<Pair<Float, Float>> {

    if (xValuesFirst.size == 4) {
        val c1 = xValuesFirst[2]
        val d1 = xValuesFirst[3]
        val a2 = xValuesSecond[0]
        val b2 = xValuesSecond[1]

        val intersectionPoint = findIntersectionPoint(c1, d1, a2, b2)
        return listOf(Pair(a2, 0F), intersectionPoint, Pair(d1, 0F))
    }

    // If xValuesFirst - triangle
    else {
        val c1 = xValuesFirst[1]
        val d1 = xValuesFirst[2]
        val a2 = xValuesSecond[0]
        val b2 = xValuesSecond[1]

        val intersectionPoint = findIntersectionPoint(c1, d1, a2, b2)
        return listOf(Pair(a2, 0F), intersectionPoint, Pair(d1, 0F))
    }
}

fun findTrapezoidIntersection(xValuesFirst: List<Float>, xValuesSecond: List<Float>): List<Pair<Float, Float>> {
    val a2 = xValuesSecond[0]
    val b2 = xValuesSecond[1]
    val c2 = xValuesSecond[2]
    val d2 = xValuesSecond[3]
    val c1 = xValuesFirst[2]
    val d1 = xValuesFirst[3]

    val intersectionPoints = mutableListOf(Pair(a2, 0F), Pair(b2, 1F))

    if (c1 < c2) {
        intersectionPoints.add(Pair(c1, 1F))
    }
    else intersectionPoints.add(Pair(c2, 1F))

    if (d1 < d2) {
        intersectionPoints.add(Pair(d1, 0F))
    }
    else intersectionPoints.add(Pair(d2, 0F))

    return intersectionPoints
}

fun findIntersectionPoint(c1: Float, d1: Float, a2: Float, b2: Float) : Pair<Float, Float> {
    val x = (b2 * d1 - a2 * c1) / (d1 - c1 + b2 - a2)
    val y = (x - a2) / (b2 - a2)
    return Pair(x, y)
}

fun getDegreeOfBelonging(fuzzySetPoints: List<Float>) : Float {
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
        return 1F - (pointB - valueX) / (pointB - pointA)
    }
    else if (valueX in pointB..pointC)
    {
        return 1F;
    }
    else
    {
        return 1F - (valueX - pointC) / (pointD - pointC)
    }
}