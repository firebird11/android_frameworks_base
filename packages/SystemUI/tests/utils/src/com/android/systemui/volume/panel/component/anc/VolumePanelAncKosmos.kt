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

package com.android.systemui.volume.panel.component.anc

import androidx.slice.SliceViewManager
import com.android.systemui.kosmos.Kosmos
import com.android.systemui.kosmos.testScope
import com.android.systemui.util.mockito.mock
import com.android.systemui.volume.domain.interactor.audioOutputInteractor
import com.android.systemui.volume.panel.component.anc.data.repository.FakeAncSliceRepository
import com.android.systemui.volume.panel.component.anc.domain.interactor.AncSliceInteractor

var Kosmos.sliceViewManager: SliceViewManager by Kosmos.Fixture { mock {} }
val Kosmos.ancSliceRepository by Kosmos.Fixture { FakeAncSliceRepository() }
val Kosmos.ancSliceInteractor by
    Kosmos.Fixture {
        AncSliceInteractor(audioOutputInteractor, ancSliceRepository, testScope.backgroundScope)
    }
