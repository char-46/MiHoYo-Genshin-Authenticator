package char46.auth.activities.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import char46.auth.BuildConfig
import char46.auth.utils.checkUpdate
import char46.auth.utils.ui.DropdownMenuItem
import char46.auth.utils.ui.IconButton

class DropdownMenuItemsBuilderScope {

    val items = mutableMapOf<String, () -> Unit>()

    fun add(name: String, func: () -> Unit) {
        items[name] = func
    }
}

fun buildDropdownMenuItems(func: DropdownMenuItemsBuilderScope.() -> Unit) =
    DropdownMenuItemsBuilderScope().apply(func).items

private var pointVisibility by mutableStateOf(false)

@Composable
@ExperimentalMaterialApi
fun TopAppBar(
    a: Int,
    normalDropdownItems: Map<String, () -> Unit>,
    debugDropdownItems: Map<String, () -> Unit> = emptyMap()
) = TopAppBar(
    modifier = Modifier.fillMaxWidth(), elevation = 10.dp, backgroundColor = Color.White
) {
    val contentColor = Color(0xFF424242)
    var mnu by remember { mutableStateOf(false) }
    Text(
        text = "账号数量: $a", modifier = Modifier.padding(start = 15.dp), color = contentColor
    )
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            DropdownMenu(
                expanded = mnu,
                onDismissRequest = { mnu = false },
                offset = DpOffset(x = 0.dp, y = 6.dp)
            ) {
                normalDropdownItems.forEach { (name, onClick) ->
                    DropdownMenuItem(name) {
                        mnu = false
                        onClick()
                    }
                }
                DropdownMenuItem(onClick = {
                    mnu = false
                    pointVisibility = false
                    showAboutDialog()
                }) {
                    BadgedBox(badge = {
                        Badge(
                            modifier = Modifier
                                .offset(5.dp)
                                .alpha(if (pointVisibility) 1F else 0F)
                        )
                    }) {
                        Text("关于")
                    }
                }
                if (BuildConfig.DEBUG) {
                    Divider()
                    debugDropdownItems.forEach { (name, onClick) ->
                        DropdownMenuItem(name) {
                            mnu = false
                            onClick()
                        }
                    }
                }
            }
        }
        BadgedBox(
            badge = {
                Badge(
                    modifier = Modifier
                        .offset((-10).dp, 10.dp)
                        .alpha(if (pointVisibility) 1F else 0F)
                )
            }, modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(
                icon = Icons.Outlined.Add, tint = contentColor
            ) {
                mnu = true
            }
        }
    }
    LaunchedEffect(Unit) {
        checkUpdate { u ->
            if (u.versionCode != BuildConfig.VERSION_CODE) {
                pointVisibility = true
            }
        }
    }
}
