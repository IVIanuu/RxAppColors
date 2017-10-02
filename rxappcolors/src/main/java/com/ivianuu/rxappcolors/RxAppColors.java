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
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import io.reactivex.Maybe;
import io.reactivex.Single;

import static com.ivianuu.rxappcolors.Preconditions.checkNotNull;

/**
 * Entry point to get colors
 */
public final class RxAppColors {

    private RxAppColors() {
        // no instances
    }

    /**
     * Maybe emits the color for the package
     */
    @CheckResult @NonNull
    public static Maybe<Integer> getColor(@NonNull Context context, @NonNull String packageName) {
        checkNotNull(context, "context == null");
        checkNotNull(packageName, "packageName == null");
        return GetColorForPackageMaybe.create(context, packageName);
    }

    /**
     * Emits the color for the package or the fallback color
     */
    @CheckResult @NonNull
    public static Single<Integer> getColor(@NonNull Context context,
                                           @NonNull String packageName,
                                           @ColorInt int fallbackColor) {
        checkNotNull(context, "context == null");
        checkNotNull(packageName, "packageName == null");
        return GetColorForPackageMaybe.create(context, packageName)
                .defaultIfEmpty(fallbackColor)
                .toSingle();
    }
}
