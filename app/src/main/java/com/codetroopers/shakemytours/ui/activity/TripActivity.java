/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codetroopers.shakemytours.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;
import com.codetroopers.shakemytours.util.Strings;
import com.codetroopers.shakemytours.service.JsonDirectionResponse;
import com.codetroopers.shakemytours.util.TravelItemFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TripActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static final String PARAM_TRAVELS = "PARAM_TRAVELS";
    private SparseArray<Bitmap> mMarkerList;
    private SparseArray<Integer> mAvailableColors;
    private ShareActionProvider mShareActionProvider;

    public static Intent newIntent(Context context, ArrayList<Travel> selectedTravels) {
        Intent intent = new Intent(context, TripActivity.class);
        intent.putParcelableArrayListExtra(PARAM_TRAVELS, selectedTravels);
        return intent;
    }

    @Bind(R.id.trip_activity_mapview)
    MapView mapView;
    @Bind(R.id.trip_activity_recyclerview)
    RecyclerView mRecyclerView;

    int primaryColor;
    @Nullable
    private GoogleMap map;

    @Nullable
    private LatLng mLastKnownLatLng;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Travel> mTravels;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);


        mTravels = getIntent().getParcelableArrayListExtra(PARAM_TRAVELS);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initAvailableColors();
        generateMarkers(mTravels.size());
        primaryColor = getResources().getColor(R.color.colorPrimary);

        mapView.onCreate(savedInstanceState);
        setupRecyclerView();
        buildGoogleApiClient();
    }

    private void initAvailableColors() {
        mAvailableColors = new SparseArray<>();
        mAvailableColors.put(0, getResources().getColor(R.color.map_marker_0));
        mAvailableColors.put(1, getResources().getColor(R.color.map_marker_1));
        mAvailableColors.put(2, getResources().getColor(R.color.map_marker_2));
        mAvailableColors.put(3, getResources().getColor(R.color.map_marker_3));
        mAvailableColors.put(4, getResources().getColor(R.color.map_marker_4));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void initMap() {

        String orgineCoord = "origin=" + mLastKnownLatLng.latitude + "," + mLastKnownLatLng.longitude;
        String destCoord = "destination=" + mTravels.get(mTravels.size() - 1).latitude + "," + mTravels.get(mTravels.size() - 1).longitude;

        String waypoints = "waypoints=";
        for (int i = 0; i < mTravels.size() - 1; i++) {
            waypoints += mTravels.get(i).latitude + "," + mTravels.get(i).longitude + "|";
        }
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?" + orgineCoord + "&" + destCoord + "&" + waypoints + "&key=AIzaSyAHPXTD5kFmDX7YzkzLPTk0hOvmEKOITz4");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final URL finalUrl = url;
        new AsyncTask<Void, Void, JsonDirectionResponse>() {
            @Override
            protected JsonDirectionResponse doInBackground(Void... params) {
                JsonDirectionResponse response = null;
                HttpURLConnection request = null;
                try {
                    request = (HttpURLConnection) finalUrl.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                BufferedReader reader = null;
                Gson gson = new GsonBuilder().create();
                try {
                    reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

                    final Type result = new TypeToken<JsonDirectionResponse>() {
                    }.getType();

                    response = gson.fromJson(reader, result);
                } catch (Exception e) {
                    Timber.e(e, "");
                } finally {
                    if (null != reader) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Timber.e(e, "");
                        }
                    }
                }
                return response;
            }

            @Override
            protected void onPostExecute(JsonDirectionResponse jsonDirectionResponse) {
                super.onPostExecute(jsonDirectionResponse);
                showMapWithPoints(jsonDirectionResponse);
            }
        }.execute();


    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void showMapWithPoints(JsonDirectionResponse jsonDirectionResponse) {


        MapsInitializer.initialize(this);
        List<MarkerOptions> mapMarkerOptionsList = new ArrayList<>();
        List<PolylineOptions> mapPolylineOptionsList = new ArrayList<>();

        final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int y = 0; y < mTravels.size(); y++) {
            Travel currentTravel = mTravels.get(y);
            //We dont add last because last have special marker
            mapMarkerOptionsList.add(new MarkerOptions()
                    .position(currentTravel.toLatLng())
                    .title(currentTravel.name)
                    .icon(BitmapDescriptorFactory.fromBitmap(mMarkerList.get(y))));
        }

        ArrayList<LatLng> commuteLatLngPoints = new ArrayList<LatLng>();
        PolylineOptions polyLineOptions = new PolylineOptions();

        commuteLatLngPoints.add(mLastKnownLatLng);
        boundsBuilder.include(mLastKnownLatLng);

        List<LatLng> latLngs = decodePoly(jsonDirectionResponse.getEncodedPolyline());
        for (LatLng point : latLngs) {
            //add point on cummute line
            commuteLatLngPoints.add(point);
            //add point in bound to perfect zoom map
            boundsBuilder.include(point);
        }
        //FIXME change color
        polyLineOptions.width(getResources().getDimensionPixelSize(R.dimen.map_polyline_walk_width));
        polyLineOptions.color(primaryColor);
        polyLineOptions.addAll(commuteLatLngPoints);
        mapPolylineOptionsList.add(polyLineOptions);


        LatLngBounds mapBounds = boundsBuilder.build();

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);

            for (MarkerOptions marker : mapMarkerOptionsList) {
                map.addMarker(marker);
            }
            for (PolylineOptions line : mapPolylineOptionsList) {
                map.addPolyline(line);
            }

            //init map position to start, so animate will be nice
            ///FIXME use my position a start stop
