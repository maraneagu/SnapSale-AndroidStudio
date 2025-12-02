package com.example.snapsale.network.requests;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.models.FavoredSale;
import com.example.snapsale.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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


public class RecipesRequest {
    private static final String TAG = RecipesRequest.class.getName();
    private static RecipesRequest instance;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    private RecipesRequest() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized RecipesRequest getInstance() {
        if (instance == null) {
            instance = new RecipesRequest();
        }
        return instance;
    }

    public void request(FavoredSale favoredSale, DataCallback<Recipe> callback) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "gpt-3.5-turbo");

        JSONArray jsonMessageArray = new JSONArray();
        JSONObject jsonMessageObject = new JSONObject();

        jsonMessageObject.put("role", "system");
        jsonMessageObject.put("content", "You are a chef.");
        jsonMessageArray.put(jsonMessageObject);

        jsonMessageObject = new JSONObject();
        jsonMessageObject.put("role", "user");
        jsonMessageObject.put("content", getUserContent(favoredSale));
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

                        Recipe recipe = getRecipe(content);
                        if (recipe != null) {
                            callback.onGetData(recipe);
                        }

                    } catch (JSONException e) {
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

    public String getUserContent(FavoredSale favoredSale) {
        String saleName;
        if (favoredSale.getSubtitle() != null) {
            saleName = favoredSale.getTitle() + " " + favoredSale.getSubtitle();
        }
        else saleName = favoredSale.getTitle();

        return "Could you please provide me with the general product category based on a sale name? " +
                "Specifically, I would like to know the general product associated with the following sale name: " + saleName +
                "The format of the answer: Product: Name of the product." +
                "Please give a recipe of a dish that has the general product you provided as an edible ingredient in the recipe's ingredients list." +
                "The format of the answer: Recipe: Name of the recipe \n\n Ingredients: List of ingredients \n\n Instructions: List of instructions." +
                "If not possible, write the answer 'No.'";
    }

    private Recipe getRecipe(String recipeText) {
        String[] sections = recipeText.split("\n\n");

        if (sections.length < 4)
            return null;

        String name = sections[1].replace("Recipe: ", "");
        String ingredientsSection = sections[2];
        String instructionsSection = sections[3];

        List<String> ingredients = new ArrayList<>();
        String[] ingredientsLines = ingredientsSection.split("\n");
        for (int i = 1; i < ingredientsLines.length; i++) {
            ingredients.add(ingredientsLines[i].replace("- ", ""));
        }

        String[] instructionsLines = instructionsSection.split("\n");
        List<String> instructions = new ArrayList<>(Arrays.asList(instructionsLines).subList(1, instructionsLines.length));

        return new Recipe(name, ingredients, instructions);
    }
}
