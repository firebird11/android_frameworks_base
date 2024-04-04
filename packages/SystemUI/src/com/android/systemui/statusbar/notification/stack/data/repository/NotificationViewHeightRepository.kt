/*
 * Copyright (C) 2024 The Android Open Source Project
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

package com.android.systemui.statusbar.notification.stack.data.repository

import com.android.systemui.dagger.SysUISingleton
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * This repository contains state generated by the NSSL and required by the composable placeholders
 * to ensure they are representing the actual contents that will be rendered.
 */
@SysUISingleton
class NotificationViewHeightRepository @Inject constructor() {

    /**
     * The height in px of the contents of notification stack. Depending on the number of
     * notifications, this can exceed the space available on screen to show notifications, at which
     * point the notification stack should become scrollable.
     */
    val stackHeight = MutableStateFlow(0f)

    /** The height in px of the current heads up notification. */
    val headsUpHeight = MutableStateFlow(0f)

    /**
     * The amount in px that the notification stack should scroll due to internal expansion. This
     * should only happen when a notification expansion hits the bottom of the screen, so it is
     * necessary to scroll up to keep expanding the notification.
     */
    val syntheticScroll = MutableStateFlow(0f)
}
