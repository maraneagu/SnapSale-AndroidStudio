package com.example.snapsale.activities.storeActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class LidlActivity extends AppCompatActivity {
    private static Store store;
    private static String[] lidlCategories;

    private static StoresRepository storesRepository;
    private static SalesRepository salesRepository;
    private static FavoredSalesRepository favoredSalesRepository;
    private static RecipesRepository recipesRepository;

    private RelativeLayout mondayPressedButton, tuesdayPressedButton, thursdayPressedButton, weekendPressedButton, marketPressedButton, pricePressedButton, plusPressedButton;
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private RelativeLayout progressBarLayout, noSalesLayout;

    private static FavoredCategory currentCategory;
    private static String targetLanguage, sourceLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lidl);

        storesRepository = StoresRepository.getInstance();
        favoredSalesRepository = FavoredSalesRepository.getInstance();
        salesRepository = SalesRepository.getInstance();
        recipesRepository = RecipesRepository.getInstance();

        lidlCategories = getResources().getStringArray(R.array.lidl_categories);

        targetLanguage = "en";
        sourceLanguage = "ro";

        nestedScrollView = findViewById(R.id.lidl_nested_scroll_view);
        recyclerView = findViewById(R.id.lidl_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBarLayout = findViewById(R.id.lidl_progress_layout);
        noSalesLayout = findViewById(R.id.lidl_no_sales_layout);

        mondayPressedButton = findViewById(R.id.lidl_pressed_monday_btn);
        tuesdayPressedButton = findViewById(R.id.lidl_pressed_tuesday_btn);
        thursdayPressedButton = findViewById(R.id.lidl_pressed_thursday_btn);
        weekendPressedButton = findViewById(R.id.lidl_pressed_weekend_btn);
        marketPressedButton = findViewById(R.id.lidl_pressed_market_btn);
        pricePressedButton = findViewById(R.id.lidl_pressed_price_btn);
        plusPressedButton = findViewById(R.id.lidl_pressed_plus_btn);

        setTranslateButton();
        setCategoryButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Checker.checkMove(LidlActivity.this, this::handleIntent);
    }

    private void handleIntent() {
        Intent intent = getIntent();

        if (intent != null) {
            String categoryName = intent.getStringExtra("category");
            RelativeLayout pressedButton;

            if (categoryName != null) {
                if (categoryName.equals(lidlCategories[1])) {
                    pressedButton = tuesdayPressedButton;
                }
                else if (categoryName.equals(lidlCategories[2])) {
                    pressedButton = thursdayPressedButton;
                }
                else if (categoryName.equals(lidlCategories[3])) {
                    pressedButton = weekendPressedButton;
                }
                else if (categoryName.equals(lidlCategories[4])) {
                    pressedButton = marketPressedButton;
                }
                else if (categoryName.equals(lidlCategories[5])) {
                    pressedButton = pricePressedButton;
                }
                else if (categoryName.equals(lidlCategories[6])) {
                    pressedButton = plusPressedButton;
                }
                else {
                    pressedButton = mondayPressedButton;
                }

                getStore(pressedButton, () -> getCategoryButton(pressedButton, categoryName));
                intent.removeExtra("category");
            }
            else {
                getStore(mondayPressedButton, () -> getCategoryButton(mondayPressedButton, lidlCategories[0]));
            }
        }
    }


    public void getStore(RelativeLayout pressedButton, Runnable getCategoryButton) {
        pressedButton.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);

        storesRepository.getStore("lidl", (Store sStore) -> {
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
        nestedScrollView.smoothScrollTo(0, 0, 400);
        progressBarLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);

        SaleAdapter adapter = new SaleAdapter(sales);
        recyclerView.setAdapter(adapter);
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
        RelativeLayout mondayButton = findViewById(R.id.lidl_category_monday_btn);
        mondayButton.setOnClickListener(view -> getCategoryButton(mondayPressedButton, lidlCategories[0]));

        RelativeLayout tuesdayButton = findViewById(R.id.lidl_category_tuesday_btn);
        tuesdayButton.setOnClickListener(view -> getCategoryButton(tuesdayPressedButton, lidlCategories[1]));

        RelativeLayout thursdayButton = findViewById(R.id.lidl_category_thursday_btn);
        thursdayButton.setOnClickListener(view -> getCategoryButton(thursdayPressedButton, lidlCategories[2]));

        RelativeLayout weekendButton = findViewById(R.id.lidl_category_weekend_btn);
        weekendButton.setOnClickListener(view -> getCategoryButton(weekendPressedButton, lidlCategories[3]));

        RelativeLayout marketButton = findViewById(R.id.lidl_category_market_btn);
        marketButton.setOnClickListener(view -> getCategoryButton(marketPressedButton, lidlCategories[4]));

        RelativeLayout priceButton = findViewById(R.id.lidl_category_price_btn);
        priceButton.setOnClickListener(view -> getCategoryButton(pricePressedButton, lidlCategories[5]));

        RelativeLayout plusButton = findViewById(R.id.lidl_category_plus_btn);
        plusButton.setOnClickListener(view -> getCategoryButton(plusPressedButton, lidlCategories[6]));
    }

    private void resetCategoryButtons() {
        mondayPressedButton.setVisibility(View.GONE);
        tuesdayPressedButton.setVisibility(View.GONE);
        thursdayPressedButton.setVisibility(View.GONE);
        weekendPressedButton.setVisibility(View.GONE);
        marketPressedButton.setVisibility(View.GONE);
        pricePressedButton.setVisibility(View.GONE);
        plusPressedButton.setVisibility(View.GONE);
    }


    // TRANSLATE BUTTON

    private void setTranslateButton() {
        ImageView translateFlag = findViewById(R.id.lidl_translate_flag);
        RelativeLayout translateButton = findViewById(R.id.lidl_translate_btn);

        TextView helloText = findViewById(R.id.lidl_hello_text);
        TextView exploreText = findViewById(R.id.lidl_explore_text);

        TextView mondayText = findViewById(R.id.lidl_category_monday_text);
        TextView tuesdayText = findViewById(R.id.lidl_category_tuesday_text);
        TextView thursdayText = findViewById(R.id.lidl_category_thursday_text);
        TextView marketText = findViewById(R.id.lidl_category_market_text);
        TextView priceText = findViewById(R.id.lidl_category_price_text);

        translateButton.setOnClickListener(view -> {
            if (sourceLanguage.equals("ro")) {
                targetLanguage = "ro";
                sourceLanguage = "en";
                translateFlag.setImageResource(R.drawable.flag_romania);

                helloText.setText(R.string.hello_romanian);
                exploreText.setText(R.string.explore_lidl_romanian);

                mondayText.setText(R.string.monday_romanian);
                tuesdayText.setText(R.string.tuesday_romanian);
                thursdayText.setText(R.string.thursday_romanian);
                marketText.setText(R.string.market_romanian);
                priceText.setText(R.string.lidl_price_romanian);
            }
            else {
                targetLanguage = "en";
                sourceLanguage = "ro";
                translateFlag.setImageResource(R.drawable.flag_uk);

                helloText.setText(R.string.hello);
                exploreText.setText(R.string.explore_lidl);

                mondayText.setText(R.string.monday);
                tuesdayText.setText(R.string.tuesday);
                thursdayText.setText(R.string.thursday);
                marketText.setText(R.string.market);
                priceText.setText(R.string.lidl_price);
            }

            getSales(currentCategory.getName());
        });
    }


    // SALE ADAPTER

    private class SaleAdapter extends RecyclerView.Adapter<LidlActivity.SaleAdapter.SaleViewHolder> {
        ArrayList<Sale> sales;

        public SaleAdapter(ArrayList<Sale> sales) {
            this.sales = sales;
        }

        @NonNull
        @Override
        public LidlActivity.SaleAdapter.SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LidlActivity.this).inflate(R.layout.lidl_sale_layout, parent, false);
            return new LidlActivity.SaleAdapter.SaleViewHolder(view);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull LidlActivity.SaleAdapter.SaleViewHolder holder, int position) {
            setFpLayout(holder);
            setItsqLayout(holder);
            setTsqLayout(holder);
            setImage(holder, position);
            setTitle(holder, position);
            setSubtitle(holder, position);
            setQuantity(holder, position);
            setPriceLayout(holder);
            setNewPrice(holder, position);
            setOldPrice(holder, position);
            setPeriod(holder, position);
            setIsFavored(holder, position);
        }

        @Override
        public int getItemCount() {
            return sales.size();
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setFpLayout(SaleViewHolder holder) {
            if (currentCategory.getName().equals("market")) {
                holder.fpLayout.setBackground(getResources().getDrawable(R.drawable.lidl_sale_market_period_bkg));
            }
        }

        private void setItsqLayout(SaleViewHolder holder) {
            if (currentCategory.getName().equals("market")) {
                holder.itsqLayout.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }

        private void setTsqLayout(SaleViewHolder holder) {
            holder.tsqProgressBar.setVisibility(View.VISIBLE);
            holder.tsqLayout.setVisibility(View.GONE);
        }

        private void setImage(SaleViewHolder holder, int position) {
            Glide.with(LidlActivity.this).load(sales.get(position).getImage()).into(holder.image);
        }

        private void setTitle(SaleViewHolder holder, int position) {
            if (targetLanguage.equals("ro")) {
                holder.title.setText(sales.get(position).getTitle());

                holder.tsqProgressBar.setVisibility(View.GONE);
                holder.tsqLayout.setVisibility(View.VISIBLE);
            }
            else {
                TranslateRequest.translate(LidlActivity.this, targetLanguage, sourceLanguage, sales.get(position).getTitle(),
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
                    TranslateRequest.translate(LidlActivity.this, targetLanguage, sourceLanguage, sales.get(position).getSubtitle(),
                            translatedText -> holder.subtitle.setText(translatedText));
                }
            }
        }

        private void setQuantity(SaleViewHolder holder, int position) {
            if (targetLanguage.equals("ro")) {
                holder.quantity.setText(sales.get(position).getQuantity());
            }
            else {
                TranslateRequest.translate(LidlActivity.this, targetLanguage, sourceLanguage, sales.get(position).getQuantity(),
                        translatedText -> holder.quantity.setText(translatedText));
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setPriceLayout(SaleViewHolder holder) {
            if (currentCategory.getName().equals("market")) {
                holder.priceLayout.setBackground(getResources().getDrawable(R.drawable.lidl_sale_market_price_bkg));
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
                    holder.isFavoredButton.setImageResource(R.drawable.icon_heart_outline_blue);
                    favoredSalesRepository.deleteFavoredSale(store, currentCategory, sales.get(position).getKey());
                }
                else {
                    holder.isFavoredButton.setImageResource(R.drawable.icon_heart_blue);
                    FavoredSale favoredSale = favoredSalesRepository.getFavoredSale(sales.get(position));
                    favoredSalesRepository.addFavoredSale(store, currentCategory, favoredSale);

                    try {
                        recipesRepository.addRecipe(store.getKey(), currentCategory.getKey(), favoredSale);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                holder.isFavored = !holder.isFavored;
            });
        }

        private class SaleViewHolder extends RecyclerView.ViewHolder {
            ImageView image, isFavoredButton;
            TextView title, subtitle, quantity, newPrice,
                    discount, oldPrice, period;
            LinearLayout itsqLayout;
            RelativeLayout fpLayout, tsqLayout, tsqProgressBar, priceLayout;

            FavoredSaleCallback favoredSaleCallback;
            boolean isFavored;

            public SaleViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.lidl_sale_image);
                title = itemView.findViewById(R.id.lidl_sale_title);
                subtitle = itemView.findViewById(R.id.lidl_sale_subtitle);
                quantity = itemView.findViewById(R.id.lidl_sale_quantity);
                newPrice = itemView.findViewById(R.id.lidl_sale_new_price);
                discount = itemView.findViewById(R.id.lidl_sale_discount);
                oldPrice = itemView.findViewById(R.id.lidl_sale_old_price);
                period = itemView.findViewById(R.id.lidl_sale_period);
                isFavoredButton = itemView.findViewById(R.id.lidl_sale_favorite);

                fpLayout = itemView.findViewById(R.id.lidl_sale_favorite_period_layout);
                tsqProgressBar = itemView.findViewById(R.id.lidl_sale_tsq_progress_layout);
                tsqLayout = itemView.findViewById(R.id.lidl_sale_tsq_layout);
                itsqLayout = itemView.findViewById(R.id.lidl_sale_itsq_layout);
                priceLayout = itemView.findViewById(R.id.lidl_sale_price_layout);

                setFavoredSaleCallback();
            }

            private void setFavoredSaleCallback() {
                favoredSaleCallback = new FavoredSaleCallback() {
                    @Override
                    public void onFavoredSaleFound() {
                        isFavoredButton.setImageResource(R.drawable.icon_heart_blue);
                        isFavored = true;
                    }

                    @Override
                    public void onFavoredSaleNotFound() {
                        isFavoredButton.setImageResource(R.drawable.icon_heart_outline_blue);
                        isFavored = false;
                    }
                };
            }
        }
    }
}