//            CameraUpdate cameraInit = CameraUpdateFactory.newLatLngZoom(currentTravel.startStation.coords.toLatLng(), getInitZoom());
//            map.moveCamera(cameraInit);

            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBounds, 15);
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //Bounds can only be done after map calculation
                    map.animateCamera(cameraUpdate);
                }
            });
        }
    }

    private void generateMarkers(int count) {
        mMarkerList = new SparseArray<>();
        for (int i = 0; i < count; i++) {
            Bitmap intermediateMarker = createIntermediateMarker(i);
            mMarkerList.put(i, intermediateMarker);
        }

    }

    private Bitmap createIntermediateMarker(int position) {
        int accentColor = mAvailableColors.get(position);
        return createMarker(
                getResources().getDimensionPixelSize(R.dimen.map_marker_radius),
                getResources().getDimensionPixelSize(R.dimen.map_marker_stroke),
                null, Color.WHITE, accentColor);
    }

    private Bitmap createBigMarker(String letter) {
        return createMarker(
                getResources().getDimensionPixelSize(R.dimen.map_big_marker_radius),
                getResources().getDimensionPixelSize(R.dimen.map_big_marker_stroke),
                letter, Color.WHITE, primaryColor);
    }

    private Bitmap createMarker(int radius, int strokeWidth, String letter, int strokeColor, int backgroundColor) {
        int width = (radius * 2) + (strokeWidth * 2);
        Bitmap marker = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(marker);

        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(strokeColor);
        strokePaint.setShadowLayer(strokeWidth, 1.0f, 1.0f, Color.BLACK);
        canvas.drawCircle(width / 2, width / 2, radius, strokePaint);

        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        canvas.drawCircle(width / 2, width / 2, radius - strokeWidth, backgroundPaint);

        if (letter != null) {
            Rect result = new Rect();
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.map_marker_text_size));
            textPaint.setColor(strokeColor);
            textPaint.getTextBounds(letter, 0, letter.length(), result);
            int yOffset = result.height() / 2;

            canvas.drawText(letter, width / 2, (width / 2) + yOffset, textPaint);
        }
        return marker;
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            mLastKnownLatLng = new LatLng(47.400542, 0.685327);
        } else {
            mLastKnownLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        initMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_shake, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        mShareActionProvider.setShareIntent(createShareIntent());
        // Return true to display menu
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<String> names = Lists.newArrayList();
        for (Travel travel : mTravels) {
            names.add(travel.name);
        }

        String textToShare = "This is a great shake (" + Strings.join("->", names) + "), SHAKE it out !";
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TravelTripRecyclerViewAdapter mTravelAdapter = new TravelTripRecyclerViewAdapter();
        mRecyclerView.setAdapter(mTravelAdapter);
        mRecyclerView.setHasFixedSize(true);


    }

    public class TravelTripRecyclerViewAdapter extends RecyclerView.Adapter<TravelTripViewHolder> {

        public TravelTripRecyclerViewAdapter() {
        }

        @Override
        public TravelTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
            return new TravelTripViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final TravelTripViewHolder holder, int position) {

            Travel currentItem = mTravels.get(position);
            holder.setTravel(currentItem);
            holder.mTravelDestinationName.setText(currentItem.name);
            holder.mMarkerImage.setImageBitmap(mMarkerList.get(position));


        }


        @Override
        public int getItemCount() {
            return mTravels.size();
        }
    }


    class TravelTripViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.trip_list_item_name)
        TextView mTravelDestinationName;
        @Bind(R.id.trip_list_item_marker)
        ImageView mMarkerImage;
        @Bind(R.id.trip_list_item_detail_layout)
        LinearLayout mLayoutDetail;
        @Bind(R.id.trip_list_item_detail_image)
        ImageView mImageDetail;
        @Bind(R.id.trip_list_item_cardview)
        CardView card;
        private Travel travel;

        public TravelTripViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;

            mImageDetail.setColorFilter(new PorterDuffColorFilter(primaryColor, PorterDuff.Mode.MULTIPLY));

            View.OnClickListener onClickDetailListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = TravelDetailActivity.newIntent(TripActivity.this, travel);
                    startActivity(startIntent);
                }
            };
            mImageDetail.setOnClickListener(onClickDetailListener);
            mLayoutDetail.setOnClickListener(onClickDetailListener);


            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraUpdate cameraInit = CameraUpdateFactory.newLatLngZoom(travel.toLatLng(), 15);
                    map.animateCamera(cameraInit);
                }
            });
        }


        public void setTravel(Travel travel) {
            this.travel = travel;
        }

        public Travel getTravel() {
            return travel;
        }
    }
}
