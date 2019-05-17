package com.getkeepsafe.taptargetviewsample;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.inflateMenu(R.menu.menu_main);
    toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp));

    // We load a drawable and create a location to show a tap target here
    // We need the display to get the width and height at this point in time
    final Display display = getWindowManager().getDefaultDisplay();
    // Load our little droid guy
    final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_android_black_24dp);
    // Tell our droid buddy where we want him to appear
    final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
    // Using deprecated methods makes you look way cool
    droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);

    final SpannableString sassyDesc = new SpannableString("It allows you to go back, sometimes");
    sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length() - "somtimes".length(), sassyDesc.length(), 0);

    // We have a sequence of targets, so lets build it!
    final TapTargetSequence sequence = new TapTargetSequence(this)
        .targets(
            // This tap target will target the back button, we just need to pass its containing toolbar
            TapTarget.forToolbarNavigationIcon(toolbar, "This is the back button", sassyDesc).id(1).drawCardShadow(true),
            // Likewise, this tap target will target the search button
            TapTarget.forToolbarMenuItem(toolbar, R.id.search, "This is a search icon", "As you can see, it has gotten pretty dark around here...")
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorAccent)
                .targetCircleColor(android.R.color.black)
                .transparentTarget(true)
                .textColor(android.R.color.black)
                    .revealDuration(250)
                    .pulseDuration(1000)
                .id(2).drawCardShadow(true),
            // You can also target the overflow button in your toolbar
            TapTarget.forToolbarOverflow(toolbar, "This will show more options", "But they're not useful :(").id(3).drawCardShadow(true),
            // This tap target will target our droid buddy at the given target rect
            TapTarget.forBounds(droidTarget, "Oh look!", "You can point to any part of the screen. You also can't cancel this one!")
                .cancelable(false)
                .icon(droid)
                .id(4).drawCardShadow(true)
        )
        .listener(new TapTargetSequence.Listener() {
          // This listener will tell us when interesting(tm) events happen in regards
          // to the sequence
          @Override
          public void onSequenceFinish() {
            ((TextView) findViewById(R.id.educated)).setText("Congratulations! You're educated now!");
          }

          @Override
          public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
            Log.d("TapTargetView", "Clicked on " + lastTarget.id());
          }

          @Override
          public void onSequenceCanceled(TapTarget lastTarget) {
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Uh oh")
                .setMessage("You canceled the sequence")
                .setPositiveButton("Oops", null).show();
            TapTargetView.showFor(dialog,
                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
                    .cancelable(false).drawCardShadow(true)
                    .tintTarget(false), new TapTargetView.Listener() {
                  @Override
                  public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    dialog.dismiss();
                  }
                });
          }
        });

    // You don't always need a sequence, and for that there's a single time tap target
    final SpannableString spannedDesc = new SpannableString("منوی چپ منوی را میتوانید در هر زمان باز کنید شما می توانید تنظیمات خود را تغییر دهید، محتوای خود را آپلود کنید و غیره. \\n لطفا شرایط استفاده و سیاست حفظ حریم خصوصی را بخوانید.\\n در هر صورت، برای ارسال بازخورد احساس راحتی کنید.");
    spannedDesc.setSpan(new UnderlineSpan(), spannedDesc.length() - "TapTargetView".length(), spannedDesc.length(), 0);
    TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.fab), "Hello, world!", spannedDesc)
            .cancelable(true)
            .showDebugInfo(true)
            .dimColor(android.R.color.black)
            .drawShadow(true)
            .drawCardShadow(true)
                            .outerCircleColor(android.R.color.transparent)
                            .outerCircleAlpha(0)
            .cardColor(android.R.color.white)
                            .targetCircleColor(R.color.colorAccent)
            .transparentTarget(true)
            .textColor(android.R.color.black), new TapTargetView.Listener() {
      @Override
      public void onTargetClick(TapTargetView view) {
        super.onTargetClick(view);
        // .. which evidently starts the sequence we defined earlier
        sequence.start();
      }

      @Override
      public void onOuterCircleClick(TapTargetView view) {
        super.onOuterCircleClick(view);
        Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
        Log.d("TapTargetViewSample", "You dismissed me :(");
      }
    });
  }

  @Override protected void attachBaseContext(final Context newBase) {
    super.attachBaseContext(SampleApplication.setupRtl(newBase));
  }
}
