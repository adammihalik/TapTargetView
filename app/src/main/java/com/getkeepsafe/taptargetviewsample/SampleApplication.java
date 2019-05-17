package com.getkeepsafe.taptargetviewsample;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.facebook.stetho.Stetho;

import java.util.Locale;

public class SampleApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    setupRtl(getApplicationContext());
  }

  @Override protected void attachBaseContext(final Context base) {
    super.attachBaseContext(setupRtl(base));
  }

  @Override public void onConfigurationChanged(final Configuration newConfig) {
    super.onConfigurationChanged(setupRtl(getApplicationContext()).getResources().getConfiguration());
  }

  public static Context setupRtl(final Context base) {
    Locale locale = new Locale("fas");
    Locale.setDefault(locale);

    Resources resources = base.getResources();

    Configuration configuration = new Configuration(resources.getConfiguration());
    configuration.setLocale(locale);

    // it's workaround with using farsi locale for defining rtl layout direction
    // some locales (e.g. Dari (prs)) won't be displayed correctly without this explicit
    // setting of layout configuration
    configuration.setLayoutDirection(new Locale("fa"));

    // Its better to set locale even for current context resources for better compatibility
    resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    // Create a proper new context with language configuration
    return base.createConfigurationContext(configuration);
  }
}
