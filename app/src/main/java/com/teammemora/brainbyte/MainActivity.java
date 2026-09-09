package com.teammemora.brainbyte;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final int VOICE_REQ = 44;
    private WebView webView;
    private TextToSpeech tts;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, this);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                applyPrototypePatch();
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new NativeBridge(), "BrainByteNative");
        webView.loadUrl("file:///android_asset/index.html");

        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
        }
    }

    private void applyPrototypePatch() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(getAssets().open("patch.js")));
            StringBuilder js = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) js.append(line).append('\n');
            r.close();
            webView.evaluateJavascript(js.toString(), null);
        } catch (Exception e) {
            Toast.makeText(this, "BrainByte UI patch could not load", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.ENGLISH);
            tts.setSpeechRate(0.9f);
        }
    }

    public class NativeBridge {
        @JavascriptInterface public void speak(String text) {
            runOnUiThread(() -> { if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "brainbyte"); });
        }

        @JavascriptInterface public void toast(String text) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show());
        }

        @JavascriptInterface public void startVoiceInput() {
            runOnUiThread(() -> {
                try {
                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to BrainByte");
                    startActivityForResult(i, VOICE_REQ);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Voice input is not available on this device", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface public void scheduleReminder(String id, String title, int minutes) {
            runOnUiThread(() -> schedule(id, title, minutes));
        }

        @JavascriptInterface public void cancelReminder(String id) {
            runOnUiThread(() -> cancel(id));
        }

        @JavascriptInterface public void testReminder(String title) {
            runOnUiThread(() -> {
                Intent i = new Intent(MainActivity.this, ReminderReceiver.class);
                i.putExtra("title", title == null || title.isEmpty() ? "BrainByte reminder test" : title);
                i.putExtra("id", String.valueOf(System.currentTimeMillis()));
                sendBroadcast(i);
            });
        }

        @JavascriptInterface public void googleLogin() {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Prototype Google login is enabled. Firebase can be added later.", Toast.LENGTH_LONG).show());
        }
    }

    private int requestCode(String id) {
        try { return (int)(Long.parseLong(id) & 0x7fffffff); }
        catch(Exception e) { return Math.abs(id.hashCode()); }
    }

    private PendingIntent reminderIntent(String id, String title) {
        Intent i = new Intent(this, ReminderReceiver.class);
        i.putExtra("title", title);
        i.putExtra("id", id);
        return PendingIntent.getBroadcast(this, requestCode(id), i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void schedule(String id, String title, int minutes) {
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        long when = System.currentTimeMillis() + Math.max(1, minutes) * 60_000L;
        PendingIntent pi = reminderIntent(id, title);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
                else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, when, pi);
            }
            Toast.makeText(this, "Reminder set", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            am.set(AlarmManager.RTC_WAKEUP, when, pi);
            Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancel(String id) {
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = reminderIntent(id, "BrainByte reminder");
        am.cancel(pi);
        pi.cancel();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_REQ && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String text = result.get(0).replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
                webView.evaluateJavascript("voiceResult('" + text + "')", null);
            }
        }
    }

    @Override public void onBackPressed() {
        if (webView != null) {
            webView.evaluateJavascript("(function(){var a=document.querySelector('.screen.active');if(a&&a.id!=='home'&&a.id!=='login'){if(window.bbShow){bbShow('home')}else if(window.show){show('home')}return 'handled'}return 'exit'})()", value -> {
                if (value != null && value.contains("handled")) return;
                super.onBackPressed();
            });
        } else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (tts != null) tts.shutdown();
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
