package com.codetroopers.shakemytours.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;
import com.codetroopers.shakemytours.ui.activity.core.BaseActionBarActivity;
import com.codetroopers.shakemytours.util.ItemTouchHelperAdapter;
import com.codetroopers.shakemytours.util.ItemTouchHelperViewHolder;
import com.codetroopers.shakemytours.util.OnStartDragListener;
import com.codetroopers.shakemytours.util.ShakeDetector;
import com.codetroopers.shakemytours.util.SimpleItemTouchHelperCallback;
import com.codetroopers.shakemytours.util.TravelItemFactory;

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

    @Bind(R.id.home_activity_tel)
    ImageView mTelImg;
    @Bind(R.id.home_activity_tel2)
    ImageView mTelImgAnim;
    @Bind(R.id.home_activity_title)
    TextView mShakeItTitle;
    @Bind(R.id.home_activity_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.real_time_fragment_fab)
    FloatingActionButton mFloatingActionButton;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mDrawerAdapter;

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<Travel> mTravelsDatas;
    private ItemTouchHelper mItemTouchHelper;
    private TravelRecyclerViewAdapter mTravelAdapter;

    private Boolean mFabVisible;
    private boolean mIsShaking;
    private android.os.Handler mHandler = new android.os.Handler();
    private int mSelectedEvent = 0;
    private MediaPlayer mp;
    private Boolean soundEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if (!mIsShaking) {
                    mIsShaking = true;
                    HomeActivity.this.onShake();
                }
            }
        });
        setupRecyclerView();
        setFabVisible(false);

//        FIXME remove for real run
//        onShake();
        soundEnable = false;

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pik only selected
                ArrayList<Travel> selectedTravels = new ArrayList<Travel>();
                for (int i = 0; i < mTravelsDatas.size(); i++) {
                    Travel travel = mTravelsDatas.get(i);
                    if (travel.selected) {
                        selectedTravels.add(travel);
                    }
                }

                startActivity(TripActivity.newIntent(HomeActivity.this, selectedTravels));
            }
        });

//        mFoodData = null;
//        InputStream inputStream;
//        BufferedReader reader = null;
//        Gson gson = new GsonBuilder().create();
//        try {
//            inputStream = HomeActivity.this.getResources().openRawResource(R.raw.restaurants);
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            final Type collectionType = new TypeToken<Collection<Travel>>() {
//            }.getType();
//            mFoodData = gson.fromJson(reader, collectionType);
//        } catch (Exception e) {
//            Timber.e(e, "");
//        } finally {
//            if (null != reader) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    Timber.e(e, "");
//                }
//            }
//        }

    }

    private void onShake() {
        if (mTravelsDatas.isEmpty()) {
            mTelImg.setVisibility(View.GONE);
            mShakeItTitle.setText(R.string.shaking);
//            mShakeItTitle.setVisibility(View.INVISIBLE);
//            ViewCompat.animate(mShakeItTitle).alpha(0).setDuration(700).start();

            mTelImgAnim.setVisibility(View.VISIBLE);
            mTelImgAnim.setAnimation(AnimationUtils.loadAnimation(this, R.anim.pendulum));
            mTelImgAnim.animate();

            playMaracas();
            mRecyclerView.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < mTravelsDatas.size(); i++) {
                Travel travel = mTravelsDatas.get(i);
                if (!travel.selected) {
                    //Upadte my travel item
                    travel.loading = true;
                }
            }
        }
        mTravelAdapter.notifyDataSetChanged();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                stopMaracas();
                mDrawer.setBackgroundColor(getResources().getColor(R.color.white));
                mTelImgAnim.clearAnimation();
                mTelImgAnim.setVisibility(View.GONE);
                mShakeItTitle.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                if (mTravelsDatas.isEmpty()) {
                    mTravelsDatas.add(0, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, 0));
                    mTravelsDatas.add(1, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, 1));
                    mTravelsDatas.add(2, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, 2));
                    mTravelsDatas.add(3, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, 3));
                    mTravelsDatas.add(4, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, 4));
                } else {
                    for (int i = 0; i < mTravelsDatas.size(); i++) {
                        Travel travel = mTravelsDatas.get(i);
                        travel.loading = false;
                        if (!travel.selected) {
                            //Upadte my travel item
                            mTravelsDatas.set(i, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, i));
                        }
                    }
                }
                mTravelAdapter.notifyDataSetChanged();

