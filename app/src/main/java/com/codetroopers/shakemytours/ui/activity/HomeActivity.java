package com.codetroopers.shakemytours.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;
import com.codetroopers.shakemytours.ui.activity.core.BaseActionBarActivity;
import com.codetroopers.shakemytours.util.ItemTouchHelperAdapter;
import com.codetroopers.shakemytours.util.ItemTouchHelperViewHolder;
import com.codetroopers.shakemytours.util.OnStartDragListener;
import com.codetroopers.shakemytours.util.ShakeDetector;
import com.codetroopers.shakemytours.util.SimpleItemTouchHelperCallback;
import com.codetroopers.shakemytours.util.TravelItemProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

@DebugLog
public class HomeActivity extends BaseActionBarActivity implements DrawerAdapter.OnItemClickListener, OnStartDragListener {

    @Bind(R.id.drawer)
    DrawerLayout mDrawer;
    @Bind(R.id.left_drawer)
    RecyclerView mDrawerList;

    @Bind(R.id.home_activity_textview)
    TextView mHomeTextview;
    @Bind(R.id.home_activity_recyclerview)
    RecyclerView mRecyclerView;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mDrawerAdapter;

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<Travel> mTravelsDatas;
    private ItemTouchHelper mItemTouchHelper;
    private TravelRecyclerViewAdapter mTravelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                HomeActivity.this.onShake();
            }
        });
        setupRecyclerView();

//        FIXME remove for real run
//        onShake();
    }

    private void onShake() {
        Toast.makeText(HomeActivity.this, "Votre journée est prête", Toast.LENGTH_SHORT).show();
        mHomeTextview.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        if (mTravelsDatas.isEmpty()) {
            mTravelsDatas.add(0, TravelItemProvider.getMorning1());
            mTravelsDatas.add(1, TravelItemProvider.getLaunch());
            mTravelsDatas.add(2, TravelItemProvider.getAfternoon1());
            mTravelsDatas.add(3, TravelItemProvider.getAfternoon2());
        } else {
            for (int i = 0; i < mTravelsDatas.size(); i++) {
                Travel travel = mTravelsDatas.get(i);
                if (!travel.selected) {
                    //Upadte my travel item
                    mTravelsDatas.set(i, TravelItemProvider.getRandomTravel(i));
                }
            }
        }
        mTravelAdapter.notifyDataSetChanged();

    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelsDatas = new ArrayList<>();
        mTravelAdapter = new TravelRecyclerViewAdapter(mTravelsDatas);
        mRecyclerView.setAdapter(mTravelAdapter);
        mRecyclerView.setHasFixedSize(true);

        //setup swipe to delete and drag'n'drop
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mTravelAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private void setupDrawer() {
        mDrawerAdapter = new DrawerAdapter(this);

        mDrawerList.setAdapter(mDrawerAdapter);
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

    private void selectItem(int position) {
        mDrawer.closeDrawer(mDrawerList);
        mDrawerAdapter.setActive(position);
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
            holder.setTravel(currentItem);
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


    class TravelViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnClickListener {
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
        @Bind(R.id.real_time_fragement_list_item_menu_button)
        ImageButton mMenuButton;
        private Travel travel;

        public TravelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;

            mCardView.setOnClickListener(this);

            mMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Travel currentItem = mTravelsDatas.get(getAdapterPosition());
                    if (currentItem.selected) {
                        currentItem.selected = false;
                        mMenuButton.setColorFilter(null);
                    } else {
                        currentItem.selected = true;
                        int color = getResources().getColor(R.color.colorPrimary);
                        mMenuButton.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                }
            });
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

        @Override
        public void onClick(View v) {
            startActivity(TravelDetailActivity.newIntent(HomeActivity.this, travel));
        }


        public void setTravel(Travel travel) {
            this.travel = travel;
        }

        public Travel getTravel() {
            return travel;
        }
    }
}
