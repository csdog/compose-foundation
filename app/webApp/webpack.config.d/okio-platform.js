// Okio catches absent Node platform probes; do not supply fake browser filesystem APIs.
config.plugins.push(new (require('webpack').IgnorePlugin)({
    resourceRegExp: /^(os|path)$/,
    contextRegExp: /[\\/]kotlin$/,
}));
