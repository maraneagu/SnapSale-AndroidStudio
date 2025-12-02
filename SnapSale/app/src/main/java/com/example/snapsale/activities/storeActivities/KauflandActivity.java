package com.example.snapsale.activities.storeActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.snapsale.callbacks.FavoredSaleCallback;
import com.example.snapsale.database.repositories.FavoredSalesRepository;
import com.example.snapsale.database.repositories.RecipesRepository;
import com.example.snapsale.database.repositories.SalesRepository;
import com.example.snapsale.database.repositories.StoresRepository;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.network.requests.TranslateRequest;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.models.FavoredSale;
import com.example.snapsale.models.Sale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snapsale.R;
import com.example.snapsale.models.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import java.util.ArrayList;


// HELPFUL LINKS:
// 1). "Android Virtical ScrollView Tutorial Example - Android Studio Tutorial", Tech Harvest BD  -  THBD, URL: https://www.youtube.com/watch?v=0oxvNM8EbvM&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=23&ab_channel=TechHarvestBD-THBD
// 2). "RecyclerView in Android Studio-Tutorial 2021", CodesKing, URL: https://www.youtube.com/watch?v=ppEw_nzCgO4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=61&t=567s&ab_channel=CodesKing
// 3). "How to Load a image from a URL in Imageview using Glide | Android Studio | Java 🔥", Android Mate, URL: https://www.youtube.com/watch?v=xrVD7LcQ5nY&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=51&ab_channel=AndroidMate
// 4). "Progress Bar in Android Studio - Mastering Android Course #34", Master Coding, URL: https://www.youtube.com/watch?v=VpnZ1wt5uDA&t=398s&ab_channel=MasterCoding


public class KauflandActivity extends AppCompatActivity{
    private static Store store;
    private static String[] kauflandCategories;

    private static StoresRepository storesRepository;
    private static SalesRepository salesRepository;
    private static FavoredSalesRepository favoredSalesRepository;
    private static RecipesRepository recipesRepository;

    private RelativeLayout meatPressedButton, vegetablesPressedButton, fishPressedButton, dairyPressedButton,
            frozenGoodsPressedButton, basicsPressedButton, bakedGoodsPressedButton, coffeePressedButton,
            sweetsPressedButton, drinksPressedButton, cosmeticsPressedButton, cleaningPressedButton,
            electronicsPressedButton, householdPressedButton, clothingPressedButton, babyPressedButton,
            animalsPressedButton, leisurePressedButton;

    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private RelativeLayout progressBarLayout, noSalesLayout;

