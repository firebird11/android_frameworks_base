/*
 * Copyright (C) 2021 The Android Open Source Project
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

package android.media.tv.interactive;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * App link information used by TV interactive app to launch Android apps.
 */
public final class AppLinkInfo implements Parcelable {
    private @NonNull ComponentName mComponentName;
    private @Nullable String mUriScheme;
    private @Nullable String mUriHost;
    private @Nullable String mUriPrefix;


    /**
     * Creates a new AppLinkInfo.
     */
    private AppLinkInfo(
            @NonNull String packageName,
            @NonNull String className,
            @Nullable String uriScheme,
            @Nullable String uriHost,
            @Nullable String uriPrefix) {
        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, packageName);
        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, className);
        this.mComponentName = new ComponentName(packageName, className);
        this.mUriScheme = uriScheme;
        this.mUriHost = uriHost;
        this.mUriPrefix = uriPrefix;
    }

    /**
     * Gets package name of the App link.
     */
    @NonNull
    public String getPackageName() {
        return mComponentName.getPackageName();
    }

    /**
     * Gets package class of the App link.
     */
    @NonNull
    public String getClassName() {
        return mComponentName.getClassName();
    }

    /**
     * Gets URI scheme of the App link.
     */
    @Nullable
    public String getUriScheme() {
        return mUriScheme;
    }

    /**
     * Gets URI host of the App link.
     */
    @Nullable
    public String getUriHost() {
        return mUriHost;
    }

    /**
     * Gets URI prefix of the App link.
     */
    @Nullable
    public String getUriPrefix() {
        return mUriPrefix;
    }

    @Override
    public String toString() {
        return "AppLinkInfo { "
                + "packageName = " + mComponentName.getPackageName() + ", "
                + "className = " + mComponentName.getClassName() + ", "
                + "uriScheme = " + mUriScheme + ", "
                + "uriHost = " + mUriHost + ", "
                + "uriPrefix = " + mUriPrefix
                + " }";
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mComponentName.getPackageName());
        dest.writeString(mComponentName.getClassName());
        dest.writeString(mUriScheme);
        dest.writeString(mUriHost);
        dest.writeString(mUriPrefix);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /* package-private */ AppLinkInfo(@NonNull Parcel in) {
        String packageName = in.readString();
        String className = in.readString();
        String uriScheme = in.readString();
        String uriHost = in.readString();
        String uriPrefix = in.readString();

        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, packageName);
        com.android.internal.util.AnnotationValidations.validate(
                NonNull.class, null, className);
        this.mComponentName = new ComponentName(packageName, className);
        this.mUriScheme = uriScheme;
        this.mUriHost = uriHost;
        this.mUriPrefix = uriPrefix;
    }

    @NonNull
    public static final Parcelable.Creator<AppLinkInfo> CREATOR =
            new Parcelable.Creator<AppLinkInfo>() {
                @Override
                public AppLinkInfo[] newArray(int size) {
                    return new AppLinkInfo[size];
                }

                @Override
                public AppLinkInfo createFromParcel(@NonNull Parcel in) {
                    return new AppLinkInfo(in);
                }
            };

    /**
     * A builder for {@link AppLinkInfo}
     */
    public static final class Builder {
        private @NonNull String mPackageName;
        private @NonNull String mClassName;
        private @Nullable String mUriScheme;
        private @Nullable String mUriHost;
        private @Nullable String mUriPrefix;

        /**
         * Creates a new Builder.
         */
        public Builder(
                @NonNull String packageName,
                @NonNull String className) {
            mPackageName = packageName;
            com.android.internal.util.AnnotationValidations.validate(
                    NonNull.class, null, mPackageName);
            mClassName = className;
            com.android.internal.util.AnnotationValidations.validate(
                    NonNull.class, null, mClassName);
        }

        /**
         * Sets URI scheme of the App link.
         */
        @NonNull
        public Builder setUriScheme(@NonNull String value) {
            mUriScheme = value;
            return this;
        }

        /**
         * Sets URI host of the App link.
         */
        @NonNull
        public Builder setUriHost(@NonNull String value) {
            mUriHost = value;
            return this;
        }

        /**
         * Sets URI prefix of the App link.
         */
        @NonNull
        public Builder setUriPrefix(@NonNull String value) {
            mUriPrefix = value;
            return this;
        }

        /** Builds the instance. This builder should not be touched after calling this! */
        @NonNull
        public AppLinkInfo build() {
            AppLinkInfo o = new AppLinkInfo(
                    mPackageName,
                    mClassName,
                    mUriScheme,
                    mUriHost,
                    mUriPrefix);
            return o;
        }
    }
}
