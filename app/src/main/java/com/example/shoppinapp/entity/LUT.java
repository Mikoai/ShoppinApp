package com.example.shoppinapp.entity;

import android.util.Log;

import com.example.shoppinapp.ShoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LUT {

    ArrayList<String> idOfFields = new ArrayList<>();

    //File catFile = new File("files/categoryOfItems.csv");

    public LUT(){
        idOfFields.add("");
        idOfFields.add("X");
        idOfFields.add("Pieczywo");
        idOfFields.add("Słodycze");
        idOfFields.add("Owoce");
        idOfFields.add("Warzywa");
        idOfFields.add("Nabiał");
        idOfFields.add("Mięso");
        idOfFields.add("Alkohol");
        idOfFields.add("Napoje");
        idOfFields.add("Higiena");
        idOfFields.add("Mrożone");
        idOfFields.add("Przyprawy");
    }

    public String getCategoryOfItem(Item item){
        String categories = "chleb,bułka,bagietka\n" +
                "cukierki,batoniki,ciasto,żelki\n" +
                "jabłko,banan,mandarynka,pomarańcza\n" +
                "pomidor,czosnek,papryka,ziemniak,cebula\n" +
                "ser żółty,mleko,śmietana,serek\n" +
                "szynka,wołowina,kurczak,schab\n" +
                "wódka,whiskey,szampan\n" +
                "woda,tymbark,pepsi,cola,sok\n" +
                "mydło,chusteczki,papier toaletowy,szczoteczki\n" +
                "lody,warzywa mrożone,pizza\n" +
                "pieprz,sól,cukier,mąka,proszek do pieczenia";

        String[] lines = categories.split("\n");
        int counter = 2;
        for (String line : lines) {
            String[] values = line.split(",");
            for(String str : values){
                if(str.equals(item.getName()) && !item.isDone){
                    return idOfFields.get(counter);
                }
            }
            counter++;
        }
        return "";
    }

//    public String getCategoryOfItem(String item){
//        String[] line;
//        int idCounter = 2;
//        try{
//            FileReader fr = new FileReader(catFile);
//            BufferedReader br = new BufferedReader(fr);
//
//            //go through each line of csv and search for item, then return category
//            while((line = br.readLine().split(",")) != null){
//                for (String str : line){
//                    if(str.equals(item)){
//                        return idOfFields.get(idCounter);
//                    }
//                }
//              idCounter++;
//            }
//        } catch(IOException ioe){
//            ioe.printStackTrace();
//        }
//
//        return "";
//    }

    public List<Integer> getArrayItemsCategoriesIds(){
        ArrayList<Integer> list = new ArrayList<>();
        List<Integer> listNoDubplicates = new ArrayList<>();

        for(Item i : ShoppingList.itemsListArray){
            if(!getCategoryOfItem(i).equals("")){
                list.add(idOfFields.indexOf(getCategoryOfItem(i)));
            }

            //Log.d("Category", idOfFields.indexOf(getCategoryOfItem(i.getName())) + " = " + getCategoryOfItem(i.getName()) + " - " + i.getName());
        }

        listNoDubplicates = list.stream().distinct().collect(Collectors.toList());

        return  listNoDubplicates;
    }

}
