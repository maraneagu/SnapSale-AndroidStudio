package com.example.snapsale.activities.guestActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snapsale.R;
import com.example.snapsale.database.repositories.BasketsRepository;
import com.example.snapsale.database.repositories.SalesRepository;
import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.navigation.CustomBottomNavigationView;
import com.example.snapsale.models.Sale;
import com.example.snapsale.activities.mainActivities.BasketsActivity;
import com.example.snapsale.services.scrapingServices.ScrapingService;
import com.example.snapsale.activities.storeActivities.KauflandActivity;
import com.example.snapsale.activities.storeActivities.PennyActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


// HELPFUL LINKS:
// 1). "AutoCompleteTextView with Custom Adapter Part 1", ToBa, URL: https://www.youtube.com/watch?v=7Ms2Q1n1sac&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=72&ab_channel=ToBa,
// 2). "AutoCompleteTextView with Custom Adapter Part 2", ToBa, URL: https://www.youtube.com/watch?v=fkZToQuOCCc&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=71&t=309s&ab_channel=ToBa
// 3). "How to Load a image from a URL in Imageview using Glide | Android Studio | Java 🔥", Android Mate, URL: https://www.youtube.com/watch?v=xrVD7LcQ5nY&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=51&ab_channel=AndroidMate
// 4). "How to Change Toggle (Button) Color and ToolBar Text Color in Android Studio App", Any Technology, URL: https://www.youtube.com/watch?v=0CrhNNhQfgc&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=29&ab_channel=AnyTechnology
// 5). "How to Implement Bottom Navigation with Activities | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=MUl19ppdu0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=22&ab_channel=AndroidKnowledge
// 6). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view
// 7). "Navigation Drawer Menu in Android Tutorial | How to Create Navigation Drawer in Android Studio", Code with Surya, URL: https://www.youtube.com/watch?v=uY9iZiamyZs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=69&t=1187s&ab_channel=CodewithSurya


