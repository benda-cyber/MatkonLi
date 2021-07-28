package com.matanhassin.matkonli.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Recipe implements Serializable {

    @PrimaryKey
    @NonNull
    public String recipeId;
    public String recipeName;
    public String categoryId;
    public String recipeIngredients;
    public String recipeContent;
    public String recipeImageUrl;
    public String userId;
    public String username;
    public long lastUpdated;

    public Recipe(){
        recipeId = "";
        recipeName = "";
        categoryId = "";
        recipeIngredients = "";
        recipeContent = "";
        recipeImageUrl = "";
        userId = "";
        username = "";
        lastUpdated = 0;
    }

    public Recipe(String recipeId, String recipeName, String categoryId, String recipeIngredients, String recipeContent, String recipeImageUrl, String userId, String username) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.categoryId = categoryId;
        this.recipeIngredients = recipeIngredients;
        this.recipeContent = recipeContent;
        this.recipeImageUrl = recipeImageUrl;
        this.userId = userId;
        this.username = username;
    }

    @NonNull
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public String getRecipeContent() {
        return recipeContent;
    }

    public void setRecipeContent(String recipeContent) {
        this.recipeContent = recipeContent;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
