package com.android.nana.connect;

import android.os.Environment;

import com.android.nana.WebCastApplication;
import com.android.nana.util.SizeUtils;

/**
 * Created by lenovo on 2018/11/26.
 */

public class Constants {
    public static  String HOST_URL ="http://qiniu.facethree.com/";//七牛云播放地址
    public static final int ITEM_SPACE_10 = SizeUtils.dp2px(WebCastApplication.getInstance(), 10);
    public static final int ITEM_SPACE_12 = SizeUtils.dp2px(WebCastApplication.getInstance(), 12);
    public static final int ITEM_SPACE_15 = SizeUtils.dp2px(WebCastApplication.getInstance(), 15);
    public static final String VIDEO_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/ShortVideo/";
    public static final String TRIM_FILE_PATH = VIDEO_STORAGE_DIR + "trimmed.mp4";
    public static final String EDITED_FILE_PATH = VIDEO_STORAGE_DIR + "edited.mp4";

    public static final String EXTRA_TIP = "ExtraTip";
    public static String DEFAULT_CITY = "北京";
    public static final String KEY_WORDS_NAME = "KeyWord";

    //发现是否关闭声音
    public static final String ENCLOSURE_VOICE = "nearby";
    //关注是否关闭声音
    public static final String FOLLOW_VOICE = "nearby";
    //附件是否关闭声音
    public static final String NEARBY_VOICE = "nearby";
}
