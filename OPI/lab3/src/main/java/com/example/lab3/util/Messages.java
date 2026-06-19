package com.example.lab3.util;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {
    private static final String BUNDLE_NAME = "i18n.messages";

    private Messages() {
    }

    public static String get(String key) {
        return ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString(key);
    }
}
