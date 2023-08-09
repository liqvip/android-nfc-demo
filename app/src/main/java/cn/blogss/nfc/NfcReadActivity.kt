package cn.blogss.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.blogss.nfc.ui.theme.AndroidnfcdemoTheme
import java.nio.charset.Charset
import java.util.Arrays

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
            if(Ndef.get(tag) != null) { // 读取  ndef 数据
                val ndef = Ndef.get(tag)
                val type = ndef.type
                val maxSize = ndef.maxSize
                tagInfo.append("ndef tag type: $type\n")
                tagInfo.append("max ndef message size: $maxSize bytes\n")

                val rawMsg = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES
                )
                var msgs: Array<NdefMessage?>? = null
                var contentSize = 0
                if (rawMsg != null) {
                    msgs = arrayOfNulls(rawMsg.size)
                    for (i in rawMsg.indices) {
                        msgs[i] = rawMsg[i] as NdefMessage
                        contentSize += msgs[i]!!.toByteArray().size
                    }
                }
                try {
                    if (msgs != null) {
                        val record = msgs[0]!!.records[0]
                        val textRecord = parseTextRecord(record)
                        tagInfo.append("msg: $textRecord\n")
                        tagInfo.append("msg size: $contentSize bytes\n")
                    }
                } catch (_: Exception) {
                }
            }
            this.tagInfo = tagInfo.toString()
            Toast.makeText(this, "Tag 信息读取完成!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     *
     * @param ndefRecord
     * @return
     */
    private fun parseTextRecord(ndefRecord: NdefRecord): String? {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.tnf != NdefRecord.TNF_WELL_KNOWN) {
            return null
        }
        //判断可变的长度的类型
        return if (!Arrays.equals(ndefRecord.type, NdefRecord.RTD_TEXT)) {
            null
        } else try {
            //获得字节数组，然后进行分析
            val payload = ndefRecord.payload
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            val textEncoding = if (payload[0].toInt() and 0x80 == 0) "UTF-8" else "UTF-16"
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            val languageCodeLength = payload[0].toInt() and 0x3f
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            val languageCode = String(payload, 1,
                languageCodeLength, Charset.forName("US-ASCII"))
            //下面开始NDEF文本数据后面的字节，解析出文本
            String(payload, languageCodeLength + 1,
                payload.size - languageCodeLength - 1,
                Charset.forName(textEncoding))
        } catch (e: Exception) {
            throw IllegalArgumentException()
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


