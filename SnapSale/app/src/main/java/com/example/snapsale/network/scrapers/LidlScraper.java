package com.example.snapsale.network.scrapers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.snapsale.database.repositories.StoreCategoriesRepository;
import com.example.snapsale.database.repositories.SalesRepository;
import com.example.snapsale.callbacks.StoreCategoryCallback;
import com.example.snapsale.models.Sale;
import com.example.snapsale.models.Store;
import com.example.snapsale.models.StoreCategory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// HELPFUL LINKS:
// 1). "How to use Jsoup in Android Studio", The Software Creators, URL: https://www.youtube.com/watch?v=PgkNC7AneKI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=49&ab_channel=TheSoftwareCreators


public class LidlScraper {
    private final Store store;
    private final String lidlUrl = "https://www.lidl.ro";
    private final StoreCategoryCallback callback;
    private boolean onGetStoreCategory;

    private static StoreCategoriesRepository storeCategoriesRepository;
    private static SalesRepository salesRepository;


    public LidlScraper(Store store, StoreCategoryCallback callback) {
        this.store = store;
        this.callback = callback;
        this.onGetStoreCategory = false;

        storeCategoriesRepository = StoreCategoriesRepository.getInstance();
        salesRepository = SalesRepository.getInstance();
    }


    private void getSale(StoreCategory category, String saleUrl) {
        SaleScrapper saleScrapper = new SaleScrapper();
        saleScrapper.execute(category, saleUrl);
    }

    private void getSales(StoreCategory category, String categoryUrl) {
        SalesScrapper salesScrapper = new SalesScrapper();
        salesScrapper.execute(category, categoryUrl);
    }

    public void getLink(String categoryName, String categoryFullName) {
        LinkScrapper linkScrapper = new LinkScrapper();
        linkScrapper.execute(store, categoryName, categoryFullName);
    }


    private class SaleScrapper extends AsyncTask<Object, Void, Sale> {

        @Override
        protected Sale doInBackground(Object... objects) {
            StoreCategory category = (StoreCategory) objects[0];
            String saleUrl = (String) objects[1];

            Document document;
            try {
                document = Jsoup.connect(saleUrl).get();
            }
            catch (IOException e) {
                return null;
            }

            Element textElement = document.selectFirst(".detail__column .keyfacts .keyfacts__block");
            if (textElement == null) return null;

            Element priceElement = document.selectFirst(".buybox__price");
            if (priceElement == null) return null;

            String image, title, subtitle, quantity, discount, oldPrice, newPrice, period;

            Element periodElement = document.selectFirst(".multimediabox .multimediabox__gallery span.label__text");
            if (periodElement != null) {
                period = getPeriod(periodElement.text());
                category.setPeriod(period);
            }
            else return null;

            Element imageElement = document.selectFirst(".multimediabox .multimediabox__gallery .m-slider__wrapper figure.gallery-image img");
            if (imageElement != null) {
                image = imageElement.attr("src");
            }
            else return null;

            Element titleElement = textElement.selectFirst("h1.keyfacts__title");
            if (titleElement != null) {
                title = titleElement.text();
            }
            else return null;

            Element newPriceElement = priceElement.selectFirst(".m-price__price");
            if (newPriceElement != null) {
                newPrice = newPriceElement.text().replaceAll("\\s*Lei$", "");
            }
            else return null;

            Sale sale = new Sale(store.getName(), category.getName(), image, title, newPrice, period);

            Element subtitleElement = textElement.selectFirst(".keyfacts__supplemental-description");
            if (subtitleElement != null) {
                subtitle = subtitleElement.text();
                sale.setSubtitle(subtitle);
            }

            Element quantityElement = priceElement.selectFirst(".price-footer small.price__base");
            if (quantityElement != null) {
                quantity = quantityElement.text();
                sale.setQuantity(quantity);
            }

            Element discountElement = priceElement.selectFirst(".m-price__label");
            if (discountElement != null) {
                discount = discountElement.text();
                sale.setDiscount(discount);
            }

            Element oldPriceElement = priceElement.selectFirst(".m-price__top");
            if (oldPriceElement != null) {
                oldPrice = oldPriceElement.text().replaceAll("\\s*Lei$", "");
                sale.setOldPrice(oldPrice);
            }

            salesRepository.addSale(store, category, sale);
            return null;
        }

        private String getPeriod(String periodText) {
            int currentYear = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentYear = LocalDate.now().getYear();
            }

            String[] dates = periodText.split("-");
            if (dates.length != 2) {
                Pattern pattern = Pattern.compile("\\b\\d{2}\\.\\d{2}\\b\\.");
                Matcher matcher = pattern.matcher(periodText);

                if (matcher.find()) {
                    String fromDate = Objects.requireNonNull(matcher.group(0)).trim() + currentYear;
                    String toDate = Objects.requireNonNull(matcher.group(0)).trim() + currentYear;
                    return fromDate + " - " + toDate;
                }
                else return periodText;
            }

            String fromDate = dates[0].trim() + currentYear;
            String toDate = dates[1].trim() + currentYear;
            return fromDate + " - " + toDate;
        }
    }

    private class SalesScrapper extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            StoreCategory category = (StoreCategory) objects[0];
            String categoryUrl = (String) objects[1];

            Document document;
            try {
                document = Jsoup.connect(categoryUrl).get();
            }
            catch (IOException e) {
                callback.onStoreCategoryNotFound();
                return null;
            }

            Element gridElement = document.selectFirst("ol.ACampaignGrid");
            if (gridElement != null) {

                Elements itemElements = gridElement.select(".ACampaignGrid__item--product.ACampaignGrid__item");
                for (Element itemElement : itemElements) {

                    if (itemElement != null) {
                        Element productDiv = itemElement.selectFirst("div[data-selector=PRODUCT]");
                        String saleUrl = productDiv.attr("canonicalpath");

                        getSale(category, lidlUrl + saleUrl);
                    }
                }
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
    }

    private class LinkScrapper extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            Store store = (Store) objects[0];
            String categoryName = (String) objects[1];
            String categoryFullName = (String) objects[2];

            Document document;
            try {
                document = Jsoup.connect(lidlUrl).get();
            }
            catch (IOException e) {
                callback.onStoreCategoryNotFound();
                return null;
            }

            boolean categoryFound = false;

            Elements listElements = document.select("ol.AHeroStageItems__List");
            if (listElements != null) {

                for (Element listElement  : listElements) {
                    if (!categoryFound) {
                        if (listElement != null) {

                            Elements itemElements = listElement.select("li.AHeroStageItems__Item");

                            for (Element itemElement : itemElements) {
                                Element headlineElement = itemElement.selectFirst("h4.AHeroStageItems__Item--Headline");

                                if (headlineElement.text().equals(categoryFullName)) {
                                    Element aElement = itemElement.selectFirst("a");
                                    String categoryUrl = aElement.attr("href");

                                    StoreCategory category = new StoreCategory(categoryName);
                                    storeCategoriesRepository.addStoreCategory(store, category);

                                    categoryFound = true;
                                    getSales(category, lidlUrl + categoryUrl);
                                    break;
                                }
                            }
                        }
                    }
                    else continue;
                }

                if (!categoryFound) callback.onStoreCategoryNotFound();
            }
            else callback.onStoreCategoryNotFound();
            return null;
        }
    }
}
