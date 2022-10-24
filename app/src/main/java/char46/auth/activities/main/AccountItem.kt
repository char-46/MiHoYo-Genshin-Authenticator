package char46.auth.activities.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import char46.auth.BuildConfig
import char46.auth.R
import char46.auth.activities.MainActivity
import char46.auth.data.IAccount
import char46.auth.data.MiAccount
import char46.auth.utils.accountList
import char46.auth.utils.getDrawableAsImageBitmap
import char46.auth.utils.removeFrom
import char46.auth.utils.ui.IconButton
import char46.auth.utils.ui.TextButton
import com.skydoves.landscapist.coil.CoilImage

private val defaultAvatar by lazy {
    getDrawableAsImageBitmap(R.drawable.ic_avatar_default)
}

@Composable
fun AccountItem(
    ia: IAccount,
    onInfoClick: () -> Unit,
    onTestClick: () -> Unit = {},
    onItemClick: () -> Unit,
) = Box(modifier = Modifier
    .fillMaxWidth()
    .clickable {
        currentAccount = ia
        onItemClick()
    }) {
    var showInfoButton = true
    val uid = if (ia !is MiAccount) {
        showInfoButton = false
        ia.uid + " (Tap)"
    } else {
        ia.guid
    }
    Row(
        modifier = Modifier.padding(
            vertical = 12.5.dp, horizontal = 20.dp
        ), verticalAlignment = Alignment.CenterVertically
    ) {
        CoilImage(
            imageModel = ia.avatar,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFE3E3E3), CircleShape),
            placeHolder = defaultAvatar
        )
        val contentColor = Color(0xFF424242)
        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = ia.name, fontSize = 20.sp, color = contentColor
            )
            Text(
                text = uid, color = contentColor
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            if (showInfoButton) {
                IconButton(
                    tint = contentColor, icon = Icons.Outlined.Info
                ) {
                    currentAccount = ia
                    onInfoClick()
                }
            }
            if (!showInfoButton && BuildConfig.DEBUG) {
                IconButton(
                    tint = contentColor, icon = Icons.Outlined.Build
                ) {
                    currentAccount = ia
                    onTestClick()
                }
            }
            IconButton(
                tint = contentColor,
                icon = Icons.Outlined.Delete,
            ) {
                currentAccount = ia
                isDialogShowing = true
            }
        }
    }
}

private var isDialogShowing by mutableStateOf(false)

@Composable
fun MainActivity.DeleteAccountDialog() = run {
    if (isDialogShowing) AlertDialog(onDismissRequest = { isDialogShowing = false },
                                     dismissButton = {
                                         TextButton("确认", Color.Red) {
                                             isDialogShowing = false
                                             currentAccount removeFrom accountList
                                         }
                                     },
                                     confirmButton = {
                                         TextButton("取消") {
                                             isDialogShowing = false
                                         }
                                     },
                                     title = {
                                         Text("要永久删除 ${currentAccount.name} 吗?")
                                     })
}
