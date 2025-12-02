package com.example.snapsale.activities.storeActivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.snapsale.R;
import com.example.snapsale.callbacks.FavoredSaleCallback;
import com.example.snapsale.database.repositories.FavoredSalesRepository;
import com.example.snapsale.database.repositories.RecipesRepository;
import com.example.snapsale.database.repositories.SalesRepository;
import com.example.snapsale.database.repositories.StoresRepository;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.network.requests.TranslateRequest;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.models.FavoredSale;
import com.example.snapsale.models.Sale;
import com.example.snapsale.models.Store;

import org.json.JSONException;

import java.util.ArrayList;


// HELPFUL LINKS:
// 1). "Android Virtical ScrollView Tutorial Example - Android Studio Tutorial", Tech Harvest BD  -  THBD, URL: https://www.youtube.com/watch?v=0oxvNM8EbvM&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=23&ab_channel=TechHarvestBD-THBD
// 2). "RecyclerView in Android Studio-Tutorial 2021", CodesKing, URL: https://www.youtube.com/watch?v=ppEw_nzCgO4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=61&t=567s&ab_channel=CodesKing
// 3). "How to Load a image from a URL in Imageview using Glide | Android Studio | Java 🔥", Android Mate, URL: https://www.youtube.com/watch?v=xrVD7LcQ5nY&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=51&ab_channel=AndroidMate
// 4). "Progress Bar in Android Studio - Mastering Android Course #34", Master Coding, URL: https://www.youtube.com/watch?v=VpnZ1wt5uDA&t=398s&ab_channel=MasterCoding


public class CarrefourActivity extends AppCompatActivity {
    private static Store store;
    private static String[] carrefourCategories;

    private static StoresRepository storesRepository;
    private static SalesRepository salesRepository;
    private static FavoredSalesRepository favoredSalesRepository;
    private static RecipesRepository recipesRepository;

    private RelativeLayout vegetablesPressedButton, meatPressedButton, frozenGoodsPressedButton, dairyPressedButton,
            basicsPressedButton, tinsPressedButton, babyPressedButton, sweetsPressedButton,
            coffeePressedButton, animalsPressedButton, drinksPressedButton, juicesPressedButton,
            cosmeticsPressedButton, cleaningPressedButton;

    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private RelativeLayout progressBarLayout, noSalesLayout;

