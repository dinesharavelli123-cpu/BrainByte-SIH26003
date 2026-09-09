package com.teammemora.brainbyte;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final int PURPLE = Color.rgb(107, 78, 255);
    private static final int DARK = Color.rgb(37, 31, 57);
    private static final int BG = Color.rgb(247, 245, 255);
    private static final int CARD = Color.WHITE;
    private LinearLayout root;
    private TextToSpeech tts;
    private int level = 1;
    private int rounds = 0;
    private int totalCorrect = 0;
    private String userName = "Friend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, this);
        showSplash();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.ENGLISH);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }

    private void baseScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(28));
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));
        setContentView(scroll);
    }

    private void showSplash() {
        LinearLayout splash = new LinearLayout(this);
        splash.setOrientation(LinearLayout.VERTICAL);
        splash.setGravity(Gravity.CENTER);
        splash.setBackgroundColor(PURPLE);
        TextView icon = text("🧠", 72, Color.WHITE, true);
        TextView title = text("BrainByte", 36, Color.WHITE, true);
        TextView tag = text("Play • Remember • Connect", 17, Color.WHITE, false);
        tag.setAlpha(.82f);
        splash.addView(icon); splash.addView(title); splash.addView(tag);
        setContentView(splash);
        icon.setScaleX(.6f); icon.setScaleY(.6f); icon.setAlpha(0f);
        icon.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(650).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        title.setAlpha(0f); title.setTranslationY(dp(18));
        title.animate().alpha(1f).translationY(0).setStartDelay(250).setDuration(600).start();
        tag.setAlpha(0f); tag.animate().alpha(.82f).setStartDelay(500).setDuration(500).start();
        new Handler(Looper.getMainLooper()).postDelayed(this::showLanguage, 1500);
    }

    private void showLanguage() {
        baseScreen();
        spacer(34);
        addTitle("Choose your language");
        addBody("BrainByte can guide you with simple text and voice. More regional languages can be added as the platform grows.");
        cardButton("English", v -> showProfile());
        cardButton("తెలుగు", v -> { toast("Telugu selected for demo"); showProfile(); });
        cardButton("हिन्दी", v -> { toast("Hindi selected for demo"); showProfile(); });
        spacer(16);
        addMuted("SIH26003 • Team MEMORA");
    }

    private void showProfile() {
        baseScreen();
        addTitle("Welcome to BrainByte");
        addBody("A gentle setup helps us keep the experience simple and personalized.");
        TextView label = text("Your name", 16, DARK, true); root.addView(label);
        EditText name = new EditText(this);
        name.setHint("Enter name");
        name.setTextSize(20); name.setSingleLine(true);
        name.setPadding(dp(16), dp(12), dp(16), dp(12));
        root.addView(name, params(-1, dp(58), 0, 8, 0, 16));
        addSection("Why are you using BrainByte?");
        addBody("Daily cognitive activity • Memory assistance • Caregiver-supported engagement");
        primaryButton("Continue", v -> {
            String typed = name.getText().toString().trim();
            if (!typed.isEmpty()) userName = typed;
            showHome();
        });
        addMuted("BrainByte supports cognitive engagement and memory assistance. It is not a medical diagnostic or treatment tool.");
    }

    private void showHome() {
        baseScreen();
        addEyebrow("GOOD MORNING");
        addTitle("Hello, " + userName + " 👋");
        addBody("Ready for a gentle daily brain activity?");
        LinearLayout hero = card();
        hero.addView(text("TODAY'S ACTIVITY", 13, PURPLE, true));
        hero.addView(text("Object Recall", 26, DARK, true));
        hero.addView(text("Look • Remember • Choose", 16, Color.DKGRAY, false));
        Button start = button("Start activity", true); hero.addView(start, params(-1, dp(56), 0, 14, 0, 0));
        start.setOnClickListener(v -> showObjectRecallIntro());
        root.addView(hero, params(-1, -2, 0, 10, 0, 16));
        addSection("Explore");
        cardButton("🎮  Cognitive Games", v -> showGames());
        cardButton("💬  BrainByte Assistant", v -> showAssistant());
        cardButton("🧘  Calm & Grounding", v -> showCalm());
        cardButton("⏰  Reminders", v -> showReminders());
        cardButton("👨‍👩‍👧  Caregiver Dashboard", v -> showCaregiver());
        spacer(10);
        addMuted("Core activities are designed to remain lightweight and suitable for low-connectivity use.");
    }

    private void showGames() {
        baseScreen(); topBack("Cognitive Games", this::showHome);
        addBody("Short activities designed for simple, repeatable cognitive engagement.");
        gameCard("🧠", "Object Recall", "Remember everyday objects and identify them.", v -> showObjectRecallIntro());
        gameCard("🃏", "Memory Match", "Match pairs using visual memory.", v -> demoComing("Memory Match"));
        gameCard("🔢", "Sequence Recall", "Remember the order of a short sequence.", v -> demoComing("Sequence Recall"));
        gameCard("🔷", "Pattern Recognition", "Spot simple visual patterns.", v -> demoComing("Pattern Recognition"));
        gameCard("☀️", "Daily Recall", "A friendly daily routine reflection.", v -> demoComing("Daily Recall"));
    }

    private void showObjectRecallIntro() {
        baseScreen(); topBack("Object Recall", this::showHome);
        addTitle("Remember what you see");
        addBody("You will see a few familiar objects for a moment. Then choose the ones you remember.");
        LinearLayout c = card();
        c.addView(text("Adaptive level: " + level, 17, PURPLE, true));
        c.addView(text(level == 1 ? "3 objects" : level == 2 ? "4 objects" : "5 objects", 25, DARK, true));
        root.addView(c, params(-1, -2, 0, 12, 0, 18));
        primaryButton("I'm ready", v -> startRecallRound());
    }

    private void startRecallRound() {
        baseScreen();
        addEyebrow("MEMORIZE");
        addTitle("Look carefully…");
        List<String> pool = new ArrayList<>(Arrays.asList("☕ Cup", "🍎 Apple", "🔑 Key", "🌸 Flower", "🪔 Diya", "🥭 Mango", "🕰 Clock"));
        Collections.shuffle(pool);
        int count = level == 1 ? 3 : level == 2 ? 4 : 5;
        List<String> shown = new ArrayList<>(pool.subList(0, count));
        LinearLayout c = card();
        for (String item : shown) c.addView(text(item, 26, DARK, true));
        root.addView(c, params(-1, -2, 0, 10, 0, 12));
        TextView timer = text("Remember these objects", 17, PURPLE, true); root.addView(timer);
        new Handler(Looper.getMainLooper()).postDelayed(() -> askRecall(shown, pool), 2800);
    }

    private void askRecall(List<String> shown, List<String> pool) {
        baseScreen();
        addEyebrow("CHOOSE");
        addTitle("Which objects did you see?");
        List<String> options = new ArrayList<>(shown);
        for (String s : pool) if (!options.contains(s) && options.size() < shown.size() + 2) options.add(s);
        Collections.shuffle(options);
        final List<String> selected = new ArrayList<>();
        LinearLayout choiceBox = card();
        for (String item : options) {
            Button b = button(item, false);
            b.setOnClickListener(v -> {
                if (selected.contains(item)) { selected.remove(item); b.setText("○  " + item); }
                else { selected.add(item); b.setText("✓  " + item); }
            });
            b.setText("○  " + item);
            choiceBox.addView(b, params(-1, dp(54), 0, 5, 0, 5));
        }
        root.addView(choiceBox, params(-1, -2, 0, 8, 0, 12));
        primaryButton("Check my answer", v -> scoreRound(shown, selected));
    }

    private void scoreRound(List<String> shown, List<String> selected) {
        int correct = 0;
        for (String s : selected) if (shown.contains(s)) correct++;
        int falseSelections = 0;
        for (String s : selected) if (!shown.contains(s)) falseSelections++;
        int score = Math.max(0, Math.round(((correct - falseSelections) * 100f) / shown.size()));
        rounds++; totalCorrect += score;
        String adaptive;
        if (score >= 80) { if (level < 3) level++; adaptive = "Great job — the next round can be a little more challenging."; }
        else if (score < 50) { if (level > 1) level--; adaptive = "We'll make the next activity a little gentler."; }
        else adaptive = "We'll keep the next activity at a similar level.";
        baseScreen();
        addEyebrow("ACTIVITY COMPLETE");
        addTitle(score + "% accuracy");
        addBody(adaptive);
        LinearLayout c = card();
        c.addView(text("Adaptive engine", 14, PURPLE, true));
        c.addView(text("Next level: " + level, 23, DARK, true));
        c.addView(text("PLAY → MEASURE → ANALYZE → ADAPT → RECOMMEND", 14, Color.DKGRAY, false));
        root.addView(c, params(-1, -2, 0, 12, 0, 16));
        primaryButton("Play another round", v -> showObjectRecallIntro());
        secondaryButton("Back to home", v -> showHome());
        addMuted("A single score is never interpreted as dementia progression or a medical diagnosis.");
    }

    private void showAssistant() {
        baseScreen(); topBack("BrainByte Assistant", this::showHome);
        addTitle("How can I help today?");
        addBody("Try a simple request. The prototype demonstrates supportive interaction without making medical claims.");
        cardButton("🔊  Read today's activity aloud", v -> speak("Your memory activity for today is Object Recall. Take your time. There is no need to rush."));
        cardButton("🌿  Help me relax", v -> { speak("Let's try a calm breathing activity together."); showCalm(); });
        cardButton("📅  What should I do today?", v -> speak("Today you can try one short memory activity, take a break, and check your reminders."));
        LinearLayout note = card();
        note.addView(text("Supportive, not diagnostic", 17, PURPLE, true));
        note.addView(text("BrainByte can guide activities and reminders, but it should not replace qualified medical professionals.", 15, Color.DKGRAY, false));
        root.addView(note, params(-1, -2, 0, 14, 0, 0));
    }

    private void showCalm() {
        baseScreen(); topBack("Calm & Grounding", this::showHome);
        addTitle("5-4-3-2-1 Grounding");
        addBody("A gentle guided awareness activity. Move through each step at your own pace.");
        String[] steps = {"👀 Find 5 things you can see", "✋ Notice 4 things you can touch", "👂 Notice 3 things you can hear", "👃 Notice 2 things you can smell", "👅 Notice 1 thing you can taste"};
        for (String s : steps) cardButton(s, v -> toast("Completed ✓"));
        secondaryButton("🔊 Voice guide", v -> speak("Let's begin. Notice five things you can see around you. Take your time."));
    }

    private void showReminders() {
        baseScreen(); topBack("Reminders", this::showHome);
        addTitle("Your daily reminders");
        addBody("Simple routine support can be configured by the user or an authorized caregiver.");
        reminder("9:00 AM", "Morning routine", "Completed");
        reminder("4:00 PM", "BrainByte activity", "Upcoming");
        reminder("7:30 PM", "Family call", "Upcoming");
        primaryButton("+ Add demo reminder", v -> toast("Demo reminder added"));
    }

    private void showCaregiver() {
        baseScreen(); topBack("Caregiver Dashboard", this::showHome);
        addEyebrow("AUTHORIZED VIEW");
        addTitle(userName + "'s engagement");
        addBody("Caregivers see activity and engagement trends — not a diagnosis.");
        LinearLayout stats = card();
        stats.addView(text("Activities completed     " + Math.max(3, rounds), 18, DARK, true));
        stats.addView(text("Average accuracy          " + (rounds == 0 ? "78%" : (totalCorrect / rounds) + "%"), 18, DARK, true));
        stats.addView(text("Engagement this week      42 min", 18, DARK, true));
        stats.addView(text("Current adaptive level    " + level, 18, DARK, true));
        root.addView(stats, params(-1, -2, 0, 10, 0, 16));
        addSection("Weekly trend");
        progressRow("Mon", 70); progressRow("Tue", 82); progressRow("Wed", 64); progressRow("Thu", 88); progressRow("Today", rounds == 0 ? 78 : totalCorrect / rounds);
        addSection("Caregiver note");
        addBody("Keep sessions short and comfortable. BrainByte uses multi-session activity patterns for personalization and does not infer medical decline from one difficult day.");
    }

    private void reminder(String time, String title, String status) {
        LinearLayout c = card();
        c.addView(text(time, 14, PURPLE, true));
        c.addView(text(title, 20, DARK, true));
        c.addView(text(status, 14, Color.DKGRAY, false));
        root.addView(c, params(-1, -2, 0, 7, 0, 7));
    }

    private void progressRow(String label, int value) {
        TextView t = text(label + "   " + value + "%   " + bar(value), 16, DARK, false);
        root.addView(t, params(-1, -2, 0, 5, 0, 5));
    }

    private String bar(int value) {
        int n = Math.max(1, Math.min(10, value / 10));
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 10; i++) s.append(i < n ? "●" : "○");
        return s.toString();
    }

    private void demoComing(String name) {
        toast(name + " prototype screen ready for the next build");
    }

    private void speak(String message) {
        if (tts != null) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "brainbyte");
        toast("Voice guidance started");
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(18), dp(18), dp(18), dp(18));
        android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
        g.setColor(CARD); g.setCornerRadius(dp(20)); g.setStroke(dp(1), Color.rgb(232, 228, 246));
        c.setBackground(g); c.setElevation(dp(3));
        return c;
    }

    private void gameCard(String emoji, String title, String desc, View.OnClickListener click) {
        LinearLayout c = card();
        c.addView(text(emoji + "  " + title, 21, DARK, true));
        c.addView(text(desc, 15, Color.DKGRAY, false));
        Button b = button("Open", false); b.setOnClickListener(click);
        c.addView(b, params(-1, dp(50), 0, 10, 0, 0));
        root.addView(c, params(-1, -2, 0, 7, 0, 7));
    }

    private void topBack(String title, Runnable back) {
        Button b = button("←  " + title, false); b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); b.setOnClickListener(v -> back.run());
        root.addView(b, params(-1, dp(52), 0, 0, 0, 18));
    }

    private void addTitle(String s) { root.addView(text(s, 30, DARK, true), params(-1, -2, 0, 4, 0, 8)); }
    private void addSection(String s) { root.addView(text(s, 20, DARK, true), params(-1, -2, 0, 18, 0, 7)); }
    private void addEyebrow(String s) { root.addView(text(s, 13, PURPLE, true), params(-1, -2, 0, 0, 0, 2)); }
    private void addBody(String s) { root.addView(text(s, 17, Color.rgb(88, 83, 104), false), params(-1, -2, 0, 0, 0, 14)); }
    private void addMuted(String s) { TextView t = text(s, 13, Color.GRAY, false); t.setGravity(Gravity.CENTER); root.addView(t, params(-1, -2, 0, 12, 0, 0)); }

    private TextView text(String s, int sp, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(color); t.setLineSpacing(0, 1.14f);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String s, boolean primary) {
        Button b = new Button(this); b.setText(s); b.setTextSize(17); b.setAllCaps(false); b.setGravity(Gravity.CENTER);
        android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
        g.setCornerRadius(dp(16));
        if (primary) { g.setColor(PURPLE); b.setTextColor(Color.WHITE); }
        else { g.setColor(Color.WHITE); g.setStroke(dp(1), Color.rgb(215, 208, 241)); b.setTextColor(DARK); }
        b.setBackground(g); b.setPadding(dp(14), 0, dp(14), 0);
        return b;
    }

    private void primaryButton(String s, View.OnClickListener l) { Button b = button(s, true); b.setOnClickListener(l); root.addView(b, params(-1, dp(58), 0, 8, 0, 8)); }
    private void secondaryButton(String s, View.OnClickListener l) { Button b = button(s, false); b.setOnClickListener(l); root.addView(b, params(-1, dp(56), 0, 6, 0, 6)); }
    private void cardButton(String s, View.OnClickListener l) { Button b = button(s, false); b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); b.setOnClickListener(l); root.addView(b, params(-1, dp(62), 0, 6, 0, 6)); }
    private void spacer(int h) { View v = new View(this); root.addView(v, new LinearLayout.LayoutParams(1, dp(h))); }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    private LinearLayout.LayoutParams params(int w, int h, int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h); p.setMargins(dp(l), dp(t), dp(r), dp(b)); return p;
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
