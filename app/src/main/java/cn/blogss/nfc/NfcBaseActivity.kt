package cn.blogss.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcV
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

open class NfcBaseActivity: ComponentActivity() {
    protected var nfcAdapter: NfcAdapter? = null
    private var filters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        techLists = arrayOf(arrayOf("android.nfc.tech.NfcV",
            "android.nfc.tech.Ndef"))
    }

    /**
     * 在 onResume 方法中启用 nfc 前台分发
     * Foreground dispatch can only be enabled when your activity is resumed.
     */
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
        /**
         * 启用 nfc 前台分发, 该 activity 位于前台时，nfc 标签会直接交给该 Activity 处理，而不是 <intent-filter> 中配置的 Activity
         * 若 filters 和 techLists 参数同时为空，会导致该 activity 接收 action 为 ACTION_TAG_DISCOVERED
         * 的 intent
         */
        nfcAdapter?.enableForegroundDispatch(this,
            pendingIntent, filters, techLists)
    }

    override fun onPause() {
        super.onPause()
        // 关闭 nfc 前台调度
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}