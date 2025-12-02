package com.example.snapsale.network.scrapers;

import android.os.AsyncTask;

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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// HELPFUL LINKS:
// 1). "How to use Jsoup in Android Studio", The Software Creators, URL: https://www.youtube.com/watch?v=PgkNC7AneKI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=49&ab_channel=TheSoftwareCreators


public class KauflandScraper {
    private final Store store;
    private final StoreCategoryCallback callback;
    private boolean onGetStoreCategory;

    private static DatabaseReference storesReference;
    private static StoreCategoriesRepository storeCategoriesRepository;


    public KauflandScraper(Store store, StoreCategoryCallback callback) {
        this.store = store;
        this.callback = callback;
        this.onGetStoreCategory = false;

        storesReference = FirebaseManager.getInstance().getStoresReference();
        storeCategoriesRepository = StoreCategoriesRepository.getInstance();
    }


    public void getSales(String categoryName, String categoryUrl) {
        SalesScrapper salesScrapper = new SalesScrapper();
        salesScrapper.execute(categoryName, categoryUrl);
    }

    private class SalesScrapper extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            String categoryName = (String) objects[0];
            String categoryUrl = (String) objects[1];

            Document document;
            try {
                document = Jsoup.connect(categoryUrl).get();
            }
            catch (IOException e) {
                callback.onStoreCategoryNotFound();
                return null;
            }

            String period;
            Element periodElement = document.selectFirst(".a-icon-tile-headline__subheadline h2");
            if (periodElement != null) {
                String periodText = periodElement.text();
                period = getPeriod(periodText);
            }
            else {
                callback.onStoreCategoryNotFound();
                return null;
            }

            Elements saleElements = document.select(".g-row.g-layout-overview-tiles.g-layout-overview-tiles--offers .g-col.o-overview-list__list-item .o-overview-list__item-inner");
            if (saleElements != null) {
                HashMap<String, Sale> sales = new HashMap<>();

                for (Element saleElement : saleElements) {
                    if (saleElement != null) {
                        String image, title, subtitle, quantity, discount, oldPrice, newPrice;

                        Element containerElement = saleElement.selectFirst(".m-offer-tile__container");
                        if (containerElement == null) continue;

                        Element textElement = containerElement.selectFirst(".m-offer-tile__text");
                        if (textElement == null) continue;

                        Element pricetagElement = saleElement.selectFirst(".m-offer-tile__split .m-offer-tile__price-tiles .a-pricetag");
                        if (pricetagElement == null) continue;

                        Element priceElement = pricetagElement.selectFirst(".a-pricetag__price-container");
                        if (priceElement == null) continue;

                        Element imageElement = containerElement.selectFirst(".m-offer-tile__image img.a-image-responsive");
                        if (imageElement != null && !imageElement.attr("alt").equals("Afișarea ofertelor")) {
                            image = imageElement.attr("data-src");
                        }
                        else continue;

                        Element titleElement = textElement.selectFirst("h5.m-offer-tile__subtitle");
                        if (titleElement != null) {
                            title = titleElement.text().trim();
                        }
                        else continue;

                        Element newPriceElement = priceElement.selectFirst(".a-pricetag__price");
                        if (newPriceElement != null) {
                            newPrice = newPriceElement.text().trim();
                        }
                        else continue;

                        Sale sale = new Sale(store.getName(), categoryName, image, title, newPrice, period);

                        Element subtitleElement = textElement.selectFirst("h4.m-offer-tile__title");
                        if (subtitleElement != null) {
                            subtitle = subtitleElement.text().trim();
                            sale.setSubtitle(subtitle);
                        }

                        Element quantityElement = textElement.selectFirst(".m-offer-tile__quantity");
                        if (quantityElement != null) {
                            quantity = quantityElement.text().trim();
                            sale.setQuantity(quantity);
                        }

                        if (pricetagElement.hasClass("a-pricetag--discount")) {
                            Element discountElement = pricetagElement.selectFirst(".a-pricetag__discount");
                            discount = discountElement.text().trim();
                            sale.setDiscount(discount);
                        }

                        Element oldPriceElement = priceElement.selectFirst(".a-pricetag__old-price");
                        if (!oldPriceElement.text().equals("")) {
                            oldPrice = oldPriceElement.text().trim();
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


        private String getPeriod(String periodText) {
            String[] dates = new String[2];
            Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2})\\.\\d{4}");
            Matcher matcher = pattern.matcher(periodText);

            int d = 0;
            while (matcher.find() && d < 2) {
                dates[d] = matcher.group();
                d++;
            }

            String fromDate = String.valueOf(dates[0]);
            String toDate = String.valueOf(dates[1]);
            return fromDate + " - " + toDate;
        }
    }
}
