package commands

import java.io.File
import java.util.concurrent.TimeUnit

object Commands {
    fun broadcast(applicationId: String, extra: String): List<String> {
        return listOf(
            "adb", "shell", "am", "broadcast",
            "-p", applicationId.trim(),
            "-a", "JsonBroadcaster.extra",
            "-e", "extra", "'${extra.replace("\\s".toRegex(), "")}'"
        )
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