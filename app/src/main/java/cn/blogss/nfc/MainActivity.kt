package cn.blogss.nfc

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme

class MainActivity : NfcBaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidnfcdemoTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    mainView()
                }
            }
        }
        if(nfcAdapter == null) {
            Toast.makeText(this, "该设备不支持 nfc", Toast.LENGTH_LONG).show();
            finish()
            return
        }

        if(!nfcAdapter!!.isEnabled) {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            Toast.makeText(this, "设备未开启 nfc", Toast.LENGTH_LONG).show()
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun mainViewPreview() {
        AndroidnfcdemoTheme {
            mainView()
        }
    }

    @Composable
    fun mainView() {
        val pageNames = listOf("Nfc 数据读取", "Nfc 数据写入")
        LazyColumn{
            items(pageNames.size) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = Color.Gray)
                        .clickable {
                            when (it) {
                                0 -> {
                                    startActivity(Intent(this@MainActivity, NfcReadActivity::class.java))
                                }
                                1 -> {
                                    startActivity(Intent(this@MainActivity, NfcWriteActivity::class.java))
                                }
                            }
                        },
                ){
                    Text(pageNames[it], color = Color.White)
                }
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                )
            }
        }
    }
}