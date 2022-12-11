import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import utils.isValidJson
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
fun main() = application {
    val windowSize = remember { DpSize(800.dp, 800.dp) }
    var result by remember { mutableStateOf("") }
    var mayShowResult by remember { mutableStateOf(true) }
    val showResult by remember(result) { derivedStateOf { result.isNotBlank() && mayShowResult } }

    Window(
        title = "Json Broadcaster",
        state = rememberWindowState(size = windowSize),
        resizable = false,
        onCloseRequest = ::exitApplication
    ) {
        Box(Modifier.fillMaxSize()) {
            App { applicationId, payload, showResult ->
                Commands.broadcast(applicationId, payload).let { command ->
                    command.runCommand().also {
                        mayShowResult = showResult
                        result = "${Date()}\n\nCommand:\n${command.joinToString(" ")}\n\nOutput:\n$it"
                        println(result)
                    }
                }
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
@Preview
private fun FrameWindowScope.App(sendBroadcast: (applicationId: String, payload: String, showResult: Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    var applicationId by remember { mutableStateOf("") }
    var payload by remember { mutableStateOf(TextFieldValue("")) }
    val isJsonValid: Boolean by remember(payload) { derivedStateOf { payload.text.isValidJson() } }
    var showResult by remember { mutableStateOf(false) }
    var autoBroadcast by remember { mutableStateOf(false) }
    val mayBroadcast: Boolean by remember(payload) { derivedStateOf { applicationId.isNotBlank() && payload.text.isValidJson() } }

    val broadcastDebounce = remember {
        debounce<String>(1000L, scope = scope) {
            if (mayBroadcast) {
                sendBroadcast(applicationId, it, showResult)
            }
        }
    }

    window.contentPane.dropTarget = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                droppedFiles.first()?.let {
                    payload = TextFieldValue((it as File).readText(Charsets.UTF_8))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    MaterialTheme {
        Column(Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        modifier = Modifier.weight(2f),
                        value = applicationId,
                        onValueChange = { applicationId = it },
                        label = { Text("applicationId") },
                        singleLine = true
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(2f)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Show result:")
                            Switch(
                                checked = showResult,
                                onCheckedChange = { showResult = it }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Auto broadcast:")
                            Switch(
                                checked = autoBroadcast,
                                onCheckedChange = { autoBroadcast = it }
                            )
                        }
                    }
                    Button(
                        enabled = mayBroadcast,
                        onClick = { sendBroadcast(applicationId, payload.text, showResult) }
                    ) {
                        Text("Broadcast")
                    }
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
                        val last = payload.text //when [BasicTextField] get focus will call this method
                        payload = it
                        if (autoBroadcast && last != it.text) {
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