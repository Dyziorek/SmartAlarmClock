package com.example.smartalarmclock.helper;

public abstract class Result<T> {
    private Result() {}

    public static final class Success<T> extends Result<T>
    {
        public T data;

        public Success(T resData)
        {
            data = resData;
        }
    }

    public static final class Error<T> extends Result<T>
    {
        public Exception errorInfo;

        public Error(Exception err)
        {
            errorInfo = err;
        }
    }


}