public class HomeGuestActivity extends AppCompatActivity implements CustomBottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static SalesRepository salesRepository;
    private static BasketsRepository basketsRepository;

    private static String[] storeKeywords;
    private static String targetLanguage, sourceLanguage;

    private AutoCompleteTextView autoCompleteTextView;
    private TextView kauflandSalesText, pennySalesText, lidlBasketsText, pennyBasketsText;

    private DrawerLayout drawerLayout;
    private CustomBottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_guest);

        salesRepository = SalesRepository.getInstance();
        basketsRepository = BasketsRepository.getInstance();

        storeKeywords = getResources().getStringArray(R.array.store_keywords);

        targetLanguage = "en";
        sourceLanguage = "ro";

        drawerLayout = findViewById(R.id.home_navigation_drawer_layout);
        setNavigationDrawer();

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.bn_home);
        navigationBar.setOnNavigationItemSelectedListener(this);

        autoCompleteTextView = findViewById(R.id.home_search_autocomplete);
        autoCompleteTextView.setEnabled(false);

        kauflandSalesText = findViewById(R.id.home_kaufland_sales_count);
        lidlBasketsText = findViewById(R.id.home_lidl_baskets_count);

        pennySalesText = findViewById(R.id.home_penny_sales_count);
        pennyBasketsText = findViewById(R.id.home_penny_baskets_count);

        setStoreButtons();
        setBasketsButtons();
        setTranslateButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.bn_home);

        ScrapingService.check(this, () -> {
            getSalesCount();
            getBasketsCount();
            getSales();
        });
    }

    private void getSales() {
        salesRepository.getSales((sales) -> {
            SaleAdapter adapter = new SaleAdapter(HomeGuestActivity.this, sales);
            autoCompleteTextView.setEnabled(true);
            autoCompleteTextView.setAdapter(adapter);
        });
    }

    private void getSalesCount() {
        salesRepository.getSalesCount(storeKeywords[0], (salesCount) -> kauflandSalesText.setText(String.valueOf(salesCount)));
        salesRepository.getSalesCount(storeKeywords[2], (salesCount) -> pennySalesText.setText(String.valueOf(salesCount)));
    }


    private void setStoreButtons() {
        RelativeLayout kauflandButton = findViewById(R.id.home_kaufland_btn);
        kauflandButton.setOnClickListener(view -> ActivityNavigator.navigate(HomeGuestActivity.this, KauflandActivity.class));

        RelativeLayout lidlButton = findViewById(R.id.home_lidl_btn);
        lidlButton.setOnClickListener(view -> Toast.makeText(HomeGuestActivity.this, "Register to access the sales of this store.", Toast.LENGTH_LONG).show());

        RelativeLayout carrefourButton = findViewById(R.id.home_carrefour_btn);
        carrefourButton.setOnClickListener(view -> Toast.makeText(HomeGuestActivity.this, "Register to access the sales of this store.", Toast.LENGTH_LONG).show());

        RelativeLayout pennyButton = findViewById(R.id.home_penny_btn);
        pennyButton.setOnClickListener(view -> ActivityNavigator.navigate(HomeGuestActivity.this, PennyActivity.class));
    }

    private void getBasketsCount() {
        basketsRepository.getBasketsCount(storeKeywords[1], (salesCount) -> lidlBasketsText.setText(String.valueOf(salesCount)));
        basketsRepository.getBasketsCount(storeKeywords[2], (salesCount) -> pennyBasketsText.setText(String.valueOf(salesCount)));
    }

    private void setBasketsButtons() {
        LinearLayout kauflandBasketsButton = findViewById(R.id.home_kaufland_baskets_btn);
        kauflandBasketsButton.setOnClickListener(view -> Toast.makeText(HomeGuestActivity.this, "Register to access the baskets of this store.", Toast.LENGTH_LONG).show());

        LinearLayout lidlBasketsButton = findViewById(R.id.home_lidl_baskets_btn);
        lidlBasketsButton.setOnClickListener(view -> ActivityNavigator.navigateToStore(HomeGuestActivity.this, BasketsActivity.class, storeKeywords[1]));

        LinearLayout carrefourBasketsButton = findViewById(R.id.home_carrefour_baskets_btn);
        carrefourBasketsButton.setOnClickListener(view -> Toast.makeText(HomeGuestActivity.this, "Register to access the baskets of this store.", Toast.LENGTH_LONG).show());

        LinearLayout pennyBasketsButton = findViewById(R.id.home_penny_baskets_btn);
        pennyBasketsButton.setOnClickListener(view -> ActivityNavigator.navigateToStore(HomeGuestActivity.this, BasketsActivity.class, storeKeywords[2]));
    }

    private void setTranslateButton() {
        ImageView translateFlag = findViewById(R.id.home_translate_flag);
        RelativeLayout translateButton = findViewById(R.id.home_translate_btn);

        TextView searchExploreText = findViewById(R.id.home_search_explore_text);
        TextView storesExploreText = findViewById(R.id.home_stores_explore_text);
        TextView basketsExploreText = findViewById(R.id.home_baskets_explore_text);

        translateButton.setOnClickListener(view -> {
            if (sourceLanguage.equals("ro")) {
                targetLanguage = "ro";
                sourceLanguage = "en";

                translateFlag.setImageResource(R.drawable.flag_romania);
                searchExploreText.setText(R.string.explore_search_romanian);
                storesExploreText.setText(R.string.explore_stores_romanian);
                basketsExploreText.setText(R.string.explore_baskets_romanian);
            }
            else {
                targetLanguage = "en";
                sourceLanguage = "ro";

                translateFlag.setImageResource(R.drawable.flag_uk);
                searchExploreText.setText(R.string.explore_search);
                storesExploreText.setText(R.string.explore_stores);
                basketsExploreText.setText(R.string.explore_baskets);
            }
        });
    }

    public class SaleAdapter extends ArrayAdapter<Sale> {
        List<Sale> sales;
        List<Sale> suggestedSales;

        public SaleAdapter(@NonNull Context context, @NonNull List<Sale> sales) {
            super(context, 0, sales);
            this.sales = sales;
            this.suggestedSales = new ArrayList<>();

            setOnItemClickListener();
        }

        private void setOnItemClickListener() {
            autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                Sale selectedSale = suggestedSales.get(position);
                if (selectedSale != null) {
                    autoCompleteTextView.setText("");

                    if (selectedSale.getStore().equals(storeKeywords[0])) {
                        ActivityNavigator.navigateToCategory(HomeGuestActivity.this, KauflandActivity.class, selectedSale.getCategory());
                    } else if (selectedSale.getStore().equals(storeKeywords[2])) {
                        ActivityNavigator.navigateToCategory(HomeGuestActivity.this, PennyActivity.class, selectedSale.getCategory());
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return suggestedSales.size();
        }

        @Override
        public Sale getItem(int position) {
            if (!suggestedSales.isEmpty()) {
                return suggestedSales.get(position);
            }

            return null;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();
                    suggestedSales = new ArrayList<>();

                    String input = charSequence.toString().toLowerCase().trim();
                    input = input.replaceAll("[ăâ]", "a").replaceAll("î", "i");

                    for (Sale sale : sales) {
                        if (sale.getStore().toLowerCase().contains(input)) {
                            suggestedSales.add(sale);
                        }
                        else if (sale.getCategory().toLowerCase().contains(input)) {
                            suggestedSales.add(sale);
                        }
                        else {
                            String saleName = sale.getSubtitle() == null ? sale.getTitle() : sale.getTitle() + " " + sale.getSubtitle();
                            saleName = saleName.toLowerCase();
                            saleName = saleName.replaceAll("[ăâ]", "a").replaceAll("î", "i");

                            if (saleName.contains(input) || input.contains(saleName)) {
                                suggestedSales.add(sale);
                            }
                        }
                    }

                    filterResults.values = suggestedSales;
                    filterResults.count = suggestedSales.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (filterResults != null && filterResults.count > 0) {
                        new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> notifyDataSetInvalidated());
                    }
                }
            };
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_autocomplete_search_layout, parent, false);
            }

            Sale sale = getItem(position);
            assert sale != null;

            setLogos(view, sale);
            setImageLayout(view, sale);
            setImage(view, sale);
            setTitle(view, sale);
            setSubtitle(view, sale);
            setQuantity(view, sale);

            return view;
        }

        private void setLogo(View view, int imageResource, int dimen) {
            ImageView logo = view.findViewById(R.id.home_autocomplete_search_logo);
            logo.setImageResource(imageResource);

            ViewGroup.LayoutParams params = logo.getLayoutParams();
            params.width = (int) view.getContext().getResources().getDimension(dimen);
            logo.setLayoutParams(params);
        }

        private void setLogos(View view, Sale sale) {
            if (sale.getStore().equals(storeKeywords[0])) {
                setLogo(view, R.drawable.kaufland_logo_blue, R.dimen.kauflandHomeDimen);
            } else if (sale.getStore().equals(storeKeywords[2])) {
                setLogo(view, R.drawable.penny_logo_blue, R.dimen.pennyHomeDimen);
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setImageLayout(View view, Sale sale) {
            RelativeLayout imageLayout = view.findViewById(R.id.home_autocomplete_search_image_layout);
            imageLayout.setBackground(getResources().getDrawable(R.drawable.home_autocomplete_search_image_bkg));
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setImage(View view, Sale sale) {
            ImageView image = view.findViewById(R.id.home_autocomplete_search_image);
            if (!sale.getStore().equals(storeKeywords[2])) {
                Glide.with(HomeGuestActivity.this).load(sale.getImage()).into(image);
            } else {
                Glide.with(HomeGuestActivity.this).load(getResources().getDrawable(R.drawable.penny_logo)).into(image);
            }
        }

        private void setTitle(View view, Sale sale) {
            TextView title = view.findViewById(R.id.home_autocomplete_search_title);
            title.setText(sale.getTitle());
        }

        private void setSubtitle(View view, Sale sale) {
            TextView subtitle = view.findViewById(R.id.home_autocomplete_search_subtitle);
            if (sale.getSubtitle() == null) {
                subtitle.setVisibility(View.GONE);
            } else {
                subtitle.setVisibility(View.VISIBLE);
                subtitle.setText(sale.getSubtitle());
            }
        }

        private void setQuantity(View view, Sale sale) {
            TextView quantity = view.findViewById(R.id.home_autocomplete_search_quantity);
            if (sale.getQuantity() == null) {
                quantity.setVisibility(View.GONE);
            } else {
                quantity.setVisibility(View.VISIBLE);
                quantity.setText(sale.getQuantity());
            }
        }
    }


    private void setNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nd, R.string.close_nd);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.home_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bn_location || id == R.id.nd_location) {
            ActivityNavigator.navigate(this, LocationGuestActivity.class);
        }
        else if (id == R.id.bn_search || id == R.id.nd_search) {
            ActivityNavigator.navigate(this, SearchGuestActivity.class);
        }
        else if (id == R.id.bn_favorites || id == R.id.nd_favorites) {
            Toast.makeText(HomeGuestActivity.this, "Register to access the favored sales.", Toast.LENGTH_LONG).show();
            navigationBar.postDelayed(() -> navigationBar.setSelectedItemId(R.id.bn_home), 400);
        }
        else if (id == R.id.bn_baskets || id == R.id.nd_baskets) {
            ActivityNavigator.navigate(this, BasketsGuestActivity.class);
        }
        else if (id == R.id.bn_profile || id == R.id.nd_profile) {
            ActivityNavigator.navigate(this, ProfileGuestActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
