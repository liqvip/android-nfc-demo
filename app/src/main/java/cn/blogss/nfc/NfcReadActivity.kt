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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme

class NfcReadActivity: NfcBaseActivity() {
    companion object {
        private const val TAG = "NfcReadActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            AndroidnfcdemoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    nfcReadView()
                }
            }
        }
        initData()
    }

    private fun initData() {
        if(nfcAdapter == null) {
            Toast.makeText(this, "该设备不支持 nfc", Toast.LENGTH_LONG).show();
            finish()
            return
        }

        if(!nfcAdapter!!.isEnabled) {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            Toast.makeText(this, "设备未开启 nfc", Toast.LENGTH_LONG).show()
        }
        readNfcTagData(intent)
    }

    /**
     * 读取 nfc 数据
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        readNfcTagData(intent)

    }

    private fun readNfcTagData(intent: Intent?) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent?.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action
        ) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val id = bytesToHexString(tag!!.id)
            Log.i(TAG, "nfc id: $id")

            // 获取tag 中的数据信息
            val tagTechList = tag.techList
            if (tagTechList != null && tagTechList.isNotEmpty()) {
                val sb = StringBuilder()
                for (i in tagTechList) {
                    sb.append("*").append(i).append("*").append("\n")
                }
                Log.i(TAG, "tagTechList: $sb}")
            }
            if(Ndef.get(tag) != null) {// 读取  ndef 数据

            }
        }
    }

    private fun getTagType(tag: Tag, args: String?): Boolean {
        val result = false
        for (type in tag.techList) {
            if (args != null && type.contains(args)) {
                return true
            }
        }
        return result
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}

@Composable
fun nfcReadView(){
    Column(
        verticalArrangement = Arrangement.Center,){
        Text(
            text = "请将 nfc 贴纸靠近手机背面",
            color = Color.White,
        )
    }
}

@Preview
@Composable
fun nfcReadPreView(){
    nfcReadView()
}
