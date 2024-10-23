package com.nissanauto.findnearbypoigooglemap;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearbyPOIFetcher {
    private static final String TAG = "NearbyPOIFetcher";
    private String apiKey;
    //private GoogleMap mMap;
    private Context context;
    public NearbyPOIFetcher(Context context, String apiKey, GoogleMap mMap) {
        this.context = context;
        this.apiKey = apiKey;
        //this.mMap = mMap;
    }

    public void fetchNearbyStations(double latitude, double longitude) {

        String radius = "1000"; // Radius in meters
        String type = "restaurant"; // Type as a variable: charging_station or supermarket or restaurant ...

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude + "," + longitude
                + "&radius=" + radius + "&type=" + type + "&key=" + apiKey;


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching nearby places: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray results = jsonResponse.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");

                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            String name = place.getString("name");

                            // Log and print nearby POI latitude and longitude
                            String latLngText = "POI Station: " + name + ", Latitude: " + lat + ", Longitude: " + lng;
                            //Log.d(TAG, latLngText);
                            System.out.println(latLngText);

                            // Show latitude and longitude as a toast
                            //runOnMainThread(() -> Toast.makeText(context, latLngText, Toast.LENGTH_LONG).show());

                            // Add marker on the map for each charging station
                            LatLng chargingStationLocation = new LatLng(lat, lng);
                            /*runOnMainThread(() -> mMap.addMarker(new MarkerOptions()
                                    .position(chargingStationLocation)
                                    .title(name)));*/

                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Response was not successful");
                }
            }
        });
    }

    private void runOnMainThread(Runnable action) {
        if (context instanceof MainActivity) {
            ((MainActivity) context).runOnUiThread(action);
        }
    }
}

