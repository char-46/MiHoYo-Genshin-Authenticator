package char46.auth.utils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CircularProgressDialog(
    text: String,
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties(
        dismissOnClickOutside = false, dismissOnBackPress = false
    )
) = Dialog(
    onDismissRequest = onDismissRequest, properties = properties
) {
    Column(
        modifier = Modifier
            .background(
                color = Color.White, shape = RoundedCornerShape(10.dp)
            )
            .size(270.dp, 160.dp)
    ) {
        CircularProgressWithText(
            text = text, modifier = Modifier
                .fillMaxSize()
                .padding(top = 3.dp)
        )
    }
}
