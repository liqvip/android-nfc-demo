package cn.blogss.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

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
        initData()
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass),
            /**
             * 注意 flags 的值设置为 FLAG_IMMUTABLE 会导致 intent 中的数据为空
             */
            PendingIntent.FLAG_MUTABLE
        )
        // 启用 nfc 前台调度, 该 activity 处理 nfc 标签的优先级会优先于在 <intent-filter> 中配置的 Activity
        nfcAdapter?.enableForegroundDispatch(this,
            pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        // 关闭 nfc 前台调度
        nfcAdapter?.disableForegroundDispatch(this)
    }


    private fun initData() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
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

    /**
     * 读取 nfc 数据
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action ||
                NfcAdapter.ACTION_TECH_DISCOVERED == intent?.action ||
                NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val id = bytesToHexString(tag!!.id)
            Log.i(TAG, "nfc id: $id")

            // 获取tag 中的数据信息
            val tagTechList = tag.techList
            if(tagTechList != null && tagTechList.isNotEmpty()) {
                val sb = StringBuilder()
                for(i in tagTechList) {
                    sb.append("*").append(i).append("*").append("\n")
                }
                Log.i(TAG, "tagTechList: $sb}")
            }
        }

    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
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