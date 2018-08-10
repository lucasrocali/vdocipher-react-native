package com.vdocipher.rnbridge;

import android.widget.FrameLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.vdocipher.aegis.player.VdoPlayerView;

public class ReactVdoPlayerView extends FrameLayout {
    private static final String TAG = "ReactVdoPlayerView";

    private final VdoPlayerView playerView;
    private final VdoPlayerControlView playerControlView;

    public ReactVdoPlayerView(ThemedReactContext context) {
        super(context);

        playerView = new VdoPlayerView(context);
        playerControlView = new VdoPlayerControlView(context);

        FrameLayout.LayoutParams matchParent = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addView(playerView, matchParent);
        addView(playerControlView, matchParent);
    }
}
