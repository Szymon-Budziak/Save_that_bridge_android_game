package unina.game.development.savethatbridge.physicsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import unina.game.development.savethatbridge.R;
import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;
import unina.game.development.savethatbridge.logic.impl.AndroidAudio;

public class MainActivity extends AppCompatActivity {
    private Music backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen and no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        // Setup sound
        setupSound();
        // Button to start the game
        Button button = findViewById(R.id.startingButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundMusic.pause();
                openStartingActivity();
            }
        });
    }

    private void setupSound() {
        Audio audio = new AndroidAudio(this);
        this.backgroundMusic = audio.newMusic("mainBackground.wav");
        this.backgroundMusic.setVolume(0.7f);
        this.backgroundMusic.setLooping(true);
        this.backgroundMusic.play();
    }

    private void openStartingActivity() {
        Intent intent = new Intent(this, StartingActivity.class);
        startActivity(intent);
    }
}