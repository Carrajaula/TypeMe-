package tadeas_toth.FastTypingApp.TypeME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity<rewardedVideoAdListener> extends AppCompatActivity implements RewardedVideoAdListener
{
    private int HighScoreSpeed = 0, HighScoreLevel = 0, level = 1, wordCount, countdownPeriod, playPeriod, randomNum = 0, mCurrentPage;
    private TextView textView, textViewResult, textViewTimeLeft, textViewOverlaySpeed, textViewOverlayLevel, textViewHighScoreLevel, textViewHighScoreSpeed; //,parameterName;
    private Button btnsub, buttonSettings, buttonMusic, buttonResume, buttonTutorial, buttonPrevious, buttonNext, buttonClose, AdRewardButton;
    private ConstraintLayout constraintLayoutGameOver, constraintLayoutPause, buttonsTutorial, MainActivityCons;
    private EditText editText2;
    private String words[], guessWord, generatedWord;
    private ArrayList<String> typedWords;
    private Handler handler;
    private boolean isStarted = false, keyboardHidden = false, Music = false, gamePaused = false, doubleBackToExitPressedOnce = false, MusicOnRestart = false, advertisementRestart = false;
    private MediaPlayer mediaPlayer;
    private ViewPager viewPager;
    private SlideAdapter myadapter;
    private TextView[] mDots;
    private LinearLayout mDotLayout;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean firstStart = true;
    private int adLeaveCheck = 0; //Declare a global variable
    private String[] dictionaries = {"dictionary1.txt", "dictionary2.txt", "dictionary3.txt", "dictionary4.txt", "dictionary5.txt", "dictionary6.txt", "dictionary7.txt", "dictionary8.txt",
    "dictionary9.txt", "dictionary10.txt", "words.txt"};

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void addDotsIndicator(int position){

        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for(int i=0; i<mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.BLACK);

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length >0){
            mDots[position].setTextColor(Color.WHITE);
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int i, float v, int i1) {
            //aktualne je aplikacia bez scrollu
        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);
            mCurrentPage = i;

            if(i==0){
                buttonNext.setEnabled(true);
                buttonPrevious.setEnabled(false);
                buttonPrevious.setVisibility(View.INVISIBLE);

                buttonNext.setText("Next");
                buttonPrevious.setText("");
            }
            else if(i == mDots.length - 1){
                buttonNext.setEnabled(false);
                buttonPrevious.setEnabled(true);
                buttonNext.setVisibility(View.INVISIBLE);
                buttonPrevious.setVisibility(View.VISIBLE);

                buttonNext.setText("");
                buttonPrevious.setText("Previous");
            }
            else{
                buttonNext.setEnabled(true);
                buttonPrevious.setEnabled(true);
                buttonNext.setVisibility(View.VISIBLE);
                buttonPrevious.setVisibility(View.VISIBLE);

                buttonNext.setText("Next");
                buttonPrevious.setText("Previous");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //introduce first time app users to the application (display tutorial, then set bool to true, so it's not displayed again on the start)
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);

        SharedPreferences musicPref = getSharedPreferences("save", MODE_PRIVATE);
        MusicOnRestart = musicPref.getBoolean("music", true);

        //declaration
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        mDotLayout = findViewById(R.id.DotLayout);
        myadapter = new SlideAdapter(this);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

        MobileAds.initialize(this, "ca-app-pub-5353979083915506/3517573764");

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        countdownPeriod = 10;
        playPeriod = 10;
        textView = findViewById(R.id.textView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewHighScoreLevel = findViewById(R.id.textViewHighScoreLevel);
        textViewHighScoreSpeed = findViewById(R.id.textViewHighScoreTypingSpeed);
        editText2 = findViewById(R.id.editText2);
        editText2.setFocusableInTouchMode(true);
        editText2.requestFocus();
        textViewTimeLeft = findViewById(R.id.textViewTimeLeft);
        textViewOverlaySpeed = findViewById(R.id.textViewOverlaySpeed);
        textViewOverlayLevel = findViewById(R.id.textViewOverlayLevelReached);
        constraintLayoutGameOver = findViewById(R.id.GameOver);
        constraintLayoutPause = findViewById(R.id.Pause);
        MainActivityCons = findViewById(R.id.mainActivity);
        buttonsTutorial = findViewById(R.id.buttonsTutorial);
        handler = new Handler();
        String text;
        typedWords = new ArrayList<>();
        btnsub = findViewById(R.id.buttonsub);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonMusic = findViewById(R.id.buttonMusic);
        buttonResume = findViewById(R.id.buttonResume);
        buttonTutorial = findViewById(R.id.buttonTutorial);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonClose = findViewById(R.id.buttonClose);
        AdRewardButton = findViewById(R.id.AdRewardButton);

        buttonNext.setVisibility(View.INVISIBLE);
        buttonClose.setVisibility(View.INVISIBLE);
        buttonPrevious.setVisibility(View.INVISIBLE);
        mDotLayout.setVisibility(View.INVISIBLE);

        AdRewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    if(!mRewardedVideoAd.isLoaded()){
                        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
                        mRewardedVideoAd.setRewardedVideoAdListener(MainActivity.this);
                    }
                    else{
                        mRewardedVideoAd.show();
                    }
                }
                else
                    {
                        Toast.makeText(MainActivity.this, "The ad couldn't load. Check your connection please.", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        mediaPlayer= MediaPlayer.create(MainActivity.this,R.raw.forest);
        if(!MusicOnRestart && !mediaPlayer.isPlaying()){
            buttonMusic.setBackgroundResource(R.drawable.button_music_off);
            mediaPlayer.stop();
            mediaPlayer.setLooping(false);
        }else{
            buttonMusic.setBackgroundResource(R.drawable.button_music);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        //Music = !Music; // reverse

        btnsub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                hideKeyboardFrom();
            }
        });
        //calling for a pause / pause menu
        buttonSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                constraintLayoutPause.setVisibility(View.VISIBLE);
                if(keyboardHidden) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                gamePaused = !gamePaused;
                handler.removeCallbacks(runnable);
                buttonSettings.setVisibility(View.INVISIBLE);
                buttonMusic.setVisibility(View.INVISIBLE);
                editText2.clearFocus();
                editText2.setEnabled(false);
                editText2.setFocusable(false);
                editText2.setInputType(0);
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                firstStart = prefs.getBoolean("firstStart", true);
            }
        });

        buttonTutorial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    constraintLayoutPause.setVisibility(View.INVISIBLE);
                    viewPager.setAdapter(myadapter);
                    viewPager.bringToFront();
                    viewPager.setVisibility(View.VISIBLE);
                    buttonClose.setVisibility(View.VISIBLE);
                    buttonNext.setVisibility(View.VISIBLE);
                    mDotLayout.setVisibility(View.VISIBLE);
                    buttonMusic.setVisibility(View.INVISIBLE);
                    buttonSettings.setVisibility(View.INVISIBLE);
                    editText2.clearFocus();
                    editText2.setEnabled(false);
                    editText2.setFocusable(false);
                    editText2.setInputType(0);
                    buttonsTutorial.bringToFront();
                    mDotLayout.bringToFront();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewPager.setVisibility(View.INVISIBLE);
                buttonClose.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);
                mDotLayout.setVisibility(View.INVISIBLE);
                buttonMusic.setVisibility(View.VISIBLE);
                buttonSettings.setVisibility(View.VISIBLE);
                constraintLayoutPause.setVisibility(View.VISIBLE);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(mCurrentPage+1);
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(mCurrentPage-1);
            }
        });

        buttonResume.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                constraintLayoutPause.setVisibility(View.GONE);
                if(gamePaused && !firstStart){
                    handler.postDelayed(runnable, 1000);
                    gamePaused = !gamePaused;
                }
                buttonSettings.setVisibility(View.VISIBLE);
                buttonMusic.setVisibility(View.VISIBLE);
                editText2.setFocusableInTouchMode(true);
                editText2.setEnabled(true);
                editText2.setFocusable(true);
                editText2.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        buttonMusic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!Music && !mediaPlayer.isPlaying()){
                    v.setBackgroundResource(R.drawable.button_music);
                    try { //before calling start again a prepare() or prepareAsync() is needed
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    MusicOnRestart = true;
                }else{
                    v.setBackgroundResource(R.drawable.button_music_off);
                    mediaPlayer.stop();
                    mediaPlayer.setLooping(false);
                    MusicOnRestart = false;
                }

                Music = !Music; // reverse
            }
        });
        //end of declaration scope

        //method to check if tutorial should be displayed for first time users
        if(firstStart){
            displayFirsTimeTutorial();
        }
        //load language pack
        try {
            Random rand = new Random();
            int randomDictionaryNumber = rand.nextInt(11);
            InputStream is = getAssets().open(dictionaries[randomDictionaryNumber]);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);

            words = text.split("\\r?\\n");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //set focus into editText for easier start
        editText2.setFocusableInTouchMode(true);
        editText2.requestFocus();
        textViewResult.setText("Level: " + level);


        //declaration
        guessWordControl();
        generatedWord = guessWord;
        textView.setText(generatedWord);
        textViewTimeLeft.setText("" + countdownPeriod);
        //end of declaration scope

        //listener for change in textinput field
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentWord = editText2.getText().toString();

                //start Typing
                if (currentWord.length() == 1) {
                    if(!isStarted) {
                        handler.postDelayed(runnable, 1000);
                        isStarted = true;
                    }
                }

                //finished typing
                if (currentWord.equals(generatedWord)) {
                    level++;
                    wordCount += generatedWord.length();

                        if(level%10==0){
                            countdownPeriod+=5;
                            playPeriod += 5;
                            textViewTimeLeft.setText("" + countdownPeriod);
                        }
                        else {
                            countdownPeriod += 1;
                            playPeriod += 1;
                            textViewTimeLeft.setText("" + countdownPeriod);
                        }

                    textViewResult.setText("Level: " + level);

                    editText2.setText("");
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                    guessWordControl();
                    generatedWord = guessWord;
                    typedWords.add(generatedWord);
                    textView.setText(generatedWord);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void displayFirsTimeTutorial() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        viewPager.setAdapter(myadapter);
        viewPager.bringToFront();
        viewPager.setVisibility(View.VISIBLE);
        buttonClose.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);
        mDotLayout.setVisibility(View.VISIBLE);
        buttonMusic.setVisibility(View.INVISIBLE);
        buttonSettings.setVisibility(View.INVISIBLE);
        editText2.clearFocus();
        editText2.setEnabled(false);
        editText2.setFocusable(false);
        editText2.setInputType(0);
        buttonsTutorial.bringToFront();
        mDotLayout.bringToFront();

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

    //zabezpecuje, aby sa opakovane negenerovali slova, ktore uz hrac zadal
    private boolean findDuplicate(){
        if(typedWords.size() != 0) {
            for (int i = 0; i < typedWords.size(); i++) {
                if (typedWords.get(i).equals(guessWord)) {
                    return true;
                }
            }
        }
        return false;
    }

    //calculate wpm in characters per second
    private int calculateWPM(){
        int result = wordCount / playPeriod;
        Math.floor(result);
        return result;
    }

    //pick a word depending on level ( higher level, longer word) + zabezpecuje obmedzenie duplicutu slov
    private void guessWordControl(){
        randomNum = (int) (Math.random() * words.length);
        guessWord = words[randomNum];
            if (level < 5) {
                while (guessWord.length() > 3) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            } else if (level < 30) {
                while (guessWord.length() >= 6 || guessWord.length() < 4) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            } else if (level < 50) {
                while ((guessWord.length() >= 8 || guessWord.length() <6)) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            } else if (level < 80) {
                while ((guessWord.length() > 9 || guessWord.length() <= 7)) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            } else if (level < 120) {
                while (guessWord.length() >= 10 || guessWord.length() <8) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            }
            else if(level < 150){
                while (guessWord.length() >= 12 || guessWord.length() <10) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            }
            else if(level < 200){
                while (guessWord.length() >= 15 || guessWord.length() <12) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            }
            else if(level > 200){
                while (guessWord.length() < 14) {
                    randomNum = (int) (Math.random() * words.length);
                    guessWord = words[randomNum];
                }
            }
        if(findDuplicate()) {
            guessWordControl();
        }
    }

    //saves scores for later use in high score table
    private void save(){
        SharedPreferences sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("music", MusicOnRestart);

        if(level > sharedPreferences.getInt("level", 0)){
            editor.putInt("level", level);
        }
        if(calculateWPM() > sharedPreferences.getInt("characterSpeed", 0)){
            editor.putInt("characterSpeed", calculateWPM());
        }
        editor.apply();
    }

    //loads score for high score table
    private void load(){
        SharedPreferences sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);

        HighScoreLevel=sharedPreferences.getInt("level", 0);
        HighScoreSpeed=sharedPreferences.getInt("characterSpeed", 0);
    }

    //zabezpecuje zmenu farby textu ak cas je mensi alebo rovny 5 sekundam
    private boolean changeTextColor(){
        if(countdownPeriod <= 5)
        {
         return true;
        }
        return false;
    }

    //pri minimalizovani aplikacii z nedavnych aplikacii pozastavi hudbu
    @Override
    protected void onStop() {
        super.onStop();
        super.onPause();
        mediaPlayer.pause();
    }

    //pri odstraneni aplikacii z nedavnych aplikacii pozastavi hudbu
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.pause();
        SharedPreferences sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("music", true);
        editor.apply();
    }


    //zabezpecuje skryvanie klavesnice v pripade, ze je hra v mode PAUSE
    public void hideKeyboardFrom() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && !keyboardHidden) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                keyboardHidden = true;
            }
            else{
                if (imm != null) {
                    imm.showSoftInput(view, 0);
                    keyboardHidden = false;
                }
            }
        }
    }

    //timer tick, decreases time left by second on every tick (real life countdown)
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(changeTextColor()){
                textViewTimeLeft.setTextColor(Color.RED);
            }
            else{
                textViewTimeLeft.setTextColor(Color.BLACK);
            }
            countdownPeriod--;
            textViewTimeLeft.setText("" + countdownPeriod);
            if (countdownPeriod == 0) {
                //call addReward method here + set advertisementRestar to true!

                editText2.setEnabled(false);
                mediaPlayer.stop();
                mediaPlayer.setLooping(false);
                constraintLayoutGameOver.setVisibility(View.VISIBLE);
                editText2.setFocusable(false);
                editText2.setInputType(0);
                editText2.setEnabled(false);
                buttonMusic.setVisibility(View.GONE);
                buttonSettings.setVisibility(View.GONE);
                textViewResult.setVisibility(View.GONE);

                save();
                load();

                textViewHighScoreLevel.setText("Highest Level: " +  HighScoreLevel);
                textViewHighScoreSpeed.setText("Highest CPS: " +  HighScoreSpeed);

                textViewOverlaySpeed.setText("Your CPS is: " + String.valueOf(calculateWPM()));
                textViewOverlayLevel.setText("You have reached level: " + String.valueOf(level));
            }
            handler.postDelayed(runnable,1000);
            if (countdownPeriod == 0){
                handler.removeCallbacks(runnable);
            }
        }
    };

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-5353979083915506/3517573764",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();

        if(advertisementRestart) {

            handler.postDelayed(runnable, 1000);

            if(!MusicOnRestart && !mediaPlayer.isPlaying()){
                buttonMusic.setBackgroundResource(R.drawable.button_music_off);
                mediaPlayer.stop();
                mediaPlayer.setLooping(false);
            }else{
                buttonMusic.setBackgroundResource(R.drawable.button_music);
                try { //before calling start again a prepare() or prepareAsync() is needed
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
            constraintLayoutGameOver.setVisibility(View.GONE);
            AdRewardButton.setVisibility(View.GONE);
            buttonMusic.setVisibility(View.VISIBLE);
            buttonSettings.setVisibility(View.VISIBLE);
            textViewResult.setVisibility(View.VISIBLE);

            editText2.setEnabled(true);
            editText2.setFocusable(true);
            editText2.setEnabled(true);
            editText2.setFocusableInTouchMode(true);
            editText2.requestFocus();
            editText2.setText("");

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        countdownPeriod += 10;

        advertisementRestart = true;

        textViewTimeLeft.setTextColor(Color.BLACK);
        textViewTimeLeft.setText("" + countdownPeriod);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "Ad could not load", Toast.LENGTH_SHORT);
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
