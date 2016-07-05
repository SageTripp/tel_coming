package com.sage.tel_coming

import android.content.Context
import android.media.AudioManager
import android.os.Environment
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import org.jetbrains.anko.audioManager
import java.io.*

/**
 * Created by zst on 2016/7/5 0005.
 * 描述:
 */
object SpeakManager {


    lateinit var mSpeechSynthesizer: SpeechSynthesizer
    lateinit var ctx: Context
    private lateinit var mSampleDirPath: String
    private var isStart = false


    fun startTTS(ctx: Context) {
        initialEnv(ctx)
        println("语音合成")
        // 获取语音合成对象实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        // 设置context
        mSpeechSynthesizer.setContext(ctx)
        // 设置语音合成状态监听器
        //        mSpeechSynthesizer.setSpeechSynthesizerListener(ctx);
        // 设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
        mSpeechSynthesizer.setApiKey(C.API_KEY, C.SECRET_KEY)
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId(C.APP_ID)
        // 设置语音合成文本模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, "$mSampleDirPath/${PhoneReceiver.TEXT_MODEL_NAME}")
        // 设置语音合成声音模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, "$mSampleDirPath/${PhoneReceiver.SPEECH_FEMALE_MODEL_NAME}")
        // 设置语音合成声音授权文件
        //        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, "$mSampleDirPath/$LICENSE_FILE_NAME")
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT)
        // 获取语音合成授权信息
        val authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        if (authInfo.isSuccess) {
            println("授权成功")
            mSpeechSynthesizer.initTts(TtsMode.MIX);
            mSpeechSynthesizer.loadModel("$mSampleDirPath/${PhoneReceiver.SPEECH_FEMALE_MODEL_NAME}", "$mSampleDirPath/${PhoneReceiver.TEXT_MODEL_NAME}")
            mSpeechSynthesizer.setSpeechSynthesizerListener(listener)
            isStart = true
        } else {
            println("授权失败")
        }
    }

    fun speak(content: String) {
        mSpeechSynthesizer.speak("$content")
    }

    private fun initialEnv(ctx: Context) {
        val sdcardPath = Environment.getExternalStorageDirectory().toString()
        mSampleDirPath = sdcardPath + "/" + C.SAMPLE_DIR_NAME
        makeDir(mSampleDirPath)

        copyFromAssetsToSdcard(ctx, false, C.SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + C.SPEECH_FEMALE_MODEL_NAME)
        copyFromAssetsToSdcard(ctx, false, C.SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + C.SPEECH_MALE_MODEL_NAME)
        copyFromAssetsToSdcard(ctx, false, C.TEXT_MODEL_NAME, mSampleDirPath + "/" + C.TEXT_MODEL_NAME)
        copyFromAssetsToSdcard(ctx, false, "english/" + C.ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + C.ENGLISH_SPEECH_FEMALE_MODEL_NAME)
        copyFromAssetsToSdcard(ctx, false, "english/" + C.ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + C.ENGLISH_SPEECH_MALE_MODEL_NAME)
        copyFromAssetsToSdcard(ctx, false, "english/" + C.ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + C.ENGLISH_TEXT_MODEL_NAME)
    }

    private fun makeDir(dirPath: String) {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
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
        val file = File(dest)
        if (isCover || (!isCover && !file.exists())) {
            var iis: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                iis = ctx.resources.assets.open(source)
                val path = dest
                fos = FileOutputStream(path)
                val buffer = ByteArray(1024)
                var size = iis.read(buffer, 0, 1024)
                while (size >= 0) {
                    fos.write(buffer, 0, size)
                    size = iis.read(buffer, 0, 1024)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                try {
                    if (iis != null) {
                        iis.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
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
