package com.example.snapsale.activities.guestActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapsale.R;
import com.example.snapsale.activities.mainActivities.HomeActivity;
import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.helpers.DrawableToBitmapConverter;
import com.example.snapsale.network.requests.NearbyStoresRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StrokeStyle;
import com.google.android.gms.maps.model.StyleSpan;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


// HELPFUL LINKS:
// 1). "How to Implement Google Map in Android Studio | 2022", Android Knowledge, URL: https://www.youtube.com/watch?v=JzxjNNCYt_o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=26&t=5s&ab_channel=AndroidKnowledge
// 2). "How to Get Location Permission in Android Studio? Viral Coder", Viral Coder, URL: https://www.youtube.com/watch?v=Vo4VaoR2Gmw&t=132s&ab_channel=ViralCoder
// 3). "Current Location On Google Map In Android Studio | Java | Hindi Tutorial @CodeByAshish", Code By Ashish, URL: https://www.youtube.com/watch?v=poJAJAhYF2E&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=43&ab_channel=CodeByAshish
// 4). "How to Change Google Map Style in your Android Application | 2020 | Source Code", Tech Mirros, URL: https://www.youtube.com/watch?v=Kim924JSBvg&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=30&t=351s&ab_channel=TechMirrors
// 5). "Custom Marker Icon On Google Map In Android Studio | Google Map Tutorials | Java | @CodeByAshish", CodeByAshish, URL: https://www.youtube.com/watch?v=2F2HWW923SY&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=31&t=455s&ab_channel=CodeByAshish
// 6). "How to Find Nearby Places on Map in Android Studio | NearbyPlaces | Android Coding", Android Coding, URL: https://www.youtube.com/watch?v=pjFcJ6EB8Dg&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=27&ab_channel=AndroidCoding
// 7). "Android Nearby Places Tutorial 06 - Making 3 classes - Google Maps Nearby Places Tutorial", Muhammad Ali's Coding Cafe, URL: https://www.youtube.com/watch?v=0QzKquJ4j8Y&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=34&ab_channel=MuhammadAli%27sCodingCafe
// 8). "Google Places API Android Studio Tutorial 07 Show Nearby Places Google Maps Nearby Places API Key", Muhammad Ali's Coding Cafe, URL:https://www.youtube.com/watch?v=Iz4y0ofVTk4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=35&t=994s&ab_channel=MuhammadAli%27sCodingCafe
// 9). "How to Display Track On Google Map in Android Studio | DisplayTrackOnMap | Android Coding", Android Coding, URL: https://www.youtube.com/watch?v=VR8RKM9LTyA&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=33&ab_channel=AndroidCoding
// 10). "Adding Polylines to a Google Map", CodingWithMitch, URL: https://www.youtube.com/watch?v=xl0GwkLNpNI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=38&ab_channel=CodingWithMitch
// 11). "Calculating Directions with Google Directions API", CodingWithMitch, URL: https://www.youtube.com/watch?v=f47L1SL5S0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=39&ab_channel=CodingWithMitch
// 12). "Google Directions API: Getting Started", CodingWithMitch, URL: https://www.youtube.com/watch?v=sdinkRanD0I&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=40&ab_channel=CodingWithMitch


