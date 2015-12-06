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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TravelDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "cheese_name";
    public static final String PARAM_TRAVEL = "travel";


    @Bind(R.id.backdrop)
    ImageView imageView;
    @Bind(R.id.detail_travel_image_location)
    ImageView locationImage;
    @Bind(R.id.detail_travel_image_phone)
    ImageView phoneImage;
    @Bind(R.id.detail_travel_image_site)
    ImageView siteImage;
    @Bind(R.id.detail_travel_location)
    TextView locationText;
    @Bind(R.id.detail_travel_phone)
    TextView phoneText;
    @Bind(R.id.detail_travel_site)
    TextView siteText;
    @Bind(R.id.detail_travel_layout_location)
    LinearLayout locationLayout;
    @Bind(R.id.detail_travel_layout_phone)
    LinearLayout phoneLayout;
    @Bind(R.id.detail_travel_layout_site)
    LinearLayout siteLayout;


    public static Intent newIntent(Context context, Travel travel) {
        Intent intent = new Intent(context, TravelDetailActivity.class);
        intent.putExtra(PARAM_TRAVEL, travel);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        final String cheeseName = intent.getStringExtra(EXTRA_NAME);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(cheeseName);


        Travel travel = intent.getParcelableExtra(PARAM_TRAVEL);
        getSupportActionBar().setTitle(travel.name);

        loadBackdrop(travel);
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        locationImage.setColorFilter(colorFilter);
        siteImage.setColorFilter(colorFilter);
        phoneImage.setColorFilter(colorFilter);
        locationText.setText(travel.getAddress());


        if (travel.telephone == null || travel.telephone.equals("")) {
            phoneLayout.setVisibility(View.GONE);
        } else {
            phoneText.setText(travel.telephone);
        }
        if (travel.siteWeb == null || travel.siteWeb.equals("")) {
            siteLayout.setVisibility(View.GONE);
        } else {
            siteText.setText(travel.siteWeb);
        }
        if (travel.getAddress() == null || travel.getAddress().equals("")) {
            locationLayout.setVisibility(View.GONE);
        } else {
            locationText.setText(travel.getAddress());
        }
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

    private void loadBackdrop(Travel travel) {
        Glide.with(this).load(travel.getResourceId()).centerCrop().into(imageView);
    }

}
