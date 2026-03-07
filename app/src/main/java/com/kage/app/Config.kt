package com.kage.app

object Config {
    /**
     * URL to fetch the streaming catalog JSON from.
     * Change this to your hosted catalog endpoint.
     */
    const val CATALOG_URL = "https://raw.githubusercontent.com/jesu-m0/Kage/refs/heads/main/backend/catalog.json?token=GHSAT0AAAAAADXFVKFLFL65RPG25YV7WMMO2NMEIMQ"

    const val ACESTREAM_ENGINE_HOST = "127.0.0.1"
    const val ACESTREAM_ENGINE_PORT = 6878
}
