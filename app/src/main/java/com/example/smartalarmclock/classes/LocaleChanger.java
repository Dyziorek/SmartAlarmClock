package com.example.smartalarmclock.classes;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocaleChanger {

    private static Locale defaultLocale;

    public static Context onAttach(Context context) {
        return setLocale(context);
    }

    public static String getLanguage(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("pref_key_language", Locale.getDefault().getLanguage());
        //I can't use my static variable for the string because it isn't initialized here yet
    }

    public static Context setLocale(Context context) {
        String language = getLanguage(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return updateResourcesLegacy(context, language);
        }
    }

    /**
     * Applies the loaded language to the context for Android N and above.
     *
     * @param context  The application context
     * @param language The language to apply
     * @return A new context with the updated language
     */
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale;

        List<String> localeList = Arrays.asList(language.split(","));

        if (language.equals("default")) {
            locale = defaultLocale;
        } else {
            if (localeList.size() == 2) {
                locale = new Locale(localeList.get(0), localeList.get(1));
            } else {
                locale = new Locale(localeList.get(0));
            }

            Locale.setDefault(locale);
        }

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    /**
     * Applies the loaded language to the context for Android M and below
     *
     * @param context  The application context
     * @param language The language to apply
     * @return A new context with the updated language
     */
    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale;

        List<String> localeList = Arrays.asList(language.split(","));

        if (language.equals("default")) {
            locale = defaultLocale;
        } else {
            if (localeList.size() == 2) {
                locale = new Locale(localeList.get(0), localeList.get(1));
            } else {
                locale = new Locale(localeList.get(0));
            }

            Locale.setDefault(locale);
        }

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
    }
}