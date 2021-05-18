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
package com.android.systemui.globalactions

import android.content.Context
import android.service.quickaccesswallet.QuickAccessWalletClient
import android.view.ViewGroup
import com.android.systemui.controls.controller.ControlsController
import com.android.systemui.plugins.ActivityStarter
import javax.inject.Inject

// Empty class, will be overridden for relevant devices
class GlobalActionsInfoProvider @Inject constructor(
    private val context: Context,
    private val walletClient: QuickAccessWalletClient,
    private val controlsController: ControlsController,
    private val activityStarter: ActivityStarter
) {

    fun addPanel(context: Context, parent: ViewGroup, nActions: Int, dismissParent: Runnable) { }

    fun shouldShowMessage(): Boolean {
        return false
    }
}