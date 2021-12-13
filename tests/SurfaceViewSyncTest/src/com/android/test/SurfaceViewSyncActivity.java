/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.test;

import android.annotation.NonNull;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.window.SurfaceSyncer;

/**
 * Test app that allows the user to resize the SurfaceView and have the new buffer sync with the
 * main window. This tests that {@link SurfaceSyncer} is working correctly.
 */
public class SurfaceViewSyncActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "SurfaceViewSyncActivity";

    private SurfaceView mSurfaceView;
    private boolean mLastExpanded = true;

    private RenderingThread mRenderingThread;

    private final SurfaceSyncer mSurfaceSyncer = new SurfaceSyncer();

    private Button mExpandButton;
    private Switch mEnableSyncSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfaceview_sync);
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);

        WindowManager windowManager = getWindowManager();
        WindowMetrics metrics = windowManager.getCurrentWindowMetrics();
        Rect bounds = metrics.getBounds();

        LinearLayout container = findViewById(R.id.container);
        mExpandButton = findViewById(R.id.expand_sv);
        mEnableSyncSwitch = findViewById(R.id.enable_sync_switch);
        mExpandButton.setOnClickListener(view -> updateSurfaceViewSize(bounds, container));

        mRenderingThread = new RenderingThread(mSurfaceView.getHolder());
    }

    private void updateSurfaceViewSize(Rect bounds, View container) {
        final float height;
        if (mLastExpanded) {
            height = bounds.height() / 2f;
            mExpandButton.setText("EXPAND SV");
        } else {
            height = bounds.height() / 1.5f;
            mExpandButton.setText("COLLAPSE SV");
        }
        mLastExpanded = !mLastExpanded;

        if (mEnableSyncSwitch.isChecked()) {
            int syncId = mSurfaceSyncer.setupSync(() -> { });
            mSurfaceSyncer.addToSync(syncId, mSurfaceView, frameCallback ->
                    mRenderingThread.setFrameCallback(frameCallback));
            mSurfaceSyncer.addToSync(syncId, container);
            mSurfaceSyncer.markSyncReady(syncId);
        } else {
            mRenderingThread.renderSlow();
        }

        ViewGroup.LayoutParams svParams = mSurfaceView.getLayoutParams();
        svParams.height = (int) height;
        mSurfaceView.setLayoutParams(svParams);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        final Canvas canvas = holder.lockCanvas();
        canvas.drawARGB(255, 100, 100, 100);
        holder.unlockCanvasAndPost(canvas);
        mRenderingThread.startRendering();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mRenderingThread.stopRendering();
    }

    private static class RenderingThread extends HandlerThread {
        private final SurfaceHolder mSurfaceHolder;
        private Handler mHandler;
        private SurfaceSyncer.SurfaceViewFrameCallback mFrameCallback;
        private boolean mRenderSlow;

        int mColorValue = 0;
        int mColorDelta = 10;

        RenderingThread(SurfaceHolder holder) {
            super("RenderingThread");
            mSurfaceHolder = holder;
        }

        public void setFrameCallback(SurfaceSyncer.SurfaceViewFrameCallback frameCallback) {
            if (mHandler != null) {
                mHandler.post(() -> {
                    mFrameCallback = frameCallback;
                    mRenderSlow = true;
                });
            }
        }

        public void renderSlow() {
            if (mHandler != null) {
                mHandler.post(() -> mRenderSlow = true);
            }
        }

        private final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mFrameCallback != null) {
                    mFrameCallback.onFrameStarted();
                }

                if (mRenderSlow) {
                    try {
                        // Long delay from start to finish to mimic slow draw
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    mRenderSlow = false;
                }

                mColorValue += mColorDelta;
                if (mColorValue > 245 || mColorValue < 10) {
                    mColorDelta *= -1;
                }

                Canvas c = mSurfaceHolder.lockCanvas();
                c.drawRGB(255, mColorValue, 255 - mColorValue);
                mSurfaceHolder.unlockCanvasAndPost(c);

                if (mFrameCallback != null) {
                    mFrameCallback.onFrameComplete();
                }
                mFrameCallback = null;

                mHandler.postDelayed(this, 50);
            }
        };

        public void startRendering() {
            start();
            mHandler = new Handler(getLooper());
            mHandler.post(mRunnable);
        }

        public void stopRendering() {
            if (mHandler != null) {
                mHandler.post(() -> mHandler.removeCallbacks(mRunnable));
            }
        }
    }
}
