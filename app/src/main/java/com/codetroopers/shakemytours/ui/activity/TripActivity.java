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
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static final String PARAM_TRAVELS = "PARAM_TRAVELS";

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
    int accentColor;
    @Nullable
    private GoogleMap map;

    @Nullable
    private LatLng mLastLatLng;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Travel> mTravels;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        primaryColor = getResources().getColor(R.color.colorPrimary);
        accentColor = getResources().getColor(R.color.colorAccent);
        mapView.onCreate(savedInstanceState);

        mTravels = getIntent().getParcelableArrayListExtra(PARAM_TRAVELS);
        setupRecyclerView();
        buildGoogleApiClient();
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

        MapsInitializer.initialize(this);
        List<MarkerOptions> mapMarkerOptionsList = new ArrayList<>();
        List<PolylineOptions> mapPolylineOptionsList = new ArrayList<>();

        Bitmap intermediateMarker = createIntermediateMarker();
        final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int y = 0; y < mTravels.size(); y++) {
            Travel currentTravel = mTravels.get(y);
            //We dont add last because last have special marker
            mapMarkerOptionsList.add(new MarkerOptions()
                    .position(currentTravel.toLatLng())
                    .title(currentTravel.name)
                    .icon(BitmapDescriptorFactory.fromBitmap(intermediateMarker)));
        }

        ArrayList<LatLng> commuteLatLngPoints = new ArrayList<LatLng>();
        PolylineOptions polyLineOptions = new PolylineOptions();

        commuteLatLngPoints.add(mLastLatLng);
        boundsBuilder.include(mLastLatLng);
        for (int i = 0; i < mTravels.size(); i++) {
            Travel currentPoint = mTravels.get(i);
            //add point on cummute line
            commuteLatLngPoints.add(currentPoint.toLatLng());
            //add point in bound to perfect zoom map
            boundsBuilder.include(currentPoint.toLatLng());
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

            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBounds, 150);
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //Bounds can only be done after map calculation
                    map.animateCamera(cameraUpdate);
                }
            });
        }

    }

    private Bitmap createIntermediateMarker() {
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
            mLastLatLng = new LatLng(47.400542, 0.685327);
        } else {
            mLastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        initMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

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
            if (currentItem.loading) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.mBackgroundImageView.setVisibility(View.GONE);
                holder.mContent.setVisibility(View.GONE);
            } else {
                holder.mProgressBar.setVisibility(View.GONE);
                holder.mBackgroundImageView.setVisibility(View.VISIBLE);
                holder.mContent.setVisibility(View.VISIBLE);
                holder.mTravelDestinationName.setText(currentItem.name);
                Glide.with(TripActivity.this).load(currentItem.getResourceId()).centerCrop().into(holder.mBackgroundImageView);
                if (!currentItem.selected) {
                    holder.mMenuButton.setImageResource(android.R.color.transparent);
                }
            }
        }


        @Override
        public int getItemCount() {
            return mTravels.size();
        }


    }


    class TravelTripViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.trip_list_item_cardview)
        CardView mCardView;
        @Bind(R.id.trip_list_item_background)
        ImageView mBackgroundImageView;
        @Bind(R.id.trip_list_item_name)
        TextView mTravelDestinationName;
        @Bind(R.id.trip_list_item_menu_button_holder)
        LinearLayout mButtonHolder;
        @Bind(R.id.trip_list_item_menu_button)
        ImageButton mMenuButton;
        @Bind(R.id.trip_list_item_progressbar)
        ProgressBar mProgressBar;
        @Bind(R.id.trip_list_item_content)
        LinearLayout mContent;
        @Bind(R.id.trip_list_item_info)
        ImageView infoImageView;
        private Travel travel;

        public TravelTripViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }


        public void setTravel(Travel travel) {
            this.travel = travel;
        }

        public Travel getTravel() {
            return travel;
        }
    }
}
