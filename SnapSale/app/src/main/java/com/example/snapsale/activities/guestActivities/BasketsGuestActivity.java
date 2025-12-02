package com.example.snapsale.activities.guestActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.snapsale.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snapsale.activities.mainActivities.BasketsActivity;
import com.example.snapsale.database.repositories.BasketsRepository;
import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.navigation.CustomBottomNavigationView;
import com.example.snapsale.network.requests.TranslateRequest;
import com.example.snapsale.models.Basket;
import com.example.snapsale.models.Sale;
import com.example.snapsale.activities.storeActivities.LidlActivity;
import com.example.snapsale.activities.storeActivities.PennyActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


// HELPFUL LINKS:
// 1). "Create Custom Alert Dialog Box in Android Studio using Java | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=3RTpdB-RszY&ab_channel=AndroidKnowledge
// 2). "RecyclerView in Android Studio-Tutorial 2021", CodesKing, URL: https://www.youtube.com/watch?v=ppEw_nzCgO4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=61&t=567s&ab_channel=CodesKing
// 3). "Android tutorial - 22 - How Create Grid Layout Recyclerview | Images & Text Recyclerview", Technical Skillz, URL: https://www.youtube.com/watch?v=PDz-fXL7q9A&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=68&t=267s&ab_channel=TechnicalSkillz
// 4). "Upload Retrieve Image from Firebase & Display in RecyclerView, GridView, Staggered | Android Studio", Android Knowdledge, URL: https://www.youtube.com/watch?v=Hn89s4oCdS8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=52&t=2451s&ab_channel=AndroidKnowledge
// 5). "How to Load a image from a URL in Imageview using Glide | Android Studio | Java 🔥", Android Mate, URL: https://www.youtube.com/watch?v=xrVD7LcQ5nY&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=51&ab_channel=AndroidMate
// 6). "How to Change Toggle (Button) Color and ToolBar Text Color in Android Studio App", Any Technology, URL: https://www.youtube.com/watch?v=0CrhNNhQfgc&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=29&ab_channel=AnyTechnology
// 7). "How to Implement Bottom Navigation with Activities | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=MUl19ppdu0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=22&ab_channel=AndroidKnowledge
// 8). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view
// 9). "Navigation Drawer Menu in Android Tutorial | How to Create Navigation Drawer in Android Studio", Code with Surya, URL: https://www.youtube.com/watch?v=uY9iZiamyZs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=69&t=1187s&ab_channel=CodewithSurya


public class BasketsGuestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CustomBottomNavigationView.OnNavigationItemSelectedListener {

    private static BasketsRepository basketsRepository;
    private String[] storeKeywords;

    private TextView pennyNumberText, lidlNumberText;

    private static String targetLanguage, sourceLanguage;