    private static FavoredCategory currentCategory;
    private static String targetLanguage, sourceLanguage;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaufland);

        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        storesRepository = StoresRepository.getInstance();

        FirebaseUser currentUser = firebaseManager.getCurrentUser();
        if (!currentUser.isAnonymous()) {
            favoredSalesRepository = FavoredSalesRepository.getInstance();
            recipesRepository = RecipesRepository.getInstance();
        }
        salesRepository = SalesRepository.getInstance();

        kauflandCategories = getResources().getStringArray(R.array.kaufland_categories);

        targetLanguage = "en";
        sourceLanguage = "ro";

        nestedScrollView = findViewById(R.id.kaufland_nested_scroll_view);
        recyclerView = findViewById(R.id.kaufland_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBarLayout = findViewById(R.id.kaufland_progress_layout);
        noSalesLayout = findViewById(R.id.kaufland_no_sales_layout);

        meatPressedButton = findViewById(R.id.kaufland_pressed_meat_btn);
        vegetablesPressedButton = findViewById(R.id.kaufland_pressed_vegetables_btn);
        fishPressedButton = findViewById(R.id.kaufland_pressed_fish_btn);
        dairyPressedButton = findViewById(R.id.kaufland_pressed_dairy_btn);
        frozenGoodsPressedButton = findViewById(R.id.kaufland_pressed_frozen_goods_btn);
        basicsPressedButton = findViewById(R.id.kaufland_pressed_basics_btn);
        bakedGoodsPressedButton = findViewById(R.id.kaufland_pressed_baked_goods_btn);
        coffeePressedButton = findViewById(R.id.kaufland_pressed_coffee_btn);
        sweetsPressedButton = findViewById(R.id.kaufland_pressed_sweets_btn);
        drinksPressedButton = findViewById(R.id.kaufland_pressed_drinks_btn);
        cosmeticsPressedButton = findViewById(R.id.kaufland_pressed_cosmetics_btn);
        cleaningPressedButton = findViewById(R.id.kaufland_pressed_cleaning_btn);
        electronicsPressedButton = findViewById(R.id.kaufland_pressed_electronics_btn);
        householdPressedButton = findViewById(R.id.kaufland_pressed_household_btn);
        clothingPressedButton = findViewById(R.id.kaufland_pressed_clothing_btn);
        babyPressedButton = findViewById(R.id.kaufland_pressed_baby_btn);
        animalsPressedButton = findViewById(R.id.kaufland_pressed_animals_btn);
        leisurePressedButton = findViewById(R.id.kaufland_pressed_leisure_btn);

        setCategoryButtons();
        setTranslateButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Checker.checkMove(KauflandActivity.this, this::handleIntent);
    }

    private void handleIntent() {
        Intent intent = getIntent();

        if (intent != null) {
            String categoryName = intent.getStringExtra("category");
            RelativeLayout pressedButton;

            if (categoryName != null) {
                if (categoryName.equals(kauflandCategories[1])) {
                    pressedButton = vegetablesPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[2])) {
                    pressedButton = fishPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[3])) {
                    pressedButton = dairyPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[4])) {
                    pressedButton = frozenGoodsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[5])) {
                    pressedButton = basicsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[6])) {
                    pressedButton = bakedGoodsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[7])) {
                    pressedButton = coffeePressedButton;
                }
                else if (categoryName.equals(kauflandCategories[8])) {
                    pressedButton = sweetsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[9])) {
                    pressedButton = drinksPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[10])) {
                    pressedButton = cosmeticsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[11])) {
                    pressedButton = cleaningPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[12])) {
                    pressedButton = electronicsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[13])) {
                    pressedButton = householdPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[14])) {
                    pressedButton = clothingPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[15])) {
                    pressedButton = babyPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[16])) {
                    pressedButton = animalsPressedButton;
                }
                else if (categoryName.equals(kauflandCategories[17])) {
                    pressedButton = leisurePressedButton;
                }
                else {
                    pressedButton = meatPressedButton;
                }

                getStore(pressedButton, () -> getCategoryButton(pressedButton, categoryName));
                intent.removeExtra("category");
            }
            else {
                getStore(meatPressedButton, () -> getCategoryButton(meatPressedButton, kauflandCategories[0]));
            }
        }
    }

    public void getStore(RelativeLayout pressedButton, Runnable getCategoryButton) {
        pressedButton.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);

        storesRepository.getStore("kaufland", (Store sStore) -> {
            store = sStore;
            getCategoryButton.run();
        });
    }

    private void getSales(String categoryName) {
        salesRepository.getSales(store, categoryName, (FavoredCategory category, ArrayList<Sale> sales) -> {
            if (sales == null) getNoSalesLayout();
            else {
                currentCategory = category;
                setNestedScrollView(sales);
            }
        });
    }

    private void setNestedScrollView(ArrayList<Sale> sales) {
        SaleAdapter adapter = new SaleAdapter(sales);
        recyclerView.setAdapter(adapter);

        nestedScrollView.smoothScrollTo(0, 0, 400);
        progressBarLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void getNoSalesLayout() {
        nestedScrollView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
        noSalesLayout.setVisibility(View.VISIBLE);
    }


    // CATEGORY BUTTONS

    private void getCategoryButton(RelativeLayout pressedButton, String categoryName) {
        resetCategoryButtons();
        pressedButton.setVisibility(View.VISIBLE);

        nestedScrollView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);

        getSales(categoryName);
    }

    private void setCategoryButtons() {
        RelativeLayout meatButton = findViewById(R.id.kaufland_category_meat_btn);
        meatButton.setOnClickListener(view -> getCategoryButton(meatPressedButton, kauflandCategories[0]));

        RelativeLayout vegetablesButton = findViewById(R.id.kaufland_category_vegetables_btn);
        vegetablesButton.setOnClickListener(view -> getCategoryButton(vegetablesPressedButton, kauflandCategories[1]));

        RelativeLayout fishButton = findViewById(R.id.kaufland_category_fish_btn);
        fishButton.setOnClickListener(view -> getCategoryButton(fishPressedButton, kauflandCategories[2]));

        RelativeLayout dairyButton = findViewById(R.id.kaufland_category_dairy_btn);
        dairyButton.setOnClickListener(view -> getCategoryButton(dairyPressedButton, kauflandCategories[3]));

        RelativeLayout frozenGoodsButton = findViewById(R.id.kaufland_category_frozen_goods_btn);
        frozenGoodsButton.setOnClickListener(view -> getCategoryButton(frozenGoodsPressedButton, kauflandCategories[4]));

        RelativeLayout basicsButton = findViewById(R.id.kaufland_category_basics_btn);
        basicsButton.setOnClickListener(view -> getCategoryButton(basicsPressedButton, kauflandCategories[5]));

        RelativeLayout bakedGoodsButton = findViewById(R.id.kaufland_category_baked_goods_btn);
        bakedGoodsButton.setOnClickListener(view -> getCategoryButton(bakedGoodsPressedButton, kauflandCategories[6]));

        RelativeLayout coffeeButton = findViewById(R.id.kaufland_category_coffee_btn);
        coffeeButton.setOnClickListener(view -> getCategoryButton(coffeePressedButton, kauflandCategories[7]));

        RelativeLayout sweetsButton = findViewById(R.id.kaufland_category_sweets_btn);
        sweetsButton.setOnClickListener(view -> getCategoryButton(sweetsPressedButton, kauflandCategories[8]));

        RelativeLayout drinksButton = findViewById(R.id.kaufland_category_drinks_btn);
        drinksButton.setOnClickListener(view -> getCategoryButton(drinksPressedButton, kauflandCategories[9]));

        RelativeLayout cosmeticsButton = findViewById(R.id.kaufland_category_cosmetics_btn);
        cosmeticsButton.setOnClickListener(view -> getCategoryButton(cosmeticsPressedButton, kauflandCategories[10]));

        RelativeLayout cleaningButton = findViewById(R.id.kaufland_category_cleaning_btn);
        cleaningButton.setOnClickListener(view -> getCategoryButton(cleaningPressedButton, kauflandCategories[11]));

        RelativeLayout electronicsButton = findViewById(R.id.kaufland_category_electronics_btn);
        electronicsButton.setOnClickListener(view -> getCategoryButton(electronicsPressedButton, kauflandCategories[12]));

        RelativeLayout householdButton = findViewById(R.id.kaufland_category_household_btn);
        householdButton.setOnClickListener(view -> getCategoryButton(householdPressedButton, kauflandCategories[13]));

        RelativeLayout clothingButton = findViewById(R.id.kaufland_category_clothing_btn);
        clothingButton.setOnClickListener(view -> getCategoryButton(clothingPressedButton, kauflandCategories[14]));

        RelativeLayout babyButton = findViewById(R.id.kaufland_category_baby_btn);
        babyButton.setOnClickListener(view -> getCategoryButton(babyPressedButton, kauflandCategories[15]));

        RelativeLayout animalsButton = findViewById(R.id.kaufland_category_animals_btn);
        animalsButton.setOnClickListener(view -> getCategoryButton(animalsPressedButton, kauflandCategories[16]));

        RelativeLayout leisureButton = findViewById(R.id.kaufland_category_leisure_btn);
        leisureButton.setOnClickListener(view -> getCategoryButton(leisurePressedButton, kauflandCategories[17]));
    }

    private void resetCategoryButtons() {
        meatPressedButton.setVisibility(View.GONE);
        vegetablesPressedButton.setVisibility(View.GONE);
        fishPressedButton.setVisibility(View.GONE);
        dairyPressedButton.setVisibility(View.GONE);
        frozenGoodsPressedButton.setVisibility(View.GONE);
        basicsPressedButton.setVisibility(View.GONE);
        bakedGoodsPressedButton.setVisibility(View.GONE);
        coffeePressedButton.setVisibility(View.GONE);
        sweetsPressedButton.setVisibility(View.GONE);
        drinksPressedButton.setVisibility(View.GONE);
        cosmeticsPressedButton.setVisibility(View.GONE);
        cleaningPressedButton.setVisibility(View.GONE);
        electronicsPressedButton.setVisibility(View.GONE);
        householdPressedButton.setVisibility(View.GONE);
        clothingPressedButton.setVisibility(View.GONE);
        babyPressedButton.setVisibility(View.GONE);
        animalsPressedButton.setVisibility(View.GONE);
        leisurePressedButton.setVisibility(View.GONE);
    }


    // TRANSLATE BUTTON

    private void setTranslateButton() {
        ImageView translateFlag = findViewById(R.id.kaufland_translate_flag);
        RelativeLayout translateButton = findViewById(R.id.kaufland_translate_btn);

        TextView helloText = findViewById(R.id.kaufland_hello_text);
        TextView exploreText = findViewById(R.id.kaufland_explore_text);

        translateButton.setOnClickListener(view -> {
            if (sourceLanguage.equals("ro")) {
                targetLanguage = "ro";
                sourceLanguage = "en";
                translateFlag.setImageResource(R.drawable.flag_romania);

                helloText.setText(R.string.hello_romanian);
                exploreText.setText(R.string.explore_kaufland_romanian);
            }
            else {
                targetLanguage = "en";
                sourceLanguage = "ro";
                translateFlag.setImageResource(R.drawable.flag_uk);

                helloText.setText(R.string.hello);
                exploreText.setText(R.string.explore_kaufland);
            }

            getSales(currentCategory.getName());
        });
    }


    // SALE ADAPTER

    private class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {
        ArrayList<Sale> sales;

        public SaleAdapter(ArrayList<Sale> sales) {
            this.sales = sales;
        }

        @NonNull
        @Override
        public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(KauflandActivity.this).inflate(R.layout.kaufland_sale_layout, parent, false);
            return new SaleViewHolder(view);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
            setTsqLayout(holder);
            setImage(holder, position);
            setTitle(holder, position);
            setSubtitle(holder, position);
            setQuantity(holder, position);
            setNewPrice(holder, position);
            setOldPrice(holder, position);
            setPeriod(holder, position);
            setIsFavored(holder, position);
        }

        @Override
        public int getItemCount() {
            return sales.size();
        }

        private void setTsqLayout(SaleViewHolder holder) {
            holder.tsqProgressBar.setVisibility(View.VISIBLE);
            holder.tsqLayout.setVisibility(View.GONE);
        }

        private void setImage(SaleViewHolder holder, int position) {
            Glide.with(KauflandActivity.this).load(sales.get(position).getImage()).into(holder.image);
        }

        private void setTitle(SaleViewHolder holder, int position) {
            if (targetLanguage.equals("ro")) {
                holder.title.setText(sales.get(position).getTitle());

                holder.tsqProgressBar.setVisibility(View.GONE);
                holder.tsqLayout.setVisibility(View.VISIBLE);
            }
            else {
                TranslateRequest.translate(KauflandActivity.this, targetLanguage, sourceLanguage, sales.get(position).getTitle(),
                        translatedText -> {
                            holder.title.setText(translatedText);

                            holder.tsqProgressBar.setVisibility(View.GONE);
                            holder.tsqLayout.setVisibility(View.VISIBLE);
                        });
            }
        }

        private void setSubtitle(SaleViewHolder holder, int position) {
            if (sales.get(position).getSubtitle() == null) holder.subtitle.setVisibility(View.GONE);
            else {
                if (targetLanguage.equals("ro")) {
                    holder.subtitle.setText(sales.get(position).getSubtitle());
                }
                else {
                    TranslateRequest.translate(KauflandActivity.this, targetLanguage, sourceLanguage, sales.get(position).getSubtitle(),
                            translatedText -> holder.subtitle.setText(translatedText));
                }
            }
        }

        private void setQuantity(SaleViewHolder holder, int position) {
            if (sales.get(position).getQuantity() == null) holder.quantity.setVisibility(View.GONE);
            else {
                if (targetLanguage.equals("ro")) {
                    holder.quantity.setText(sales.get(position).getQuantity());
                }
                else {
                    TranslateRequest.translate(KauflandActivity.this, targetLanguage, sourceLanguage, sales.get(position).getQuantity(),
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

        private void setPeriod(SaleViewHolder holder, int position) {
            holder.period.setText(sales.get(position).getPeriod());
        }

        private void setIsFavored(SaleViewHolder holder, int position) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null && currentUser.isAnonymous()) {
                holder.isFavoredButton.setVisibility(View.GONE);
            }
            else {
                favoredSalesRepository.checkIsFavored(store, currentCategory, sales.get(position).getKey(), holder.favoredSaleCallback);

                holder.isFavoredButton.setOnClickListener(v -> {
                    if (holder.isFavored) {
                        holder.isFavoredButton.setImageResource(R.drawable.icon_heart_outline_yellow);
                        favoredSalesRepository.deleteFavoredSale(store, currentCategory, sales.get(position).getKey());
                    }
                    else {
                        holder.isFavoredButton.setImageResource(R.drawable.icon_heart_yellow);
                        FavoredSale favoredSale = favoredSalesRepository.getFavoredSale(sales.get(position));
                        favoredSalesRepository.addFavoredSale(store, currentCategory, favoredSale);

                        if (isRecipeCategory(currentCategory.getName())) {
                            try {
                                recipesRepository.addRecipe(store.getKey(), currentCategory.getKey(), favoredSale);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    holder.isFavored = !holder.isFavored;
                });
            }
        }

        private boolean isRecipeCategory(String categoryName) {
            return !categoryName.equals(kauflandCategories[10]) &&
                    !categoryName.equals(kauflandCategories[11]) &&
                    !categoryName.equals(kauflandCategories[12]) &&
                    !categoryName.equals(kauflandCategories[13]) &&
                    !categoryName.equals(kauflandCategories[14]) &&
                    !categoryName.equals(kauflandCategories[15]) &&
                    !categoryName.equals(kauflandCategories[16]) &&
                    !categoryName.equals(kauflandCategories[17]);
        }


        private class SaleViewHolder extends RecyclerView.ViewHolder {
            ImageView image, isFavoredButton;
            TextView title, subtitle, quantity, newPrice,
                    discount, oldPrice, period;
            RelativeLayout tsqLayout, tsqProgressBar;
            FavoredSaleCallback favoredSaleCallback;
            boolean isFavored;

            public SaleViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.kaufland_sale_image);
                title = itemView.findViewById(R.id.kaufland_sale_title);
                subtitle = itemView.findViewById(R.id.kaufland_sale_subtitle);
                quantity = itemView.findViewById(R.id.kaufland_sale_quantity);
                newPrice = itemView.findViewById(R.id.kaufland_sale_new_price);
                discount = itemView.findViewById(R.id.kaufland_sale_discount);
                oldPrice = itemView.findViewById(R.id.kaufland_sale_old_price);
                period = itemView.findViewById(R.id.kaufland_sale_period);
                isFavoredButton = itemView.findViewById(R.id.kaufland_sale_favorite);

                tsqProgressBar = itemView.findViewById(R.id.kaufland_sale_tsq_progress_layout);
                tsqLayout = itemView.findViewById(R.id.kaufland_sale_tsq_layout);

                setFavoredSaleCallback();
            }

            private void setFavoredSaleCallback() {
                favoredSaleCallback = new FavoredSaleCallback() {
                    @Override
                    public void onFavoredSaleFound() {
                        isFavoredButton.setImageResource(R.drawable.icon_heart_yellow);
                        isFavored = true;
                    }

                    @Override
                    public void onFavoredSaleNotFound() {
                        isFavoredButton.setImageResource(R.drawable.icon_heart_outline_yellow);
                        isFavored = false;
                    }
                };
            }
        }
    }
}