package cn.blogss.nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme

class NfcReadActivity: NfcBaseActivity() {
    private var tagInfo by mutableStateOf("")

    companion object {
        private const val TAG = "NfcReadActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            AndroidnfcdemoTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    nfcReadView()
                }
            }
        }
        initData()
    }

    private fun initData() {
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
            val tagInfo = StringBuilder()
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val id = bytesToHexString(tag!!.id)
            tagInfo.append("tagId: $id\n")
            tagInfo.append("tagTechList:\n")

            // 获取tag 中的数据信息
            val tagTechList = tag.techList
            if (tagTechList != null && tagTechList.isNotEmpty()) {
                for (i in tagTechList) {
                    tagInfo.append("$i\n")
                }
            }
            if(Ndef.get(tag) != null) {// 读取  ndef 数据

            }
            this.tagInfo = tagInfo.toString()
            Toast.makeText(this, "Tag 信息读取完成!", Toast.LENGTH_LONG).show()
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

    @Composable
    fun nfcReadView(){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "请将 nfc 贴纸靠近手机背面",
                Modifier.padding(bottom = 10.dp),
            )
            Text(
                text = "Tag 信息:",
                Modifier.padding(bottom = 10.dp),
            )
            Text(
                text = tagInfo,
            )
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun nfcReadPreView(){
        nfcReadView()
    }
}


