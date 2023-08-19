import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import commands.Commands
import commands.runCommand
import utils.debounce
import utils.generateAPNS
import utils.getSimulatorDevices
import utils.getPhysicalDevices
import utils.isValidJson
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

    var result by remember { mutableStateOf("") }
    val showResult by remember(result) { derivedStateOf { result.isNotBlank() && mayShowResult } }

    println(getSimulatorDevices())
    println(getPhysicalDevices())

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
                        val apns = generateAPNS(deviceId = "Simulator Target Bundle", bundleId = bundleId, json = payload)

                        @Suppress("BlockingMethodInNonBlockingContext")
                        val file = File.createTempFile("payload", ".apns")
                        file.writeText(apns)
                        Commands.notification(bundleId, "booted", file.absolutePath).let { command ->
                            command.runCommand().also { output ->
                                result = "${Date()}\n\nCommand:\n${command.joinToString(" ")}\n\nOutput:\n$output"
                                println(result)
                                file.delete()
                            }
                        }
                    }
                },
                showSettings = { showSettings = true }
            )

            if (showSettings) {
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
private fun SettingsPanel(
    applicationId: String,
    bundleId: String,
    mayShowResult: Boolean,
    autoBroadcast: Boolean,
    onSave: (applicationId: String, bundleId: String, mayShowResult: Boolean, autoBroadcast: Boolean) -> Unit
) {
    var appId by remember { mutableStateOf(applicationId) }
    var bunId by remember { mutableStateOf(bundleId) }
    var show by remember { mutableStateOf(mayShowResult) }
    var auto by remember { mutableStateOf(autoBroadcast) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.3f))
                .clickable { /* just to intercept */ }
                .padding(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .border(
                        border = BorderStroke(2.dp, Color.Gray),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(50.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = appId,
                    onValueChange = { appId = it },
                    label = { Text("Application Id") },
                    singleLine = true
                )
                Spacer(Modifier.size(20.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bunId,
                    onValueChange = { bunId = it },
                    label = { Text("Bundle Identifier") },
                    singleLine = true
                )
                Spacer(Modifier.size(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Show result:")
                    Switch(
                        checked = show,
                        onCheckedChange = { show = it }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Auto broadcast:")
                    Switch(
                        checked = auto,
                        onCheckedChange = { auto = it }
                    )
                }
                Spacer(Modifier.size(20.dp))
                Button(onClick = { onSave(appId, bunId, show, auto) }) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
@Preview
private fun SettingsPanelPreview() {
    SettingsPanel("", "", mayShowResult = false, autoBroadcast = true) { _, _, _, _ -> }
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

@Composable
private fun EditorPanel(
    droppedPayload: String,
    hasIds: Boolean,
    autoBroadcast: Boolean,
    sendBroadcast: (payload: String) -> Unit,
    showSettings: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var payload by remember(droppedPayload) { mutableStateOf(TextFieldValue(droppedPayload)) }
    val isJsonValid: Boolean by remember(payload) { derivedStateOf { payload.text.isValidJson() } }
    val broadcastDebounce = remember(hasIds, autoBroadcast) {
        debounce<String>(1000L, scope = scope) {
            if (hasIds && autoBroadcast) {
                sendBroadcast(it)
            }
        }
    }

    MaterialTheme {
        Column(Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { showSettings() }) {
                    Text("Settings")
                }
                Button(
                    enabled = hasIds && isJsonValid,
                    onClick = { sendBroadcast(payload.text) }
                ) {
                    Text("Broadcast")
                }
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(4f)
                    .border(
                        border = BorderStroke(2.dp, Color.Gray),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                this@Column.AnimatedVisibility(
                    visible = payload.text.isBlank(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    val stroke = remember { Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)) }
                    Box(
                        Modifier.size(250.dp, 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRoundRect(color = Color.Black, style = stroke)
                        }
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Drag & drop a json file\nor copy & paste it here"
                        )
                    }
                }
                BasicTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp, 10.dp, 10.dp, 0.dp),
                    value = payload,
                    onValueChange = {
                        val last = payload //when [BasicTextField] get focus will call this method
                        payload = it
                        if (autoBroadcast && last != it) {
                            broadcastDebounce(payload.text)
                        }
                    }
                )
                this@Column.AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(
                            color = if (isJsonValid) Color(0XFF6CA653) else Color(0xFFA51A08),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    visible = payload.text.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "JSON",
                        color = Color.White,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun EditorPanelPreview() {
    EditorPanel("yay!", hasIds = true, autoBroadcast = false, sendBroadcast = {}, showSettings = {})
}