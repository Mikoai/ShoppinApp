package com.example.shoppinapp;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Navigator {

    List<Integer> itemsCategories;
    List<Integer>left2navigate = new ArrayList<>();
    //File shopFile;
    int height = 14;
    int width = 9;
    int[][] shopLayout = new int[height][width];
    Dictionary categoriesPathPoint = new Hashtable();

    int[] currentPosition = {13, 7};
    int[] exitPosition = {13, 1};
    int[] lastPosition;

    ImageView shopImage;
    Bitmap bitmap;
    Canvas canvas;


    public Navigator(List<Integer> arlist, ImageView shopImage, Canvas canvas, Bitmap bitmap){
        itemsCategories = arlist;
        this.shopImage = shopImage;
        this.canvas = canvas;
        this.bitmap = bitmap;
        loadShop();
    }

    private void loadShop(){
        //starting point = [13][7], ending point = [13][1]
        String shop =   "0,0,0,0,6,0,0,0,0\n" +
                        "0,1,1,1,1,1,1,1,0\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "11,1,0,0,1,0,0,1,7\n" +
                        "0,1,10,12,1,5,4,1,0\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "0,1,1,1,1,1,1,1,0\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "8,1,9,9,1,3,3,1,2\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "0,1,0,0,1,0,0,1,0\n" +
                        "0,1,1,1,1,1,1,1,0";

        String[] lines = shop.split("\n");
        String[] values;

        //save shop to array[][]
        for (int y = 0; y < height; y++) {
            values = lines[y].split(",");
            for (int x = 0; x < width; x++) {
                shopLayout[y][x] = Integer.parseInt(values[x]);
            }
        }

        //if pointID is category, find closes path point and add to dict
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(shopLayout[y][x] > 1)
                    categoriesPathPoint.put(shopLayout[y][x], closestPathPointXY(new int[]{y, x}));
            }
        }

        for(int i : itemsCategories)
            left2navigate.add(i);

    }

    private int[] closestPathPointXY(int[] categoryXY){

        //check for border points
        if(categoryXY[0] == 0 || categoryXY[0] == height-1 || categoryXY[1] == 0 || categoryXY[1] == width-1)
        {
            //check for border Y
            if (categoryXY[1] == 0) {
                return new int[]{categoryXY[0], 1};
            } else if (categoryXY[1] == width - 1)
                return new int[]{categoryXY[0], width - 2};

            //check for border X
            if (categoryXY[0] == 0)
                return new int[]{1, categoryXY[1]};
            else if (categoryXY[0] == height - 1)
                return new int[]{height - 2, categoryXY[1]};
        } else{
            //check if surrounding points id == 1
            if(shopLayout[categoryXY[0]-1][categoryXY[1]] == 1) {
                return new int[]{categoryXY[0]-1, categoryXY[1]};
            } else if(shopLayout[categoryXY[0]+1][categoryXY[1]] == 1){
                return new int[]{categoryXY[0]+1, categoryXY[1]};
            } else if(shopLayout[categoryXY[0]][categoryXY[1]-1] == 1){
                return new int[]{categoryXY[0], categoryXY[1]-1};
            } else if(shopLayout[categoryXY[0]][categoryXY[1]+1] == 1){
                return new int[]{categoryXY[0], categoryXY[1]+1};
            }
        }

        return new int[]{0, 0};
    }

    private int manhattanDistance(int categoryId, int[] startingPoint){
        //return Manhattan distance between points

        int[] endPoint = (int[]) categoriesPathPoint.get(categoryId);

        int md = Math.abs(startingPoint[0] - endPoint[0]) + Math.abs(startingPoint[1] - endPoint[1]);

        //if endPoint is literally behind, add 6 (distance)
        if(md == 3){
            if(startingPoint[1] == endPoint[1]){
                return md;
            }
                md += 6;
        }

        return md;
    }

    private int[] nextClosestPoint(int[] currentPosition){
        int closestCategoryId = 0;
        int md = 999;

        for(int cat : left2navigate){
            int temp = manhattanDistance(cat, currentPosition);

            //if category point is the same as current one, skip it
            if(temp < md){
                md = temp;
                closestCategoryId = cat;
            }
        }

        left2navigate.remove(left2navigate.indexOf(closestCategoryId));
        return (int[]) categoriesPathPoint.get(closestCategoryId);
    }

    private void moveX(int[] position2be){
        lastPosition = new int[]{currentPosition[0], currentPosition[1]};

        if(Math.abs(currentPosition[1] - position2be[1]) % 3 == 0){
            if(currentPosition[0] == height-1 || currentPosition[0] == 1){
                lastPosition = new int[]{currentPosition[0], currentPosition[1]};
                currentPosition = new int[]{currentPosition[0], position2be[1]};
            } else {
                moveY(position2be);
                lastPosition = new int[]{currentPosition[0], currentPosition[1]};
                currentPosition = new int[]{currentPosition[0], position2be[1]};
            }
        } else{
            //Log.d("WTF", "Welp..."); //should never enter here
            //currentPosition = new int[]{currentPosition[0], position2be[1]};
        }
        drawLineOnMap(canvas, bitmap);
        //Log.d("Next line end X", currentPosition[0] + ", " + currentPosition[1]);
    }

    private void moveY(int[] position2be){
        lastPosition = new int[]{currentPosition[0], currentPosition[1]};

        if(currentPosition[1] == position2be[1]){
            //if X is same, move straight
            currentPosition = new int[]{currentPosition[0] - ((currentPosition[0] - position2be[0])), currentPosition[1]};
        } else if(currentPosition[0] - position2be[0] == -1 || currentPosition[0] - position2be[0] == 0){
            currentPosition = new int[]{(currentPosition[0] - (3 + (currentPosition[0] - position2be[0]))), currentPosition[1]};
        } else if(currentPosition[0] - position2be[0] == 1){
            currentPosition = new int[]{(currentPosition[0] - 3), currentPosition[1]};
        } else if(Math.abs(currentPosition[0] - position2be[0]) > 4){
            currentPosition = new int[]{7, currentPosition[1]};
        } else if(Math.abs(currentPosition[0] - position2be[0]) == 3 || Math.abs(currentPosition[0] - position2be[0]) == 2){
            currentPosition = new int[]{currentPosition[0] - (currentPosition[0] - position2be[0]), currentPosition[1]};
        }

        //if for some reason Y is out of boundries, adjust it
        if(currentPosition[0] < 1){
            currentPosition = new int[]{1, currentPosition[1]};
        }

        drawLineOnMap(canvas, bitmap);
        //Log.d("Next line end Y", currentPosition[0] + ", " + currentPosition[1]);
    }

    //for now return next point and not the path to it
    public void navigate2next(){

        int[] positionToBe = nextClosestPoint(currentPosition);

        //make path from currentPosition to positionToBe
        while(currentPosition[0] != positionToBe[0] || currentPosition[1] != positionToBe[1]){
            //first align on X axis, then on Y axis
            if(currentPosition[1] == positionToBe[1]){
                moveY(positionToBe);
            } else {
                moveX(positionToBe);
            }
        }
    }

    public void navigate2exit(){

        lastPosition = new int[]{currentPosition[0], currentPosition[1]};
        currentPosition = new int[]{exitPosition[0], currentPosition[1]};
        drawLineOnMap(canvas, bitmap);

        lastPosition = new int[]{currentPosition[0], currentPosition[1]};
        currentPosition = new int[]{currentPosition[0], exitPosition[1]};
        drawLineOnMap(canvas, bitmap);

        //draw BLUE circle for Cashout
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(50);

        canvas.drawCircle((float) ((float)shopImage.getWidth() / width * (currentPosition[1] + 0.5)),
                (float) ((float)shopImage.getHeight() / height * (currentPosition[0] + 0.3)),
                30, paint);
    }

    public Bitmap navigate(){
        shopImage.draw(canvas);

        for(int i = 0; i < itemsCategories.size(); i++){
            navigate2next();
            drawMarkOnMap();
            //Log.d("PitStop "+ (i + 1) + ": ", "" + currentPosition[0] + ", " + currentPosition[1]);
        }

        navigate2exit();

        //TODO: drawing!
        return this.bitmap;
    }

    private void drawLineOnMap(Canvas canvas, Bitmap bitmap){
        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(10);



        canvas.drawLine((float) ((float) shopImage.getWidth() / width * (lastPosition[1] + 0.5)),
                        (float) ((float)shopImage.getHeight() / height * (lastPosition[0] + 0.4)),
                        (float) ((float)shopImage.getWidth() / width * (currentPosition[1] + 0.5)),
                        (float) ((float)shopImage.getHeight() / height * (currentPosition[0] + 0.4)),
                        paint);
    }

    private void drawMarkOnMap(){
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(50);

        canvas.drawCircle((float) ((float)shopImage.getWidth() / width * (currentPosition[1] + 0.5)),
                (float) ((float)shopImage.getHeight() / height * (currentPosition[0] + 0.4)),
                30, paint);
    }


    //make navigate function and drawing function

