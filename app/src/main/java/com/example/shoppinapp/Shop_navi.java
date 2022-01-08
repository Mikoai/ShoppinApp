package com.example.shoppinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shoppinapp.entity.LUT;

import java.util.List;

public class Shop_navi extends AppCompatActivity {

    List<Integer> listOfCategories = null;
    LUT lut = new LUT();
    Navigator navi;
    Toast t;
    ImageView shopImage;
    Bitmap bitmap;
    Canvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_navi);
        Button navigateBtn = findViewById(R.id.navigateBtn);
        Button listBtn = findViewById(R.id.listBtn);
        shopImage = findViewById(R.id.shopImg);

        navigateBtn.setOnClickListener(v -> {
            navigate();
        });

        listBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, ShoppingList.class);
            startActivity(intent);
            finish();
        });
    }

    private void navigate(){
        listOfCategories = lut.getArrayItemsCategoriesIds();

        bitmap = Bitmap.createBitmap(shopImage.getWidth(), shopImage.getHeight(),  Bitmap.Config.RGB_565);
        canvas  = new Canvas(bitmap);

        makeToast("Wyznaczanie trasy...");
        navi = new Navigator(listOfCategories, shopImage, canvas, bitmap);

        shopImage.setImageBitmap(navi.navigate());
    }

    public void makeToast(String s){
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ShoppingList.class);
        startActivity(intent);
        finish();
    }
}