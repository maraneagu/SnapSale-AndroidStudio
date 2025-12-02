package com.example.snapsale.activities.mainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snapsale.R;
import com.example.snapsale.database.repositories.FavoredSalesRepository;
import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.navigation.CustomBottomNavigationView;
import com.example.snapsale.adapters.IngredientsAdapter;
import com.example.snapsale.adapters.InstructionsAdapter;
import com.example.snapsale.network.requests.TranslateRequest;
import com.example.snapsale.models.FavoredSale;
import com.example.snapsale.models.Recipe;
import com.example.snapsale.models.Sale;
import com.example.snapsale.activities.storeActivities.CarrefourActivity;
import com.example.snapsale.activities.storeActivities.KauflandActivity;
import com.example.snapsale.activities.storeActivities.LidlActivity;
import com.example.snapsale.activities.storeActivities.PennyActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


// HELPFUL LINKS:
// 1). "Create Custom Alert Dialog Box in Android Studio using Java | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=3RTpdB-RszY&ab_channel=AndroidKnowledge
// 2). "RecyclerView in Android Studio-Tutorial 2021", CodesKing, URL: https://www.youtube.com/watch?v=ppEw_nzCgO4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=61&t=567s&ab_channel=CodesKing
// 3). "How to Create Horizontal RecyclerView in Android Studio Easily", The Code City, URL: https://www.youtube.com/watch?v=Zj9ZE6_HtEo&ab_channel=TheCodeCity
// 4). "Upload Retrieve Image from Firebase & Display in RecyclerView, GridView, Staggered | Android Studio", Android Knowdledge, URL: https://www.youtube.com/watch?v=Hn89s4oCdS8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=52&t=2451s&ab_channel=AndroidKnowledge
// 5). "Delete Specific Item From RecyclerView || Android Studio Tutorials", Hello Coders, URL: https://www.youtube.com/watch?v=vrhwgjz6wGQ&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=58&ab_channel=HelloCoders
// 6). "How to Load a image from a URL in Imageview using Glide | Android Studio | Java 🔥", Android Mate, URL: https://www.youtube.com/watch?v=xrVD7LcQ5nY&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=51&ab_channel=AndroidMate
// 7). "How to Change Toggle (Button) Color and ToolBar Text Color in Android Studio App", Any Technology, URL: https://www.youtube.com/watch?v=0CrhNNhQfgc&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=29&ab_channel=AnyTechnology
// 8). "How to Implement Bottom Navigation with Activities | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=MUl19ppdu0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=22&ab_channel=AndroidKnowledge
// 9). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view
// 10). "Navigation Drawer Menu in Android Tutorial | How to Create Navigation Drawer in Android Studio", Code with Surya, URL: https://www.youtube.com/watch?v=uY9iZiamyZs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=69&t=1187s&ab_channel=CodewithSurya


public class FavoritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CustomBottomNavigationView.OnNavigationItemSelectedListener{

    private static FavoredSalesRepository favoredSalesRepository;

    private static String[] storeKeywords;
    private TextView kauflandNumberText, lidlNumberText, carrefourNumberText, pennyNumberText;

    private static String targetLanguage, sourceLanguage;

    private SaleAdapter adapter;
    private DrawerLayout drawerLayout;
    private CustomBottomNavigationView navigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoredSalesRepository = FavoredSalesRepository.getInstance();
        storeKeywords = getResources().getStringArray(R.array.store_keywords);

        targetLanguage = "en";
        sourceLanguage = "ro";

        drawerLayout = findViewById(R.id.favorites_navigation_drawer_layout);
        setNavigationDrawer();

        navigationBar = findViewById(R.id.favorites_navigation_bar);
        navigationBar.setSelectedItemId(R.id.bn_favorites);
        navigationBar.setOnNavigationItemSelectedListener(this);

        kauflandNumberText = findViewById(R.id.favorites_kaufland_number);
        lidlNumberText = findViewById(R.id.favorites_lidl_number);
        carrefourNumberText = findViewById(R.id.favorites_carrefour_number);
        pennyNumberText = findViewById(R.id.favorites_penny_number);

