package com.vdocipher.rnbridge;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.Map;

import javax.annotation.Nullable;

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

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        builder.put("onInitSuccess", MapBuilder.of("registrationName", "onInitSuccess"));
        builder.put("onInitFailure", MapBuilder.of("registrationName", "onInitFailure"));
        return builder.build();
    }
}
