package com.codetroopers.shakemytours.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.ui.activity.core.BaseActionBarActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActionBarActivity {


    @Bind(R.id.about_activity_sw)
    ImageView swImage;
    @Bind(R.id.about_activity_frenchtech)
    ImageView frenchTechImage;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);


        setTitle(getResources().getString(R.string.title_about));
        Glide.with(AboutActivity.this).load(R.drawable.sw).fitCenter().into(swImage);
        Glide.with(AboutActivity.this).load(R.drawable.frenchtech).fitCenter().into(frenchTechImage);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
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

}
