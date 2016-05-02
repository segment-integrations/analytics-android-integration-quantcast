package com.segment.analytics.android.integrations.quantcast;

import android.app.Activity;
import com.quantcast.measurement.service.QuantcastClient;
import com.segment.analytics.Analytics;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;

import static com.segment.analytics.Analytics.LogLevel.VERBOSE;

/**
 * Quantcast is an audience measurement tool that captures demographic and traffic data about the
 * visitors to your site, to make sure your ads are targeted at the right people.
 *
 * @see <a href="https://www.quantcast.com/">Quantcast</a>
 * @see <a href="https://segment.com/docs/integrations/quantcast/">Quantcast Integration</a>
 * @see <a href="https://github.com/quantcast/android-measurement#quantcast-android-sdk">Quantcast
 * Android SDK</a>
 */
public class QuantcastIntegration extends Integration<Void> {
  private static final String QUANTCAST_KEY = "Quantcast";
  private static final String VIEWED_EVENT_FORMAT = "Viewed %s Screen";

  public static final Factory FACTORY = new Factory() {
    @Override public Integration<?> create(ValueMap settings, Analytics analytics) {
      String apiKey = settings.getString("apiKey");
      Logger logger = analytics.logger(QUANTCAST_KEY);
      if (logger.logLevel == VERBOSE) {
        logger.verbose("QuantcastClient.enableLogging(true);");
        QuantcastClient.enableLogging(true);
      }
      return new QuantcastIntegration(apiKey, logger);
    }

    @Override public String key() {
      return QUANTCAST_KEY;
    }
  };

  final String apiKey;
  final Logger logger;

  public QuantcastIntegration(String apiKey, Logger logger) {
    this.apiKey = apiKey;
    this.logger = logger;
  }

  @Override public void onActivityStarted(Activity activity) {
    super.onActivityStarted(activity);
    logger.verbose("QuantcastClient.activityStart(activity, %s, null, null);", apiKey);
    QuantcastClient.activityStart(activity, apiKey, null, null);
  }

  @Override public void onActivityStopped(Activity activity) {
    super.onActivityStopped(activity);
    logger.verbose("QuantcastClient.activityStop();");
    QuantcastClient.activityStop();
  }

  @Override public void identify(IdentifyPayload identify) {
    super.identify(identify);
    String userId = identify.userId();
    logger.verbose("QuantcastClient.recordUserIdentifier(%s);", userId);
    QuantcastClient.recordUserIdentifier(userId);
  }

  @Override public void screen(ScreenPayload screen) {
    super.screen(screen);
    logEvent(String.format(VIEWED_EVENT_FORMAT, screen.event()));
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    logEvent(track.event());
  }

  private void logEvent(String event) {
    logger.verbose("QuantcastClient.logEvent(%s);", event);
    QuantcastClient.logEvent(event);
  }
}
