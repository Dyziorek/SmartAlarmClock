package com.example.smartalarmclock;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.example.smartalarmclock.classes.LocaleChanger;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TU = TimeUnit.SECONDS;


    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TU,
            workQueue
            );

    @Override
    protected void attachBaseContext(Context base) {
        LocaleChanger.setDefaultLocale(Locale.getDefault());
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleChanger.setLocale(this);
    }
}
