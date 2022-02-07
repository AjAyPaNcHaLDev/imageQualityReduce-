package ajay.developer.camera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ajay.developer.camera.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button capturePic;
    public static final int REQUEST_IMAGE_CAPTURE=1;
    private String TAG = "TAGA";
    File photoFile = null;
    EditText inpFileName;
    String fileName;
    String imageFileName;
    File storageDir;
TextView rangeValue;
    int Quality;
    Button inpImage;
Intent myFileIntent;

SeekBar seekBar;
    public int getQuality() {
        return Quality;
    }

    public void setQuality(int quality) {
        Quality = quality;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myPermission();
    }




    private void dispatchTakePictureIntent() {
        imageView.setImageBitmap(null);
        setFileName(inpFileName.getText().toString());

        if(getFileName().isEmpty() ){
            Toast.makeText(MainActivity.this,"please fill file name", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e(TAG, "dispatchTakePictureIntent: " );
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                Log.e(TAG, "dispatchTakePictureIntent: inside resoleActivity," );
                // Create the File where the photo should go

                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "ajay.developer.camera.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Start the image capture intent to take photo
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
            Log.e(TAG, "dispatchTakePictureIntent: end" );
        }




    }



    String currentPhotoPath;

    private File createImageFile() throws IOException {
        Log.e(TAG, "createImageFile: " );
        // Create an image file name

        imageFileName =  getFileName()+"  __";
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image= File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.e(TAG, "createImageFile: end "+currentPhotoPath );



        return image;




    }


    public  void myPermission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                imageView=findViewById(R.id.image_view);
                capturePic=findViewById(R.id.capturePic);
                inpFileName=findViewById(R.id.inpFileName);
                seekBar=findViewById(R.id.inpseekBar);
                inpImage=findViewById(R.id._inpImage);
                rangeValue=findViewById(R.id.rangeValue);
                accessCamera();
                accessMemory();
                seekBar.setProgress(50);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        setQuality(progress);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
Toast.makeText(MainActivity.this,"Quality set "+getQuality()+"%",Toast.LENGTH_LONG).show();
                        rangeValue.setText("Drag to set Quality "+getQuality()+"%");
                    }
                });
                capturePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dispatchTakePictureIntent();
                    }
                });
                inpImage.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                imagePicker();
                            }
                        }
                );
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                capturePic=findViewById(R.id.capturePic);
                inpImage=findViewById(R.id._inpImage);
                capturePic.setText("Please allow all permissions");
                inpImage.setText("Please allow all permissions");
                capturePic.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myPermission();
                                capturePic.setText("Capture & Save ");
                                inpImage.setText("Click to Choose");
                            }
                        }
                );
                inpImage.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myPermission();
                                capturePic.setText("Capture & Save ");
                                inpImage.setText("Click to Choose");
                            }
                        }
                );
                Intent intent =new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package",getPackageName(),null);
                intent.setData(uri);
                accessCamera();
                accessMemory();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    public void accessCamera(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);}

    }
    public  void accessMemory(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new  String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },100);
        }
    }

    public  void  imagePicker(){
        setFileName(inpFileName.getText().toString());
        if(getFileName().isEmpty() ){
            Toast.makeText(MainActivity.this,"please fill file name", Toast.LENGTH_SHORT).show();
        }
        else {
        setFileName(inpFileName.getText().toString());
        myFileIntent  =new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("image/*");
        startActivityForResult(myFileIntent,10);
    }}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview

                imageView.setImageBitmap(takenImage);
                String yyyy = new SimpleDateFormat("yyyy",
                        Locale.getDefault()).format(new Date());
                String mm = new SimpleDateFormat("MM",
                        Locale.getDefault()).format(new Date());
                String dd = new SimpleDateFormat("dd",
                        Locale.getDefault()).format(new Date());
                String file_name=getFileName();
                String root = Environment.getExternalStorageDirectory().getPath();
                File myDir = new File(root + "/Image Quality reduce/Pictures/");
                myDir.mkdirs();
                Log.e(TAG, "onactivity function: end "+currentPhotoPath );
                if(myDir.exists()){

                    String fname = file_name+".jpeg";
                    File file = new File (myDir, fname);


                    if(file.exists()){

                        AlertDialog.Builder reName=new AlertDialog.Builder(MainActivity.this);
                        reName.setTitle("Alert");
                        reName.setMessage("You went to " +getFileName()+" reCapture picture ?"+currentPhotoPath+ imageFileName);
                        reName.setCancelable(false);

                        reName.setPositiveButton("Yes reCapture",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        file.delete ();
                                        try {

                                            FileOutputStream out = new FileOutputStream(file);
                                            takenImage.compress(Bitmap.CompressFormat.JPEG, getQuality(), out);
                                            out.flush();
                                            out.close();
                                            imageView.setImageBitmap(takenImage);
                                            Toast.makeText(MainActivity.this,getFileName()+ "re recapture successfully Saved", Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }


                        );

                        reName.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alr=reName.create();
                        alr.show();

                    }
                    if(!file.exists()){

                        try {
                            Toast.makeText(MainActivity.this,"try block enter", Toast.LENGTH_LONG).show();
                            FileOutputStream out = new FileOutputStream(file);
                            takenImage.compress(Bitmap.CompressFormat.JPEG, getQuality(), out);
                            out.flush();
                            out.close();
                            imageView.setImageBitmap(takenImage);
                            Toast.makeText(MainActivity.this,getFileName()+ " successfully Saved", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }




                }else{

                    AlertDialog.Builder dirNotFound  =new AlertDialog.Builder(MainActivity.this);
                    dirNotFound.setMessage("a Technical error  while create Folder !\n"+myDir);
                    dirNotFound.setTitle("Opps");
                    dirNotFound.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alr=dirNotFound.create();
                    alr.show();

                }

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
            File del = new File (currentPhotoPath);
            del.delete();
        }
        if(requestCode==10){
if(resultCode==RESULT_OK){

    AlertDialog.Builder Change_quality=new AlertDialog.Builder(MainActivity.this);
    imageView.setImageURI(data.getData());
    Change_quality.setTitle("Change quality of this picture");
    Change_quality.setMessage(data.getData().getPath());
    Change_quality.setCancelable(false);
    Change_quality.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            try{
                InputStream inputStream=getContentResolver().openInputStream(data.getData());

                Bitmap tempImage=BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(tempImage);

                try {
                    String root = Environment.getExternalStorageDirectory().getPath();
                    File myDir = new File(root + "/Image Quality reduce/Pictures/");
                    File file = new File (myDir, getFileName()+".jpeg");
                    FileOutputStream out = new FileOutputStream(file);
                    tempImage.compress(Bitmap.CompressFormat.JPEG, getQuality(), out);
                    out.flush();
                    out.close();
                    imageView.setImageBitmap(tempImage);
                    Toast.makeText(MainActivity.this,getFileName()+ " capture successfully Saved", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_LONG).show();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    });


    Change_quality.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            imageView.setImageURI(null);
            dialog.cancel();
        }
    });

    AlertDialog ChangeQuality=Change_quality.create();
    ChangeQuality.show();

}

        }




    }




}