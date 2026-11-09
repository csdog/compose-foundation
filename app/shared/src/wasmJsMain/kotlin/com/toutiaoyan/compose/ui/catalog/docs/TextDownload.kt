@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.toutiaoyan.compose.ui.catalog.docs

internal actual fun platformTextDownloader(): TextDownloader? =
    { value, filename ->
        downloadText(value, filename)
    }

@JsFun(
    """(value, filename) => {
    const url = URL.createObjectURL(new Blob([value], {type: 'text/plain;charset=utf-8'}));
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    setTimeout(() => URL.revokeObjectURL(url), 0);
}""",
)
private external fun downloadText(
    value: String,
    filename: String,
)
