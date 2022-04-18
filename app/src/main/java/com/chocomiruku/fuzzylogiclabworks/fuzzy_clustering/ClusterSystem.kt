package com.chocomiruku.fuzzylogiclabworks.fuzzy_clustering

import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

class ClusterSystem(
    var objects: List<Float>,
    var clustersCount: Int,
    var fuzzinessDegree: Float,
    var calculationsPrecision: Float,
    var maxIterationsCount: Int
) {
    var degreesOfBelongingMatrix: MutableList<MutableList<Float>> = mutableListOf()
    var clusters: MutableList<Cluster> = MutableList(clustersCount) { Cluster() }

    private fun generateInitialDegreesOfBelongingMatrix() {
        for (item in objects) {
            val randomList = MutableList(clustersCount) { Random.nextFloat() }
            val sum = randomList.sum()
            val normalizedRandomList = randomList.map { it / sum }.toMutableList()
            degreesOfBelongingMatrix.add(normalizedRandomList)
        }
    }

    private fun getDistance(x: Float, center: Float): Float {
        return abs(x - center)
    }

    private fun getEstimatorValue(): Float {
        var functionValue = 0f

        for (i in objects.indices) {
            for (j in clusters.indices) {
                functionValue += degreesOfBelongingMatrix[i][j].pow(fuzzinessDegree) * getDistance(
                    objects[i],
                    clusters[j].center
                )
            }
        }
        return functionValue
    }

    private fun updateClustersCenters() {
        for (i in clusters.indices) {
            var numerator = 0f
            var denominator = 0f

            for (j in objects.indices) {
                numerator += degreesOfBelongingMatrix[j][i].pow(fuzzinessDegree) * objects[i]
                denominator += degreesOfBelongingMatrix[j][i].pow(fuzzinessDegree)
            }

            clusters[i].center = numerator / denominator
        }
    }

    private fun updateDegreesOfBelongingMatrix() {
        var numerator: Float

        for (i in objects.indices) {
            for (j in clusters.indices) {
                numerator = getDistance(objects[i], clusters[j].center)
                if (numerator == 0f) {
                    degreesOfBelongingMatrix[i] = MutableList(clustersCount) { 0f }
                    degreesOfBelongingMatrix[i][j] = 1f
                    break
                }

                var sum = 0f
                var denominator: Float
                for (k in clusters.indices) {
                    denominator = getDistance(objects[i], clusters[k].center)
                    if (denominator != 0f) {
                        sum += (numerator / denominator).pow(2f / (fuzzinessDegree - 1f))
                    }
                }
                if (sum == 0f) {
                    degreesOfBelongingMatrix[i][j] = 0f
                } else {
                    degreesOfBelongingMatrix[i][j] = 1f / sum
                }
            }
        }
    }

    fun runFCMAlgorithm() {
        generateInitialDegreesOfBelongingMatrix()
        updateClustersCenters()

        var previousEstimatorValue = 0f
        var currentEstimatorValue = getEstimatorValue()

        var iteration = 0

        while (abs(currentEstimatorValue - previousEstimatorValue) > calculationsPrecision && iteration < maxIterationsCount) {
            iteration++
            previousEstimatorValue = currentEstimatorValue

            updateDegreesOfBelongingMatrix()
            updateClustersCenters()

            currentEstimatorValue = getEstimatorValue()
        }

        for (i in objects.indices) {
            val maxDegreeOfBelonging = degreesOfBelongingMatrix[i].maxOrNull()
            var clusterIndex = 0
            maxDegreeOfBelonging?.let {
                clusterIndex = degreesOfBelongingMatrix[i].indexOf(it)
            }

            val cluster = clusters.get(clusterIndex)
            if (cluster.objectsCount == 0) {
                cluster.objectsCount += 1
                cluster.minObjectValue = objects[i]
                cluster.maxObjectValue = objects[i]
            } else {
                cluster.objectsCount += 1
                if (objects[i] < cluster.minObjectValue) {
                    cluster.minObjectValue = objects[i]
                }
                if (objects[i] > cluster.maxObjectValue) {
                    cluster.maxObjectValue = objects[i]
                }
            }
        }

        clusters.sortWith(compareBy { it.minObjectValue })

        for (cluster in clusters) {
            println(cluster.objectsCount)
            println(cluster.minObjectValue)
            println(cluster.maxObjectValue)
            println(cluster.center)
        }

        println(degreesOfBelongingMatrix)
    }
}