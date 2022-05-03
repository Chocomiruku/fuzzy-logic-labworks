package com.chocomiruku.fuzzylogiclabworks.fuzzy_util

fun searchPairs(
    searchValue: Float,
    trendValuesList: List<Float>
): MutableMap<Pair<Float, Float>, Int> {
    val pairsMap = mutableMapOf<Pair<Float, Float>, Int>()

    for (i in 0..trendValuesList.size - 2) {
        if (trendValuesList[i] == searchValue) {
            val pair = Pair(trendValuesList[i], trendValuesList[i + 1])
            if (pairsMap.containsKey(pair)) {
                pairsMap[pair] = pairsMap.getValue(pair) + 1
            } else {
                pairsMap[pair] = 1
            }
        }
    }
    return pairsMap
}

fun predictValue(crispObjects: List<Float>, fuzzyValuesList: List<String>, lingVariable: String) : Float {
    var sum = 0f
    var count = 0
    for (i in crispObjects.indices) {
        if (fuzzyValuesList[i] == lingVariable) {
            sum += crispObjects[i]
            count++
        }
    }

    return sum / count
}