    private static FavoredCategory currentCategory;
    private static String targetLanguage, sourceLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrefour);

        storesRepository = StoresRepository.getInstance();
        salesRepository = SalesRepository.getInstance();
        favoredSalesRepository = FavoredSalesRepository.getInstance();
        recipesRepository = RecipesRepository.getInstance();

        carrefourCategories = getResources().getStringArray(R.array.carrefour_categories);

        targetLanguage = "en";
        sourceLanguage = "ro";

        nestedScrollView = findViewById(R.id.carrefour_nested_scroll_view);
        recyclerView = findViewById(R.id.carrefour_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBarLayout = findViewById(R.id.carrefour_progress_layout);
        noSalesLayout = findViewById(R.id.carrefour_no_sales_layout);

        vegetablesPressedButton = findViewById(R.id.carrefour_pressed_vegetables_btn);
        meatPressedButton = findViewById(R.id.carrefour_pressed_meat_btn);
        frozenGoodsPressedButton = findViewById(R.id.carrefour_pressed_frozen_goods_btn);
        dairyPressedButton = findViewById(R.id.carrefour_pressed_dairy_btn);
        basicsPressedButton = findViewById(R.id.carrefour_pressed_basics_btn);
        tinsPressedButton = findViewById(R.id.carrefour_pressed_tins_btn);
        babyPressedButton = findViewById(R.id.carrefour_pressed_baby_btn);
        sweetsPressedButton = findViewById(R.id.carrefour_pressed_sweets_btn);
        coffeePressedButton = findViewById(R.id.carrefour_pressed_coffee_btn);
        animalsPressedButton = findViewById(R.id.carrefour_pressed_animals_btn);
        drinksPressedButton = findViewById(R.id.carrefour_pressed_drinks_btn);
        juicesPressedButton = findViewById(R.id.carrefour_pressed_juices_btn);
        cosmeticsPressedButton = findViewById(R.id.carrefour_pressed_cosmetics_btn);
        cleaningPressedButton = findViewById(R.id.carrefour_pressed_cleaning_btn);

        setTranslateButton();
        setCategoryButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Checker.checkMove(CarrefourActivity.this, this::handleIntent);
    }

    private void handleIntent() {
        Intent intent = getIntent();

        if (intent != null) {
            String categoryName = intent.getStringExtra("category");
            RelativeLayout pressedButton;

            if (categoryName != null) {
                if (categoryName.equals(carrefourCategories[1])) {
                    pressedButton = meatPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[2])) {
                    pressedButton = frozenGoodsPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[3])) {
                    pressedButton = dairyPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[4])) {
                    pressedButton = basicsPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[5])) {
                    pressedButton = tinsPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[6])) {
                    pressedButton = babyPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[7])) {
                    pressedButton = sweetsPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[8])) {
                    pressedButton = coffeePressedButton;
                }
                else if (categoryName.equals(carrefourCategories[9])) {
                    pressedButton = animalsPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[10])) {
                    pressedButton = drinksPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[11])) {
                    pressedButton = juicesPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[12])) {
                    pressedButton = cosmeticsPressedButton;
                }
                else if (categoryName.equals(carrefourCategories[13])) {
                    pressedButton = cleaningPressedButton;
                }
                else {
                    pressedButton = vegetablesPressedButton;
                }

                getStore(pressedButton, () -> getCategoryButton(pressedButton, categoryName));
                intent.removeExtra("category");
            }
            else {
                getStore(vegetablesPressedButton, () -> getCategoryButton(vegetablesPressedButton, carrefourCategories[0]));
            }
        }
    }


    public void getStore(RelativeLayout pressedButton, Runnable getCategoryButton) {
        pressedButton.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);

        storesRepository.getStore("carrefour", (Store sStore) -> {
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
        RelativeLayout vegetablesButton = findViewById(R.id.carrefour_category_vegetables_btn);
        vegetablesButton.setOnClickListener(view -> getCategoryButton(vegetablesPressedButton, carrefourCategories[0]));

        RelativeLayout meatButton = findViewById(R.id.carrefour_category_meat_btn);
        meatButton.setOnClickListener(view -> getCategoryButton(meatPressedButton, carrefourCategories[1]));

        RelativeLayout frozenGoodsButton = findViewById(R.id.carrefour_category_frozen_goods_btn);
        frozenGoodsButton.setOnClickListener(view -> getCategoryButton(frozenGoodsPressedButton, carrefourCategories[2]));

        RelativeLayout dairyButton = findViewById(R.id.carrefour_category_dairy_btn);
        dairyButton.setOnClickListener(view -> getCategoryButton(dairyPressedButton, carrefourCategories[3]));

        RelativeLayout basicsButton = findViewById(R.id.carrefour_category_basics_btn);
        basicsButton.setOnClickListener(view -> getCategoryButton(basicsPressedButton, carrefourCategories[4]));

        RelativeLayout tinsButton = findViewById(R.id.carrefour_category_tins_btn);
        tinsButton.setOnClickListener(view -> getCategoryButton(tinsPressedButton, carrefourCategories[5]));

        RelativeLayout babyButton = findViewById(R.id.carrefour_category_baby_btn);
        babyButton.setOnClickListener(view -> getCategoryButton(babyPressedButton, carrefourCategories[6]));

        RelativeLayout sweetsButton = findViewById(R.id.carrefour_category_sweets_btn);
        sweetsButton.setOnClickListener(view -> getCategoryButton(sweetsPressedButton, carrefourCategories[7]));

        RelativeLayout coffeeButton = findViewById(R.id.carrefour_category_coffee_btn);
        coffeeButton.setOnClickListener(view -> getCategoryButton(coffeePressedButton, carrefourCategories[8]));

        RelativeLayout animalsButton = findViewById(R.id.carrefour_category_animals_btn);
        animalsButton.setOnClickListener(view -> getCategoryButton(animalsPressedButton, carrefourCategories[9]));

        RelativeLayout drinksButton = findViewById(R.id.carrefour_category_drinks_btn);
        drinksButton.setOnClickListener(view -> getCategoryButton(drinksPressedButton, carrefourCategories[10]));

        RelativeLayout juicesButton = findViewById(R.id.carrefour_category_juices_btn);
        juicesButton.setOnClickListener(view -> getCategoryButton(juicesPressedButton, carrefourCategories[11]));

        RelativeLayout cosmeticsButton = findViewById(R.id.carrefour_category_cosmetics_btn);
        cosmeticsButton.setOnClickListener(view -> getCategoryButton(cosmeticsPressedButton, carrefourCategories[12]));

        RelativeLayout cleaningButton = findViewById(R.id.carrefour_category_cleaning_btn);
        cleaningButton.setOnClickListener(view -> getCategoryButton(cleaningPressedButton, carrefourCategories[13]));
    }

    private void resetCategoryButtons() {
        vegetablesPressedButton.setVisibility(View.GONE);
        meatPressedButton.setVisibility(View.GONE);
        frozenGoodsPressedButton.setVisibility(View.GONE);
        dairyPressedButton.setVisibility(View.GONE);
        basicsPressedButton.setVisibility(View.GONE);
        tinsPressedButton.setVisibility(View.GONE);
        babyPressedButton.setVisibility(View.GONE);
        sweetsPressedButton.setVisibility(View.GONE);
        coffeePressedButton.setVisibility(View.GONE);
        animalsPressedButton.setVisibility(View.GONE);
        drinksPressedButton.setVisibility(View.GONE);
        juicesPressedButton.setVisibility(View.GONE);
        cosmeticsPressedButton.setVisibility(View.GONE);
        cleaningPressedButton.setVisibility(View.GONE);
    }

    // TRANSLATE BUTTON

    private void setTranslateButton() {
        ImageView translateFlag = findViewById(R.id.carrefour_translate_flag);
        RelativeLayout translateButton = findViewById(R.id.carrefour_translate_btn);

        TextView helloText = findViewById(R.id.carrefour_hello_text);
        TextView exploreText = findViewById(R.id.carrefour_explore_text);

        translateButton.setOnClickListener(view -> {
            if (sourceLanguage.equals("ro")) {
                targetLanguage = "ro";
                sourceLanguage = "en";
                translateFlag.setImageResource(R.drawable.flag_romania);

                helloText.setText(R.string.hello_romanian);
                exploreText.setText(R.string.explore_carrefour_romanian);
            }
            else {
                targetLanguage = "en";
                sourceLanguage = "ro";
                translateFlag.setImageResource(R.drawable.flag_uk);

                helloText.setText(R.string.hello);
                exploreText.setText(R.string.explore_carrefour);
            }

            getSales(currentCategory.getName());
        });
    }


    // SALE ADAPTER

    private class SaleAdapter extends RecyclerView.Adapter<CarrefourActivity.SaleAdapter.SaleViewHolder> {
        ArrayList<Sale> sales;

        public SaleAdapter(ArrayList<Sale> sales) {
            this.sales = sales;
        }

        @NonNull
        @Override
        public CarrefourActivity.SaleAdapter.SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CarrefourActivity.this).inflate(R.layout.carrefour_sale_layout, parent, false);
            return new CarrefourActivity.SaleAdapter.SaleViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull CarrefourActivity.SaleAdapter.SaleViewHolder holder, int position) {
            setTsqLayout(holder);
            setImage(holder, position);
            setTitle(holder, position);
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
            Glide.with(CarrefourActivity.this).load(sales.get(position).getImage()).into(holder.image);
        }

        private void setTitle(SaleViewHolder holder, int position) {
            if (targetLanguage.equals("ro")) {
                holder.title.setText(sales.get(position).getTitle());

                holder.tsqProgressBar.setVisibility(View.GONE);
                holder.tsqLayout.setVisibility(View.VISIBLE);
            }
            else {
                TranslateRequest.translate(CarrefourActivity.this, targetLanguage, sourceLanguage, sales.get(position).getTitle(),
                        translatedText -> {
                            holder.title.setText(translatedText);

                            holder.tsqProgressBar.setVisibility(View.GONE);
                            holder.tsqLayout.setVisibility(View.VISIBLE);
                        });
            }
        }

        private void setQuantity(SaleViewHolder holder, int position) {
            if (targetLanguage.equals("ro")) {
                holder.quantity.setText(sales.get(position).getQuantity());
            }
            else {
                TranslateRequest.translate(CarrefourActivity.this, targetLanguage, sourceLanguage, sales.get(position).getQuantity(),
                        translatedText -> holder.quantity.setText(translatedText));
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
            favoredSalesRepository.checkIsFavored(store, currentCategory, sales.get(position).getKey(), holder.favoredSaleCallback);

            holder.isFavoredButton.setOnClickListener(v -> {
                if (holder.isFavored) {
                    holder.isFavoredButton.setImageResource(R.drawable.icon_heart_outline_light_yellow);
                    favoredSalesRepository.deleteFavoredSale(store, currentCategory, sales.get(position).getKey());
                }
                else {
                    holder.isFavoredButton.setImageResource(R.drawable.icon_heart_light_yellow);
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

        private boolean isRecipeCategory(String categoryName) {
            return !categoryName.equals(carrefourCategories[6]) &&
                    !categoryName.equals(carrefourCategories[9]) &&
                    !categoryName.equals(carrefourCategories[12]) &&
                    !categoryName.equals(carrefourCategories[13]);
        }

        private class SaleViewHolder extends RecyclerView.ViewHolder {
            ImageView image, isFavoredButton;
            TextView title, quantity, newPrice,
                    discount, oldPrice, period;
            RelativeLayout tsqLayout, tsqProgressBar;
            FavoredSaleCallback favoredSaleCallback;
            boolean isFavored;

            public SaleViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.carrefour_sale_image);
                title = itemView.findViewById(R.id.carrefour_sale_title);
                quantity = itemView.findViewById(R.id.carrefour_sale_quantity);
                newPrice = itemView.findViewById(R.id.carrefour_sale_new_price);
                discount = itemView.findViewById(R.id.carrefour_sale_discount);
                oldPrice = itemView.findViewById(R.id.carrefour_sale_old_price);
                period = itemView.findViewById(R.id.carrefour_sale_period);
                isFavoredButton = itemView.findViewById(R.id.carrefour_sale_favorite);

                tsqProgressBar = itemView.findViewById(R.id.carrefour_sale_tsq_progress_layout);
                tsqLayout = itemView.findViewById(R.id.carrefour_sale_tsq_layout);

                setFavoredSaleCallback();
            }

            private void setFavoredSaleCallback() {
                favoredSaleCallback = new FavoredSaleCallback() {
                    @Override
                    public void onFavoredSaleFound() {
                        isFavoredButton.setImageResource(R.drawable.icon_heart_light_yellow);
                        isFavored = true;
                    }

                    @Override
                    public void onFavoredSaleNotFound() {
                        isFavoredButton.setImageResource(R.drawable.icon_heart_outline_light_yellow);
                        isFavored = false;
                    }
                };
            }
        }
    }
 }