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
        Button btnChrono = findViewById(R.id.btnChrono);

        button1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,LabyrintheActivity.class);
            startActivity(intent);
        });

        // Intégration de l'Intent pour naviguer vers le Compteur de pas
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompteurActivity.class);
            startActivity(intent);
        });

        btnChrono.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChronometreActivity.class);
            intent.putExtra("source", "MainActivity");
            startActivity(intent);
        });
    }
}