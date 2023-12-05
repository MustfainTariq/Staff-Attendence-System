package com.project.i200770_i200608_project;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class register_face extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);

        if(checkCameraHardware(this)){
            System.out.println("Camera is available");

            mCamera = getFrontCameraInstance();
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            Button captureButton = (Button) findViewById(R.id.captureButton);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mCamera.takePicture(null, null, mPicture);

                        }
                    }
            );

        }
        else{
            System.out.println("Camera is not available");
        }

    }

    private boolean checkCameraHardware(Context context) {

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){

            return true;
        } else {

            return false;
        }
    }
    private static Camera getFrontCameraInstance() {
        Camera c = null;
        try {
            int cameraId = findFrontFacingCamera();
            if (cameraId != -1) {
                c = Camera.open(cameraId);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return c;
    }

    private static int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){

        }
        return c;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken: Picture taken callback");
            Toast.makeText(register_face.this, "Picture taken", Toast.LENGTH_SHORT).show();

            saveImageToFirebaseStorage(data);
        }
    };

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type, getApplicationContext()));
    }

    private static File getOutputMediaFile(int type, Context context) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaStorageDir;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + timeStamp + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyCameraApp");

            Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri insertUri = context.getContentResolver().insert(externalUri, values);

            return new File(insertUri.getPath());
        } else {

            mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void saveImageToFirebaseStorage(byte[] data) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images").child(userId).child(fileName);

            UploadTask uploadTask = storageRef.putBytes(data);
            Toast.makeText(register_face.this, "Uploading image to Firebase Storage", Toast.LENGTH_SHORT).show();
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    storageRef.getDownloadUrl().addOnCompleteListener(downloadTask -> {
                        if (downloadTask.isSuccessful()) {
                            Toast.makeText(register_face.this, "Image uploaded to Firebase Storage", Toast.LENGTH_SHORT).show();
                            String imageUrl = downloadTask.getResult().toString();

                            saveImageUrlToFirestore(userId, imageUrl);
                            mCamera.release();
                            Intent intent = new Intent(register_face.this, HomeScreen.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "Error getting image download URL: " + downloadTask.getException());
                        }
                    });
                } else {
                    Log.e(TAG, "Error uploading image to Firebase Storage: " + task.getException());
                }
            });
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(register_face.this, "Current user is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToFirestore(String userId, String imageUrl) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).update("ver_image", imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Image URL saved to Firestore");
                    } else {
                        Log.e(TAG, "Error saving image URL to Firestore: " + task.getException());
                    }
                });

    }

}