        setFavoredButtons();
        setTranslateButton();

        handleItent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigationBar.setSelectedItemId(R.id.bn_favorites);
        Checker.checkMove(FavoritesActivity.this, this::getFavoredSalesCount);
    }

    // FLAG_ACTIVITY_REORDER_TO_FRONT may resume the target activity with its previous state if it is already running.
    // This means the new intent extras might not be automatically processed.
    // When the activity is brought to the front, onNewIntent(Intent) is called to handle the new intent.
    // The extra data is processed in this function to ensure the extra strings are handled correctly.

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleItent(intent);
    }

    private void handleItent(Intent intent) {
        if (intent != null) {
            String storeName = intent.getStringExtra("store");
            if (storeName != null) {
                if (storeName.equals(storeKeywords[0]))
                    getFavoredSales(storeKeywords[0], kauflandNumberText);
                else if (storeName.equals(storeKeywords[1]))
                    getFavoredSales(storeKeywords[1], lidlNumberText);
                else if (storeName.equals(storeKeywords[3]))
                    getFavoredSales(storeKeywords[3], carrefourNumberText);
                else if (storeName.equals(storeKeywords[2]))
                    getFavoredSales(storeKeywords[2], pennyNumberText);
            }

            intent.removeExtra("store");
        }
    }

    private void getFavoredSalesCount() {
        favoredSalesRepository.getFavoredSalesCount(storeKeywords[0], (salesCount) -> kauflandNumberText.setText(String.valueOf(salesCount)));
        favoredSalesRepository.getFavoredSalesCount(storeKeywords[1], (salesCount) -> lidlNumberText.setText(String.valueOf(salesCount)));
        favoredSalesRepository.getFavoredSalesCount(storeKeywords[2], (salesCount) -> pennyNumberText.setText(String.valueOf(salesCount)));
        favoredSalesRepository.getFavoredSalesCount(storeKeywords[3], (salesCount) -> carrefourNumberText.setText(String.valueOf(salesCount)));
    }

    private void getFavoredSales(String storeName, TextView storeText) {
        favoredSalesRepository.getFavoredSales(storeName, (List<FavoredSale> sales, List<String> categoryKeys) -> {
            Log.d("SCRAPING", String.valueOf(sales.size()));
            if (sales.isEmpty()) {
                Toast.makeText(FavoritesActivity.this, "Unfortunately, there are no favored sales for this store.", Toast.LENGTH_LONG).show();
            }
            else {
                Map<Sale, String> map = new HashMap<>();
                for (int i = 0; i < sales.size(); i++) map.put(sales.get(i), categoryKeys.get(i));
                sales.sort((sale1, sale2) -> Long.compare(sale2.getFavoredTimestamp(), sale1.getFavoredTimestamp()));
                for (int i = 0; i < sales.size(); i++) categoryKeys.set(i, map.get(sales.get(i)));

                getFavoredSales(storeName, storeText, sales, categoryKeys);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void getFavoredSales(String storeName, TextView storeText, List<FavoredSale> sales, List<String> categoryKeys) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
        View view = getLayoutInflater().inflate(R.layout.favorites_recycler_view_layout, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        RecyclerView recyclerView = view.findViewById(R.id.favorites_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnTouchListener((view1, motionEvent) -> true);

        adapter = new SaleAdapter(storeName, storeText, sales, categoryKeys, dialog, recyclerView);
        recyclerView.setAdapter(adapter);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

       dialog.show();
    }

    private void setFavoredButtons() {
        RelativeLayout kauflandButton = findViewById(R.id.favorites_kaufland_btn);
        kauflandButton.setOnClickListener(view -> getFavoredSales(storeKeywords[0], kauflandNumberText));

        RelativeLayout lidlButton = findViewById(R.id.favorites_lidl_btn);
        lidlButton.setOnClickListener(view -> getFavoredSales(storeKeywords[1], lidlNumberText));

        RelativeLayout carrefourButton = findViewById(R.id.favorites_carrefour_btn);
        carrefourButton.setOnClickListener(view -> getFavoredSales(storeKeywords[3], carrefourNumberText));

        RelativeLayout pennyButton = findViewById(R.id.favorites_penny_btn);
        pennyButton.setOnClickListener(view -> getFavoredSales(storeKeywords[2], pennyNumberText));
    }

    private void setTranslateButton() {
        ImageView translateFlag = findViewById(R.id.favorites_translate_flag);
        RelativeLayout translateButton = findViewById(R.id.favorites_translate_btn);
        TextView exploreText = findViewById(R.id.favorites_explore_text);

        TextView kauflandFavoredText = findViewById(R.id.favorites_kaufland_favored_text);
        TextView lidlFavoredText = findViewById(R.id.favorites_lidl_favored_text);
        TextView carrefourFavoredText = findViewById(R.id.favorites_carrefour_favored_text);
        TextView pennyFavoredText = findViewById(R.id.favorites_penny_favored_text);

        TextView kauflandSalesText = findViewById(R.id.favorites_kaufland_sales_text);
        TextView lidlSalesText = findViewById(R.id.favorites_lidl_sales_text);
        TextView carrefourSalesText = findViewById(R.id.favorites_carrefour_sales_text);
        TextView pennySalesText = findViewById(R.id.favorites_penny_sales_text);

        translateButton.setOnClickListener(view -> {
            if (sourceLanguage.equals("ro")) {
                targetLanguage = "ro";
                sourceLanguage = "en";

                translateFlag.setImageResource(R.drawable.flag_romania);
                exploreText.setText(R.string.explore_favored_romanian);

                kauflandFavoredText.setText(R.string.favored_romanian);
                kauflandSalesText.setText(R.string.sales_romanian);

                lidlFavoredText.setText(R.string.favored_romanian);
                lidlSalesText.setText(R.string.sales_romanian);

                carrefourFavoredText.setText(R.string.favored_romanian);
                carrefourSalesText.setText(R.string.sales_romanian);

                pennyFavoredText.setText(R.string.favored_romanian);
                pennySalesText.setText(R.string.sales_romanian);
            }
            else {
                targetLanguage = "en";
                sourceLanguage = "ro";

                translateFlag.setImageResource(R.drawable.flag_uk);
                exploreText.setText(R.string.explore_favored);

                kauflandFavoredText.setText(R.string.favored);
                kauflandSalesText.setText(R.string.sales);

                lidlFavoredText.setText(R.string.favored);
                lidlSalesText.setText(R.string.sales);

                carrefourFavoredText.setText(R.string.favored);
                carrefourSalesText.setText(R.string.sales);

                pennyFavoredText.setText(R.string.favored);
                pennySalesText.setText(R.string.sales);
            }
        });
    }

    private class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {
        String storeName;
        TextView storeText;

        AlertDialog dialog;
        RecyclerView recyclerView;

        List<FavoredSale> sales;
        List<String> categoryKeys;

        public SaleAdapter(String storeName, TextView storeText, List<FavoredSale> sales, List<String> categoryKeys, AlertDialog dialog, RecyclerView recyclerView) {
            this.storeName = storeName;
            this.storeText = storeText;

            this.sales = sales;
            this.categoryKeys = categoryKeys;

            this.dialog = dialog;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SaleViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_store_layout, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
            setBkg(holder, position);
            setCategory(holder, position);
            setImage(holder, position);
            setPeriod(holder, position);
            setTitle(holder, position);
            setSubtitle(holder, position);
            setQuantity(holder, position);
            setNewPrice(holder, position);
            setOldPrice(holder, position);
            setIsFavored(holder, position);
            setLeftArrow(holder, position);
            setRightArrow(holder, position);
            setRecipeBtn(holder, position);
        }

        @Override
        public int getItemCount() {
            return sales.size();
        }

        private void setBkg(SaleViewHolder holder, int position) {
            if (storeName.equals(storeKeywords[1])) {
                if (sales.get(position).getCategory().equals("market")) {
                    holder.bkg.setBackground(ContextCompat.getDrawable(FavoritesActivity.this, R.drawable.favorites_lidl_market_bkg));
                    holder.imageLayout.setBackground(ContextCompat.getDrawable(FavoritesActivity.this, R.drawable.favorites_store_market_image_bkg));
                }
                else {
                    holder.bkg.setBackground(ContextCompat.getDrawable(FavoritesActivity.this, R.drawable.favorites_lidl_bkg));
                    holder.imageLayout.setBackground(ContextCompat.getDrawable(FavoritesActivity.this, R.drawable.favorites_store_image_bkg));
                }
            }
        }

        private void setCategory(SaleViewHolder holder, int position) {
            if (Objects.equals(targetLanguage, "en")) {
                holder.category.setText(sales.get(position).getCategory());
            }
            else {
                TranslateRequest.translate(FavoritesActivity.this, targetLanguage, sourceLanguage, sales.get(position).getCategory(),
                        translatedText -> holder.category.setText(translatedText));
            }

            holder.category.setOnClickListener(view -> {
                if (sales.get(position).getStore().equals(storeKeywords[0])) {
                    ActivityNavigator.navigateToCategory(FavoritesActivity.this, KauflandActivity.class, sales.get(position).getCategory());
                }
                else if (sales.get(position).getStore().equals(storeKeywords[1])) {
                    ActivityNavigator.navigateToCategory(FavoritesActivity.this, LidlActivity.class, sales.get(position).getCategory());
                }
                else if (sales.get(position).getStore().equals(storeKeywords[2])) {
                    ActivityNavigator.navigateToCategory(FavoritesActivity.this, PennyActivity.class, sales.get(position).getCategory());
                }
                else if (sales.get(position).getStore().equals(storeKeywords[3])) {
                    ActivityNavigator.navigateToCategory(FavoritesActivity.this, CarrefourActivity.class, sales.get(position).getCategory());
                }
            });
        }

        private void setImage(SaleViewHolder holder, int position) {
            if (!storeName.equals(storeKeywords[2])) {
                Glide.with(FavoritesActivity.this).load(sales.get(position).getImage()).into(holder.image);
            }
        }

        private void setPeriod(SaleViewHolder holder, int position) {
            holder.period.setText(sales.get(position).getPeriod());
        }

        private void setTitle(SaleViewHolder holder, int position) {
            if (Objects.equals(targetLanguage, "ro")) {
                holder.title.setText(sales.get(position).getTitle());
            }
            else {
                TranslateRequest.translate(FavoritesActivity.this, targetLanguage, sourceLanguage, sales.get(position).getTitle(),
                        translatedText -> holder.title.setText(translatedText));
            }
        }

        private void setSubtitle(SaleViewHolder holder, int position) {
            if (sales.get(position).getSubtitle() == null)
                holder.subtitle.setVisibility(View.GONE);
            else {
                if (Objects.equals(targetLanguage, "ro")) {
                    holder.subtitle.setText(sales.get(position).getSubtitle());
                }
                else {
                    TranslateRequest.translate(FavoritesActivity.this, targetLanguage, sourceLanguage, sales.get(position).getSubtitle(),
                            translatedText -> holder.subtitle.setText(translatedText));
                }
            }
        }

        private void setQuantity(SaleViewHolder holder, int position) {
            if (sales.get(position).getQuantity() == null)
                holder.quantity.setVisibility(View.GONE);
            else {
                if (Objects.equals(targetLanguage, "ro")) {
                    holder.quantity.setText(sales.get(position).getQuantity());
                }
                else {
                    TranslateRequest.translate(FavoritesActivity.this, targetLanguage, sourceLanguage, sales.get(position).getQuantity(),
                            translatedText -> holder.quantity.setText(translatedText));
                }
            }
        }

        private void setNewPrice(SaleViewHolder holder, int position) {
            holder.newPrice.setText(sales.get(position).getNewPrice());
        }

        private void setOldPrice(SaleViewHolder holder, int position) {
            if (sales.get(position).getDiscount() == null || sales.get(position).getOldPrice() == null) {
                holder.discount.setVisibility(View.GONE);
                holder.oldPrice.setVisibility(View.GONE);
            }
            else {
                holder.discount.setText(sales.get(position).getDiscount());
                holder.oldPrice.setText(sales.get(position).getOldPrice());
                holder.oldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        private void setIsFavored(SaleViewHolder holder, int position) {
            holder.isFavored.setOnClickListener(v -> {
                favoredSalesRepository.deleteFavoredSale(storeName, categoryKeys.get(position), sales.get(position));

                int numberText = Integer.parseInt((String) storeText.getText());
                numberText--;
                storeText.setText(String.valueOf(numberText));

                sales.remove(position);
                categoryKeys.remove(position);

                adapter.notifyItemRemoved(position);
                recyclerView.setAdapter(adapter);
                recyclerView.smoothScrollToPosition(position);

                if (sales.isEmpty()) {
                    dialog.dismiss();
                }
            });
        }

        private void setLeftArrow(SaleViewHolder holder, int position) {
            if (position == 0) {
                holder.leftArrow.setVisibility(View.GONE);
            }
            else holder.leftArrow.setVisibility(View.VISIBLE);
            holder.leftArrow.setOnClickListener(view -> recyclerView.smoothScrollToPosition(position - 1));
        }


        private void setRightArrow(SaleViewHolder holder, int position) {
            if (position == sales.size() - 1) {
                holder.rightArrow.setVisibility(View.GONE);
            }
            else holder.rightArrow.setVisibility(View.VISIBLE);
            holder.rightArrow.setOnClickListener(view -> recyclerView.smoothScrollToPosition(position + 1));
        }


        private void setRecipeBtn(SaleViewHolder holder, int position) {
            if (sales.get(position).getRecipe() != null) {
                holder.recipeBtn.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                View recipeView = getLayoutInflater().inflate(R.layout.favorites_store_recipe_layout, null);

                builder.setView(recipeView);
                AlertDialog recipeDialog = builder.create();

                Recipe recipe = sales.get(position).getRecipe();
                setName(recipeView, recipe.getName());
                setIngredients(recipeView, recipe.getIngredients(), () -> setInstructions(recipeView, recipe.getInstructions()));
                setCancelBtn(recipeView, recipeDialog);

                if (recipeDialog.getWindow() != null) {
                    recipeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                holder.recipeBtn.setOnClickListener(view -> recipeDialog.show());
            }
            else holder.recipeBtn.setVisibility(View.GONE);
        }

        private void setName(View recipeView, String name) {
            TextView recipeName = recipeView.findViewById(R.id.favorites_store_recipe_name);
            TranslateRequest.translate(FavoritesActivity.this, targetLanguage, sourceLanguage, name,
                    recipeName::setText);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setIngredients(View recipeView, List<String> ingredients, IngredientsAdapter.IngredientsCallback callback) {
            RecyclerView ingredientsRecyclerView = recipeView.findViewById(R.id.favorites_store_recipe_ingredients);

            LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritesActivity.this, LinearLayoutManager.VERTICAL, false);
            ingredientsRecyclerView.setLayoutManager(layoutManager);
            ingredientsRecyclerView.setOnTouchListener((view, motionEvent) -> true);

            IngredientsAdapter adapter = new IngredientsAdapter(FavoritesActivity.this, ingredients, targetLanguage, sourceLanguage, callback);
            ingredientsRecyclerView.setAdapter(adapter);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setInstructions(View recipeView, List<String> instructions) {
            RecyclerView instructionsRecyclerView = recipeView.findViewById(R.id.favorites_store_recipe_instructions);

            LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritesActivity.this, LinearLayoutManager.VERTICAL, false);
            instructionsRecyclerView.setLayoutManager(layoutManager);
            instructionsRecyclerView.setOnTouchListener((view, motionEvent) -> true);

            InstructionsAdapter adapter = new InstructionsAdapter(FavoritesActivity.this, instructions, targetLanguage, sourceLanguage);
            instructionsRecyclerView.setAdapter(adapter);
        }

        private void setCancelBtn(View recipeView, AlertDialog recipeDialog) {
            ImageView recipeCancelButton = recipeView.findViewById(R.id.favorites_store_recipe_cancel_btn);
            recipeCancelButton.setOnClickListener(view -> recipeDialog.dismiss());
        }


        private class SaleViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout bkg, imageLayout, leftArrow, rightArrow, recipeBtn;
            TextView category, title, subtitle, quantity, period, newPrice, discount, oldPrice;
            ImageView logo, image, isFavored;

            public SaleViewHolder(@NonNull View itemView) {
                super(itemView);

                bkg = itemView.findViewById(R.id.favorites_store_bkg);
                logo = itemView.findViewById(R.id.favorites_store_logo);
                category = itemView.findViewById(R.id.favorites_store_category);
                period = itemView.findViewById(R.id.favorites_store_period);

                title = itemView.findViewById(R.id.favorites_store_title);
                subtitle = itemView.findViewById(R.id.favorites_store_subtitle);
                quantity = itemView.findViewById(R.id.favorites_store_quantity);

                newPrice = itemView.findViewById(R.id.favorites_store_new_price);
                discount = itemView.findViewById(R.id.favorites_store_discount);
                oldPrice = itemView.findViewById(R.id.favorites_store_old_price);
                isFavored = itemView.findViewById(R.id.favorites_store_btn);

                imageLayout = itemView.findViewById(R.id.favorites_store_image_layout);
                image = itemView.findViewById(R.id.favorites_store_image);

                leftArrow = itemView.findViewById(R.id.favorites_store_left_arrow);
                rightArrow = itemView.findViewById(R.id.favorites_store_right_arrow);

                recipeBtn = itemView.findViewById(R.id.favorites_store_recipe_btn);

                setBkgs();
                setLogos();
                setArrows();
            }

            private void setBkgs() {
                if (storeName.equals(storeKeywords[0])) {
                    bkg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.favorites_kaufland_bkg));
                }
                else if (storeName.equals(storeKeywords[1])) {
                    bkg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.favorites_lidl_bkg));
                }
                else if (storeName.equals(storeKeywords[2])) {
                    bkg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.favorites_penny_bkg));
                }
                else if (storeName.equals(storeKeywords[3])) {
                    bkg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.favorites_carrefour_bkg));
                }
            }

            private void setLogo(int imageResource, int dimen) {
                logo.setImageResource(imageResource);

                ViewGroup.LayoutParams params = logo.getLayoutParams();
                params.width = (int) itemView.getContext().getResources().getDimension(dimen);
                logo.setLayoutParams(params);
            }

            private void setLogos() {
                if (storeName.equals(storeKeywords[0])) {
                    setLogo(R.drawable.kaufland_logo_white, R.dimen.kauflandFavoritesDimen);
                }
                else if (storeName.equals(storeKeywords[1])) {
                    setLogo(R.drawable.lidl_logo_white, R.dimen.lidlFavoritesDimen);
                }
                else if (storeName.equals(storeKeywords[2])) {
                    setLogo(R.drawable.penny_logo_white, R.dimen.pennyFavoritesDimen);
                }
                else if (storeName.equals(storeKeywords[3])) {
                    setLogo(R.drawable.carrefour_logo_white, R.dimen.carrefourFavoritesDimen);
                }
            }

            private void setArrows() {
                if (sales.size() == 1) {
                    leftArrow.setVisibility(View.GONE);
                    rightArrow.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.favorites_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nd, R.string.close_nd);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.favorites_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nd_home || id == R.id.bn_home) {
            ActivityNavigator.navigate(this, HomeActivity.class);
        }
        else if (id == R.id.nd_location || id == R.id.bn_location) {
            ActivityNavigator.navigate(this, LocationActivity.class);
        }
        else if (id == R.id.nd_search || id == R.id.bn_search) {
            ActivityNavigator.navigate(this, SearchActivity.class);
        }
        else if (id == R.id.nd_baskets || id == R.id.bn_baskets) {
            ActivityNavigator.navigate(this, BasketsActivity.class);
        }
        else if (id == R.id.nd_profile || id == R.id.bn_profile) {
            ActivityNavigator.navigate(this, ProfileActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}