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

package com.android.server.pm.pkg;

import android.annotation.Nullable;
import android.content.pm.SuspendDialogInfo;
import android.os.BaseBundle;
import android.os.PersistableBundle;
import android.util.Slog;

import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Objects;

/**
 * Container to describe suspension parameters.
 * @hide
 */
public final class SuspendParams {

    private static final String LOG_TAG = "FrameworkPackageUserState";
    private static final String TAG_DIALOG_INFO = "dialog-info";
    private static final String TAG_APP_EXTRAS = "app-extras";
    private static final String TAG_LAUNCHER_EXTRAS = "launcher-extras";

    private final SuspendDialogInfo dialogInfo;
    private final PersistableBundle appExtras;
    private final PersistableBundle launcherExtras;

    public SuspendParams(SuspendDialogInfo dialogInfo, PersistableBundle appExtras,
            PersistableBundle launcherExtras) {
        this.dialogInfo = dialogInfo;
        this.appExtras = appExtras;
        this.launcherExtras = launcherExtras;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SuspendParams)) {
            return false;
        }
        final SuspendParams other = (SuspendParams) obj;
        if (!Objects.equals(dialogInfo, other.dialogInfo)) {
            return false;
        }
        if (!BaseBundle.kindofEquals(appExtras, other.appExtras)) {
            return false;
        }
        if (!BaseBundle.kindofEquals(launcherExtras, other.launcherExtras)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = Objects.hashCode(dialogInfo);
        hashCode = 31 * hashCode + ((appExtras != null) ? appExtras.size() : 0);
        hashCode = 31 * hashCode + ((launcherExtras != null) ? launcherExtras.size() : 0);
        return hashCode;
    }

    /**
     * Serializes this object into an xml format
     *
     * @param out the {@link XmlSerializer} object
     */
    public void saveToXml(TypedXmlSerializer out) throws IOException {
        if (dialogInfo != null) {
            out.startTag(null, TAG_DIALOG_INFO);
            dialogInfo.saveToXml(out);
            out.endTag(null, TAG_DIALOG_INFO);
        }
        if (appExtras != null) {
            out.startTag(null, TAG_APP_EXTRAS);
            try {
                appExtras.saveToXml(out);
            } catch (XmlPullParserException e) {
                Slog.e(LOG_TAG, "Exception while trying to write appExtras."
                        + " Will be lost on reboot", e);
            }
            out.endTag(null, TAG_APP_EXTRAS);
        }
        if (launcherExtras != null) {
            out.startTag(null, TAG_LAUNCHER_EXTRAS);
            try {
                launcherExtras.saveToXml(out);
            } catch (XmlPullParserException e) {
                Slog.e(LOG_TAG, "Exception while trying to write launcherExtras."
                        + " Will be lost on reboot", e);
            }
            out.endTag(null, TAG_LAUNCHER_EXTRAS);
        }
    }

    /**
     * Parses this object from the xml format. Returns {@code null} if no object related
     * information could be read.
     *
     * @param in the reader
     */
    public static SuspendParams restoreFromXml(TypedXmlPullParser in) throws IOException {
        SuspendDialogInfo readDialogInfo = null;
        PersistableBundle readAppExtras = null;
        PersistableBundle readLauncherExtras = null;

        final int currentDepth = in.getDepth();
        int type;
        try {
            while ((type = in.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG
                    || in.getDepth() > currentDepth)) {
                if (type == XmlPullParser.END_TAG
                        || type == XmlPullParser.TEXT) {
                    continue;
                }
                switch (in.getName()) {
                    case TAG_DIALOG_INFO:
                        readDialogInfo = SuspendDialogInfo.restoreFromXml(in);
                        break;
                    case TAG_APP_EXTRAS:
                        readAppExtras = PersistableBundle.restoreFromXml(in);
                        break;
                    case TAG_LAUNCHER_EXTRAS:
                        readLauncherExtras = PersistableBundle.restoreFromXml(in);
                        break;
                    default:
                        Slog.w(LOG_TAG, "Unknown tag " + in.getName()
                                + " in SuspendParams. Ignoring");
                        break;
                }
            }
        } catch (XmlPullParserException e) {
            Slog.e(LOG_TAG, "Exception while trying to parse SuspendParams,"
                    + " some fields may default", e);
        }
        return new SuspendParams(readDialogInfo, readAppExtras, readLauncherExtras);
    }

    public SuspendDialogInfo getDialogInfo() {
        return dialogInfo;
    }

    public PersistableBundle getAppExtras() {
        return appExtras;
    }

    public PersistableBundle getLauncherExtras() {
        return launcherExtras;
    }
}
