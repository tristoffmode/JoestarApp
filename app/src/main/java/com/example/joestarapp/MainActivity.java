package com.example.joestarapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        // Intégration de l'Intent pour naviguer vers le Compteur de pas
        button1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompteurActivity.class);
            startActivity(intent);
        });

        button2.setOnClickListener(v -> Toast.makeText(this, "Button 2 est cliqué", Toast.LENGTH_SHORT).show());
        button3.setOnClickListener(v -> Toast.makeText(this, "Button 3 est cliqué", Toast.LENGTH_SHORT).show());
    }
}