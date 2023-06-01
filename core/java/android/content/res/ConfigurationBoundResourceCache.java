/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.res;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.pm.ActivityInfo.Config;

/**
 * A Cache class which can be used to cache resource objects that are easy to clone but more
 * expensive to inflate.
 *
 * @hide For internal use only.
 */
public class ConfigurationBoundResourceCache<T> extends ThemedResourceCache<ConstantState<T>> {

    @UnsupportedAppUsage
    public ConfigurationBoundResourceCache() {
    }

    /**
     * If the resource is cached, creates and returns a new instance of it.
     *
     * @param key a key that uniquely identifies the drawable resource
     * @param resources a Resources object from which to create new instances.
     * @param theme the theme where the resource will be used
     * @return an Entry wrapping a new instance of the resource, or {@code null} if not in
     *         the cache
     */
    public Entry<T> getInstance(long key, Resources resources, Resources.Theme theme) {
        final Entry<ConstantState<T>> e = get(key, theme);
        if (e.hasValue()) {
            return new Entry<>(e.getValue().newInstance(resources, theme), e.getGeneration());
        }

        return new Entry<>(null, e.getGeneration());
    }

    @Override
    public boolean shouldInvalidateEntry(ConstantState<T> entry, @Config int configChanges) {
        return Configuration.needNewResources(configChanges, entry.getChangingConfigurations());
    }
}
