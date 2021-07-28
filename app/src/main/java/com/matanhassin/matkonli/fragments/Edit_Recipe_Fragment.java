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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.matanhassin.matkonli.MyApplication;
import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.Model;
import com.matanhassin.matkonli.model.ModelStorage;
import com.matanhassin.matkonli.model.Recipe;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class Edit_Recipe_Fragment extends Fragment {

    View view;
    Recipe recipe;
    EditText recipeTitle;
    EditText recipeIngredients;
    EditText recipeInstructions;
    Button saveChangesBtn;
    ImageView recipeImageView;
    Spinner chooseCategory;
    Uri recipeImageUri;
    Bitmap recipeImgBitmap;
    static int REQUEST_CODE = 1;

    public Edit_Recipe_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit__recipe_, container, false);
        recipeTitle = view.findViewById(R.id.Edit_Recipe_Recipe_title_edittext);
        recipeIngredients = view.findViewById(R.id.Edit_Recipe_Ingredients_edittext);
        recipeInstructions = view.findViewById(R.id.Edit_Recipe_Instructions_edittext);
        recipeImageView = view.findViewById(R.id.Edit_Recipe_Photo_imageview);
        chooseCategory = (Spinner) view.findViewById(R.id.Edit_Recipe_Spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MyApplication.context,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseCategory.setAdapter(adapter);

        recipe = Recipe_Page_FragmentArgs.fromBundle(getArguments()).getRecipe();

        if (recipe != null)
        {
            setEditRecipeHints();
        }

        recipeImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                chooseImageFromGallery();
            }
        });

        saveChangesBtn = view.findViewById(R.id.Edit_Recipe_Save_Changes_btn);
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecipe();
            }
        });

        return view;
    }

    void updateRecipe()
    {

        if (recipeImageUri != null)
        {
            ModelStorage.uploadImage(recipeImgBitmap, new ModelStorage.Listener() {
                @Override
                public void onSuccess(String url) {

                    Model.instance.addRecipe(generatedEditedRecipe(url), new Model.Listener<Boolean>() {
                        @Override
                        public void onComplete(Boolean data)
                        {
                            NavController navCtrl = Navigation.findNavController(view);
                            navCtrl.navigateUp();
                            navCtrl.navigateUp();
                        }
                    });
                }

                @Override
                public void onFail()
                {
                    Snackbar.make(view, "Failed to edit post", Snackbar.LENGTH_LONG).show();
                }
            });
        }
        else {
            Model.instance.addRecipe(generatedEditedRecipe(null), new Model.Listener<Boolean>() {
                @Override
                public void onComplete(Boolean data)
                {
                    NavController navCtrl = Navigation.findNavController(view);
                    navCtrl.navigateUp();
                    navCtrl.navigateUp();
                }
            });
        }

    }

    private Recipe generatedEditedRecipe(String imageUrl)
    {
        Recipe editedRecipe = recipe;
        if (recipeTitle.getText().toString() != null && !recipeTitle.getText().toString().equals(""))
            editedRecipe.recipeName = recipeTitle.getText().toString();
        else editedRecipe.recipeName = recipe.recipeName;

        if (recipeIngredients.getText().toString() != null && !recipeIngredients.getText().toString().equals(""))
            editedRecipe.recipeIngredients = recipeIngredients.getText().toString();
        else editedRecipe.recipeIngredients = recipe.recipeIngredients;

        if (recipeInstructions.getText().toString() != null && !recipeInstructions.getText().toString().equals(""))
            editedRecipe.recipeContent = recipeInstructions.getText().toString();
        else editedRecipe.recipeContent = recipe.recipeContent;

        if (chooseCategory.getSelectedItem().toString() != null && !chooseCategory.getSelectedItem().toString().equals(""))
            editedRecipe.categoryId = chooseCategory.getSelectedItem().toString();
        else editedRecipe.categoryId = recipe.categoryId;

        if (imageUrl != null)
            editedRecipe.recipeImageUrl = imageUrl;

        return editedRecipe;
    }

    private void setEditRecipeHints()
    {
        if (recipe.recipeImageUrl != null) { Picasso.get().load(recipe.recipeImageUrl).noPlaceholder().into(recipeImageView); }
        recipeTitle.setText(recipe.recipeName);
        recipeInstructions.setText(recipe.recipeContent);
        recipeIngredients.setText(recipe.recipeIngredients);
    }

    private void chooseImageFromGallery()
    {
        try
        {
            Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            openGalleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(openGalleryIntent, REQUEST_CODE);
        }
        catch (Exception e)
        {Toast.makeText(getContext(), "Edit post Page: " + e.getMessage(), Toast.LENGTH_SHORT).show();}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && resultCode == RESULT_OK){
            recipeImageUri = data.getData();
            recipeImageView.setImageURI(recipeImageUri);
            recipeImgBitmap = uriToBitmap(recipeImageUri);
        }

        else {
            Toast.makeText(getContext(), "No image was selected", Toast.LENGTH_SHORT).show();
        }
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