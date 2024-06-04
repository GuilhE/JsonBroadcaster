package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import utils.debounce
import utils.isValidJson

@Composable
internal fun EditorPanel(
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