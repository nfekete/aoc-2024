package me.nfekete.adventofcode.y2024.common

import me.nfekete.adventofcode.y2024.common.Grid2D.Coord
import java.util.PriorityQueue

interface Graph<V, W>
        where V : Any,
              W : Comparable<W> {
    val vertices: Set<V>
    fun V.neighborsWithCost(): Map<V, W>
}

interface Path<V, W> {
    val path: List<V>
    val cost: W
}

fun interface SingleSourceShortestPaths<V, W> {
    operator fun get(target: V): Path<V, W>?
}

fun interface SingleSourceAllShortestPaths<V, W> {
    operator fun get(target: V): List<Path<V, W>>
}

private data class NodeWithCost<V, W>(val node: V, val cost: W)

context(Monoid<W>)
fun <V, W> Graph<V, W>.dijkstra(source: V): SingleSourceShortestPaths<V, W>
        where V : Any,
              W : Comparable<W> {

    val queue = PriorityQueue<NodeWithCost<V, W>>(compareBy { it.cost })
        .apply { add(NodeWithCost(source, identity)) }
    val distances = mutableMapOf<V, W>()
        .apply { put(source, identity) }
    val precedingNodes = mutableMapOf<V, V>()

    while (queue.isNotEmpty()) {
        val current = queue.remove().node
        val currentDistance = distances[current]!!
        val neighbors = current.neighborsWithCost()

        neighbors.forEach { neighbor ->
            val oldDistance = distances[neighbor.key]
            val alternateDistance = currentDistance.combine(neighbor.value)
            if (oldDistance == null || alternateDistance < oldDistance) {
                distances[neighbor.key] = alternateDistance
                queue.offer(NodeWithCost(neighbor.key, alternateDistance))
                precedingNodes[neighbor.key] = current
            }
        }
    }

    return SingleSourceShortestPaths { target ->
        distances[target]?.let { cost ->
            object : Path<V, W> {
                override val path: List<V>
                    get() = generateSequence(target) { v -> precedingNodes[v] }.toList().asReversed()
                override val cost: W = cost
                override fun toString(): String {
                    return "Cost=$cost, path=$path"
                }
            }
        }
    }
}

private data class NodeWithCostAndPath<V, W>(val node: V, val cost: W, val path: List<V>)

context(Monoid<W>)
fun <V, W> Graph<V, W>.dijkstraCapturingAllShortestPaths(source: V): SingleSourceAllShortestPaths<V, W>
        where V : Any,
              W : Comparable<W> {

    val queue = PriorityQueue<NodeWithCostAndPath<V, W>>(compareBy { it.cost })
        .apply { add(NodeWithCostAndPath(source, identity, listOf(source))) }
    val distances = mutableMapOf<V, W>()
        .apply { put(source, identity) }
    val paths = mutableMapOf<V, List<List<V>>>()

    while (queue.isNotEmpty()) {
        val (current, currentDistance, path) = queue.remove()
        val neighbors = current.neighborsWithCost()

        neighbors.forEach { neighbor ->
            val oldDistance = distances[neighbor.key]
            val alternateDistance = currentDistance.combine(neighbor.value)
            if (oldDistance == null || alternateDistance <= oldDistance) {
                distances[neighbor.key] = alternateDistance
                queue.offer(NodeWithCostAndPath(neighbor.key, alternateDistance, path + neighbor.key))
                paths[neighbor.key] = paths[neighbor.key].orEmpty() + listOf(path + neighbor.key)
            }
        }
    }

    return SingleSourceAllShortestPaths { target ->
        distances[target]?.let { cost ->
            paths[target]?.map { path ->
                object : Path<V, W> {
                    override val path = path
                    override val cost: W = cost
                    override fun toString(): String {
                        return "Cost=$cost, path=$path"
                    }
                }
            }
        } ?: emptyList()
    }
}


class WeightedDirectedGraph<V, W>(
    override val vertices: Set<V>,
    val neighborWithCostFunction: (V) -> Map<V, W>,
) : Graph<V, W> where V : Any,
                      W : Comparable<W> {
    override fun V.neighborsWithCost(): Map<V, W> = neighborWithCostFunction(this)
}

fun <V, W> Graph<V, W>.reversed(): Graph<V, W> where V : Any, W : Comparable<W> =
    vertices.flatMap { source -> source.neighborsWithCost().map { it to source } }
        .groupBy({ it.first.key }) { it.second to it.first.value }
        .mapValues { it.value.toMap() }
        .let { reverseNeighborsMap ->
            WeightedDirectedGraph(
                vertices,
                neighborWithCostFunction = { reverseNeighborsMap[it] ?: emptyMap() },
            )
        }

class GridGraph2D(val grid: Grid2D<*>) : Graph<Coord, Long> {
    override val vertices: Set<Coord> = grid.map.keys
    override fun Coord.neighborsWithCost() = axisNeighbors.filter { it in vertices }.associateWith { 1L }
}
