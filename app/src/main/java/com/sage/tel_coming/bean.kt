package com.sage.tel_coming

import java.util.*

/**
 * Created by zhang on 2016/5/14 0014.
 * 实体类
 */
data class LinkMan(val name: String = "", val phones: List<String> = ArrayList<String>()) {}

object C {
    const val API_KEY = "oHn1s5sZxGEwC7pRSsQL7ZaW"
    const val SECRET_KEY = "5363865c953a07d32456a67eb6bbeccf"
    const val APP_ID = "8129917"

    const val SAMPLE_DIR_NAME = "baiduTTS"
    const val SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat"
    const val SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat"
    const val TEXT_MODEL_NAME = "bd_etts_text.dat"
    const val LICENSE_FILE_NAME = "temp_license"
    const val ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat"
    const val ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat"
    const val ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat"
}