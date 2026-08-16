package com.example.goldalert.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FlowWatcher<T>(private val flow: Flow<T>) {
    fun watch(block: (T) -> Unit): () -> Unit {
        val job = CoroutineScope(Dispatchers.Main).launch {
            flow.collect { block(it) }
        }
        return {
            job.cancel()
        }
    }
}

fun <T> Flow<T>.asWatcher(): FlowWatcher<T> = FlowWatcher(this)

fun <T> Flow<T>.watch(block: (T) -> Unit): () -> Unit {
    val job = CoroutineScope(Dispatchers.Main).launch {
        collect { block(it) }
    }
    return { job.cancel() }
}
