package utils

import commands.Commands
import commands.runCommand
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

fun generateAPNS(json: String): String = """
    {
        "aps": {
            "alert": {
                "title": "State Change Broadcast",
                "body": "Updating app ui state..."
            },
            "badge": 1
        },        
        "payload": "${json.replace("\n", "").replace(" ", "").replace("\"", "\\\"")}"
    }
""".trimIndent()

fun getAppleBootedDevices(): List<String> {
    Commands.appleBootedDevices().let { command ->
        command.runCommand().also { output ->
            val devices = mutableListOf<String>()
            val lines = output.lines()
            for (line in lines) {
                if (line.contains("Booted")) {
                    devices.add(line)
                }
            }
            return devices
        }
    }
}

fun extractUDID(device: String): String? {
    val regex = "\\([A-F0-9\\-]+\\)".toRegex()
    val matchResult = regex.find(device)
    return matchResult?.value?.removeSurrounding("(", ")")
}