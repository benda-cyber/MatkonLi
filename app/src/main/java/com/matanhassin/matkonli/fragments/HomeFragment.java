package com.matanhassin.matkonli.fragments;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.matanhassin.matkonli.R;

public class HomeFragment extends Fragment {

    ImageButton spaghetti;
    ImageButton steak;
    ImageButton fish;
    ImageButton cupcake;
    ImageButton bread;
    ImageButton pizza;
    ImageButton salad;
    ImageButton cheese;
    ImageButton beer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        spaghetti = view.findViewById(R.id.spaghetti);
        steak= view.findViewById(R.id.steak);
        fish= view.findViewById(R.id.fish);
        cupcake= view.findViewById(R.id.cupcake);
        bread= view.findViewById(R.id.bread);
        pizza= view.findViewById(R.id.pizza);
        salad= view.findViewById(R.id.salad);
        cheese= view.findViewById(R.id.cheese);
        beer= view.findViewById(R.id.beer);

        spaghetti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("spaghetti");
                Navigation.findNavController(view).navigate(action); }});

        steak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("steak");
                Navigation.findNavController(view).navigate(action); }});

        fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("fish");
                Navigation.findNavController(view).navigate(action); }});

        cupcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("cupcake");
                Navigation.findNavController(view).navigate(action); }});

        bread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("bread");
                Navigation.findNavController(view).navigate(action); }});

        pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("pizza");
                Navigation.findNavController(view).navigate(action); }});

        salad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("salad");
                Navigation.findNavController(view).navigate(action); }});

        cheese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("cheese");
                Navigation.findNavController(view).navigate(action); }});

        beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("beer");
                Navigation.findNavController(view).navigate(action); }});

        return view;
    }
}