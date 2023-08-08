package cn.blogss.nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme

class MainActivity : NfcBaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidnfcdemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
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

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidnfcdemoTheme {
        Greeting("Android")
    }
}