package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class HttpException extends Exception {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4010634120321127684L;

    public HttpException() {
        super();
    }

    public HttpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public HttpException(String detailMessage) {
        super(detailMessage);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }
}