//                Toast.makeText(HomeActivity.this, "Votre journée est prête", Toast.LENGTH_SHORT).show();
                setTitle("Programme de la journée");

                mIsShaking = false;
            }
        }, 2000);
    }

    private void stopMaracas() {
        if (soundEnable) {
            mp.stop();
        }
    }

    private void playMaracas() {
        if (soundEnable) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.maracas);
            mp.setLooping(true);
            mp.start();
        }
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelsDatas = new ArrayList<>();
        mTravelAdapter = new TravelRecyclerViewAdapter(mTravelsDatas);
        mRecyclerView.setAdapter(mTravelAdapter);
        mRecyclerView.setHasFixedSize(true);


        mRecyclerView.setItemAnimator(new SimpleItemAnimator() {
            @Override
            public boolean animateRemove(RecyclerView.ViewHolder holder) {
                return false;
            }

            @Override
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                holder.itemView.setAlpha(0);
                ViewCompat.animate(holder.itemView).alpha(1).setDuration(500).start();
                return true;
            }

            @Override
            public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
                return false;
            }

            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
                return false;
            }

            @Override
            public void runPendingAnimations() {

            }

            @Override
            public void endAnimation(RecyclerView.ViewHolder item) {

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        });
        //setup swipe to delete and drag'n'drop
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mTravelAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        selectItem(0);
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
        menu.findItem(R.id.action_reset).setVisible(!drawerOpen);
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
            case R.id.action_reset:
                mSelectedEvent = 0;
                mTravelsDatas.clear();
                mDrawerAdapter.notifyItemRangeRemoved(0, 5);
                mRecyclerView.setVisibility(View.GONE);
                mTelImg.setVisibility(View.VISIBLE);
                mShakeItTitle.setVisibility(View.VISIBLE);
                mShakeItTitle.setAlpha(1);
                mShakeItTitle.setText(R.string.shakeIt);
                setFabVisible(false);
                mDrawer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
        if (position == 1) {
            startActivity(AboutActivity.newIntent(this));
        }
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public class TravelRecyclerViewAdapter extends RecyclerView.Adapter<TravelViewHolder> implements ItemTouchHelperAdapter {

        public TravelRecyclerViewAdapter(List<Travel> allRealTime) {
            mTravelsDatas = allRealTime;
        }

        @Override
        public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_list_item, parent, false);
            return new TravelViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final TravelViewHolder holder, int position) {

            Travel currentItem = mTravelsDatas.get(position);
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
                Glide.with(HomeActivity.this).load(currentItem.getResourceId()).centerCrop().into(holder.mBackgroundImageView);
                if (!currentItem.selected) {
                    holder.mMenuButton.setImageResource(android.R.color.transparent);
                }
            }
        }


        @Override
        public int getItemCount() {
            return mTravelsDatas.size();
        }

        @Override
        public boolean canSwipe(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return !((TravelViewHolder) viewHolder).getTravel().selected;
        }

        @Override
        public void onItemDismiss(final int position) {
            mTravelsDatas.get(position).selected = false;
            final Travel remove = mTravelsDatas.remove(position);
            notifyItemRemoved(position);
            mTravelsDatas.add(position, TravelItemFactory.getRandomFoodEvent(HomeActivity.this, position));
            notifyItemInserted(position);
        }

        @Override
        public boolean onItemMove(int fromPosition, RecyclerView.ViewHolder from, int toPosition, RecyclerView.ViewHolder to) {
            return false;
        }
    }


    class TravelViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnLongClickListener {
        public final View mView;
        @Bind(R.id.travel_list_item_cardview)
        CardView mCardView;
        @Bind(R.id.travel_list_item_background)
        ImageView mBackgroundImageView;
        @Bind(R.id.travel_list_item_name)
        TextView mTravelDestinationName;
        @Bind(R.id.travel_list_item_menu_button_holder)
        LinearLayout mButtonHolder;
        @Bind(R.id.travel_list_item_menu_button)
        ImageButton mMenuButton;
        @Bind(R.id.travel_list_item_progressbar)
        ProgressBar mProgressBar;
        @Bind(R.id.travel_list_item_content)
        LinearLayout mContent;
        @Bind(R.id.travel_list_item_info)
        ImageView infoImageView;
        private Travel travel;

        public TravelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;


            View.OnClickListener checkItemListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Travel currentItem = mTravelsDatas.get(getAdapterPosition());

                    if (currentItem.selected) {
                        mSelectedEvent--;
                        currentItem.selected = false;
                        mMenuButton.setImageResource(android.R.color.transparent);
                    } else {
                        mSelectedEvent++;
                        currentItem.selected = true;
                        int color = getResources().getColor(R.color.colorPrimary);
                        mMenuButton.setImageResource(R.drawable.ic_check_circle_white_big);
                        mMenuButton.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                    setFabVisible(mSelectedEvent > 0);
                }
            };
            mMenuButton.setOnClickListener(checkItemListener);
            mButtonHolder.setOnClickListener(checkItemListener);
            mCardView.setOnClickListener(checkItemListener);

            View.OnClickListener openDetailActivityListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetailActivity();
                }
            };
            infoImageView.setOnClickListener(openDetailActivityListener);
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
        public boolean onLongClick(View v) {
            openDetailActivity();
            return true;
        }

        private void openDetailActivity() {
            Intent startIntent = TravelDetailActivity.newIntent(HomeActivity.this, travel);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, mBackgroundImageView, "openDetailImage");
                startActivity(startIntent, options.toBundle());
            } else {
                startActivity(startIntent);
            }
        }


        public void setTravel(Travel travel) {
            this.travel = travel;
        }

        public Travel getTravel() {
            return travel;
        }
    }

    public void setFabVisible(boolean visible) {
        if (mFabVisible == null || mFabVisible != visible) {
            if (visible) {
                mFloatingActionButton.setVisibility(View.VISIBLE);
                mFloatingActionButton.animate().setDuration(200).translationY(0).alpha(1f);
            } else {
                final int height = mFloatingActionButton.getHeight();
                mFloatingActionButton.animate().setDuration(200).translationY(height > 0 ? height : 128).alpha(0f);
            }
        }
        mFabVisible = visible;
    }
}
