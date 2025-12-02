package com.example.snapsale.network.scrapers;

import static org.jsoup.internal.StringUtil.isNumeric;

import android.os.AsyncTask;
import android.util.Log;

import com.example.snapsale.database.repositories.StoreCategoriesRepository;
import com.example.snapsale.callbacks.StoreCategoryCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.Sale;
import com.example.snapsale.models.Store;
import com.example.snapsale.models.StoreCategory;
import com.google.firebase.database.DatabaseReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


// HELPFUL LINKS:
// 1). "How to use Jsoup in Android Studio", The Software Creators, URL: https://www.youtube.com/watch?v=PgkNC7AneKI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=49&ab_channel=TheSoftwareCreators


public class PennyScraper {
    private final Store store;
    private static final String pennyUrl = "https://www.penny.ro";
    private static final String pennySalesUrl = "https://www.penny.ro/oferta-saptamanii";
    private final StoreCategoryCallback callback;
    private boolean onGetStoreCategory;

    private static DatabaseReference storesReference;
    private static StoreCategoriesRepository storeCategoriesRepository;


    public PennyScraper(Store store, StoreCategoryCallback callback) {
        this.store = store;
        this.callback = callback;
        this.onGetStoreCategory = false;

        storesReference = FirebaseManager.getInstance().getStoresReference();
        storeCategoriesRepository = StoreCategoriesRepository.getInstance();
    }


    public void getLink(String categoryName, String categoryUrl) {
        LinkScrapper linkScrapper = new LinkScrapper();
        linkScrapper.execute(categoryName, categoryUrl);
    }

    private void getSales(String categoryName, String salesUrl, String period) {
        SalesScrapper salesScrapper = new SalesScrapper();
        salesScrapper.execute(categoryName, salesUrl, period);
    }


    private class SalesScrapper extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            String categoryName = (String) objects[0];
            String salesUrl = (String) objects[1];
            String period = (String) objects[2];

            Document document;
            try {
                document = Jsoup.connect(salesUrl).get();
            }
            catch (IOException e) {
                callback.onStoreCategoryNotFound();
                return null;
            }

            Element listElement = document.selectFirst("ul.ws-product-grid__list");
            if (listElement != null) {
                Map<String, Sale> sales = new HashMap<>();

                Elements itemElements = listElement.select("li[data-teaser-group=product]");
                for (Element itemElement : itemElements) {

                    if (itemElement != null) {
                        String image, title, quantity, discount, oldPrice, newPrice;
                        image = "pennyLogo";

                        Element titleElement = itemElement.selectFirst("h3[data-test=product-title]");
                        if (titleElement != null) {
                            title = getTitle(titleElement.text().toLowerCase());
                        }
                        else continue;

                        Element priceElement = itemElement.selectFirst("div[data-test=product-price]");
                        if (priceElement != null) {
                            Element newPriceElement = priceElement.selectFirst(".ws-product-price-type__value.subtitle-1");
                            if (newPriceElement != null) {
                                newPrice = newPriceElement.text().replaceAll("\\s*LEI$", "");
                            }
                            else continue;
                        }
                        else continue;

                        Sale sale = new Sale(store.getName(), categoryName, image, title, newPrice, period);

                        Element quantityElement = itemElement.selectFirst("ul[data-test=product-information-piece-description] li.body-2");
                        if (quantityElement != null) {
                            quantity = quantityElement.text();
                            sale.setQuantity(quantity);
                        }

                        Element discountElement = itemElement.selectFirst("div[data-test=product-badge-discount]");
                        if (discountElement != null) {
                            String discountValue = discountElement.selectFirst(".ws-product-badge-discount__value.title").text();
                            discount = discountValue + "%";
                            sale.setDiscount(discount);
                        }

                        Element oldPriceElement = priceElement.selectFirst(".ws-product-price-strike.body-2 s");
                        if (oldPriceElement != null) {
                            oldPrice = oldPriceElement.text().replaceAll("\\s*LEI$", "");
                            sale.setOldPrice(oldPrice);
                        }

                        String saleKey = storesReference.push().getKey();
                        sale.setKey(saleKey);
                        sales.put(saleKey, sale);
                    }
                }

                StoreCategory category = new StoreCategory(categoryName, period, sales);
                storeCategoriesRepository.addStoreCategory(store, category);

                onGetStoreCategory = true;
            }
            else callback.onStoreCategoryNotFound();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (onGetStoreCategory)
                callback.onStoreCategoryScraped();
        }

        private String getTitle(String titleText) {
            String[] words = titleText.split("\\s+");

            StringBuilder modifiedTitle = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    modifiedTitle.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
                }
            }

            return modifiedTitle.toString().trim();
        }
    }

    private class LinkScrapper extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            String categoryName = (String) objects[0];
            String categoryUrl = (String) objects[1];

            Document document;
            try {
                document = Jsoup.connect(pennySalesUrl).get();
            }
            catch (IOException e) {
                callback.onStoreCategoryNotFound();
                return null;
            }

            String period;
            Element periodElement = document.selectFirst("h3[id=ofert_disponibil_n_perioada_]");
            if (periodElement != null) {
                period = getPeriod(periodElement.text());
            }
            else {
                callback.onStoreCategoryNotFound();
                return null;
            }

            Element presentationElement = document.selectFirst(".ws-product-grid ul.ws-product-grid__list li[role=presentation]");
            if (presentationElement != null) {
                Element aElement = presentationElement.selectFirst("a");
                String salesUrl = aElement.attr("href");

                salesUrl = pennyUrl + salesUrl + "?" + categoryUrl;
                getSales(categoryName, salesUrl, period);
            }
            else callback.onStoreCategoryNotFound();
            return null;
        }

        private String getPeriod(String periodText) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

            String[] parts = periodText.split(":+");
            String[] dates = parts[1].trim().split("-");

            try {
                Date endDate = format.parse(dates[1]);
                assert endDate != null;

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                String formattedEndDate = format.format(calendar.getTime());

                dates[0] = dates[0].trim();
                if (isNumeric(dates[0])) {
                    int startDay = Integer.parseInt(dates[0]);

                    calendar.set(Calendar.DAY_OF_MONTH, startDay);
                    String formattedStartDate = format.format(calendar.getTime());

                    return formattedStartDate + " - " + formattedEndDate;
                }

                dates = dates[0].trim().split("\\.");
                int startDay = Integer.parseInt(dates[0]);
                int startMonth = Integer.parseInt(dates[1]) - 1;

                calendar.set(Calendar.DAY_OF_MONTH, startDay);
                calendar.set(Calendar.MONTH, startMonth);
                if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                    calendar.add(Calendar.YEAR, -1);
                }

                String formattedStartDate = format.format(calendar.getTime());
                return formattedStartDate + " - " + formattedEndDate;

            }
            catch (ParseException e) {
                return null;
            }
        }
    }
}
