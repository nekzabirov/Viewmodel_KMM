package com.nekzabirov.viewmodel.unit

interface Closeable {
    fun close()
}

inline fun <C : Closeable, R> C.use(block: (C) -> R): R {
    var closed = false

    return try {
        block(this)
    } catch (first: Throwable) {
        try {
            closed = true
            close()
        } catch (second: Throwable) {
            second.printStackTrace()
        }

        throw first
    } finally {
        if (!closed) {
            close()
        }
    }
}