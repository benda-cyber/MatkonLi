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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.Model;
import com.matanhassin.matkonli.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;


public class My_Recipes_List_Fragment extends Fragment {

    String userId;
    RecyclerView list;
    List<Recipe> data = new LinkedList<>();
    My_Recipes_List_Fragment.MyRecipeListAdapter adapter;
    My_Recipe_List_ViewModel viewModel;
    LiveData<List<Recipe>> liveData;

    public My_Recipes_List_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(My_Recipe_List_ViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my__recipes__list_, container, false);
        userId = My_Recipes_List_FragmentArgs.fromBundle(getArguments()).getUserId();


        list= view.findViewById(R.id.list_of_my_recipes_recycler_view);
        list.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);

        adapter = new MyRecipeListAdapter();

        list.setAdapter(adapter);

        adapter.setOnClickListener(new My_Recipes_List_Fragment.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Recipe recipe = data.get(position);
                My_Recipes_List_FragmentDirections.ActionMyRecipesListFragmentToRecipePageFragment action = My_Recipes_List_FragmentDirections.actionMyRecipesListFragmentToRecipePageFragment(recipe);
                Navigation.findNavController(view).navigate(action);
            }
        });

        liveData = viewModel.getDataByUser(userId);
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {

                List<Recipe> reversedData = reverseData(recipes);
                data = reversedData;
                adapter.notifyDataSetChanged();
            }
        });

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
            }
        });

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

    static class MyRecipeViewHolder extends RecyclerView.ViewHolder {

        TextView recipeTitle;
        ImageView recipeImage;
        TextView username;
        Recipe recipe;

        public MyRecipeViewHolder(@NonNull View itemView, final My_Recipes_List_Fragment.OnItemClickListener listener) {
            super(itemView);

            recipeTitle = itemView.findViewById(R.id.recipe_row_recipe_title_text_view);
            recipeImage = itemView.findViewById(R.id.recipe_row_image_view);
            username = itemView.findViewById(R.id.recipe_row_username_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onClick(position);
                    }
                }
            });
        }

        public void bind(Recipe recipeToBind){
            recipeTitle.setText(recipeToBind.recipeName);
            username.setText(recipeToBind.username);
            recipe = recipeToBind;
            if (recipeToBind.recipeImageUrl !=null)
            {
                Picasso.get().load(recipeToBind.recipeImageUrl).placeholder(R.drawable.matkonlilogo).into(recipeImage);
            }else {
                recipeImage.setImageResource(R.drawable.ic_launcher_background);
            }

        }
    }

    interface OnItemClickListener {
        void onClick(int position);
    }


    class MyRecipeListAdapter extends RecyclerView.Adapter<MyRecipeViewHolder> {

        private OnItemClickListener listener;

        void setOnClickListener(OnItemClickListener listener){ this.listener=listener; }

        @NonNull
        @Override
        public MyRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //create row
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.recipe_row_list,parent,false);
            My_Recipes_List_Fragment.MyRecipeViewHolder myRecipeViewHolder = new My_Recipes_List_Fragment.MyRecipeViewHolder(view,listener);
            return myRecipeViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyRecipeViewHolder holder, int position) {
            Recipe recipe = data.get(position);
            holder.bind(recipe);
        }


        @Override
        public int getItemCount() {return data.size();}
    }
}