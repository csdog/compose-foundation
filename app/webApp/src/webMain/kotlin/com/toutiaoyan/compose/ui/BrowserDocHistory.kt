@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.toutiaoyan.compose.ui

import com.toutiaoyan.compose.ui.catalog.DocumentHistoryPositions
import kotlin.js.js

/** Capture departing entry positions in CSS pixels; navigation selects when to restore them. */
internal class BrowserDocHistory {
    var entryId: String? = claimDocumentHistoryEntry(null)
        private set
    private val positions = DocumentHistoryPositions()

    fun positionForTraversal(
        hasFragment: Boolean,
        crossRoute: Boolean,
    ): Double? = positions.restoreForTraversal(entryId, hasFragment, crossRoute, restorePosition)

    var restorePosition: Double? = readPosition(fromNewDocument = true)
        private set
    private var boundEntryId: String? = null
    private var currentPosition: (() -> Double)? = null

    fun bindPosition(
        id: String?,
        position: () -> Double,
    ) {
        boundEntryId = id
        currentPosition = position
    }

    fun unbindPosition(id: String?) {
        if (boundEntryId == id) {
            capturePosition()
            currentPosition = null
        }
    }

    private fun recordPosition(
        id: String?,
        position: Double,
    ) {
        if (id == null || id != entryId) return
        positions.record(id, position)
        writeDocumentHistoryPosition(id, position)
    }

    fun push(url: String) {
        capturePosition()
        positions.depart(entryId, sameRouteTraversal = false)
        claimDocumentHistoryEntry(url)
    }

    fun sync(
        traversal: Boolean,
        crossRoute: Boolean,
    ): Boolean {
        val nextId = claimDocumentHistoryEntry(null)
        val changed = nextId != entryId
        if (traversal || changed) {
            // On popstate the browser already owns the destination. The id guard in the writer
            // keeps this final outgoing sample in memory without replacing the destination state.
            capturePosition()
            positions.depart(entryId, sameRouteTraversal = traversal && !crossRoute)
            entryId = nextId
            restorePosition = readPosition()
        }
        return changed
    }

    fun capturePosition(persistForReload: Boolean = false) {
        val id = entryId?.takeIf { it == boundEntryId } ?: return
        val position = currentPosition?.invoke() ?: return
        val ownsEntry = ownsCurrentEntry(id)
        if (ownsEntry && persistForReload) writeReloadPosition(id, position)
        recordPosition(id, position)
        if (ownsEntry && !persistForReload) clearReloadPosition(id)
    }

    fun ownsCurrentEntry(id: String?): Boolean = id != null && id == entryId && documentHistoryEntryMatches(id)

    private fun readPosition(fromNewDocument: Boolean = false): Double? =
        entryId?.let { id ->
            val savedReload = if (fromNewDocument) readReloadPosition(id) else Double.NaN
            positions.position(id)
                ?: savedReload.takeIf { it.isFinite() && it >= 0 }
                ?: documentHistoryPosition().takeIf { it.isFinite() && it >= 0 }
        }
}

// Leave non-record state and a foreign namespace untouched. Product pushes retain foreign fields.
@Suppress("UnusedParameter") // Parameters are referenced inside js().
private fun claimDocumentHistoryEntry(url: String?): String? =
    js(
        """
        (() => {
            const state = history.state;
            const record = state === null || (typeof state === 'object' && Object.getPrototypeOf(state) === Object.prototype);
            const hasEntry = record && state !== null && Object.hasOwn(state, '__composeFoundationDocument');
            const owned = hasEntry && state.__composeFoundationDocument;
            const compatible = record && (!hasEntry || (owned && owned.version === 1 &&
                typeof owned.id === 'string' && typeof owned.url === 'string'));
            if (!compatible) {
                if (url !== null) history.pushState(state, '', url);
                return null;
            }
            const address = url === null ? location.pathname + location.search + location.hash : url;
            if (url === null && owned && owned.url === address) return owned.id;
            const id = typeof crypto.randomUUID === 'function'
                ? crypto.randomUUID()
                : crypto.getRandomValues(new Uint32Array(4)).join('-');
            const entry = {version: 1, id, url: address};
            const next = {...state, __composeFoundationDocument: entry};
            if (url === null) history.replaceState(next, '');
            else history.pushState(next, '', url);
            return entry.id;
        })()
        """,
    )

private fun documentHistoryPosition(): Double =
    js(
        """
        (() => {
            const entry = history.state && history.state.__composeFoundationDocument;
            const address = location.pathname + location.search + location.hash;
            return entry && entry.version === 1 && entry.url === address &&
                typeof entry.y === 'number' && Number.isFinite(entry.y) && entry.y >= 0 ? entry.y : NaN;
        })()
        """,
    )

@Suppress("UnusedParameter") // Parameters are referenced inside js().
private fun writeDocumentHistoryPosition(
    id: String,
    position: Double,
): Unit =
    js(
        """
        (() => {
            const state = history.state;
            const entry = state && state.__composeFoundationDocument;
            const address = location.pathname + location.search + location.hash;
            if (entry && entry.version === 1 && entry.id === id && entry.url === address) {
                history.replaceState({...state, __composeFoundationDocument: {...entry, y: position}}, '');
            }
        })()
        """,
    )

@Suppress("UnusedParameter") // Parameters are referenced inside js().
private fun documentHistoryEntryMatches(id: String): Boolean =
    js(
        """
        (() => {
            const entry = history.state && history.state.__composeFoundationDocument;
            const address = location.pathname + location.search + location.hash;
            return !!entry && entry.version === 1 && entry.id === id && entry.url === address;
        })()
        """,
    )

// Browsers may snapshot history.state before pagehide. Session storage survives that reload boundary.
@Suppress("UnusedParameter") // Parameters are referenced inside js().
private fun writeReloadPosition(
    id: String,
    position: Double,
): Unit =
    js(
        """
        (() => {
            try {
                sessionStorage.setItem('compose-foundation-document-scroll', JSON.stringify({id, position}));
            } catch (_) {
                // Storage may be unavailable. The history-state write remains available.
            }
        })()
        """,
    )

@Suppress("UnusedParameter") // Parameters are referenced inside js().
private fun readReloadPosition(id: String): Double =
    js(
        """
        (() => {
            try {
                const key = 'compose-foundation-document-scroll';
                const saved = JSON.parse(sessionStorage.getItem(key));
                if (!saved || saved.id !== id) return NaN;
                const navigation = performance.getEntriesByType('navigation')[0];
                if (!navigation || navigation.type !== 'reload') {
                    sessionStorage.removeItem(key);
                    return NaN;
                }
                return typeof saved.position === 'number' ? saved.position : NaN;
            } catch (_) {
                return NaN;
            }
        })()
        """,
    )

@Suppress("UnusedParameter") // Parameters are referenced inside js().
private fun clearReloadPosition(id: String): Unit =
    js(
        """
        (() => {
            try {
                const key = 'compose-foundation-document-scroll';
                const saved = JSON.parse(sessionStorage.getItem(key));
                if (saved && saved.id === id) sessionStorage.removeItem(key);
            } catch (_) {
                // Storage may be unavailable.
            }
        })()
        """,
    )
