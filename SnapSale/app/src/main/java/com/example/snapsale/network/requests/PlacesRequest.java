package com.example.snapsale.network.requests;

import android.app.Activity;

import com.example.snapsale.R;
import com.example.snapsale.callbacks.DoubleDataCallback;
import com.example.snapsale.network.DownloadUrl;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


// HELPFUL LINKS:
// 1). "How to Implement Autocomplete Place Api in Android Studio | AutocompletePlaceApi | Android Coding", Android Coding, URL: https://www.youtube.com/watch?v=t8nGh4gN1Q0&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=45&t=27s&ab_channel=AndroidCoding
// 2). "How to Search places by using google API in android studio | Game App Studio", Game App Studio, URL: https://www.youtube.com/watch?v=V3LISe99qs0&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=46&t=403s&ab_channel=GameAppStudio


public class PlacesRequest {
    private final Activity activity;

    public PlacesRequest(Activity activity) {
        this.activity = activity;
    }

    public PlacesClient getPlacesClient() {
        Places.initialize(activity, activity.getResources().getString(R.string.google_places_key));
        return Places.createClient(activity);
    }

    public String getPlacesAutoCompleteUrl(String input) {
        return "https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
                "input=" + input +
                "&components=country:RO" +
                "&key=" + activity.getResources().getString(R.string.google_places_key);
    }

    public void getPlaces(String placesUrl, DoubleDataCallback<ArrayList<String>, ArrayList<String>> callback) throws JSONException, IOException {
        ArrayList<String> placesDescription = new ArrayList<>();
        ArrayList<String> placesId = new ArrayList<>();

        String placesData;
        DownloadUrl downloadUrl = new DownloadUrl();
        placesData = downloadUrl.download(placesUrl);

        JSONObject jsonObject = new JSONObject(placesData);
        JSONArray jsonPredictions = jsonObject.getJSONArray("predictions");

        for (int i = 0; i < jsonPredictions.length(); i++) {
            String description = jsonPredictions.getJSONObject(i).getString("description");
            String placeId = jsonPredictions.getJSONObject(i).getString("place_id");

            placesDescription.add(description);
            placesId.add(placeId);
        }

        callback.onGetDoubleData(placesDescription, placesId);
    }
}
