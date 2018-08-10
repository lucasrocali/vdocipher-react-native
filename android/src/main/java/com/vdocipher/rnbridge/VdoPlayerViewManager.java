package com.vdocipher.rnbridge;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class VdoPlayerViewManager extends SimpleViewManager<ReactVdoPlayerView> {
    private static final String TAG = "VdoPlayerViewManager";

    private static final String REACT_CLASS = "RCTVdoPlayerView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public ReactVdoPlayerView createViewInstance(ThemedReactContext context) {
        return new ReactVdoPlayerView(context);
    }
}