    private DrawerLayout drawerLayout;
    private CustomBottomNavigationView navigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baskets_guest);

        basketsRepository = BasketsRepository.getInstance();

        targetLanguage = "en";
        sourceLanguage = "ro";

        drawerLayout = findViewById(R.id.baskets_navigation_drawer_layout);
        setNavigationDrawer();

        navigationBar = findViewById(R.id.baskets_navigation_bar);
        navigationBar.setSelectedItemId(R.id.bn_baskets);
        navigationBar.setOnNavigationItemSelectedListener(this);

        storeKeywords = getResources().getStringArray(R.array.store_keywords);
        lidlNumberText = findViewById(R.id.baskets_lidl_number);
        pennyNumberText = findViewById(R.id.baskets_penny_number);

        setButtons();
        setTranslateButton();

        handleItent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigationBar.setSelectedItemId(R.id.bn_baskets);
        Checker.checkMove(BasketsGuestActivity.this, this::getBasketsCount);
    }

    // !
    // FLAG_ACTIVITY_REORDER_TO_FRONT may resume the target activity with its previous state if it is already running.
    // This means the new intent extras might not be automatically processed.
    // When the activity is brought to the front, onNewIntent(Intent) is called to handle the new intent.
    // The extra data is processed in this function to ensure the extra strings are handled correctly.
    // !

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleItent(intent);
    }

    private void handleItent(Intent intent) {
        if (intent != null) {
            String storeName = intent.getStringExtra("store");
            if (storeName != null) {
                if (storeName.equals(storeKeywords[1]))
                    getBaskets(storeKeywords[1],
                            R.drawable.lidl_logo_white_border,
                            R.drawable.baskets_lidl_logo_bkg,
                            R.drawable.baskets_lidl_bkg);
                else if (storeName.equals(storeKeywords[2]))
                    getBaskets(storeKeywords[2],
                            R.drawable.penny_logo_white_border,
                            R.drawable.baskets_penny_logo_bkg,
                            R.drawable.baskets_penny_bkg);

                intent.removeExtra("store");
            }
        }
    }

    private void getBasketsCount() {
        basketsRepository.getBasketsCount(storeKeywords[1], (salesCount) -> lidlNumberText.setText(String.valueOf(salesCount)));
        basketsRepository.getBasketsCount(storeKeywords[2], (salesCount) -> pennyNumberText.setText(String.valueOf(salesCount)));
    }


    private void getBaskets(String storeName, int logo, int logoBkg, int bkg) {
        basketsRepository.getBaskets(storeName, (baskets) -> {
            if (baskets.isEmpty()) Toast.makeText(BasketsGuestActivity.this, "Unfortunately, there are no baskets for this store.", Toast.LENGTH_LONG).show();
            else getBaskets(storeName, baskets, logo, logoBkg, bkg);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getBaskets(String storeName, List<Basket> baskets, int logo, int logoBkg, int bkg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BasketsGuestActivity.this);
        View view = getLayoutInflater().inflate(R.layout.baskets_store_layout, null);

        RelativeLayout layout = view.findViewById(R.id.baskets_store_layout);
        layout.setBackground(getResources().getDrawable(bkg));

        RelativeLayout logoLayout = view.findViewById(R.id.baskets_store_logo_layout);
        logoLayout.setBackground(getResources().getDrawable(logoBkg));

        ImageView logoImage = view.findViewById(R.id.baskets_store_logo);
        logoImage.setImageResource(logo);

        RecyclerView recyclerView = view.findViewById(R.id.baskets_store_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        BasketAdapter basketAdapter = new BasketAdapter(storeName, baskets);
        recyclerView.setAdapter(basketAdapter);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }


    private void setButtons() {
        RelativeLayout storesButton = findViewById(R.id.baskets_stores_btn);
        storesButton.setOnClickListener(view -> Toast.makeText(BasketsGuestActivity.this, "Register to access the baskets of the stores.", Toast.LENGTH_LONG).show());

        RelativeLayout lidlButton = findViewById(R.id.baskets_lidl_btn);
        lidlButton.setOnClickListener(view -> getBaskets(storeKeywords[1],
                R.drawable.lidl_logo_white_border,
                R.drawable.baskets_lidl_logo_bkg,
                R.drawable.baskets_lidl_bkg));

        RelativeLayout carrefourButton = findViewById(R.id.baskets_carrefour_btn);
        carrefourButton.setOnClickListener(view -> Toast.makeText(BasketsGuestActivity.this, "Register to access the baskets of this store.", Toast.LENGTH_LONG).show());

        RelativeLayout pennyButton = findViewById(R.id.baskets_penny_btn);
        pennyButton.setOnClickListener(view -> getBaskets(storeKeywords[2],
                R.drawable.penny_logo_white_border,
                R.drawable.baskets_penny_logo_bkg,
                R.drawable.baskets_penny_bkg));

        RelativeLayout kauflandButton = findViewById(R.id.baskets_kaufland_btn);
        kauflandButton.setOnClickListener(view -> Toast.makeText(BasketsGuestActivity.this, "Register to access the baskets of this store.", Toast.LENGTH_LONG).show());
    }


    private void setTranslateButton() {
        ImageView translateFlag = findViewById(R.id.baskets_translate_flag);
        RelativeLayout translateButton = findViewById(R.id.baskets_translate_btn);
        TextView exploreText = findViewById(R.id.baskets_explore_text);

        TextView kauflandBasketsText = findViewById(R.id.baskets_kaufland_text);
        TextView lidlBasketsText = findViewById(R.id.baskets_lidl_text);
        TextView carrefourBasketsText = findViewById(R.id.baskets_carrefour_text);
        TextView pennyBasketsText = findViewById(R.id.baskets_penny_text);
        TextView storesBasketsText = findViewById(R.id.baskets_stores_text);

        translateButton.setOnClickListener(view -> {
            if (sourceLanguage.equals("ro")) {
                targetLanguage = "ro";
                sourceLanguage = "en";

                translateFlag.setImageResource(R.drawable.flag_romania);
                exploreText.setText(R.string.explore_baskets_romanian);

                kauflandBasketsText.setText(R.string.baskets_kaufland_text_romanian);
                lidlBasketsText.setText(R.string.baskets_lidl_text_romanian);
                carrefourBasketsText.setText(R.string.baskets_carrefour_text_romanian);
                pennyBasketsText.setText(R.string.baskets_penny_text_romanian);
                storesBasketsText.setText(R.string.baskets_stores_text_romanian);
            } else {
                targetLanguage = "en";
                sourceLanguage = "ro";

                translateFlag.setImageResource(R.drawable.flag_uk);
                exploreText.setText(R.string.explore_favored);

                kauflandBasketsText.setText(R.string.baskets_kaufland_text);
                lidlBasketsText.setText(R.string.baskets_lidl_text);
                carrefourBasketsText.setText(R.string.baskets_carrefour_text);
                pennyBasketsText.setText(R.string.baskets_penny_text);
                storesBasketsText.setText(R.string.baskets_stores_text);
            }
        });
    }


    private class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {
        private static final int VIEW_TYPE_LIDL = 2;
        private static final int VIEW_TYPE_PENNY = 4;
        private static final int VIEW_TYPE_MARKET = 5;

        List<Sale> sales;

        public SaleAdapter(List<Sale> sales) {
            this.sales = sales;
        }

        @Override
        public int getItemViewType(int position) {
            if (sales.get(position).getStore().equals(storeKeywords[1])) {
                if (sales.get(position).getCategory().equals("market")) {
                    return VIEW_TYPE_MARKET;
                }
                return VIEW_TYPE_LIDL;
            } else {
                return VIEW_TYPE_PENNY;
            }
        }

        @NonNull
        @Override
        public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_LIDL) {
                return new SaleAdapter.SaleViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.baskets_lidl_sale_layout, parent, false)
                );
            } else if (viewType == VIEW_TYPE_PENNY) {
                return new SaleAdapter.SaleViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.baskets_penny_sale_layout, parent, false)
                );
            } else {
                return new SaleAdapter.SaleViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.baskets_lidl_market_sale_layout, parent, false)
                );
            }
        }

        @Override
        public void onBindViewHolder(@NonNull SaleAdapter.SaleViewHolder holder, int position) {
            setCategory(holder, position);
            setImage(holder, position);
            setTitle(holder, position);
            setSubtitle(holder, position);
            setQuantity(holder, position);
            setNewPrice(holder, position);
            setOldPrice(holder, position);
        }

        @Override
        public int getItemCount() {
            return sales.size();
        }


        private void setCategory(SaleViewHolder holder, int position) {
            if (Objects.equals(targetLanguage, "en")) {
                holder.category.setText(sales.get(position).getCategory());
            } else {
                TranslateRequest.translate(BasketsGuestActivity.this, targetLanguage, sourceLanguage, sales.get(position).getCategory(),
                        translatedText -> holder.category.setText(translatedText));
            }

            holder.category.setOnClickListener(view -> {
                if (sales.get(position).getStore().equals(storeKeywords[1])) {
                    ActivityNavigator.navigateToCategory(BasketsGuestActivity.this, LidlActivity.class, sales.get(position).getCategory());
                } else if (sales.get(position).getStore().equals(storeKeywords[2])) {
                    ActivityNavigator.navigateToCategory(BasketsGuestActivity.this, PennyActivity.class, sales.get(position).getCategory());
                }
            });
        }

        private void setImage(SaleViewHolder holder, int position) {
            if (!sales.get(position).getStore().equals(storeKeywords[2])) {
                Glide.with(BasketsGuestActivity.this).load(sales.get(position).getImage()).into(holder.image);
            }
        }

        private void setTitle(SaleViewHolder holder, int position) {
            if (Objects.equals(targetLanguage, "ro")) {
                holder.title.setText(sales.get(position).getTitle());
            } else {
                TranslateRequest.translate(BasketsGuestActivity.this, targetLanguage, sourceLanguage, sales.get(position).getTitle(),
                        translatedText -> holder.title.setText(translatedText));
            }
        }

        private void setSubtitle(SaleViewHolder holder, int position) {
            if (sales.get(position).getSubtitle() == null)
                holder.subtitle.setVisibility(View.GONE);
            else {
                if (Objects.equals(targetLanguage, "ro")) {
                    holder.subtitle.setText(sales.get(position).getSubtitle());
                } else {
                    TranslateRequest.translate(BasketsGuestActivity.this, targetLanguage, sourceLanguage, sales.get(position).getSubtitle(),
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
                } else {
                    TranslateRequest.translate(BasketsGuestActivity.this, targetLanguage, sourceLanguage, sales.get(position).getQuantity(),
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
            } else {
                holder.discount.setText(sales.get(position).getDiscount());
                holder.oldPrice.setText(sales.get(position).getOldPrice());
                holder.oldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }


        private class SaleViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layout, categoryLayout;
            RelativeLayout priceLayout;
            TextView category, title, subtitle, quantity, discount, oldPrice, newPrice;
            ImageView image;

            public SaleViewHolder(@NonNull View itemView) {
                super(itemView);

                layout = itemView.findViewById(R.id.baskets_sale_layout);
                categoryLayout = itemView.findViewById(R.id.baskets_sale_category_layout);
                category = itemView.findViewById(R.id.baskets_sale_category);
                image = itemView.findViewById(R.id.baskets_sale_image);
                title = itemView.findViewById(R.id.baskets_sale_title);
                subtitle = itemView.findViewById(R.id.baskets_sale_subtitle);
                quantity = itemView.findViewById(R.id.baskets_sale_quantity);
                priceLayout = itemView.findViewById(R.id.baskets_sale_price_layout);
                discount = itemView.findViewById(R.id.baskets_sale_discount);
                oldPrice = itemView.findViewById(R.id.baskets_sale_old_price);
                newPrice = itemView.findViewById(R.id.baskets_sale_new_price);
            }
        }
    }


    private class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketViewHolder> {
        String storeName;
        List<Basket> baskets;

        public BasketAdapter(String storeName, List<Basket> baskets) {
            this.storeName = storeName;
            this.baskets = baskets;
        }

        @NonNull
        @Override
        public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BasketAdapter.BasketViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.baskets_general_layout, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull BasketViewHolder holder, int position) {
            setNumber(holder, position);
            setLayout(holder, position);
            setName(holder, position);
            setType(holder, position);
            setPeriod(holder, position);
            setProductCount(holder, position);
        }


        @Override
        public int getItemCount() {
            return baskets.size();
        }


        private void setNumber(BasketViewHolder holder, int position) {
            Integer number = position + 1;
            holder.number.setText(String.valueOf(number));

            if (number >= 10) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.number.getLayoutParams();
                int marginStartPx = 30;
                params.setMarginStart(marginStartPx);
                holder.number.setLayoutParams(params);
            }
        }

        private void setLayout(BasketViewHolder holder, int position) {
            holder.layout.setOnClickListener(view -> {
                List<Sale> sales = new ArrayList<>();
                float price = 0;

                for (Map.Entry<String, Sale> saleEntry : baskets.get(position).getSales().entrySet()) {
                    sales.add(saleEntry.getValue());
                    price += Float.parseFloat(saleEntry.getValue().getNewPrice().replaceAll(",", "."));
                }

                setBasket(baskets.get(position), sales, price);
            });
        }

        private void setName(BasketViewHolder holder, int position) {
            if (Objects.equals(targetLanguage, "en")) {
                holder.name.setText(baskets.get(position).getName());
            } else {
                TranslateRequest.translate(BasketsGuestActivity.this, targetLanguage, sourceLanguage, baskets.get(position).getName(),
                        translatedText -> holder.name.setText(translatedText));
            }
        }

        private void setType(BasketViewHolder holder, int position) {
            if (Objects.equals(targetLanguage, "en")) {
                holder.type.setText(baskets.get(position).getType());
            } else {
                TranslateRequest.translate(BasketsGuestActivity.this, targetLanguage, sourceLanguage, baskets.get(position).getType(),
                        translatedText -> holder.type.setText(translatedText));
            }
        }

        private void setPeriod(BasketViewHolder holder, int position) {
            holder.period.setText(baskets.get(position).getPeriod());
        }

        private void setProductCount(BasketViewHolder holder, int position) {
            int productCount = baskets.get(position).getSales().size();
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(productCount));

            if (Objects.equals(targetLanguage, "en")) {
                if (productCount == 1) stringBuilder.append(" product");
                else stringBuilder.append(" products");
            } else {
                if (productCount == 1) stringBuilder.append(" produs");
                else stringBuilder.append(" produse");
            }

            holder.productCount.setText(stringBuilder);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setBasket(Basket basket, List<Sale> sales, float priceF) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BasketsGuestActivity.this);
            View view = setLayout();

            setType(view, basket);
            setName(view, basket);
            setPeriod(view, basket);
            setSales(view, sales);
            setPrice(view, priceF);

            builder.setView(view);
            AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            dialog.show();
        }

        private View setLayout() {
            View view;

            if (storeName.equals(storeKeywords[1])) {
                view = getLayoutInflater().inflate(R.layout.baskets_lidl_layout, null);
            } else {
                view = getLayoutInflater().inflate(R.layout.baskets_penny_layout, null);
            }

            return view;
        }

        private void setType(View view, Basket basket) {
            TextView type = view.findViewById(R.id.baskets_detailed_type);
            type.setText(basket.getType());
        }

        private void setName(View view, Basket basket) {
            TextView name = view.findViewById(R.id.baskets_detailed_name);
            name.setText(basket.getName());
        }

        private void setPeriod(View view, Basket basket) {
            TextView period = view.findViewById(R.id.baskets_detailed_period);
            period.setText(basket.getPeriod());
        }

        private void setSales(View view, List<Sale> sales) {
            RecyclerView recyclerView = view.findViewById(R.id.baskets_detailed_recycler_view);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(BasketsGuestActivity.this, 2);
            recyclerView.setLayoutManager(layoutManager);

            SaleAdapter saleAdapter = new SaleAdapter(sales);
            recyclerView.setAdapter(saleAdapter);
        }

        private void setPrice(View view, float priceF) {
            TextView price = view.findViewById(R.id.baskets_detailed_price);
            String priceS = String.format(Locale.ENGLISH, "%.2f", priceF) + " Lei";
            price.setText(priceS);
        }


        private class BasketViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layout;
            TextView name, type, period, productCount, number;

            public BasketViewHolder(@NonNull View itemView) {
                super(itemView);

                layout = itemView.findViewById(R.id.baskets_general_layout);
                name = itemView.findViewById(R.id.baskets_general_name);
                type = itemView.findViewById(R.id.baskets_general_type);
                period = itemView.findViewById(R.id.baskets_general_period);
                productCount = itemView.findViewById(R.id.baskets_general_product_count);
                number = itemView.findViewById(R.id.baskets_general_number);
            }
        }
    }

    private void setNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.baskets_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nd, R.string.close_nd);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.baskets_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nd_home || id == R.id.bn_home) {
            ActivityNavigator.navigate(this, HomeGuestActivity.class);
        }
        else if (id == R.id.nd_location || id == R.id.bn_location) {
            ActivityNavigator.navigate(this, LocationGuestActivity.class);
        }
        else if (id == R.id.nd_search || id == R.id.bn_search) {
            ActivityNavigator.navigate(this, SearchGuestActivity.class);
        }
        else if (id == R.id.nd_favorites || id == R.id.bn_favorites) {
            Toast.makeText(BasketsGuestActivity.this, "Register to access the favored sales.", Toast.LENGTH_LONG).show();
            navigationBar.postDelayed(() -> navigationBar.setSelectedItemId(R.id.bn_baskets), 400);
        }
        else if (id == R.id.nd_profile || id == R.id.bn_profile) {
            ActivityNavigator.navigate(this, ProfileGuestActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}