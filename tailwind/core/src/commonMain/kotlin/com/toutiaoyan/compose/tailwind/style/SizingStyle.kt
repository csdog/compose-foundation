package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.fillWidth as foundationFillWidth
import com.toutiaoyan.compose.tailwind.core.twSpacing

/** Tailwind's `w-full` via [androidx.compose.foundation.style.fillWidth]. */

fun StyleScope.fillWidth() {
    foundationFillWidth()
}

/** Tailwind's `w-4`/`h-4` utilities via [StyleScope.width]/[StyleScope.height]. */
fun StyleScope.width4() {
    width(twSpacing.scale4)
}

fun StyleScope.height4() {
    height(twSpacing.scale4)
}

// min-w-*/min-h-*/max-w-*/max-h-* (MinSizeScope/MaxSizeScope) live in
// tailwind-style-experimental instead of here: verified via a real test run (not just a
// compile check) that minWidth(value)/maxWidth(value) compile fine against 1.11.1 but don't
// actually enforce the constraint -- a 10.dp width with minWidth(40.dp) still measured
// 10.dp. That's a genuine behavioral gap in 1.11.1's Style API, not a missing-symbol issue,
// so these stay on 1.12.0-beta01 until 1.11.1 (or whatever stable line ships next) fixes it.
