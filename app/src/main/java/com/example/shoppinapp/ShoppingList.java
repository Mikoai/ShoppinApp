package com.example.shoppinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shoppinapp.entity.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.android.OpenCVLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {

    ListView listView;
    public static ArrayList<Item> itemsListArray = new ArrayList<>();
    ItemsListAdapter adapter;
    Toast t;
    EditText addItemTxt;
    ImageView addItemImg;
    ImageView addItemWithCamera;
    public static String detectedItem;
    public static boolean isDetected = false;

    static {
        if(OpenCVLoader.initDebug()){
            Log.d("ShoppingList: ","Opencv is loaded");
        }
        else {
            Log.d("ShoppingList: ","Opencv failed to load");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        addItemTxt = findViewById(R.id.addItemText);
        addItemImg = findViewById(R.id.addImg);
        addItemWithCamera = findViewById(R.id.addPhotoImg);
        listView = findViewById(R.id.listOfItems);
        adapter = new ItemsListAdapter(getApplicationContext());
        listView.setAdapter(adapter);

        //load saved items from SharedPrefs
        loadItems();

        addItemWithCamera.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraRecog.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        addItemImg.setOnClickListener(v -> {
            String newItemTxt = addItemTxt.getText().toString();
            if(newItemTxt.length() < 3) makeToast("Enter valid item");
            else {
                addItemToList(newItemTxt);
                addItemTxt.setText("");
            }
        });

        if(isDetected){
            addItemToList(detectedItem);
            isDetected = false;
            detectedItem = "";
        }

    }

    public void addItemToList(String item){
        itemsListArray.add(new Item(item));
        saveItems();
        adapter.notifyDataSetChanged();
        makeToast("Added: " + item);
    }

    public void makeToast(String s){
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

    public void saveItems(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("savedItems", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemsListArray);
        prefsEditor.putString("items", json);
        prefsEditor.apply();
    }

    private void loadItems(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("savedItems", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("items", null);
        Type type = new TypeToken<ArrayList<Item>>(){}.getType();

        itemsListArray = gson.fromJson(json, type);

        if(itemsListArray == null) itemsListArray = new ArrayList<>();
    }

    @Override
    public void onBackPressed(){
        saveItems();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}