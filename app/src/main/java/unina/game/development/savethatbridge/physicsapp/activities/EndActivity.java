package unina.game.development.savethatbridge.physicsapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

import unina.game.development.savethatbridge.R;
import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;
import unina.game.development.savethatbridge.logic.impl.AndroidAudio;

public class EndActivity extends AppCompatActivity {
    private Music backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen and no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_end);

        // Setup sound
        setupSound();

        // Button to play the game again
        Button button = findViewById(R.id.playAgain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundMusic.pause();
                playGameAgain();
            }
        });
    }

    private void setupSound() {
        Audio audio = new AndroidAudio(this);
        this.backgroundMusic = audio.newMusic("congratulations.wav");
        this.backgroundMusic.setVolume(0.5f);
        this.backgroundMusic.setLooping(true);
        this.backgroundMusic.play();
    }

    private void playGameAgain() {
        Intent intent = new Intent(this, StartingActivity.class);
        startActivity(intent);
    }
}