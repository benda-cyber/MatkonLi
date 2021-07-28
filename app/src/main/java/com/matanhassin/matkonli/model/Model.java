package com.matanhassin.matkonli.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;
import com.matanhassin.matkonli.MyApplication;

import java.util.List;

public class Model {

    public final static Model instance = new Model();
    private Model(){
    }

    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompListener {
        void onComplete();
    }

    @SuppressLint("StaticFieldLeak")
    public void addRecipe(final Recipe recipe, Listener<Boolean> listener) {
        ModelFirebase.addRecipe(recipe, listener);
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                AppLocalDb.db.RecipeDao().insertAllRecipes(recipe);
                return "";
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteRecipe(final Recipe recipe, Listener<Boolean> listener) {
        ModelFirebase.deleteRecipe(recipe, listener);
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                AppLocalDb.db.RecipeDao().deleteRecipe(recipe);
                return "";
            }
        }.execute();
    }

    public void refreshRecipesList(final CompListener listener) {
        long lastUpdated = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("RecipesLastUpdateDate", 0);
        ModelFirebase.getAllRecipesSince(lastUpdated, new Listener<List<Recipe>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(List<Recipe> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        long lastUpdated = 0;
                        for (Recipe r : data) {
                            AppLocalDb.db.RecipeDao().insertAllRecipes(r);
                            if (r.lastUpdated > lastUpdated)
                                lastUpdated = r.lastUpdated;
                        }
                        SharedPreferences.Editor edit = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
                        edit.putLong("RecipesLastUpdateDate", lastUpdated);
                        edit.commit();
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        cleanLocalDb();
                        if (listener != null)
                            listener.onComplete();
                    }
                }.execute("");

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void cleanLocalDb() {
        ModelFirebase.getDeletedRecipesId(new Listener<List<String>>() {
            @Override
            public void onComplete(final List<String> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        for (String id : data) {
                            AppLocalDb.db.RecipeDao().deleteByRecipeId(id);
                        }
                        return "";
                    }
                }.execute("");
            }
        });
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        LiveData<List<Recipe>> liveData = AppLocalDb.db.RecipeDao().getAllRecipes();
        refreshRecipesList(null);
        return liveData;
    }

    public Recipe getRecipeById(String recipeId) {
        Recipe recipe = AppLocalDb.db.RecipeDao().GetRecipeById(recipeId);
        refreshRecipesList(null);
        return recipe;
    }

    public LiveData<List<Recipe>> getAllRecipesPerCategory(String categoryId) {
        LiveData<List<Recipe>> liveData = AppLocalDb.db.RecipeDao().getAllRecipesPerCategory(categoryId);
        refreshRecipesList(null);
        return liveData;
    }

    public LiveData<List<Recipe>> getAllRecipesPerUser(String userId) {
        LiveData<List<Recipe>> liveData = AppLocalDb.db.RecipeDao().getAllRecipesPerUser(userId);
        refreshRecipesList(null);
        return liveData;
    }

    public void updateUserProfile(String username, String profileImgUrl, Listener<Boolean> listener) {
        ModelFirebase.updateUserProfile(username, profileImgUrl, listener);
    }

    public void setUserAppData(String email) {
        ModelFirebase.setUserData(email);
    }

}
