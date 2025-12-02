package com.example.snapsale.network.requests;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.snapsale.R;
import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.network.DownloadUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


// HELPFUL LINKS:
// 1). "How to Find Nearby Places on Map in Android Studio | NearbyPlaces | Android Coding", Android Coding, URL: https://www.youtube.com/watch?v=pjFcJ6EB8Dg&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=27&ab_channel=AndroidCoding
// 2). "Android Nearby Places Tutorial 06 - Making 3 classes - Google Maps Nearby Places Tutorial", Muhammad Ali's Coding Cafe, URL: https://www.youtube.com/watch?v=0QzKquJ4j8Y&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=34&ab_channel=MuhammadAli%27sCodingCafe
// 3). "Google Places API Android Studio Tutorial 07 Show Nearby Places Google Maps Nearby Places API Key", Muhammad Ali's Coding Cafe, URL:https://www.youtube.com/watch?v=Iz4y0ofVTk4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=35&t=994s&ab_channel=MuhammadAli%27sCodingCafe


public class NearbyStoresRequest {
    private final Activity activity;

    public NearbyStoresRequest(Activity activity) {
        this.activity = activity;
    }

    public String getNearbySearchUrl(double latitude, double longitude, String keyword) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "keyword=" + keyword +
                "&location=" + latitude + "," + longitude +
                "&rankby=distance" +
                "&key=" + activity.getResources().getString(R.string.google_places_key);
    }

    @SuppressLint("StaticFieldLeak")
    public static class NearbyStores extends AsyncTask<Object, String, String> {
        String name;
        List<HashMap<String, String>> stores;
        DataCallback<List<HashMap<String, String>>> callback;

        @Override
        protected String doInBackground(Object... objects) {
            String storesUrl = (String) objects[0];
            name = (String) objects[1];
            callback = (DataCallback<List<HashMap<String, String>>>) objects[2];

            String storesData = null;
            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                storesData = downloadUrl.download(storesUrl);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return storesData;
        }

        @Override
        protected void onPostExecute(String data) {
            NearbyStoreDataParser dataParser = new NearbyStoreDataParser();
            try {
                stores = dataParser.parse(data, name);
                callback.onGetData(stores);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class NearbyStoreDataParser {
        String name;
        private HashMap<String, String> getStore(JSONObject jsonStore) throws JSONException {
            HashMap<String, String> store = new HashMap<>();

            String storeName = "N/A";
            String storeVicinity = "N/A";
            String storeLatitude;
            String storeLongitude;
            String storeReference = "N/A";
            String storeOpen = "N/A";

            if (!jsonStore.isNull("name")) {
                storeName = jsonStore.getString("name");
            }

            if (!jsonStore.isNull("vicinity")) {
                storeVicinity = jsonStore.getString("vicinity");
            }

            storeLatitude = jsonStore.getJSONObject("geometry").getJSONObject("location").getString("lat");
            storeLongitude = jsonStore.getJSONObject("geometry").getJSONObject("location").getString("lng");

            if (!jsonStore.isNull("reference")) {
                storeReference = jsonStore.getString("reference");
            }

            if (!jsonStore.isNull("opening_hours")) {
                storeOpen = jsonStore.getJSONObject("opening_hours").getString("open_now");
            }

            store.put("name", storeName);
            store.put("vicinity", storeVicinity);
            store.put("latitude", storeLatitude);
            store.put("longitude", storeLongitude);
            store.put("reference", storeReference);
            store.put("open", storeOpen);

            return store;
        }

        private List<HashMap<String, String>> getStores(JSONArray jsonStores) throws JSONException {
            List<HashMap<String, String>> stores = new ArrayList<>();
            HashMap<String, String> store;

            for (int s = 0; s < jsonStores.length(); s++) {
                store = getStore((JSONObject) jsonStores.get(s));

                if (Objects.requireNonNull(store.get("name")).toLowerCase().contains(name.toLowerCase())) {
                    stores.add(store);
                }
            }

            return stores;
        }

        public List<HashMap<String, String>> parse(String data, String name) throws JSONException {
            JSONArray jsonStores;
            JSONObject jsonObject = new JSONObject(data);
            this.name = name;

            jsonStores = jsonObject.getJSONArray("results");
            return getStores(jsonStores);
        }
    }
}
