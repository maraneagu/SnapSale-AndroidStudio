package com.example.snapsale.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snapsale.R;
import com.example.snapsale.network.requests.TranslateRequest;


// HELPFUL LINKS:
// 1). "RecyclerView in Android Studio-Tutorial 2021", CodesKing, URL: https://www.youtube.com/watch?v=ppEw_nzCgO4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=61&t=567s&ab_channel=CodesKing


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private final Activity activity;
    private final List<String> ingredients;
    private final String targetLanguage, sourceLanguage;
    private final IngredientsCallback callback;

    public IngredientsAdapter(Activity activity, List<String> ingredients, String targetLanguage, String sourceLanguage, IngredientsCallback callback) {
        this.activity = activity;
        this.ingredients = ingredients;

        this.targetLanguage = targetLanguage;
        this.sourceLanguage = sourceLanguage;

        this.callback = callback;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_store_recipe_ingredients_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.ingredient.setVisibility(View.GONE);

        if (targetLanguage.equals("en")) {
            holder.ingredient.setText(ingredients.get(position));
            holder.ingredient.setVisibility(View.VISIBLE);
        }
        else {
            TranslateRequest.translate(activity, targetLanguage, sourceLanguage, ingredients.get(position),
                    translatedText -> { holder.ingredient.setText(translatedText);
                        holder.ingredient.setVisibility(View.VISIBLE);});
        }

        callback.onIngredientsDisplayed();
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredient;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredient = itemView.findViewById(R.id.favorites_store_recipe_instruction);
        }
    }

    public interface IngredientsCallback {
        void onIngredientsDisplayed();
    }
}
