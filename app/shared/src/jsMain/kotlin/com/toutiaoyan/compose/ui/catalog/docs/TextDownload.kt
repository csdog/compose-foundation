package com.toutiaoyan.compose.ui.catalog.docs

internal actual fun platformTextDownloader(): TextDownloader? =
    { value, filename ->
        downloadText(value, filename)
    }

private fun downloadText(
    value: String,
    filename: String,
) {
    val save =
        js(
            """(function(value, filename) {
        const url = URL.createObjectURL(new Blob([value], {type: 'text/plain;charset=utf-8'}));
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        link.remove();
        setTimeout(function() { URL.revokeObjectURL(url); }, 0);
    })""",
        )
    save(value, filename)
}
