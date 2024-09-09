package com.example.ecommerce.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.ecommerce.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {
    public static String saveImage(Uri imageUri, String TAG, Context appContext) {
        try {
            // Get the input stream from the selected image URI
            InputStream inputStream = appContext.getContentResolver().openInputStream(imageUri);

            // Define the directory and file name where the image will be saved
            File directory = new File(appContext.getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs(); // Create directory if it doesn't exist
            }

            File file = new File(directory, "TAG" + System.currentTimeMillis() + ".jpg");

            // Save the image using FileOutputStream
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public static void deleteImage(String filePath, String TAG) {
        try{
            if(filePath != null && !filePath.isEmpty()){
                File file = new File(filePath);
                if(file.exists()){
                    file.delete();
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}
