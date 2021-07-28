package com.matanhassin.matkonli.model;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("select * from Recipe")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("select * from Recipe where userId = :userId")
    LiveData<List<Recipe>> getAllRecipesPerUser(String userId);

    @Query("delete from Recipe where recipeId = :recipeId")
    void deleteByRecipeId(String recipeId);

    @Query("select * from Recipe where categoryId = :categoryId")
    LiveData<List<Recipe>> getAllRecipesPerCategory(String categoryId);

    @Query("select * from Recipe where recipeId = :recipeId")
    Recipe GetRecipeById(String recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllRecipes(Recipe...recipes);

    @Delete
    void deleteRecipe(Recipe recipe);
}