//    private void loadShop(){
//        String[] line = null;
//        List<List<Integer>> shopLayout = new ArrayList<>();
//
//        try{
//            BufferedReader br = new BufferedReader(new FileReader(shopFile));
//
//            while((line = br.readLine().split(",")) != null) {
//                List<Integer> temp = null;
//                for (String str : line) {
//                    temp.add(Integer.parseInt(str));
//                }
//                shopLayout.add(temp);
//            }
//        } catch(IOException ioe){
//            ioe.printStackTrace();
//        }
//        width = shopLayout.get(0).size();
//        height = shopLayout.size();
//
//        this.shopLayout = new int[height][width];
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                this.shopLayout[i][j] = shopLayout.get(i).get(j);
//                //Log.d("TAB values", this.shopLayout[i][j] + " ");
//            }
//        }
//    }

//    private void moveX(int[] position2be){
//        //if dest. X is not current X AND next position (left/right) is 1, move there
//        //otherwise move on Y
//        if(currentPosition[1] > position2be[1]){
//            if(shopLayout[currentPosition[0]][currentPosition[1]-1] == 1){
//                currentPosition = new int[]{currentPosition[0], currentPosition[1]-1};
//            } else moveY(position2be);
//        } else if(currentPosition[1] < position2be[1]){
//            if(shopLayout[currentPosition[0]][currentPosition[1]+1] == 1){
//                currentPosition = new int[]{currentPosition[0], currentPosition[1]+1};
//            } else moveY(position2be);
//        }
//    }
//
//    private void moveY(int[] position2be){
//        //if dest. Y is not current Y AND next position (up/down) is 1, move there
//        if(currentPosition[0] > position2be[0]){
//            if(shopLayout[currentPosition[0]-1][currentPosition[1]] == 1){
//                currentPosition = new int[]{currentPosition[0]-1, currentPosition[1]};
//            }
//        } else if(currentPosition[0] < position2be[0]){
//            if(shopLayout[currentPosition[0]+1][currentPosition[1]] == 1){
//                currentPosition = new int[]{currentPosition[0]+1, currentPosition[1]};
//            }
//        }
//    }
    //    public void navigate2exit(){
//        currentPosition = new int[]{exitPosition[0], currentPosition[1]};
//        //draw line
//        currentPosition = new int[]{currentPosition[0], exitPosition[1]};
//        //draw line
//    }


}

//0,1,0,0,0,0,0,1,0