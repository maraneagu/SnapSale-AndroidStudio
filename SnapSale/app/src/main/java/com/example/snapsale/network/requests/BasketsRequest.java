package com.example.snapsale.network.requests;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.BasketCallback;
import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.models.Basket;
import com.example.snapsale.models.Sale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


// HELPFUL LINKS:
// 1). "How to integrate Open AI Chat GPT model "gpt-3.5-turbo" in your Android app?", Programmer World, URL: https://www.youtube.com/watch?v=wcrEmBVDQ0Y&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=60&ab_channel=ProgrammerWorld
// 2). "How to create ChatGPT android app | Full Tutorial | 2024", Easy Tuto, URL: https://www.youtube.com/watch?v=ahhze_u5ZUs&ab_channel=EasyTuto


public class BasketsRequest {
    private static final String TAG = BasketsRequest.class.getName();
    private static BasketsRequest instance;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    private BasketsRequest() {
        client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(70, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized BasketsRequest getInstance() {
        if (instance == null) {
            instance = new BasketsRequest();
        }
        return instance;
    }

    public void request(String name, String type, final List<Sale> sales, BasketCallback callback, DataCallback<Basket> dataCallback) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "gpt-3.5-turbo");

        JSONArray jsonMessageArray = new JSONArray();
        JSONObject jsonMessageObject = new JSONObject();

        jsonMessageObject.put("role", "system");
        jsonMessageObject.put("content", "You work in a supermarket.");

        jsonMessageObject = new JSONObject();
        jsonMessageObject.put("role", "user");

        String userContent = getUserContent(type, sales);
        jsonMessageObject.put("content", userContent);

        jsonMessageArray.put(jsonMessageObject);
        jsonBody.put("messages", jsonMessageArray);

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        okhttp3.Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer sk-proj-4jJmjslWRTnGLLneVVWkT3BlbkFJNMkyR7RjgbuydwUVzN4V")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonChoicesArray = jsonObject.getJSONArray("choices");
                        JSONObject jsonFirstIndex = jsonChoicesArray.getJSONObject(0);
                        JSONObject jsonMessage = jsonFirstIndex.getJSONObject("message");
                        String content = jsonMessage.getString("content");

                        sales.removeIf(sale -> !content.contains(sale.getTitle()));
                        if (!sales.isEmpty()) {
                            String period = getPeriod(sales);

                            if (period == null) callback.onBasketNotFound();
                            else {
                                HashMap<String, Sale> updatedSales = new HashMap<>();
                                for (Sale sale : sales) updatedSales.put(sale.getKey(), sale);

                                Basket basket = new Basket(name, type, period, updatedSales);
                                dataCallback.onGetData(basket);
                            }
                        }
                        else callback.onBasketNotFound();

                    }
                    catch (JSONException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                else Log.e(TAG, response.message());
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    private String getUserContent(String type, List<Sale> sales) {
        StringBuilder content = new StringBuilder();
        content.append("I'm putting together a ").append(type).append(" themed shopping basket and need help selecting the perfect items for it from a list of products.\n");
        content.append("Could you please assist me in selecting the items from the list that would be suitable for this themed basket?\n");
        content.append("If the theme of the basket is edible, remove all of the decorative plants from the list, if they exist.\n\n");

        content.append("List of products:\n");
        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);
            String saleName = sale.getSubtitle() == null ? sale.getTitle() : sale.getTitle() + " " + sale.getSubtitle();
            content.append(i + 1).append(". ").append(saleName).append(";\n");
        }

        content.append("\nPlease provide the list of suitable products, no translation needed.\n");
        content.append("Format: Updated List: the updated list, indexed.");
        return content.toString();
    }


    public String getPeriod(List<Sale> sales) throws ParseException {
        String[] period = sales.get(0).getPeriod().split(" - ");
        String minStartDate = period[0];
        String maxEndDate = period[1];

        for (int i = 1; i < sales.size(); i++) {
            period = sales.get(i).getPeriod().split(" - ");
            String startDate = period[0];
            String endDate = period[1];

            if (startDate.compareTo(minStartDate) > 0) minStartDate = startDate;
            if (endDate.compareTo(maxEndDate) < 0) maxEndDate = endDate;
        }

        if (isBasketUnavailable(maxEndDate)) return null;
        return minStartDate + " - " + maxEndDate;
    }

    private boolean isBasketUnavailable(String lastPeriod) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date currentDate = new Date();

            Date lastPeriodDate = dateFormat.parse(lastPeriod);
            assert lastPeriodDate != null;
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            lastPeriodDate.setTime(lastPeriodDate.getTime() + oneDayInMillis);

            return currentDate.after(lastPeriodDate);
        }
        catch (ParseException e) {
            return false;
        }
    }
}
