package com.xiaoniu.common.http.callback;

import okhttp3.ResponseBody;

public abstract class HttpCallback<T> {
    /**
     * UI Thread
     */
    public void onStart() {
    }

    public abstract void onFailure(Throwable e);

    public abstract void onSuccess(T result);

    public void onUpProgress(long bytesWritten, long totalSize) {
    }

    public void onComplete() {
    }

    /**
     * Thread Pool Thread
     */
    public abstract T parseResponse(ResponseBody body) throws Exception;


}