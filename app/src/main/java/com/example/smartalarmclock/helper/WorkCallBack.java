package com.example.smartalarmclock.helper;

public interface WorkCallBack<T> {
    void onCompleted(Result<T> result);
}
