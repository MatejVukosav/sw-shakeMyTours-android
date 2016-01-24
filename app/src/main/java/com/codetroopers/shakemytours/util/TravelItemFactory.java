package com.codetroopers.shakemytours.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.RawRes;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;
import com.codetroopers.shakemytours.ui.activity.HomeActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

public class TravelItemFactory {


    private static List<Travel> travelActivities;
    private static List<Travel> travelLunch;
    private static List<Travel> travelNight;

    public static Travel getRandomFoodEvent(Context context, int moment) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int cityId = prefs.getInt(HomeActivity.APP_PREF_CITY_TO_USE, HomeActivity.APP_PREF_CITY_ID_TOURS);

        switch (moment) {
            case 0:
                return getMorning1(context, cityId);
            case 1:
                return getMorning2(context, cityId);
            case 2:
                return getLunch(context, cityId);
            case 3:
                return getAfternoon1(context, cityId);
            default:
            case 4:
                return getAfternoon2(context, cityId);
        }
    }


    public static Travel getMorning1(Context context, int cityId) {
        if (travelActivities == null) {
            travelActivities = loadDataForCity(context, cityId, R.raw.orleans_activity, R.raw.tours_activity);
        }
        return getRandomItemFrom(travelActivities);
    }


    public static Travel getMorning2(Context context, int cityId) {
        if (travelActivities == null) {
            travelActivities = loadDataForCity(context, cityId, R.raw.orleans_activity, R.raw.tours_activity);
        }
        return getRandomItemFrom(travelActivities);
    }


    public static Travel getLunch(Context context, int cityId) {
        if (travelLunch == null) {
            travelLunch = loadDataForCity(context, cityId, R.raw.orleans_restautant, R.raw.tours_restaurant);
        }
        return getRandomItemFrom(travelLunch);
    }

    public static Travel getAfternoon1(Context context, int cityId) {
        if (travelActivities == null) {
            travelActivities = loadDataForCity(context, cityId, R.raw.orleans_activity, R.raw.tours_activity);
        }
        return getRandomItemFrom(travelActivities);
    }

    public static Travel getAfternoon2(Context context, int cityId) {
        if (travelNight == null) {
            travelNight = loadDataForCity(context, cityId, R.raw.orleans_night, R.raw.tours_night);
        }
        return getRandomItemFrom(travelNight);
    }


    private static List<Travel> loadDataForCity(Context context, int cityId, @RawRes int orleans_activity, @RawRes int tours_activity) {
        List<Travel> activities;
        if (cityId == HomeActivity.APP_PREF_CITY_ID_ORELANS) {
            activities = loadData(context, orleans_activity);
        } else {
            activities = loadData(context, tours_activity);
        }
        return activities;
    }

    private static Travel getRandomItemFrom(List<Travel> travels) {
        return travels.get(Strings.randInt(travels.size()));
    }

    public static List<Travel> loadData(Context context, @RawRes int fileResourceId) {
        List<Travel> travels = null;
        InputStream inputStream;
        BufferedReader reader = null;
        Gson gson = new GsonBuilder().create();
        try {
            inputStream = context.getResources().openRawResource(fileResourceId);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            final Type collectionType = new TypeToken<Collection<Travel>>() {
            }.getType();
            travels = gson.fromJson(reader, collectionType);
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
        return travels;
    }
    /**
     * Called on city change
     */
    public static void resetLists() {
        travelActivities = null;
        travelLunch = null;
        travelNight = null;
    }
}
