package com.toutiaoyan.compose.ui.catalog

/** Reload samples and same-document traversal samples have different lifetimes. */
class DocumentHistoryPositions {
    private val positions = mutableMapOf<String, Double>()
    private val traversalPositions = mutableMapOf<String, Double>()

    fun record(
        id: String,
        position: Double,
    ) {
        positions[id] = position
    }

    fun position(id: String): Double? = positions[id]

    fun restoreForTraversal(
        id: String?,
        hasFragment: Boolean,
        crossRoute: Boolean,
        savedPosition: Double?,
    ): Double? =
        when {
            crossRoute -> null
            !hasFragment -> savedPosition
            else -> traversalPositions[id]
        }

    fun depart(
        id: String?,
        sameRouteTraversal: Boolean,
    ) {
        if (id == null) return
        // An explicit new anchor push must still re-seek its departing fragment on return.
        traversalPositions.remove(id)
        if (sameRouteTraversal) positions[id]?.let { traversalPositions[id] = it }
    }
}
