package com.example.cordovacameraplugin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.provider.MediaStore;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import static androidx.core.content.FileProvider.getUriForFile;
import static com.canhub.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
//import com.canhub.cropper.CropImageActivity;
import static android.graphics.Color.RED;


public class MainActivity extends AppCompatActivity {
    private Uri fileUri;
    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 1001;
    final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button= (Button)findViewById(R.id.camera_btn);
        button.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    String fileName = System.currentTimeMillis() + ".jpg";

                    fileUri = getCacheImagePath(fileName);

//                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                         intent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true);
                         intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                         intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                         intent.putExtra("android.intent.extras.CAMERA_FACING", 1);

                        // Samsung
                         intent.putExtra("camerafacing", "front");
                         intent.putExtra("previous_mode", "front");

                        // Huawei
                         intent.putExtra("default_camera", "1");
                         intent.putExtra("default_mode", "com.huawei.camera2.mode.photo.PhotoMode");

                    } else {
                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    }


                    /////
//                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                    photoPickerIntent.setType("image/*");
//                    startActivityForResult(photoPickerIntent, PICK_IMAGE);
                }
            }
        });
    }

   @RequiresApi(api = Build.VERSION_CODES.M)
   public void captureImage(){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            String fileName = System.currentTimeMillis() + ".jpg";

            fileUri = getCacheImagePath(fileName);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            } else {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            }            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(MainActivity.this, getPackageName() + ".provider", image);
    }


    private void performCrop() {
        // take care of exceptions
        try {
//            CropImage.activity().setAllowFlipping(false);
//            CropImage.activity(fileUri).setAspectRatio(4,3)

          JSONArray arguments = new JSONArray();
            arguments.put("false");

//            String isLivePhoto = args.getString(0);
//
//            CropImage.activity(fileUri).setAllowFlipping(false).setAllowRotation(false).start(this);
            try{
                String isLivePhoto = arguments.getString(0);
//                System.out.println("TESTING"+arguments.getString(1));
                if(isLivePhoto == "true"){
//                    CropImage.getPickImageChooserIntent(this,android.provider.MediaStore.ACTION_IMAGE_CAPTURE,false,true);
                    CropImage.activity(fileUri).setAllowFlipping(false)
                            .setAllowRotation(false)
                            .start(this);

                }else{
                    CropImage.getPickImageChooserIntent(this,MediaStore.ACTION_IMAGE_CAPTURE,false,false);
                    CropImage.activity(fileUri).setAspectRatio(5,5).setAllowFlipping(false).setAllowRotation(false)
                            .start(this);
                }
            }catch( JSONException e){

            }

        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException  anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                /////
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, PICK_IMAGE);

            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                performCrop();
                switch (requestCode){
                    case PICK_IMAGE:
//                        Uri selectedImage = data.getData();
//                        fileUri = selectedImage;
//                        System.out.println(selectedImage);
                        performCrop();
                        break;
                }

            }
        }
        else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUriContent();

                ImageView cropImageView = (ImageView) findViewById(R.id.cropImageView);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , Uri.parse(String.valueOf(resultUri)));
                    cropImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}