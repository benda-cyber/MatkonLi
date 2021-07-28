package com.matanhassin.matkonli.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.Model;
import com.matanhassin.matkonli.model.ModelStorage;
import com.matanhassin.matkonli.model.User;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class Edit_Profile_Fragment extends Fragment {
    View view;
    ImageView editProfileImage;
    EditText editUsername;
    Button saveChangesBtn;
    Uri profileImageUrl;
    Bitmap postImgBitmap;
    static int REQUEST_CODE = 1;

    public Edit_Profile_Fragment()
    {
        // Empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit__profile_, container, false);
        editProfileImage = view.findViewById(R.id.edit_profile_edit_photo_image_view);
        editUsername = view.findViewById(R.id.edit_profile_username_edit_text);
        saveChangesBtn = view.findViewById(R.id.edit_profile_save_changes_btn);

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        setEditProfileHints();

        return view;
    }

    private void setEditProfileHints() {
        if (User.getInstance().userprofileImageUrl != null) { Picasso.get().load(User.getInstance().userprofileImageUrl).noPlaceholder().into(editProfileImage); }
        editUsername.setHint(User.getInstance().username);
    }

    private void updateUserProfile() {
        final String username;

        if (editUsername.getText().toString() != null && !editUsername.getText().toString().equals(""))
            username = editUsername.getText().toString();

        else username = User.getInstance().username;

        if (profileImageUrl != null)
        {
            ModelStorage.uploadImage(postImgBitmap, new ModelStorage.Listener() {
                @Override
                public void onSuccess(String url)
                {
                    Model.instance.updateUserProfile(username, url,new Model.Listener<Boolean>()
                    {
                        @Override
                        public void onComplete(Boolean data)
                        {
                            Model.instance.setUserAppData(User.getInstance().userEmail);
                            NavController navCtrl = Navigation.findNavController(view);
                            navCtrl.navigateUp();
                            navCtrl.navigateUp();
                        }});
                }

                @Override
                public void onFail() { Snackbar.make(view, "Failed to edit profile", Snackbar.LENGTH_LONG).show(); }
            });
        }

        else {
            Model.instance.updateUserProfile(username,null, new Model.Listener<Boolean>() {
                @Override
                public void onComplete(Boolean data)
                {
                    Model.instance.setUserAppData(User.getInstance().userEmail);
                    NavController navCtrl = Navigation.findNavController(view);
                    navCtrl.navigateUp();
                    navCtrl.navigateUp();
                }});
        }
    }

    private void chooseImageFromGallery() {
        try{
            Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            openGalleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(openGalleryIntent, REQUEST_CODE);
        }
        catch (Exception e){
            Toast.makeText(getContext(), "Edit profile Page: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            profileImageUrl = data.getData();
            editProfileImage.setImageURI(profileImageUrl);
            postImgBitmap = uriToBitmap(profileImageUrl);
        }

        else { Toast.makeText(getContext(), "No image was selected", Toast.LENGTH_SHORT).show(); }
    }

    private Bitmap uriToBitmap(Uri selectedFileUri)
    {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}