public class LocationGuestActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int DIRECTION_REQUEST_CODE = 102;
    private static final int LOCATION_SETTINGS_CODE = 103;
    private static final int REFRESH_TIME = 4000;
    private static final int OFFSET_LATITUDE = 120;

    private NearbyStoresRequest nearbyStoresRequest;

    private DrawerLayout drawerLayout;
    private RelativeLayout searchLayout;

    private ImageView kauflandButton, lidlButton, pennyButton, carrefourButton;

    private LinearLayout directionLayout;
    private Button currentLocationButton, zoomInButton, zoomOutButton;

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private BitmapDescriptor currentLocationMapIcon, storeMapIcon, selectedStoreMapIcon;
    private boolean isStoreButtonPressed = false;

    private Handler mapHandler;
    private Runnable mapRunnable;
    private Location currentLocation;
    private Marker currentMarker;

    private GeoApiContext mapContext;
    private List<Polyline> polylines;

    private int currentStore;
    private List<Marker> storeMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_guest);

        nearbyStoresRequest = new NearbyStoresRequest(this);

        drawerLayout = findViewById(R.id.location_navigation_drawer_layout);
        setNavigationDrawer();

        searchLayout = findViewById(R.id.location_search_layout);
        AutoCompleteTextView searchAutoComplete = findViewById(R.id.location_search_autocomplete);
        searchAutoComplete.setEnabled(false);

        kauflandButton = findViewById(R.id.location_kaufland_btn);
        lidlButton = findViewById(R.id.location_lidl_btn);
        pennyButton = findViewById(R.id.location_penny_btn);
        carrefourButton = findViewById(R.id.location_carrefour_btn);

        directionLayout = findViewById(R.id.location_direction_layout);
        currentLocationButton = findViewById(R.id.location_current_location_btn);
        zoomInButton = findViewById(R.id.location_zoom_in_btn);
        zoomOutButton = findViewById(R.id.location_zoom_out_btn);

        currentLocationMapIcon = new DrawableToBitmapConverter().convert(LocationGuestActivity.this, R.drawable.icon_location_pin_orange);
        storeMapIcon = new DrawableToBitmapConverter().convert(LocationGuestActivity.this, R.drawable.icon_location_pin_blue);
        selectedStoreMapIcon = new DrawableToBitmapConverter().convert(LocationGuestActivity.this, R.drawable.icon_location_pin_dark_blue);

        polylines = new ArrayList<>();
        storeMarkers = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.location_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(LocationGuestActivity.this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isStoreButtonPressed) {
            resetStoreButtons();
        }
        Checker.checkMove(LocationGuestActivity.this, this::checkLocationPermissions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_SETTINGS_CODE) {
            if (resultCode == RESULT_OK) getCurrentLocation();
            else {
                finish();
                ActivityNavigator.navigate(this, HomeActivity.class);
            }
        }

        if (requestCode == DIRECTION_REQUEST_CODE) {
            isStoreButtonPressed = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (!(storeMarkers.isEmpty())) {
            getCurrentLocationMap();
        }
        else super.onBackPressed();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            checkLocationSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings();
            }
            else checkLocationPermissions();
        }
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            getMapHandler();
            getCurrentLocation();
        });

        task.addOnFailureListener(this, e -> {
            if (mapHandler != null && mapRunnable != null) {
                mapHandler.removeCallbacks(mapRunnable);
            }

            ResolvableApiException resolvable = (ResolvableApiException) e;
            try {
                resolvable.startResolutionForResult(LocationGuestActivity.this, LOCATION_SETTINGS_CODE);
            } catch (IntentSender.SendIntentException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void getMapHandler() {
        mapHandler = new Handler();
        mapHandler.postDelayed(mapRunnable = () -> {
            checkLocationPermissions();
            mapHandler.postDelayed(mapRunnable, REFRESH_TIME);
        }, REFRESH_TIME);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("Current Location").icon(currentLocationMapIcon);

                if (currentMarker != null) {
                    float[] distance = new float[1];
                    Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            currentMarker.getPosition().latitude, currentMarker.getPosition().longitude, distance);

                    if (distance[0] >= 20) {
                        currentMarker.remove();
                        currentMarker = map.addMarker(markerOptions);
                    }
                }
                else {
                    currentMarker = map.addMarker(markerOptions);
                    currentLatLng = new LatLng(currentLocation.getLatitude() + 0.0025, currentLocation.getLongitude());

                    map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.location_map_style));
        setMapButtons();
        setStoreButtons();

        map.setOnMarkerClickListener(marker -> {
            if (Objects.equals(marker.getTitle(), "Current Location")) {
                getCurrentLocationMap();
            }
            else {
                storeMarkers.get(currentStore).setIcon(storeMapIcon);
                currentStore = storeMarkers.indexOf(marker);
                getDirectionToStore(marker);
            }

            return true;
        });

        if (mapContext == null) {
            mapContext = new GeoApiContext.Builder().apiKey(getResources().getString(R.string.google_places_key)).build();
        }
        setDirectionButtons();
    }

    private void setMapButtons() {
        currentLocationButton.setOnClickListener(view -> {
            getCurrentLocationMap();
        });

        zoomInButton.setOnClickListener(view -> {
            if (map != null) {
                float currentZoom = map.getCameraPosition().zoom;
                float newZoom = currentZoom + 1;
                map.animateCamera(CameraUpdateFactory.zoomTo(newZoom));
            }
        });

        zoomOutButton.setOnClickListener(view -> {
            if (map != null) {
                float currentZoom = map.getCameraPosition().zoom;
                float newZoom = currentZoom - 1;
                map.animateCamera(CameraUpdateFactory.zoomTo(newZoom));
            }
        });
    }

    private void getCurrentLocationMap() {
        map.clear();
        removePolylines();
        resetStoreButtons();
        storeMarkers.clear();

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("Current Location").icon(currentLocationMapIcon);

        currentMarker.remove();
        currentMarker = map.addMarker(markerOptions);
        currentLatLng = new LatLng(currentLocation.getLatitude() + 0.0025, currentLocation.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .tilt(0)
                .bearing(0)
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (searchLayout.getVisibility() == View.GONE)
            searchLayout.setVisibility(View.VISIBLE);

        if (directionLayout.getVisibility() == View.VISIBLE)
            directionLayout.setVisibility(View.GONE);
    }


    // STORE BUTTONS

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setStoreButton(ImageView button, int icon, int pressedIcon, String keyword, String name) {
        if (Objects.equals(button.getDrawable().getConstantState(), getResources().getDrawable(icon).getConstantState())) {
            resetStoreButtons();

            button.setImageResource(pressedIcon);
            ((GradientDrawable) button.getBackground()).setColor(ContextCompat.getColor(getApplicationContext(), R.color.middleOrange));

            findNearbyStores(keyword, name);
        } else {
            button.setImageResource(icon);
            ((GradientDrawable) button.getBackground()).setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));

            getCurrentLocationMap();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setStoreButtons() {
        String[] storeKeywords = getResources().getStringArray(R.array.store_keywords);
        String[] storeNames = getResources().getStringArray(R.array.store_names);

        kauflandButton.setOnClickListener(view -> Toast.makeText(LocationGuestActivity.this, "Register to access the locations of this store.", Toast.LENGTH_LONG).show());

        lidlButton.setOnClickListener(view -> setStoreButton(lidlButton,
                R.drawable.location_logo_lidl,
                R.drawable.location_logo_lidl_pressed,
                storeKeywords[1],
                storeNames[1]));

        pennyButton.setOnClickListener(view -> Toast.makeText(LocationGuestActivity.this, "Register to access the locations of this store.", Toast.LENGTH_LONG).show());

        carrefourButton.setOnClickListener(view -> setStoreButton(carrefourButton,
                R.drawable.location_logo_carrefour,
                R.drawable.location_logo_carrefour_pressed,
                storeKeywords[3],
                storeNames[3]));
    }

    private void resetStoreButtons() {
        kauflandButton.setImageResource(R.drawable.location_guest_logo_kaufland);
        ((GradientDrawable) kauflandButton.getBackground()).setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));

        lidlButton.setImageResource(R.drawable.location_logo_lidl);
        ((GradientDrawable) lidlButton.getBackground()).setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));

        pennyButton.setImageResource(R.drawable.location_guest_logo_penny);
        ((GradientDrawable) pennyButton.getBackground()).setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));

        carrefourButton.setImageResource(R.drawable.location_logo_carrefour);
        ((GradientDrawable) carrefourButton.getBackground()).setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));
    }


    // NEARBY STORES

    private void findNearbyStores(String keyword, String name) {
        String storesUrl = nearbyStoresRequest.getNearbySearchUrl(currentLocation.getLatitude(), currentLocation.getLongitude(), keyword);

        DataCallback<List<HashMap<String, String>>> callback = this::getNearbyStores;
        NearbyStoresRequest.NearbyStores nearbyStores = new NearbyStoresRequest.NearbyStores();
        nearbyStores.execute(storesUrl, name, callback);

        map.clear();

        if (searchLayout.getVisibility() == View.VISIBLE)
            searchLayout.setVisibility(View.GONE);

        if (directionLayout.getVisibility() == View.VISIBLE)
            directionLayout.setVisibility(View.GONE);
    }

    private void getNearbyStores(List<HashMap<String, String>> stores) {
        storeMarkers.clear();

        if (stores.isEmpty()) {
            getCurrentLocationMap();
            Toast.makeText(LocationGuestActivity.this, "No stores of that kind in the area.", Toast.LENGTH_LONG).show();
        }
        else {
            for (int s = 0; s < stores.size(); s++) {
                HashMap<String, String> store = stores.get(s);

                String storeName = store.get("name");
                String storeVicinity = store.get("vicinity");

                double storeLatitude = Double.parseDouble(Objects.requireNonNull(store.get("latitude")));
                double storeLongitude = Double.parseDouble(Objects.requireNonNull(store.get("longitude")));

                String storeOpen;
                if (Objects.equals(store.get("open"), "true"))
                    storeOpen = "Open";
                else if (Objects.equals(store.get("open"), "false"))
                    storeOpen = "Closed";
                else storeOpen = store.get("open");

                LatLng storeLatLng = new LatLng(storeLatitude, storeLongitude);
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(storeLatLng);
                markerOptions.title(storeName);
                markerOptions.snippet(storeVicinity + "\n\n" + storeOpen);
                markerOptions.icon(storeMapIcon);

                Marker storeMarker = map.addMarker(markerOptions);
                storeMarkers.add(storeMarker);
            }

            currentStore = 0;
            getDirectionToStore(storeMarkers.get(currentStore));
        }
    }


    // HELPERS FOR NEARBY STORES

    private void setDirectionButtons() {
        ImageView backButton = findViewById(R.id.direction_back);
        backButton.setOnClickListener(view -> {
            storeMarkers.get(currentStore).setIcon(storeMapIcon);

            if (currentStore == 0)
                currentStore = storeMarkers.size() - 1;
            else currentStore -= 1;
            getDirectionToStore(storeMarkers.get(currentStore));
        });

        ImageView nextButton = findViewById(R.id.direction_next);
        nextButton.setOnClickListener(view -> {
            storeMarkers.get(currentStore).setIcon(storeMapIcon);

            if (currentStore == storeMarkers.size() - 1)
                currentStore = 0;
            else currentStore += 1;

            getDirectionToStore(storeMarkers.get(currentStore));
        });
    }

    private void getDirectionToStore(Marker marker) {
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(mapContext);
        directionsApiRequest.alternatives(false);

        directionsApiRequest.origin(new com.google.maps.model.LatLng(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        ));

        directionsApiRequest.destination(new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        )).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                setInformationToStore(result, marker);
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void setInformationToStore(DirectionsResult result, Marker marker) {
        new Handler(Looper.getMainLooper()).post(() -> {
            removePolylines();

            if (directionLayout.getVisibility() != View.VISIBLE)
                directionLayout.setVisibility(View.VISIBLE);

            TextView directionTitle = findViewById(R.id.direction_title);
            directionTitle.setText(marker.getTitle());

            TextView directionSnippet = findViewById(R.id.direction_snippet);
            directionSnippet.setText(marker.getSnippet());

            TextView directionDistance = findViewById(R.id.direction_distance);
            Distance distance = result.routes[0].legs[0].distance;
            directionDistance.setText(String.valueOf(distance));

            TextView directionDuration = findViewById(R.id.direction_duration);
            directionDuration.setText(String.valueOf(result.routes[0].legs[0].duration));

            ImageView directionCancelButton = findViewById(R.id.direction_cancel_button);
            directionCancelButton.setOnClickListener(view -> {
                marker.setIcon(storeMapIcon);
                removePolylines();

                if (directionLayout.getVisibility() == View.VISIBLE) {
                    directionLayout.setVisibility(View.GONE);
                }
                if (searchLayout.getVisibility() == View.GONE) {
                    searchLayout.setVisibility(View.VISIBLE);
                }
            });

            RelativeLayout directionButton = findViewById(R.id.direction_button);
            directionButton.setOnClickListener(view -> {
                double destinationLatitude = marker.getPosition().latitude;
                double destinationLongitude = marker.getPosition().longitude;
                String destinationName = marker.getTitle();

                String uri = "geo:" + destinationLatitude + "," + destinationLongitude + "?q=" + destinationLatitude + "," + destinationLongitude + "(" + destinationName + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                Intent chooser = Intent.createChooser(intent, "Choose Navigation App");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(chooser, DIRECTION_REQUEST_CODE);
                }
            });

            LatLng originLatLng;
            MarkerOptions markerOptions;
            originLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            markerOptions = new MarkerOptions().position(originLatLng).title("Current Location").icon(currentLocationMapIcon);

            map.addMarker(markerOptions);
            marker.setIcon(selectedStoreMapIcon);

            float zoomLevel = calculateZoomLevel(distance.inMeters);
            LatLng destinationLatLng = marker.getPosition();
            destinationLatLng = new LatLng(destinationLatLng.latitude + OFFSET_LATITUDE / Math.pow(2, zoomLevel), destinationLatLng.longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(destinationLatLng)
                    .tilt(0)
                    .bearing(0)
                    .zoom(zoomLevel)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            DirectionsRoute[] route = result.routes;
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route[0].overviewPolyline.getEncodedPath());

            List<LatLng> newDecodedPath = new ArrayList<>();
            for (com.google.maps.model.LatLng latLng : decodedPath) {
                newDecodedPath.add(new LatLng(
                        latLng.lat,
                        latLng.lng
                ));
            }

            Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath)
                    .addSpan(new StyleSpan(StrokeStyle.gradientBuilder(ContextCompat.getColor(LocationGuestActivity.this, R.color.middleOrange),
                            ContextCompat.getColor(LocationGuestActivity.this, R.color.middleBlue)).build())));
            polylines.add(polyline);
            polyline.setClickable(true);
        });
    }

    private float calculateZoomLevel(double distance) {
        distance = distance / 1000;
        float zoomLevel;

        if (distance > 0 && distance <= 1) {
            zoomLevel = 14.5F;
        } else if (distance > 1 && distance <= 2.5) {
            zoomLevel = 14F;
        } else if (distance > 2.5 && distance <= 4) {
            zoomLevel = 13F;
        } else if (distance > 4 && distance <= 6) {
            zoomLevel = 12.5F;
        } else if (distance > 6 && distance <= 8) {
            zoomLevel = 12F;
        } else if (distance > 8 && distance <= 10) {
            zoomLevel = 11.5F;
        } else if (distance > 10 && distance <= 12) {
            zoomLevel = 11F;
        } else if (distance > 12 && distance <= 15) {
            zoomLevel = 10.5F;
        } else {
            zoomLevel = 10F;
        }
        return zoomLevel;
    }

    private void removePolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }

    // NAVIGATION DRAWER

    private void setNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.location_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nd, R.string.close_nd);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.location_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nd_home) {
            ActivityNavigator.navigate(this, HomeGuestActivity.class);
        }
        else if (id == R.id.nd_search) {
            ActivityNavigator.navigate(this, SearchGuestActivity.class);
        }
        else if (id == R.id.nd_favorites) {
            Toast.makeText(LocationGuestActivity.this, "Register to access the favored sales.", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.nd_baskets) {
            ActivityNavigator.navigate(this, BasketsGuestActivity.class);
        }
        else if (id == R.id.nd_profile) {
            ActivityNavigator.navigate(this, ProfileGuestActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}