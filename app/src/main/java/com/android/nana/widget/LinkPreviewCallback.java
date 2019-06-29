package com.android.nana.widget;

/**
 * Created by lenovo on 2017/10/19.
 */

public interface LinkPreviewCallback {

    void onPre();

    /**
     *
     * @param sourceContent
     *            Class with all contents from preview.
     * @param isNull
     *            Indicates if the content is null.
     */
    void onPos(SourceContent sourceContent, boolean isNull);
}
