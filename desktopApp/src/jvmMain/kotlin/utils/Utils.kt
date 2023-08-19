package utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

fun generateAPNS(deviceId: String, bundleId: String, json: String): String =
    """
    {
        "aps": {
            "alert": {
                "title": "State Change Broadcast",
                "body": "Updating app ui state..."
            },
            "badge": 1
        },
        "$deviceId": "$bundleId",
        "payload": "${json.replace("\n", "").replace(" ", "").replace("\"", "\\\"")}"
    }
""".trimIndent()
