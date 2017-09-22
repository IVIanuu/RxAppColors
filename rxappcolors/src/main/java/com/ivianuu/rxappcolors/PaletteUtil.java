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

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

import java.util.Collections;
import java.util.Comparator;

/**
 * Palette util
 */
final class PaletteUtil {

    private static Comparator<Palette.Swatch> SWATCH_COMPARATOR
            = (lhs, rhs) -> lhs.getPopulation() - rhs.getPopulation();

    private PaletteUtil() {
        // no instances
    }

    /**
     * Returns the best color of this palette
     */
    @ColorInt
    static int getBestColor(@NonNull Palette palette, @ColorInt int fallbackColor) {
        if (palette.getDarkVibrantSwatch() != null) {
            return palette.getDarkVibrantSwatch().getRgb();
        } else if (palette.getMutedSwatch() != null) {
            return palette.getMutedSwatch().getRgb();
        } else if (palette.getDarkVibrantSwatch() != null) {
            return palette.getDarkVibrantSwatch().getRgb();
        } else if (palette.getDarkMutedSwatch() != null) {
            return palette.getDarkMutedSwatch().getRgb();
        } else if (palette.getLightVibrantSwatch() != null) {
            return palette.getLightVibrantSwatch().getRgb();
        } else if (palette.getLightMutedSwatch() != null) {
            return palette.getLightMutedSwatch().getRgb();
        } else if (!palette.getSwatches().isEmpty()) {
            return Collections.max(palette.getSwatches(), SWATCH_COMPARATOR).getRgb();
        }

        return fallbackColor;
    }
}
