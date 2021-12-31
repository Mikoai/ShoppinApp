package com.example.shoppinapp;

import static com.example.shoppinapp.ShoppingList.detectedItem;
import static com.example.shoppinapp.ShoppingList.isDetected;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ObjectDetector {

    private Interpreter interpreter;
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE = 3; //RGB
    private int IMAGE_MEAN = 0;
    private float IMAGE_STD = 255.0f;

    //for GPU
    private GpuDelegate gpuDelegate;
    private int height = 0;
    private int width = 0;

    boolean foundItem = false;

    ObjectDetector(AssetManager assetManager, String modelPath, String labelPath, int inputSize) throws IOException{
        INPUT_SIZE = inputSize;

        //gpu or cpu // no. of threads
        Interpreter.Options options = new Interpreter.Options();
        gpuDelegate=new GpuDelegate();
        options.addDelegate(gpuDelegate);
        options.setNumThreads(8);

        //load model
        interpreter = new Interpreter(loadModelFile(assetManager,modelPath),options);

        //load labelmap
        labelList = loadLabelList(assetManager,labelPath);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;

        while ((line = reader.readLine()) != null){
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        // use to get description of file
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset  = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }

    public Mat recognizeImage(Mat mat_image){
        // Rotate original image by 90 degree get get portrait frame

        Mat rotated_mat_image = new Mat();

        Mat a = mat_image.t();
        Core.flip(a,rotated_mat_image,1);
        // Release mat
        a.release();

        // convert it to bitmap
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(rotated_mat_image.cols(), rotated_mat_image.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rotated_mat_image, bitmap);
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        // scale the bitmap to input size of model
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

        // convert bitmap to bytebuffer
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);

        // defining output
        //  float[][][]result = new float[1][10][4];
        Object[] input = new Object[1];
        input[0] = byteBuffer;

        Map<Integer, Object> output_map = new TreeMap<>();

        float[][][]boxes  = new float[1][10][4];
        // 4: there coordinate in image
        float[][] scores = new float[1][10];
        // stores scores of 10 object
        float[][] classes = new float[1][10];
        // stores class of object

        // add it to object_map;
        output_map.put(0, boxes);
        output_map.put(1, classes);
        output_map.put(2, scores);

        // predict
        interpreter.runForMultipleInputsOutputs(input, output_map);

        Object value = output_map.get(0);
        Object Object_class = output_map.get(1);
        Object score = output_map.get(2);

        // loop through each object
        for (int i = 0; i < 10; i++){
            float class_value = (float) Array.get(Array.get(Object_class,0),i);
            float score_value = (float) Array.get(Array.get(score,0),i);

            // define threshold for score
            if(score_value > 0.8){
                Object box1 = Array.get(Array.get(value,0),i);
                // multiplying it with original height and width of frame

                float top = (float) Array.get(box1,0) * height;
                float left = (float) Array.get(box1,1) * width;
                float bottom = (float) Array.get(box1,2) * height;
                float right = (float) Array.get(box1,3) * width;

                Imgproc.rectangle(rotated_mat_image,new Point(left,top),new Point(right,bottom),new Scalar(0, 255, 0, 255),2);
                // write text on frame
                Imgproc.putText(rotated_mat_image,labelList.get((int) class_value) + " " + ((int) (score_value * 100)) + "%",new Point(left,top),3,1,new Scalar(255, 0, 0, 255),2);
            }
            if(score_value > 0.9){
                ShoppingList.detectedItem = labelList.get((int) class_value);
                ShoppingList.isDetected = true;
                foundItem = true;
                //Log.i("Detector", "Score value: " + score_value + ", item: " + labelList.get((int) class_value) + ", isDetected: " + isDetected);
            }

        }
        // before returning rotate back by -90 degree
        Mat b = rotated_mat_image.t();
        Core.flip(b,mat_image,0);
        b.release();
        return mat_image;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        // some model input should be quant = 0  for some quant = 1
        int quant = 1;
        int size_images = INPUT_SIZE;
        if(quant == 0){
            byteBuffer = ByteBuffer.allocateDirect(1 * size_images*size_images*3);
        }
        else {
            byteBuffer = ByteBuffer.allocateDirect(4 * 1 * size_images*size_images*3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel = 0;

        for (int i = 0;i<size_images;++i){
            for (int j = 0;j<size_images;++j){
                final  int val = intValues[pixel++];
                if(quant == 0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF)) / 255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF)) / 255.0f);
                    byteBuffer.putFloat((((val) & 0xFF)) / 255.0f);
                }
            }
        }
        return byteBuffer;
    }

    public boolean addItem(){
        return foundItem;
    }

}
