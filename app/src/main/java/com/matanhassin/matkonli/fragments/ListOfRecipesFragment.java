package com.matanhassin.matkonli.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.Model;
import com.matanhassin.matkonli.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;


public class ListOfRecipesFragment extends Fragment {

    TextView categoryTextView;
    String category;
    RecyclerView list;
    List<Recipe> data = new LinkedList<>();
    RecipeListAdapter adapter;
    Recipe_List_ViewModel viewModel;
    LiveData<List<Recipe>> liveData;
    public ListOfRecipesFragment(){}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel = new ViewModelProvider(this).get(Recipe_List_ViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_recipes, container, false);
        category = ListOfRecipesFragmentArgs.fromBundle(getArguments()).getCategory();
        String s1=category.substring(0,1).toUpperCase()+category.substring(1);
        list=view.findViewById(R.id.list_of_my_recipes_recycler_view);
        list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);
        adapter = new RecipeListAdapter();
        list.setAdapter(adapter);
        categoryTextView=view.findViewById(R.id.list_of_recipes_category_textview);
        categoryTextView.setText(s1);
        adapter.setOnClickListener(new OnItemClickListener() {

            @Override
            public void onClick(int position) {
                Recipe recipe = data.get(position);


                    ListOfRecipesFragmentDirections.ActionListOfRecipesFragmentToRecipePageFrag action = ListOfRecipesFragmentDirections.actionListOfRecipesFragmentToRecipePageFrag(recipe);
                    Navigation.findNavController(view).navigate(action);

                    //HomeFragmentDirections.ActionHomeFragmentToListOfRecipesFragment action = HomeFragmentDirections.actionHomeFragmentToListOfRecipesFragment("fish");
                    //Navigation.findNavController(view).navigate(action);



            }});

        liveData = viewModel.getDataByCategory(category);
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {

                List<Recipe> reversedData = reverseData(recipes);
                data = reversedData;
                adapter.notifyDataSetChanged();
            }});

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.my_list_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(new Model.CompListener() {
                    @Override
                    public void onComplete() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }});

        return view;
    }

    private List<Recipe> reverseData(List<Recipe> recipes) {
        List<Recipe> reversedData = new LinkedList<>();
        for (Recipe recipe: recipes) {
            reversedData.add(0, recipe);
        }
        return reversedData;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder{
        ImageView recipeImage;
        TextView recipeTitle;
        TextView recipeUsername;
        Recipe recipe;

        public RecipeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_row_image_view);
            recipeTitle = itemView.findViewById(R.id.recipe_row_recipe_title_text_view);
            recipeUsername = itemView.findViewById(R.id.recipe_row_username_textview);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onClick(position);
                    }}});
        }
        public void bind(Recipe recipeToBind){
            recipeTitle.setText(recipeToBind.recipeName);
            recipeUsername.setText(recipeToBind.username);
            recipe = recipeToBind;

            if (recipeToBind.recipeImageUrl !=null)
            {
                Picasso.get().load(recipeToBind.recipeImageUrl).placeholder(R.drawable.matkonlilogo).into(recipeImage);
            }
            else {
                recipeImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

    class RecipeListAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

        private OnItemClickListener listener;

        void setOnClickListener(OnItemClickListener listener){ this.listener=listener; }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.recipe_row_list,parent,false);
            RecipeViewHolder recipeViewHolder = new RecipeViewHolder(view,listener);
            return recipeViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            Recipe recipe = data.get(position);
            holder.bind(recipe);
        }

        @Override
        public int getItemCount() {return data.size();}
    }
}