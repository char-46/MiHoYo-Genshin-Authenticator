package char46.auth.activities

import android.os.Bundle
import char46.auth.activities.tap.UI
import char46.auth.activities.tap.destroyWebView
import char46.auth.utils.ui.ComposeActivity

class TapAuthActivity : ComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init {
            UI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
    }

}
