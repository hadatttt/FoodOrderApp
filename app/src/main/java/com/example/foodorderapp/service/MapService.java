package com.example.foodorderapp.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapService {

    public interface OnLocationResult {
        void onLocationFound(double lat, double lng);
    }

    public interface OnTravelTimeResult {
        void onTimeResult(String timeStr);
    }
    public interface OnRouteCoordinatesResult {
        void onRouteCoordinates(JSONArray coordinates);
    }

    public void getRouteCoordinatesOSRM(double userLat, double userLng, double shopLat, double shopLng, OnRouteCoordinatesResult callback) {
        new Thread(() -> {
            try {
                String url = String.format(
                        "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                        userLng, userLat, shopLng, shopLat);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "FastFood/1.0")
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray routes = json.getJSONArray("routes");
                    if (routes.length() > 0) {
                        JSONObject geometry = routes.getJSONObject(0).getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        callback.onRouteCoordinates(coordinates);
                    } else {
                        callback.onRouteCoordinates(null);
                    }
                } else {
                    callback.onRouteCoordinates(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onRouteCoordinates(null);
            }
        }).start();
    }
    public void getCoordinatesFromAddress(String address, OnLocationResult callback) {
        new Thread(() -> {
            try {
                String url = "https://nominatim.openstreetmap.org/search?q="
                        + URLEncoder.encode(address, "UTF-8") + "&format=json";
                Log.d("SIZEEE", url);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "FastFood/1.0")
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    if (jsonArray.length() > 0) {
                        JSONObject location = jsonArray.getJSONObject(0);
                        double lat = location.getDouble("lat");
                        double lon = location.getDouble("lon");
                        callback.onLocationFound(lat, lon);
                    } else {
                        callback.onLocationFound(0, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onLocationFound(0, 0);
            }
        }).start();
    }

    public void getTravelTimeOSRM(double userLat, double userLng, double shopLat, double shopLng, OnTravelTimeResult callback) {
        new Thread(() -> {
            try {
                String url = String.format(
                        "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=false",
                        userLng, userLat, shopLng, shopLat);

                Log.d("SIZEEE", url);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "FastFood/1.0")
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray routes = json.getJSONArray("routes");
                    if (routes.length() > 0) {
                        JSONObject route = routes.getJSONObject(0);
                        double durationSeconds = route.getDouble("duration");

                        int hours = (int) (durationSeconds / 3600);
                        int minutes = (int) ((durationSeconds % 3600) / 60);

                        String timeStr = (hours > 0) ? (hours + " giờ " + minutes + " phút") : (minutes + " phút");
                        callback.onTimeResult(timeStr);
                    } else {
                        callback.onTimeResult("--");
                    }
                } else {
                    callback.onTimeResult("--");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onTimeResult("--");
            }
        }).start();
    }
}
