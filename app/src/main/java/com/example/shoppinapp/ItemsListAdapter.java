package com.example.shoppinapp;

import static com.example.shoppinapp.ShoppingList.itemsListArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class ItemsListAdapter extends BaseAdapter {
    Context context;
    long doubleClickQualificationSpanInMillis = 200;
    long timestampLastClick = 0;
    Toast t;

    //Displaying reversed array for better usability,
    //to revert replace 'itemsListArray.size() - (position + 1)' with 'position'

    public ItemsListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return itemsListArray.size();
    }

    @Override
    public Object getItem(int position) {
        return  itemsListArray.get(itemsListArray.size() - (position + 1));
    }

    @Override
    public long getItemId(int position) {
        return itemsListArray.size() - (position + 1);
    }

    private class ViewHolder {
        ImageView checkMark;
        TextView itemName;
        EditText itemCountValue;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= new ViewHolder();

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.itemslist_item, null);

        holder.checkMark = convertView.findViewById(R.id.checkImg);
        holder.itemName = convertView.findViewById(R.id.itemNameText);
        holder.itemCountValue = convertView.findViewById(R.id.itemCount);

        //set values
        holder.itemName.setText(itemsListArray.get(itemsListArray.size() - (position + 1)).getName());
        holder.itemCountValue.setText(itemsListArray.get(itemsListArray.size() - (position + 1)).getQuantity());


        //set values, select ImageView as checkmark depending on item state isDone
        if (itemsListArray.get(itemsListArray.size() - (position + 1)).isDone()) holder.checkMark.setImageResource(R.drawable.ic_full_check);
        else holder.checkMark.setImageResource(R.drawable.ic_empty_check);

        holder.checkMark.setOnClickListener(v -> {
            if (itemsListArray.get(itemsListArray.size() - (position + 1)).isDone()) {
                itemsListArray.get(itemsListArray.size() - (position + 1)).setDone(false);
            }
            else {
                itemsListArray.get(itemsListArray.size() - (position + 1)).setDone(true);
            }
            notifyDataSetChanged();

        });

        //delete item on double click
        holder.itemName.setOnClickListener(v -> {
            if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
                makeToast(itemsListArray.get(itemsListArray.size() - (position + 1)).getName() + " deleted!");
                itemsListArray.remove(itemsListArray.size() - (position + 1));
                saveItems();
                notifyDataSetChanged();
            } else makeToast(itemsListArray.get(itemsListArray.size() - (position + 1)).getName());
            timestampLastClick = SystemClock.elapsedRealtime();
        });


        holder.itemCountValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    itemsListArray.get(itemsListArray.size() - (position + 1)).setQuantity(s.toString());
                    //makeToast("Saved " + s.toString());
                }
            }
        });

        return convertView;
    }

    public void makeToast(String s){
        if (t != null) t.cancel();
        t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        t.show();
    }

    public void saveItems(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("savedItems", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemsListArray);
        prefsEditor.putString("items", json);
        prefsEditor.apply();
    }
}
