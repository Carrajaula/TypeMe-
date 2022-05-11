package tadeas_toth.FastTypingApp.TypeME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class WelcomeScreen extends AppCompatActivity {
    private ConstraintLayout l1,l2;
    private TextView textView, textHighScore;
    private Button btnsub;
    private Animation uptodown,downtoup;
    private int HighScoreLevel;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        btnsub = findViewById(R.id.buttonsub);
        textView = findViewById(R.id.textViewName);
        textHighScore = findViewById(R.id.textHighScore);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");
        textView.setTypeface(font);
        textHighScore.setTypeface(font);

        SharedPreferences sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);

        HighScoreLevel=sharedPreferences.getInt("level", 0);

        if (HighScoreLevel>0){
            textHighScore.setText("Highest Level: " + HighScoreLevel);
        }
        else {
            textHighScore.setVisibility(View.GONE);
        }

        btnsub.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreen.this, MainActivity.class));
            }
        });

        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);

        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
    }
}