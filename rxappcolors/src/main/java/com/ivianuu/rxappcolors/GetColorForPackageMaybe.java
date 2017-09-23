/*
 * Copyright 2017 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.rxappcolors;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.util.Log;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

/**
 * Fetches the color for the package
 */
final class GetColorForPackageMaybe implements MaybeOnSubscribe<Integer> {

    private static final String ATTR = "attr";

    private static final String APP_COMPAT_COLOR_PRIMARY = "colorPrimary";
    private static final String LOLLIPOP_COLOR_PRIMARY = "android:colorPrimary";

    private static final int DEFAULT_GREY_LIGHT = Color.parseColor("#F5F5F5");
    private static final int DEFAULT_GREY_DARK = Color.parseColor("#212121");

    private final PackageManager packageManager;
    private final String packageName;

    private GetColorForPackageMaybe(PackageManager packageManager, String packageName) {
        this.packageManager = packageManager;
        this.packageName = packageName;
    }

    /**
     * Returns the color for this package
     */
    @CheckResult @NonNull
    static Maybe<Integer> create(@NonNull Context context, @NonNull String packageName) {
        return Maybe.create(new GetColorForPackageMaybe(context.getPackageManager(), packageName));
    }

    @Override
    public void subscribe(MaybeEmitter<Integer> e) throws Exception {
        Resources resources = packageManager.getResourcesForApplication(packageName);

        // check if the resources are available
        if (resources == null
                && !e.isDisposed()) {
            e.onComplete();
            log("resources null");
            return;
        }

        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        // check activity theme
        if (intent != null) {
            ActivityInfo activityInfo = packageManager.getActivityInfo(intent.getComponent(), 0);
            if (activityInfo != null) {
                // check app compat
                int appCompatId = resources.getIdentifier(APP_COMPAT_COLOR_PRIMARY, ATTR, packageName);
                if (appCompatId > 0) {
                    int color = getColorFromTheme(activityInfo.theme, appCompatId, resources);
                    if (isValidColor(color)
                            && !e.isDisposed()) {
                        log("use app compat activity");
                        e.onSuccess(color);
                        e.onComplete();
                        return;
                    }
                }

                int lollipopId = resources.getIdentifier(LOLLIPOP_COLOR_PRIMARY, ATTR, packageName);
                if (lollipopId > 0) {
                    int color = getColorFromTheme(activityInfo.theme, lollipopId, resources);
                    if (isValidColor(color)
                            && !e.isDisposed()) {
                        log("use lollipop activity");
                        e.onSuccess(color);
                        e.onComplete();
                        return;
                    }
                }
            }
        }

        // check application theme
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        if (applicationInfo != null) {
            // check app compat
            int appCompatId = resources.getIdentifier(APP_COMPAT_COLOR_PRIMARY, ATTR, packageName);
            if (appCompatId > 0) {
                int color = getColorFromTheme(applicationInfo.theme, appCompatId, resources);
                if (isValidColor(color)
                        && !e.isDisposed()) {
                    log("use app compat app");
                    e.onSuccess(color);
                    e.onComplete();
                    return;
                }
            }

            int lollipopId = resources.getIdentifier(LOLLIPOP_COLOR_PRIMARY, ATTR, packageName);
            if (lollipopId > 0) {
                int color = getColorFromTheme(applicationInfo.theme, lollipopId, resources);
                if (isValidColor(color)
                        && !e.isDisposed()) {
                    log("use lollipop app");
                    e.onSuccess(color);
                    e.onComplete();
                    return;
                }
            }
        }

        // extract the color from the app icon
        Drawable appIcon = packageManager.getApplicationIcon(packageName);
        if (appIcon != null) {
            Bitmap iconAsBitmap = ImageUtil.drawableToBitmap(appIcon);
            Palette palette = Palette.from(iconAsBitmap)
                    .clearFilters()
                    .generate();

            int color = PaletteUtil.getBestColor(palette, 0);

            if (isValidColor(color)
                    && !e.isDisposed()) {
                log("use app icon");
                e.onSuccess(color);
                e.onComplete();
                return;
            }
        }

        if (!e.isDisposed()) {
            e.onComplete();
        }
    }

    private int getColorFromTheme(int themeId, int attrId, Resources resources) {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(themeId, false);

        TypedArray array = theme.obtainStyledAttributes(new int[]{attrId});
        int color = array.getColor(0, 0);
        array.recycle();

        return color;
    }

    private boolean isValidColor(int color) {
        return color != 0 && color != DEFAULT_GREY_LIGHT && color != DEFAULT_GREY_DARK;
    }

    private void log(String message, Object... args) {
        Log.d("testt", String.format(message, args));
    }
}
