package com.matanhassin.matkonli.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.Model;
import com.matanhassin.matkonli.model.ModelStorage;
import com.matanhassin.matkonli.model.Recipe;
import com.matanhassin.matkonli.model.User;
import com.squareup.picasso.Picasso;


public class Recipe_Page_Fragment extends Fragment {

    Recipe recipe;
    View view;
    Button editBtn;
    Button deleteBtn;
    TextView categoryTitle;
    TextView recipeTitle;
    TextView ingredientsTitle;
    TextView ingredientsList;
    TextView instructionTitle;
    TextView instructionList;
    ImageView recipeImage;

    public Recipe_Page_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_recipe__page_, container, false);
        categoryTitle = view.findViewById(R.id.recipe_page_Category_textview);
        recipeTitle = view.findViewById(R.id.recipe_page_Recipe_Title_textview);
        ingredientsTitle = view.findViewById(R.id.recipe_page_Ingredients_Title_textview);
        ingredientsList = view.findViewById(R.id.recipe_page_ingredients_Container_textview);
        instructionTitle = view.findViewById(R.id.recipe_page_Instructions_textview);
        instructionList = view.findViewById(R.id.recipe_page_Instructions_Container_textview);
        recipeImage = view.findViewById(R.id.recipe_page_Image_Recipe);
        instructionList.setMovementMethod(new ScrollingMovementMethod());
        ingredientsList.setMovementMethod(new ScrollingMovementMethod());
        recipe = Recipe_Page_FragmentArgs.fromBundle(getArguments()).getRecipe();

        if (recipe !=null){

            categoryTitle.setText(recipe.categoryId);
            recipeTitle.setText(recipe.recipeName);
            ingredientsList.setText(recipe.recipeIngredients);
            instructionList.setText(recipe.recipeContent);
            if (recipe.recipeImageUrl != null)
            {
                Picasso.get().load(recipe.recipeImageUrl).placeholder(R.drawable.matkonlilogo).into(recipeImage);
            }else {
                recipeImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
        editBtn = view.findViewById(R.id.recipe_page_Edit_btn);
        editBtn.setVisibility(view.INVISIBLE);
        deleteBtn = view.findViewById(R.id.recipe_page_Delete_btn);
        deleteBtn.setVisibility(view.INVISIBLE);

        if (recipe.userId.equals(User.getInstance().userId))
        {
            editBtn.setVisibility(view.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toEditRecipePage(recipe);
                }
            });

            deleteBtn.setVisibility(view.VISIBLE);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRecipe(recipe);
                }
            });
        }
        return view;
    }

    private void toEditRecipePage(Recipe recipe) {

        NavController navController = Navigation.findNavController(getActivity(),R.id.mainactivity_navhost);
        Recipe_Page_FragmentDirections.ActionRecipePageFragmentToEditRecipeFragment action = Recipe_Page_FragmentDirections.actionRecipePageFragmentToEditRecipeFragment(recipe);
        navController.navigate(action);
    }

    private void deleteRecipe(Recipe recipeToDelete) {

        Model.instance.deleteRecipe(recipeToDelete, new Model.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                ModelStorage.deleteImage(recipe.recipeImageUrl, new ModelStorage.Listener() {
                    @Override
                    public void onSuccess(String url) {
                        NavController navController = Navigation.findNavController(view);
                        navController.navigateUp();
                    }

                    @Override
                    public void onFail() {

                        Snackbar.make(view,"Failed to delete recipe",Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}