import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import commands.Commands
import commands.runCommand
import ui.EditorPanel
import ui.SettingsPanel
import utils.extractUDID
import utils.generateAPNS
import utils.getAppleBootedDevices
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.util.Date

fun main() = application {
    val windowSize = remember { DpSize(800.dp, 800.dp) }
    var showSettings by remember { mutableStateOf(true) }

    var applicationId by remember { mutableStateOf("") }
    var bundleId by remember { mutableStateOf("") }
    var mayShowResult by remember { mutableStateOf(false) }
    var autoBroadcast by remember { mutableStateOf(false) }
    val hasIds by remember(applicationId, bundleId) { mutableStateOf(applicationId.isNotBlank() || bundleId.isNotBlank()) }
    var appleBootedIds by remember { mutableStateOf(listOf("Booted")) }

    var result by remember { mutableStateOf("") }
    val showResult by remember(result) { derivedStateOf { result.isNotBlank() && mayShowResult } }

    Window(
        title = "Json Broadcaster",
        state = rememberWindowState(size = windowSize),
        resizable = false,
        onCloseRequest = ::exitApplication
    ) {
        Box(Modifier.fillMaxSize()) {
            App(
                hasIds = hasIds,
                autoBroadcast = autoBroadcast,
                sendBroadcast = { payload ->
                    if (applicationId.isNotBlank()) {
                        Commands.broadcast(applicationId, payload).let { command ->
                            command.runCommand().also { output ->
                                result = "${Date()}\n\nCommand:\n${command.joinToString(" ")}\n\nOutput:\n$output"
                                println(result)
                            }
                        }
                    }
                    if (bundleId.isNotBlank()) {
                        val apns = generateAPNS(json = payload)
                        val file = File.createTempFile("payload", ".apns")
                        file.writeText(apns)
                        appleBootedIds.forEachIndexed { index, id ->
                            Commands.notification(bundleId, id, file.absolutePath).let { command ->
                                command.runCommand().also { output ->
                                    if (index == appleBootedIds.size - 1) {
                                        file.delete()
                                    }
                                    result = "${Date()}\n\nCommand:\n${command.joinToString(" ")}\n\nOutput:\n$output"
                                    println(result)
                                }
                            }
                        }
                    }
                },
                showSettings = { showSettings = true }
            )

            AnimatedVisibility(
                visible = showSettings,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                SettingsPanel(
                    applicationId = applicationId,
                    bundleId = bundleId,
                    mayShowResult = mayShowResult,
                    autoBroadcast = autoBroadcast,
                    onSave = { appId, bId, showResult, broadcast ->
                        applicationId = appId
                        bundleId = bId
                        mayShowResult = showResult
                        autoBroadcast = broadcast
                        showSettings = false
                        appleBootedIds = getAppleBootedDevices().map { extractUDID(it) ?: "" }.toList()
                    }
                )
            }
        }

        if (showResult) {
            AlertDialog(
                modifier = Modifier.width(windowSize.width / 2),
                title = { Text("Broadcast") },
                text = { Text(result) },
                onDismissRequest = { },
                confirmButton = {
                    Button(onClick = { result = "" }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun FrameWindowScope.App(hasIds: Boolean, autoBroadcast: Boolean, sendBroadcast: (payload: String) -> Unit, showSettings: () -> Unit) {
    var droppedPayload by remember { mutableStateOf("") }
    window.contentPane.dropTarget = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                droppedFiles.first()?.let {
                    droppedPayload = (it as File).readText(Charsets.UTF_8)
                }
            } catch (ex: Exception) {
                println(ex.printStackTrace())
            }
        }
    }
    EditorPanel(droppedPayload, hasIds, autoBroadcast, sendBroadcast, showSettings)
}