package com.FindersKeepers.backend.config.resources.locale;

import java.util.Locale;

public class LocaleThreadStorage {


    private static final ThreadLocal<Locale> locale = new ThreadLocal<>();

    public static void setLocale(Locale locale) {
        LocaleThreadStorage.locale.set(locale);
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void clear() {
        locale.remove();
    }

}
