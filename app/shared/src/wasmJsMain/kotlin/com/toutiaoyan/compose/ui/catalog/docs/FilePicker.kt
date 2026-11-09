@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.toutiaoyan.compose.ui.catalog.docs

internal actual fun platformFilePicker(): FilePicker? = { onResult -> chooseFile(onResult) }

@JsFun(
    """(onResult) => {
    const input = document.createElement('input');
    input.type = 'file';
    input.style.display = 'none';
    input.setAttribute('aria-label', 'Picture');
    let settled = false;
    const finish = (filename) => {
        if (settled) return;
        settled = true;
        input.remove();
        onResult(filename);
    };
    input.addEventListener('change', () => finish(input.files?.[0]?.name ?? null), {once: true});
    input.addEventListener('cancel', () => finish(null), {once: true});
    document.body.appendChild(input);
    input.click();
}""",
)
private external fun chooseFile(onResult: (String?) -> Unit)
