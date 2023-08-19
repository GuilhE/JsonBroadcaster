package commands

import java.io.File
import java.util.concurrent.TimeUnit

object Commands {
    fun broadcast(applicationId: String, json: String): List<String> {
        return listOf(
            "adb", "shell", "am", "broadcast",
            "-p", applicationId.trim(),
            "-a", "JsonBroadcaster.extra",
            "-e", "extra", "'${json.replace("\\s".toRegex(), "")}'"
        )
    }

    fun notification(bundleId: String, deviceId: String, filePath: String): List<String> {
        return listOf("xcrun", "simctl", "push", deviceId, bundleId, filePath)
    }

    fun simulatorDevices(): List<String> {
//        return listOf("xcrun", "simctl", "list", "devices", "|", "grep", "-i", "booted")
        return listOf("xcrun", "simctl", "list", "devices")
    }

    fun physicalDevices(): List<String> {
        return listOf("xcrun", "xctrace", "list", "devices")
    }
}

fun List<String>.runCommand(workingDir: File = File("."), timeoutAmount: Long = 60, timeoutUnit: TimeUnit = TimeUnit.SECONDS): String {
    return try {
        runCatching {
            ProcessBuilder(this)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
                .also { it.waitFor(timeoutAmount, timeoutUnit) }
                .inputStream.bufferedReader().readText()
        }.onFailure { it.printStackTrace() }.getOrThrow()
    } catch (e: Exception) {
        e.message ?: e.cause?.message ?: "Exception occurred, but no message found"
    }
}