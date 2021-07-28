package com.matanhassin.matkonli.model;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.matanhassin.matkonli.MyApplication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {

    final static String RECIPE = "recipe";

    public interface Listener<T>{
        void onComplete();
        void onFail();
    }

    public static void loginUser(final String email, String password, final Listener<Boolean> listener){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (email != null && !email.equals("") && password != null && !password.equals(""))
        {
            if (firebaseAuth.getCurrentUser() != null)
            {
                firebaseAuth.signOut();
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>()
            {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    Toast.makeText(MyApplication.context, "Hello :)", Toast.LENGTH_SHORT).show();
                    setUserData(email);
                    listener.onComplete();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MyApplication.context, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    listener.onFail();
                }
            });
        }
        else {
            Toast.makeText(MyApplication.context, "You must fill in all of the fields", Toast.LENGTH_SHORT).show();
        }
    }


    public static void registerUser(final String username, final String password, final String email, final Uri imageUri, final Listener<Boolean> listener){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            firebaseAuth.signOut();
        }

        if(firebaseAuth.getCurrentUser()== null && username!=null
        && !username.equals("") && password !=null && !password.equals("")
        && email != null && !email.equals("") && imageUri !=null){
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(MyApplication.context,"User registered", Toast.LENGTH_SHORT).show();
                    CreateUser(username,email,imageUri);
                    listener.onComplete();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MyApplication.context,"Failed registering user",Toast.LENGTH_SHORT).show();
                    listener.onFail();
                }
            });
        }

        else{
            Toast.makeText(MyApplication.context,"You must fill in all of the fields and profile image",Toast.LENGTH_SHORT).show();
            listener.onFail();
        }
    }

    public static void getAllRecipesSince(long since, final Model.Listener<List<Recipe>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(since,0);
        db.collection(RECIPE).whereGreaterThanOrEqualTo("lastUpdated", ts).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Recipe> recipesData = null;
                if (task.isSuccessful()){
                    recipesData = new LinkedList<Recipe>();
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Map<String,Object> json = doc.getData();
                        Recipe recipe = fromMap(json);
                        recipesData.add(recipe);
                    }
                }
                listener.onComplete(recipesData);
            }
        });
    }

    private static void CreateUser(final String username, final String email, Uri imageUri){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images");
        if (imageUri != null){
            String imageName = username + "." + getExtension(imageUri);
            final StorageReference imageRef = storageReference.child(imageName);

            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){

                        Map<String,Object> data = new HashMap<>();
                        data.put("profileImageUrl", task.getResult().toString());
                        data.put("username", username);
                        data.put("email", email);
                        data.put("info", "NA");
                        firebaseFirestore.collection("userData").document(email).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                if (firebaseAuth.getCurrentUser() != null){
                                    firebaseAuth.signOut();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MyApplication.context, "Fails to create user and upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if (!task.isSuccessful()){
                        Toast.makeText(MyApplication.context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(MyApplication.context, "Please choose a profile image", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getExtension(Uri uri)
    {
        try{
            ContentResolver contentResolver = MyApplication.context.getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        } catch (Exception e) {
            Toast.makeText(MyApplication.context, "Register page: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static void setUserData(final String email)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();;
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db.collection("userData").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
//                String username = User.getInstance().getUsername();
//                String userprofileImageUrl = User.getInstance().getUserprofileImageUrl();
//                String userPassword = User.getInstance().getUserPassword();
//                String userAddress = User.getInstance().getUserAddress();
//                String userEmail = User.getInstance().getUserEmail();
//                String userId = User.getInstance().getUserId();

                if (task.isSuccessful()){
                    User.getInstance().username = (String) task.getResult().get("username");
                    User.getInstance().userprofileImageUrl = (String) task.getResult().get("profileImageUrl");
                    User.getInstance().userPassword = (String) task.getResult().get("password");
                    User.getInstance().userAddress = (String) task.getResult().get("address");
                    User.getInstance().userEmail = email;
                    User.getInstance().userId = firebaseAuth.getUid();
                }
            }
        });
    }

    public static void addRecipe(Recipe recipe, final Model.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPE).document(recipe.getRecipeId()).set(toMap(recipe)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful()); }
            }});
    }
    public static void deleteRecipe(Recipe recipe, final Model.Listener<Boolean> listener) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPE).document(recipe.getRecipeId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Map<String,Object> deleted = new HashMap<>();
                deleted.put("recipeId", recipe.getRecipeId());
                db.collection("deleted").document(recipe.getRecipeId()).set(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (listener!=null){
                            listener.onComplete(task.isSuccessful());
                        }
                    }
                });
            }
        });
    }
    public static void getDeletedRecipesId(final Model.Listener<List<String>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("deleted").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> deletedRecipsIds = null;
                if (task.isSuccessful()){
                    deletedRecipsIds = new LinkedList<String>();
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String deleted = (String) doc.getData().get("recipeId");
                        deletedRecipsIds.add(deleted);
                    }
                }
                listener.onComplete(deletedRecipsIds);
            }
        });
    }

    public static Map<String, Object> toMap(Recipe recipe){
        HashMap<String, Object> map = new HashMap<>();
        map.put("recipeId", recipe.recipeId);
        map.put("recipeName", recipe.recipeName);
        map.put("categoryId", recipe.categoryId);
        map.put("recipeIngredients", recipe.recipeIngredients);
        map.put("recipeContent", recipe.recipeContent);
        map.put("recipeImgUrl", recipe.recipeImageUrl);
        map.put("userId", User.getInstance().userId);
        map.put("username", User.getInstance().username);
        map.put("lastUpdated", FieldValue.serverTimestamp());
        return map;
    }

    private static Recipe fromMap(Map<String, Object> json){
        Recipe newRecipe = new Recipe();
        newRecipe.recipeId = (String) json.get("recipeId");
        newRecipe.recipeName = (String) json.get("recipeName");
        newRecipe.categoryId = (String) json.get("categoryId");
        newRecipe.recipeIngredients = (String) json.get("recipeIngredients");
        newRecipe.recipeContent = (String) json.get("recipeContent");
        newRecipe.recipeImageUrl = (String) json.get("recipeImgUrl");
        newRecipe.userId = (String) json.get("userId");
        newRecipe.username = (String) json.get("username");
        Timestamp ts = (Timestamp)json.get("lastUpdated");

        if (ts != null)
            newRecipe.lastUpdated = ts.getSeconds();

        return newRecipe;
    }

    private static void uploadUserData(final String username, final String email, Uri imageUri)
    {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images");

        if (imageUri != null){
            String imageName = username + "." + getExtension(imageUri);
            final StorageReference imageRef = storageReference.child(imageName);
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){

                        Map<String,Object> data = new HashMap<>();
                        data.put("profileImageUrl", task.getResult().toString());
                        data.put("username", username);
                        data.put("email", email);
                        data.put("info", "NA");
                        firebaseFirestore.collection("userData").document(email).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (firebaseAuth.getCurrentUser() != null){
                                    firebaseAuth.signOut();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MyApplication.context, "Fails to create user and upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if (!task.isSuccessful()){
                        Toast.makeText(MyApplication.context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }}});
        }

        else {
            Toast.makeText(MyApplication.context, "Please choose a profile image", Toast.LENGTH_SHORT).show();
        }
    }


    public static void updateUserProfile(String username, String profileImgUrl, final Model.Listener<Boolean> listener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> json = new HashMap<>();
        if (username != null)
            json.put("username", username);
        else json.put("username", User.getInstance().username);

        if (profileImgUrl != null)
            json.put("profileImageUrl", profileImgUrl);
        else json.put("profileImageUrl", User.getInstance().userprofileImageUrl);

        json.put("email", User.getInstance().userEmail);

        db.collection("userData").document(User.getInstance().userEmail).set(json).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (listener != null)
                    listener.onComplete(task.isSuccessful());
            }});
    }
}
