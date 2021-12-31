package com.example.shoppinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ShoppingList.class);
        startActivity(intent);

//        setContentView(R.layout.activity_main);
//        Button shopListBtn = findViewById(R.id.shoppingListBtn);
//        Button shopNaviBtn = findViewById(R.id.storeBtn);
//
//        shopListBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(this, ShoppingList.class);
//            startActivity(intent);
//        });
//
//        shopNaviBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(this, Shop_navi.class);
//            startActivity(intent);
//        });

    }
}