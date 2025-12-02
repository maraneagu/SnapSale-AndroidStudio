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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// HELPFUL LINKS:
// 1). "How to use Jsoup in Android Studio", The Software Creators, URL: https://www.youtube.com/watch?v=PgkNC7AneKI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=49&ab_channel=TheSoftwareCreators


public class CarrefourScraper extends AsyncTask<Object, Void, Void> {
    private final Store store;
    private final StoreCategoryCallback callback;
    private boolean onGetStoreCategory;

    private static DatabaseReference storesReference;
    private static StoreCategoriesRepository storeCategoriesRepository;


    public CarrefourScraper(Store store, StoreCategoryCallback callback) {
        this.store = store;
        this.callback = callback;
        this.onGetStoreCategory = false;

        storesReference = FirebaseManager.getInstance().getStoresReference();
        storeCategoriesRepository = StoreCategoriesRepository.getInstance();
    }


    @Override
    protected Void doInBackground(Object... objects) {
        String categoryName = (String) objects[0];
        String categoryFullName = (String) objects[1];
        String categoryUrl = (String) objects[2];

        Document document;
        String period;

        try {
            document = Jsoup.connect(categoryUrl).get();
            Element endDateElement = document.selectFirst(".main-campaign-wrapper_footer span");

            String endDate = getEndDate(endDateElement.text());
            period = getPeriod(endDate);
        }
        catch (IOException | ParseException e) {
            callback.onStoreCategoryNotFound();
            return null;
        }

        Element mainGridElement = document.selectFirst(".main-campaign-component .main_grid");
        if (mainGridElement != null) {

            Elements sectionElements = mainGridElement.select("section.category-container section.subcategory-container");
            if (sectionElements != null) {

                for (Element sectionElement : sectionElements) {
                    if (sectionElement.attr("data-slug").contains(categoryFullName) || categoryFullName.contains(sectionElement.attr("data-slug"))) {

                        Elements productElements = sectionElement.select("div[class*=product][class*=item-grid]");
                        if (productElements != null) {
                            Map<String, Sale> sales = new HashMap<>();

                            for (Element productElement : productElements) {
                                Element headerElement = productElement.selectFirst(".product_header");
                                if (headerElement == null) continue;

                                Element footerElement = productElement.selectFirst(".product_footer");
                                if (footerElement == null) continue;

                                Element pricesElement = footerElement.selectFirst("div[class*=Prices_wrapper]");
                                if (pricesElement == null) continue;

                                String image, title, quantity, discount, oldPrice, newPrice;

                                Element spanElement = headerElement.selectFirst("div[class*=product_image] span noscript");
                                Element imageElement = spanElement.selectFirst("img");
                                if (imageElement != null) {
                                    image = imageElement.attr("src");
                                }
                                else continue;

                                Element titleElement = footerElement.selectFirst(".product_title span");
                                if (titleElement != null) {
                                    title = titleElement.text();
                                }
                                else continue;

                                Element newPriceElement = pricesElement.selectFirst("div[class*=Price_priceWrapperRed]");
                                if (newPriceElement != null) {
                                    Element fullElement = newPriceElement.selectFirst("span[class*=Price_priceWrapperFullLarge]");
                                    Element decimalElement = newPriceElement.selectFirst("span[class*=Price_priceWrapperDecimalCurrency]");

                                    String fullPrice = fullElement.text().trim();
                                    String decimalPrice = decimalElement.text().trim();
                                    decimalPrice = decimalPrice.replaceAll("[^\\d.]", "");

                                    newPrice = fullPrice + "," + decimalPrice;
                                }
                                else continue;

                                Sale sale = new Sale(store.getName(), categoryName, image, title, newPrice, period);

                                Element quantityElement = footerElement.selectFirst("div[class*=product_measurementUnit]");
                                if (quantityElement != null) {
                                    quantity = quantityElement.text();
                                    sale.setQuantity(quantity);
                                }

                                Element discountElement = headerElement.selectFirst("div[class*=product_discount-special]");
                                if (discountElement != null) {
                                    discount = discountElement.text().replaceAll("\\s+", "");
                                    sale.setDiscount(discount);
                                }

                                Element oldPriceElement = pricesElement.selectFirst("div[class*=Price_priceWrapperStrikeThrough]");
                                if (oldPriceElement != null) {
                                    Element fullElement = oldPriceElement.selectFirst("span[class*=Price_priceWrapperFullLarge]");
                                    Element decimalElement = oldPriceElement.selectFirst("span[class*=Price_priceWrapperDecimalCurrency]");

                                    String fullPrice = fullElement.text().trim();
                                    String decimalPrice = decimalElement.text().trim();
                                    decimalPrice = decimalPrice.replaceAll("[^\\d.]", "");

                                    oldPrice = fullPrice + "," + decimalPrice;
                                    sale.setOldPrice(oldPrice);
                                }

                                String saleKey = storesReference.push().getKey();
                                sale.setKey(saleKey);
                                sales.put(saleKey, sale);
                            }

                            StoreCategory category = new StoreCategory(categoryName, period, sales);
                            storeCategoriesRepository.addStoreCategory(store, category);

                            onGetStoreCategory = true;
                            return null;
                        }
                    }
                }
            }
        }
        
        callback.onStoreCategoryNotFound();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (onGetStoreCategory)
            callback.onStoreCategoryScraped();
    }

    private String getEndDate(String endDateText) throws ParseException {
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2})-\\d{2}");
        Matcher matcher = pattern.matcher(endDateText);

        if (matcher.find()) {
            String period = matcher.group();

            String day = period.substring(8);
            String month = period.substring(5, 7);
            String year = period.substring(0, 4);

            return String.format("%s.%s.%s", day, month, year);
        }
        return null;
    }

    private String getPeriod(String endString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        try {
            Date endDate = dateFormat.parse(endString);

            assert endDate != null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);

            calendar.add(Calendar.DAY_OF_MONTH, -6);
            Date startDate = calendar.getTime();

            return dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return endString;
    }
}
