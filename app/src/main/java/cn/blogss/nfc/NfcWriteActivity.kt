package cn.blogss.nfc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme

class NfcWriteActivity: NfcBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidnfcdemoTheme(darkTheme = false) {
                nfcWriteView()
            }
        }
    }

    @Composable
    @Preview
    fun nfcWritePreView(){
        nfcWriteView()
    }

    @Composable
    fun nfcWriteView(){

    }

}