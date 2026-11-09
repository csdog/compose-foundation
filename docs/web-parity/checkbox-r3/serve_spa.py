#!/usr/bin/env python3

import os
from functools import partial
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlsplit


class SpaHandler(SimpleHTTPRequestHandler):
    def do_GET(self):
        route = urlsplit(self.path).path
        if not os.path.splitext(route)[1]:
            self.path = "/index.html"
        super().do_GET()


def main():
    distribution = (
        "/Users/cscs/IdeaProjects/angryecho/compose-foundation/"
        "app/webApp/build/dist/wasmJs/productionExecutable"
    )
    handler = partial(SpaHandler, directory=distribution)
    ThreadingHTTPServer(("127.0.0.1", 8765), handler).serve_forever()


if __name__ == "__main__":
    main()
