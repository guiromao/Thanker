package co.thanker;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private final String STRING_NAME = "username-string";
    private TextView textWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        textWelcome = (TextView) findViewById(R.id.welcome_text);
        String name = getIntent().getStringExtra(STRING_NAME);

        textWelcome.setText("Welcome: " + name);
    }

    public void nextScreen(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
