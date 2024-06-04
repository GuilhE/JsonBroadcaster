package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingsPanel(
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
