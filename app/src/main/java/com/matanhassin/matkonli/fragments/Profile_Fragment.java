package com.matanhassin.matkonli.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.activities.login_page;
import com.matanhassin.matkonli.model.User;
import com.squareup.picasso.Picasso;

public class Profile_Fragment extends Fragment {
    View view;
    TextView userName;
    TextView userEmail;
    ImageView userProfileImage;
    Button editProfileBtn;
    Button myRecipesBook;
    Button logoutBtn;

    public Profile_Fragment() {
        // Required empty public constructor}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_, container, false);
        userName = view.findViewById(R.id.profile_page_username_text_view);
        userEmail = view.findViewById(R.id.profile_page_email_text_view);
        userProfileImage = view.findViewById(R.id.profile_page_profile_image_view);
        editProfileBtn = view.findViewById(R.id.profile_page_edit_profile_btn);
        myRecipesBook = view.findViewById(R.id.profile_page_my_recipes_book_btn);
        logoutBtn = view.findViewById(R.id.profile_page_logout_btn);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfilePage();
            }
        });

        myRecipesBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile_FragmentDirections.ActionProfileFragmentToMyRecipesListFragment action = Profile_FragmentDirections.actionProfileFragmentToMyRecipesListFragment(User.getInstance().userId);
                Navigation.findNavController(view).navigate(action);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPage();
            }
        });

        setUserProfile();

        return view;
    }

    private void setUserProfile() {
        userName.setText(User.getInstance().username);
        userEmail.setText(User.getInstance().userEmail);

        if (User.getInstance().userprofileImageUrl != null)
        {
            Picasso.get().load(User.getInstance().userprofileImageUrl).noPlaceholder().into(userProfileImage);
        }
    }

    private void logoutPage() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this.getActivity(), login_page.class));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void EditProfilePage()
    {
        NavController navCtrl = Navigation.findNavController(getActivity(), R.id.mainactivity_navhost);
        NavDirections directions = Profile_FragmentDirections.actionProfileFragmentToEditProfileFragment();
        navCtrl.navigate(directions);
    }
}