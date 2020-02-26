package com.curiocodes.i_volunteer.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curiocodes.i_volunteer.AddOn.General;
import com.curiocodes.i_volunteer.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1001;
    private CircleImageView mProfile;
    private Uri selectedImage;
    private EditText mName,mPhone,mPlace;
    private ProgressBar progress;
    private byte[] uploadBytes;
    private CheckBox isVol;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        //init
        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone);
        mPlace = findViewById(R.id.place);
        mProfile = findViewById(R.id.profile_image);
        progress = findViewById(R.id.my_progressBar);
        mAuth = FirebaseAuth.getInstance();
        isVol = findViewById(R.id.volunteer);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                String phone = mPhone.getText().toString();
                String place = mPlace.getText().toString();

                if (name.isEmpty()||phone.isEmpty()||place.isEmpty()){
                    if (name.isEmpty()){
                        mName.setError("Name is missing");
                    }
                    if (phone.isEmpty()){
                        mPhone.setError("Phone number is missing");
                    }
                    if (name.isEmpty()){
                        mPlace.setError("Place is missing");
                    }
                }else {
                    if (phone.length() < 10){
                        mPhone.setError("Invalid phone number");
                    }else {
                        if (selectedImage == null) {
                            General.toast(getApplicationContext(), "Please select an image");
                        } else {
                            progress.setVisibility(View.VISIBLE);
                            ImageResize imageResize = new ImageResize(null);
                            imageResize.execute(selectedImage);
                        }
                    }

                }
            }
        });

        findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE){
            selectedImage = data.getData();
            mProfile.setImageURI(selectedImage);
        }
    }

    //compression class
    public class ImageResize extends AsyncTask<Uri,Integer,byte[]> {

        Bitmap bitmap;

        public ImageResize(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {;
            if (bitmap==null){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uris[0]);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return getBytesFromBitmap(bitmap,10);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            uploadBytes = bytes;
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Uploads").child("profile")
                    .child(mAuth.getCurrentUser().getUid());
            UploadTask uploadTask = ref.putBytes(uploadBytes);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        Map<String,Object> map = new HashMap<>();
                        map.put("user_photo",downloadUri.toString());
                        map.put("name",mName.getText().toString());
                        map.put("phone",mPhone.getText().toString());
                        map.put("place",mPlace.getText().toString());
                        map.put("created",new Timestamp(System.currentTimeMillis()));
                        map.put("isVol",isVol.isChecked());
                        db.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progress.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(SetProfileActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    progress.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        // Handle failures
                        // ...
                        General.toast(SetProfileActivity.this,"failed");
                        progress.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap,int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }


}
