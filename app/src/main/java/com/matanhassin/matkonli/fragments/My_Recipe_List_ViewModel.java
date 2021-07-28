package com.matanhassin.matkonli.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.matanhassin.matkonli.model.Model;
import com.matanhassin.matkonli.model.Recipe;

import java.util.List;

public class My_Recipe_List_ViewModel extends ViewModel {

    LiveData<List<Recipe>> liveData;

    public LiveData<List<Recipe>> getData(){
        if (liveData == null)
            liveData = Model.instance.getAllRecipes();
        return liveData;
    }

    public LiveData<List<Recipe>> getDataByUser(String userId){
        if (liveData == null)
            liveData = Model.instance.getAllRecipesPerUser(userId);
        return liveData;
    }

    public void refresh(Model.CompListener listener){
        Model.instance.refreshRecipesList(listener);
    }
}
