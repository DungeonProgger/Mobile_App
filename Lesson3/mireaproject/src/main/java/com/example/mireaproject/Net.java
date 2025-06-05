package com.example.mireaproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Net extends Fragment {

    private static final String TAG = "HttpURLConnectionExample";

    private TextView textViewIp, textViewCity, textViewRegion, textViewCountry, textViewCoordinates, textViewWeather, textViewStatus;
    private Button buttonGetData;

    // IP Info
    private String ip;
    private String city;
    private String region;
    private String country;
    private String latitude;
    private String longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_net, container, false);

        // Views
        textViewIp = rootView.findViewById(R.id.textViewIp);
        textViewCity = rootView.findViewById(R.id.textViewCity);
        textViewRegion = rootView.findViewById(R.id.textViewCoordinates);
        textViewCountry = rootView.findViewById(R.id.textViewCountry);
        textViewCoordinates = rootView.findViewById(R.id.textViewCoordinates);
        textViewWeather = rootView.findViewById(R.id.textViewWeather);
        textViewStatus = rootView.findViewById(R.id.textViewStatus);
        buttonGetData = rootView.findViewById(R.id.buttonGetData);

        buttonGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connMgr != null) {
                    networkInfo = connMgr.getActiveNetworkInfo();
                }

                if (networkInfo != null && networkInfo.isConnected()) {
                    textViewStatus.setText("Loading IP info...");
                    new DownloadIpInfoTask().execute("https://ipinfo.io/json");
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    textViewStatus.setText("Status: No internet");
                }
            }
        });

        textViewStatus.setText("Status: Idle");

        return rootView;
    }

    private class DownloadIpInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadContent(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "Unable to retrieve web page. URL may be invalid. " + e.getMessage());
                return "Error: Unable to retrieve web page.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "IPInfo JSON: " + result);
            try {
                JSONObject responseJson = new JSONObject(result);
                ip = responseJson.optString("ip");
                city = responseJson.optString("city");
                region = responseJson.optString("region");
                country = responseJson.optString("country");
                String loc = responseJson.optString("loc"); // "latitude,longitude"
                if (!loc.isEmpty() && loc.contains(",")) {
                    String[] coordinates = loc.split(",");
                    latitude = coordinates[0];
                    longitude = coordinates[1];
                }

                textViewIp.setText(ip);
                textViewCity.setText(city);
                textViewRegion.setText(region);
                textViewCountry.setText(country);
                if (latitude != null && longitude != null) {
                    textViewCoordinates.setText(latitude + ", " + longitude);
                    // Now fetch weather
                    textViewStatus.setText("Loading weather data...");
                    String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                            "&longitude=" + longitude + "&current_weather=true";
                    Log.d(TAG, "Weather URL: " + weatherUrl);
                    new DownloadWeatherTask().execute(weatherUrl);
                } else {
                    textViewCoordinates.setText("N/A");
                    textViewWeather.setText("N/A (no coordinates)");
                    textViewStatus.setText("Status: IP info loaded, no coordinates for weather.");
                }

            } catch (JSONException e) {
                Log.e(TAG, "JSONException in IPInfo: " + e.getMessage());
                textViewStatus.setText("Error parsing IP info.");
                textViewIp.setText("Error");
            }
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadContent(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "Unable to retrieve weather data. " + e.getMessage());
                return "Error: Unable to retrieve weather data.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Weather JSON: " + result);
            try {
                JSONObject responseJson = new JSONObject(result);
                if (responseJson.has("current_weather")) {
                    JSONObject currentWeather = responseJson.getJSONObject("current_weather");
                    double temperature = currentWeather.getDouble("temperature");
                    textViewWeather.setText(String.format("%.1f°C", temperature));
                    textViewStatus.setText("Status: All data loaded.");
                } else {
                    textViewWeather.setText("N/A");
                    textViewStatus.setText("Status: Weather data not found in response.");
                }

            } catch (JSONException e) {
                Log.e(TAG, "JSONException in Weather: " + e.getMessage());
                textViewWeather.setText("Error");
                textViewStatus.setText("Error parsing weather data.");
            }
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000); // milliseconds
            connection.setConnectTimeout(100000); // milliseconds
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                bos.close();
                data = bos.toString();
            } else {
                data = "Error: " + responseCode + " " + connection.getResponseMessage();
                Log.e(TAG, data);
            }
            connection.disconnect();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}