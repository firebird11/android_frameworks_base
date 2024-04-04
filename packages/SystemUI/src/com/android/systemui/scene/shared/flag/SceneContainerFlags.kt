/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("NOTHING_TO_INLINE")

package com.android.systemui.scene.shared.flag

import com.android.systemui.Flags.FLAG_SCENE_CONTAINER
import com.android.systemui.Flags.sceneContainer
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.flags.FlagToken
import com.android.systemui.flags.RefactorFlagUtils
import com.android.systemui.keyguard.KeyguardBottomAreaRefactor
import com.android.systemui.keyguard.KeyguardWmStateRefactor
import com.android.systemui.keyguard.MigrateClocksToBlueprint
import com.android.systemui.keyguard.shared.ComposeLockscreen
import com.android.systemui.media.controls.util.MediaInSceneContainerFlag
import com.android.systemui.statusbar.notification.shared.NotificationsHeadsUpRefactor
import com.android.systemui.statusbar.phone.PredictiveBackSysUiFlag
import dagger.Module
import dagger.Provides

/** Helper for reading or using the scene container flag state. */
object SceneContainerFlag {
    /** The flag description -- not an aconfig flag name */
    const val DESCRIPTION = "SceneContainerFlag"

    @JvmStatic
    inline val isEnabled
        get() =
            sceneContainer() && // mainAconfigFlag
            ComposeLockscreen.isEnabled &&
                KeyguardBottomAreaRefactor.isEnabled &&
                KeyguardWmStateRefactor.isEnabled &&
                MediaInSceneContainerFlag.isEnabled &&
                MigrateClocksToBlueprint.isEnabled &&
                NotificationsHeadsUpRefactor.isEnabled &&
                PredictiveBackSysUiFlag.isEnabled
    // NOTE: Changes should also be made in getSecondaryFlags and @EnableSceneContainer

    /** The main aconfig flag. */
    inline fun getMainAconfigFlag() = FlagToken(FLAG_SCENE_CONTAINER, sceneContainer())

    /** The set of secondary flags which must be enabled for scene container to work properly */
    inline fun getSecondaryFlags(): Sequence<FlagToken> =
        sequenceOf(
            ComposeLockscreen.token,
            KeyguardBottomAreaRefactor.token,
            KeyguardWmStateRefactor.token,
            MediaInSceneContainerFlag.token,
            MigrateClocksToBlueprint.token,
            NotificationsHeadsUpRefactor.token,
            PredictiveBackSysUiFlag.token,
            // NOTE: Changes should also be made in isEnabled and @EnableSceneContainer
        )

    /** The full set of requirements for SceneContainer */
    inline fun getAllRequirements(): Sequence<FlagToken> {
        return sequenceOf(getMainAconfigFlag()) + getSecondaryFlags()
    }

    /** Return all dependencies of this flag in pairs where [Pair.first] depends on [Pair.second] */
    inline fun getFlagDependencies(): Sequence<Pair<FlagToken, FlagToken>> {
        val mainAconfigFlag = getMainAconfigFlag()
        return getSecondaryFlags().map { mainAconfigFlag to it }
    }

    /**
     * Called to ensure code is only run when the flag is enabled. This protects users from the
     * unintended behaviors caused by accidentally running new logic, while also crashing on an eng
     * build to ensure that the refactor author catches issues in testing.
     */
    @JvmStatic
    inline fun isUnexpectedlyInLegacyMode() =
        RefactorFlagUtils.isUnexpectedlyInLegacyMode(isEnabled, DESCRIPTION)

    /**
     * Called to ensure code is only run when the flag is disabled. This will throw an exception if
     * the flag is enabled to ensure that the refactor author catches issues in testing.
     */
    @JvmStatic
    inline fun assertInLegacyMode() = RefactorFlagUtils.assertInLegacyMode(isEnabled, DESCRIPTION)
}

/**
 * Defines interface for classes that can check whether the scene container framework feature is
 * enabled.
 */
interface SceneContainerFlags {

    /** Returns `true` if the Scene Container Framework is enabled; `false` otherwise. */
    fun isEnabled(): Boolean

    /** Returns a developer-readable string that describes the current requirement list. */
    fun requirementDescription(): String
}

class SceneContainerFlagsImpl : SceneContainerFlags {

    override fun isEnabled(): Boolean {
        return SceneContainerFlag.isEnabled
    }

    override fun requirementDescription(): String {
        return buildString {
            SceneContainerFlag.getAllRequirements().forEach { requirement ->
                append('\n')
                append(if (requirement.isEnabled) "    [MET]" else "[NOT MET]")
                append(" ${requirement.name}")
            }
        }
    }
}

@Module
object SceneContainerFlagsModule {

    @Provides @SysUISingleton fun impl(): SceneContainerFlags = SceneContainerFlagsImpl()
}
