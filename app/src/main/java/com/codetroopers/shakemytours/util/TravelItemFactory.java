package com.codetroopers.shakemytours.util;

import android.content.Context;
import android.support.annotation.RawRes;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;
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
    private static List<Travel> travelBonus;

    public static Travel getRandomFoodEvent(Context context, int i) {
        switch (i) {
            case 0:
                return getMorning1(context);
            case 1:
                return getMorning2(context);
            case 2:
                return getLunch(context);
            case 3:
                return getAfternoon1(context);
            default:
            case 4:
                return getAfternoon2(context);
        }
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

    public static Travel getMorning1(Context context) {
        if (travelBonus == null) {
            travelBonus = loadData(context, R.raw.bonus);
        }
        return getRandomItemFrom(travelBonus);
    }

    private static Travel getRandomItemFrom(List<Travel> travels) {
        return travels.get(Strings.randInt(travels.size()));
    }

    public static Travel getMorning2(Context context) {
        if (travelActivities == null) {
            travelActivities = loadData(context, R.raw.activity);
        }
        return getRandomItemFrom(travelActivities);
    }


    public static Travel getLunch(Context context) {
        if (travelLunch == null) {
            travelLunch = loadData(context, R.raw.restaurant);
        }
        return getRandomItemFrom(travelLunch);
    }

    public static Travel getAfternoon1(Context context) {
        if (travelActivities == null) {
            travelActivities = loadData(context, R.raw.activity);
        }
        return getRandomItemFrom(travelActivities);
    }

    public static Travel getAfternoon2(Context context) {
        if (travelNight == null) {
            travelNight = loadData(context, R.raw.night);
        }
        return getRandomItemFrom(travelNight);
    }
}
