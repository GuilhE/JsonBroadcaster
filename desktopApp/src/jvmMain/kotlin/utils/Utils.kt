package utils

import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Processes input data and passes it to [action] only if there's no new data for at least [waitMs]
 */
fun <T> debounce(waitMs: Long = 700L, scope: CoroutineScope = CoroutineScope(Dispatchers.Main), action: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(waitMs)
            action(param)
        }
    }
}

fun String.isValidJson(): Boolean {
    return try {
        Json.parseToJsonElement(this)
        true
    } catch (ex: SerializationException) {
        false
    }
}