package com.sage.tel_coming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Environment
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import org.jetbrains.anko.audioManager
import org.jetbrains.anko.telephonyManager
import java.io.*
import java.util.*

/**
 * Created by zhang on 2016/5/14 0014.
 * 来电监听广播
 */
class PhoneReceiver : BroadcastReceiver() {

    companion object {
        val SAMPLE_DIR_NAME = "baiduTTS";
        val SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
        val SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
        val TEXT_MODEL_NAME = "bd_etts_text.dat";
        val LICENSE_FILE_NAME = "temp_license";
        val ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
        val ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
        val ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    }

    lateinit var mSpeechSynthesizer: SpeechSynthesizer
    lateinit var ctx: Context
    private lateinit var mSampleDirPath: String


    override fun onReceive(context: Context, intent: Intent) {
        ctx = context
        println("收到广播")
        when (intent.action) {
            Intent.ACTION_NEW_OUTGOING_CALL -> {
                //拨打电话
            }
            else -> {
                println(context.telephonyManager.callState)
                startTTS(context)
                when (context.telephonyManager.callState) {
                    TelephonyManager.CALL_STATE_RINGING -> {//响铃
                        val coming = intent.getStringExtra("incoming_number")
                        val name = hasLinkMan(context, coming)
                        println("coming = $coming")
                        println("name = $name")
                        val sb = "有电话来自:${if (name.isEmpty()) coming else name}"
                        val msp = mSpeechSynthesizer.speak("$sb.     $sb.     $sb.")
                        println("msp = $msp")
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {//接听
                        mSpeechSynthesizer.stop()
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {//挂断
                        mSpeechSynthesizer.stop()
                    }

                }
            }
        }
    }

    fun getLinkMan(ctx: Context): ArrayList<LinkMan> {
        println("获取联系人")
        var linkMans = ArrayList<LinkMan>()
        var name = ""
        var phones = ArrayList<String>()
        val cr = ctx.contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=$id", null, null)
            while (phone.moveToNext()) {
                var num = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                num = num.replace("-", "")
                num = num.replace(" ", "")
                phones.add(num)
            }
            linkMans.add(LinkMan(name, phones))
        }
        return linkMans
    }

    fun hasLinkMan(ctx: Context, num: String): String {
        println("对比联系人")
        getLinkMan(ctx).forEach {
            if (it.phones.contains(num))
                return it.name
        }
        return ""
    }

    // 初始化语音合成客户端并启动
    fun startTTS(ctx: Context) {
        initialEnv(ctx)
        println("语音合成")
        // 获取语音合成对象实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        // 设置context
        mSpeechSynthesizer.setContext(ctx);
        // 设置语音合成状态监听器
        //        mSpeechSynthesizer.setSpeechSynthesizerListener(ctx);
        // 设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
        mSpeechSynthesizer.setApiKey("oHn1s5sZxGEwC7pRSsQL7ZaW", "5363865c953a07d32456a67eb6bbeccf");
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId("8129917");
        // 设置语音合成文本模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, "$mSampleDirPath/$TEXT_MODEL_NAME");
        // 设置语音合成声音模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, "$mSampleDirPath/$SPEECH_FEMALE_MODEL_NAME");
        // 设置语音合成声音授权文件
        //        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, "$mSampleDirPath/$LICENSE_FILE_NAME");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 获取语音合成授权信息
        var authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        if (authInfo.isSuccess()) {
            println("授权成功")
            mSpeechSynthesizer.initTts(TtsMode.MIX);
            mSpeechSynthesizer.loadModel("$mSampleDirPath/$SPEECH_FEMALE_MODEL_NAME", "$mSampleDirPath/$TEXT_MODEL_NAME")
            mSpeechSynthesizer.setSpeechSynthesizerListener(listener)
        } else {
            println("授权失败")
        }
    }


    private fun initialEnv(ctx: Context) {
        val sdcardPath = Environment.getExternalStorageDirectory().toString();
        mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        makeDir(mSampleDirPath);

        copyFromAssetsToSdcard(ctx, false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(ctx, false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(ctx, false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(ctx, false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(ctx, false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(ctx, false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private fun makeDir(dirPath: String) {
        val file = File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private fun copyFromAssetsToSdcard(ctx: Context, isCover: Boolean, source: String, dest: String) {
        val file = File(dest);
        if (isCover || (!isCover && !file.exists())) {
            var iis: InputStream? = null;
            var fos: FileOutputStream? = null;
            try {
                iis = ctx.resources.assets.open(source);
                val path = dest;
                fos = FileOutputStream(path);
                var buffer = ByteArray(1024);
                var size = 0;
                size = iis.read(buffer, 0, 1024)
                while (size >= 0) {
                    fos.write(buffer, 0, size);
                    size = iis.read(buffer, 0, 1024)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
            } catch (e: IOException) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (e: IOException) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (iis != null) {
                        iis.close();
                    }
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
        }
    }

    private val listener = object : SpeechSynthesizerListener {
        override fun onSpeechFinish(p0: String) {
            println("结束")
            ctx.audioManager.setStreamVolume(AudioManager.STREAM_RING, ctx.audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
        }

        override fun onSpeechProgressChanged(p0: String, p1: Int) {
        }

        override fun onSynthesizeStart(p0: String) {
        }

        override fun onSynthesizeFinish(p0: String) {
        }

        override fun onSpeechStart(p0: String) {
            println("开始")
            ctx.audioManager.setStreamVolume(AudioManager.STREAM_RING, 3, 0)
            ctx.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ctx.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        }

        override fun onSynthesizeDataArrived(p0: String, p1: ByteArray, p2: Int) {
        }

        override fun onError(p0: String, p1: SpeechError) {
            ctx.audioManager.setStreamVolume(AudioManager.STREAM_RING, ctx.audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
        }
    }

}