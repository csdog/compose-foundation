package com.toutiaoyan.compose.ui.catalog.docs

internal actual fun platformFilePicker(): FilePicker? =
    { onResult ->
        val choose =
            js(
                """(function(onResult) {
    const input = document.createElement('input');
    input.type = 'file';
    input.style.display = 'none';
    input.setAttribute('aria-label', 'Picture');
    let settled = false;
    const finish = function(filename) {
        if (settled) return;
        settled = true;
        input.remove();
        onResult(filename);
    };
    input.addEventListener('change', function() { finish(input.files && input.files.length ? input.files[0].name : null); }, {once: true});
    input.addEventListener('cancel', function() { finish(null); }, {once: true});
    document.body.appendChild(input);
    input.click();
})""",
            )
        choose(onResult)
    }
