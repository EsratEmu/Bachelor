package com.salekur.bachelor.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salekur.bachelor.MainActivity;
import com.salekur.bachelor.R;
import com.salekur.bachelor.SupportActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileImageActivity extends AppCompatActivity {
    private static final int PICK_PROFILE_IMAGE_CODE = 1002;
    private CircleImageView XmlUserProfileImage;
    private MaterialButton XmlSelectImageButton, XmlUpdateImageButton;

    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;
    private StorageReference StorageRef;
    private Uri UserProfileImageUri;

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_image);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        StorageRef = FirebaseStorage.getInstance().getReference();

        LoadingBar = new ProgressDialog(this);

        XmlUserProfileImage = (CircleImageView) findViewById(R.id.update_profile_image_circular_image_profile_image);
        XmlSelectImageButton = (MaterialButton) findViewById(R.id.update_profile_image_material_button_select_image);
        XmlUpdateImageButton = (MaterialButton) findViewById(R.id.update_profile_image_material_button_update);

        XmlSelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAndroidVersionAndPermission();
            }
        });

        XmlUpdateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadUserProfileImage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            RetrieveUserProfileImage();
        }
    }

    private void RetrieveUserProfileImage() {
        RootRef.child("Users").child(CurrentUser.getUid()).child("information").child("profile_image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.getValue().toString();

                    if (!image.isEmpty()) {
                        Picasso.get().load(image).placeholder(R.drawable.boy_image).into(XmlUserProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            CropRequest(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            UserProfileImageUri = result.getUri();

            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), UserProfileImageUri);
                    XmlUserProfileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //.....
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PICK_PROFILE_IMAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PickProfileImageFromLibrary();
        } else {
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckAndroidVersionAndPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PICK_PROFILE_IMAGE_CODE);
            } catch (Exception e) {
                //......
            }
        } else {
            PickProfileImageFromLibrary();
        }
    }

    private void PickProfileImageFromLibrary() {
        CropImage.startPickImageActivity(this);
    }

    private void CropRequest(Uri uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setCropShape(CropImageView.CropShape.OVAL).setMultiTouchEnabled(true).start(this);
    }

    private void UploadUserProfileImage() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(UserProfileImageUri).build();

        LoadingBar.setMessage("Uploading image");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        CurrentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StorageRef.child("images").child("profile_image").child(CurrentUser.getUid()).putFile(UserProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            LoadingBar.setMessage("Updating profile...");
                            LoadingBar.setCanceledOnTouchOutside(false);
                            StorageRef.child("images").child("profile_image").child(CurrentUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    RootRef.child("Users").child(CurrentUser.getUid()).child("information").child("profile_image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                LoadingBar.dismiss();
                                                SendUserToMainActivity();
                                            } else {
                                                LoadingBar.dismiss();
                                                Toast.makeText(UpdateProfileImageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            LoadingBar.dismiss();
                            Toast.makeText(UpdateProfileImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            LoadingBar.setMessage("Uploading " + (int)progress + "%");
                        }
                    });
                } else {
                    Toast.makeText(UpdateProfileImageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(UpdateProfileImageActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(UpdateProfileImageActivity.this, MainActivity.class);
        startActivity(MainIntent);
    }
}
