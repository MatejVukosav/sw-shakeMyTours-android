package com.codetroopers.shakemytours.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.HasComponent;
import com.codetroopers.shakemytours.core.components.HomeActivityComponent;
import com.codetroopers.shakemytours.core.entities.Travel;
import com.codetroopers.shakemytours.ui.activity.core.BaseActionBarActivity;
import com.codetroopers.shakemytours.util.ItemTouchHelperAdapter;
import com.codetroopers.shakemytours.util.ItemTouchHelperViewHolder;
import com.codetroopers.shakemytours.util.OnStartDragListener;
import com.codetroopers.shakemytours.util.ShakeDetector;
import com.codetroopers.shakemytours.util.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

import static java.lang.String.format;

@DebugLog
public class HomeActivity extends BaseActionBarActivity implements DrawerAdapter.OnItemClickListener, HasComponent<HomeActivityComponent>,
        OnStartDragListener {

    private HomeActivityComponent component;

    @Bind(R.id.drawer)
    DrawerLayout mDrawer;
    @Bind(R.id.left_drawer)
    RecyclerView mDrawerList;

    @Bind(R.id.home_activity_textview)
    TextView mHomeTextview;
    @Bind(R.id.home_activity_recyclerview)
    RecyclerView mRecyclerView;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mAdapter;

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<Travel> mTravelsDatas;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(HomeActivity.this, "Aaaaaaaaaaaaaaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                mHomeTextview.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                setupRecyclerView(mRecyclerView);

            }
        });


    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelsDatas = new ArrayList<>();
        TravelRecyclerViewAdapter adapter = new TravelRecyclerViewAdapter(mTravelsDatas);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        mTravelsDatas.add(new Travel().setName("Name1").setDistance("10km").setTarif("20€"));
        mTravelsDatas.add(new Travel().setName("Name2").setDistance("50km").setTarif("10€"));
        mTravelsDatas.add(new Travel().setName("Name3").setDistance("10km").setTarif("1000€"));
        mTravelsDatas.add(new Travel().setName("Name4").setDistance("20km").setTarif("2€"));


        //setup swipe to delete and drag'n'drop
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
// Registers (mShakeDetector which implements) SensorEventListener
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private void setupDrawer() {
        mAdapter = new DrawerAdapter(this);

        mDrawerList.setAdapter(mAdapter);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawer.setStatusBarBackground(R.color.statusBarTransparentColor);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, getToolbar(), R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawer.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    @Override
    public HomeActivityComponent getComponent() {
        return component;
    }

    private void selectItem(int position) {
        mDrawer.closeDrawer(mDrawerList);
        mAdapter.setActive(position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public class TravelRecyclerViewAdapter extends RecyclerView.Adapter<TravelViewHolder> implements ItemTouchHelperAdapter {
        List<Travel> mValues;

        public TravelRecyclerViewAdapter(List<Travel> allRealTime) {
            mValues = allRealTime;
        }

        @Override
        public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_list_item, parent, false);
            return new TravelViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final TravelViewHolder holder, int position) {

            Travel currentItem = mValues.get(position);
            holder.mTravelDestinationName.setText(currentItem.name);
            holder.mTravelTarif.setText(currentItem.tarif);
            holder.mTravelDistance.setText(currentItem.distance);
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }


        @Override
        public void onItemDismiss(final int position) {

            final Travel remove = mValues.remove(position);

            mTravelsDatas.add(position, new Travel().setName("Name New").setDistance("20km").setTarif("2€"));
            notifyItemChanged(position);

        }

        @Override
        public boolean onItemMove(int fromPosition, RecyclerView.ViewHolder from, int toPosition, RecyclerView.ViewHolder to) {
            return false;
        }
    }


    class TravelViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public final View mView;
        @Bind(R.id.travel_list_item_cardview)
        CardView mCardView;
        @Bind(R.id.travel_list_item_name)
        TextView mTravelDestinationName;
        @Bind(R.id.travel_list_item_details)
        TextView mTravelDetails;
        @Bind(R.id.travel_list_item_distance)
        TextView mTravelDistance;
        @Bind(R.id.travel_list_item_tarif)
        TextView mTravelTarif;


        public String routeId;
        public String stopId;
        public String realTimeStopId;
        public String index;

        public TravelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }

        private void delete() {
            int position = getAdapterPosition();

            mTravelsDatas.remove(position);
//            mRecyclerView.getAdapter().notifyItemChanged(position);

        }

        @Override
        public void onItemSelected(int actionState) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mCardView.setBackgroundColor(Color.LTGRAY);
                    mCardView.setCardElevation(10);
                }
            }
        }

        @Override
        public void onItemClear() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCardView.setCardElevation(6);
                mCardView.setBackgroundColor(0xFFFAFAFA);
            }
        }
    }
}
