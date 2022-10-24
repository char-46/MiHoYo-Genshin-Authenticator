package char46.auth.activities.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import char46.auth.activities.MainActivity
import char46.auth.data.MiAccount
import char46.auth.data.TapAccount
import char46.auth.utils.MiHoYoUrlRegex
import char46.auth.utils.TapUrlRegex
import char46.auth.utils.toast
import char46.auth.utils.ui.PermissionRequiredDialog
import char46.auth.utils.ui.QRCodeScanner
import char46.auth.utils.ui.TextButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

var captureManager by mutableStateOf<CaptureManager?>(null)
var barcodeView by mutableStateOf<DecoratedBarcodeView?>(null)

private fun stopCamera() {
    captureManager?.onPause()
    captureManager = null
    barcodeView = null
}

private var isDialogShowing by mutableStateOf(false)

private typealias ScanCallback = (BarcodeResult) -> Unit

private var currentCallback: ScanCallback? = null
private var currentCheckRegex: Regex? = null

fun showQRCodeScannerDialog() {
    currentCheckRegex = when (currentAccount) {
        is MiAccount -> MiHoYoUrlRegex
        is TapAccount -> TapUrlRegex
        else -> throw IllegalArgumentException("Unknown account type.")
    }
    isDialogShowing = true
}

fun registerScanCallback(func: ScanCallback) {
    currentCallback = func
}

@Composable
@ExperimentalPermissionsApi
fun MainActivity.QRCodeScannerDialog() {
    if (isDialogShowing) QCD()
}

@Composable
@ExperimentalPermissionsApi
private fun MainActivity.QCD(
    onDismissRequest: () -> Unit = { isDialogShowing = false }
) = PermissionRequiredDialog(permission = Manifest.permission.CAMERA,
                             permissionNotGrantedContent = {
                                 AlertDialog(onDismissRequest = onDismissRequest, title = {
                                     Text("需要权限")
                                 }, text = {
                                     Text(buildAnnotatedString {
                                         append("扫描二维码需要您授予")
                                         withStyle(SpanStyle(Color.Red)) {
                                             append(" 相机 ")
                                         }
                                         append("权限")
                                     })
                                 }, confirmButton = {
                                     TextButton("确定") {
                                         it.launchPermissionRequest()
                                     }
                                 })
                             },
                             permissionNotAvailableContent = {
                                 AlertDialog(onDismissRequest = onDismissRequest, title = {
                                     Text("您拒绝了权限申请")
                                 }, text = {
                                     Text(buildAnnotatedString {
                                         append("应用程序无法在不使用")
                                         withStyle(SpanStyle(Color.Red)) {
                                             append(" 相机 ")
                                         }
                                         append("权限的情况下正常提供服务")
                                     })
                                 }, confirmButton = {
                                     TextButton("打开应用设置") {
                                         startActivity(
                                             Intent(
                                                 Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                 Uri.fromParts("package", packageName, null)
                                             )
                                         )
                                     }
                                 })
                             }) {
    Dialog(onDismissRequest = {
        onDismissRequest()
        stopCamera()
    }) {
        QRCodeScanner(status = currentAccount.uid,
                      modifier = Modifier
                          .size(275.dp)
                          .clip(RoundedCornerShape(15.dp)),
                      callback = {
                          if (text.matches(currentCheckRegex!!)) {
                              stopCamera()
                              onDismissRequest()
                              currentCallback!!.invoke(this)
                          } else {
                              toast("无效的二维码")
                          }
                      })
    }
}
