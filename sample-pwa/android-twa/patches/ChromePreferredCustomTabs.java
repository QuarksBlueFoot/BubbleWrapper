package com.example.pwasample;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

/**
 * Prefer Chrome for Custom Tabs when available, otherwise fall back to the system chosen provider.
 *
 * Use this helper for launching external links from your Android wrapper or other Activities.
 *
 * Notes:
 * - A TWA itself is hosted by a browser that supports the TWA protocol (commonly Chrome on Android).
 * - For regular Custom Tabs, setting the package to com.android.chrome prefers Chrome when installed.
 */
public final class ChromePreferredCustomTabs {
    private static final String CHROME_PACKAGE = "com.android.chrome";

    private ChromePreferredCustomTabs() {}

    public static void launch(Activity activity, Uri uri) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();

        if (isPackageInstalled(activity.getPackageManager(), CHROME_PACKAGE)) {
            intent.intent.setPackage(CHROME_PACKAGE);
        } else {
            intent.intent.setPackage(null); // let Android choose a compatible Custom Tabs provider
        }

        intent.launchUrl(activity, uri);
    }

    private static boolean isPackageInstalled(PackageManager pm, String packageName) {
        try {
            pm.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
