package com.segment.analytics.android.integrations.quantcast;

import android.app.Activity;
import android.app.Application;
import com.quantcast.measurement.service.QuantcastClient;
import com.segment.analytics.Analytics;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.segment.analytics.test.ScreenPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.segment.analytics.Analytics.LogLevel.VERBOSE;
import static com.segment.analytics.Utils.createTraits;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest(QuantcastClient.class) public class QuantcastTest {

  @Rule public PowerMockRule rule = new PowerMockRule();
  @Mock Application context;
  @Mock Analytics analytics;
  QuantcastIntegration integration;

  @Before public void setUp() {
    initMocks(this);
    PowerMockito.mockStatic(QuantcastClient.class);
    integration = new QuantcastIntegration("foo", Logger.with(VERBOSE));
  }

  @Test public void initialize() throws IllegalStateException {
    when(analytics.logger("Quantcast")).thenReturn(Logger.with(VERBOSE));
    QuantcastIntegration.FACTORY.create(new ValueMap().putValue("apiKey", "foo"), analytics);

    verifyStatic();
    QuantcastClient.enableLogging(true);
  }

  @Test public void activityStart() {
    Activity activity = mock(Activity.class);
    integration.onActivityStarted(activity);

    verifyStatic();
    QuantcastClient.activityStart(activity, "foo", null, null);
  }

  @Test public void activityStop() {
    Activity activity = mock(Activity.class);
    integration.onActivityStopped(activity);

    verifyStatic();
    QuantcastClient.activityStop();
  }

  @Test public void identify() {
    integration.identify(new IdentifyPayloadBuilder() //
        .traits(createTraits("bar")).build());

    verifyStatic();
    QuantcastClient.recordUserIdentifier("bar");
  }

  @Test public void track() {
    integration.track(new TrackPayloadBuilder().event("bar").build());

    verifyStatic();
    QuantcastClient.logEvent("bar");
  }

  @Test public void screen() {
    integration.screen(new ScreenPayloadBuilder().category("bar").build());
    verifyStatic();
    QuantcastClient.logEvent("Viewed bar Screen");
  }
}