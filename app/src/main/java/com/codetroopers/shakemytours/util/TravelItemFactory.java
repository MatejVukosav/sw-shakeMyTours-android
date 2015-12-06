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


    public static Travel getRandomFoodEvent(Context context, int i) {
        switch (i) {
            case 0:
                return getMorning1(context);
            case 1:
                return getMorning2();
            case 2:
                return getLunch();
            case 3:
                return getAfternoon1();
            default:
            case 4:
                return getAfternoon2();
        }
    }

    public static List<Travel> loadData(Context context,@RawRes int fileResourceId){
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

//    public static Travel getRandomTravel(int position) {
//        switch (position) {
//            case 0:
//                return getMorning1();
//            case 1:
//                return getLunch();
//            case 2:
//                return getAfternoon1();
//            case 3:
//                return getAfternoon2();
//            case 4:
//                return getMorning2();
//        }
//        return null;
//    }

    public static Travel getMorning1(Context context) {
        List<Travel> travels = loadData(context, R.raw.activity);
//        return new Travel()
//                .setName("Morning" + Strings.nextSessionId())
//                .setDistance("10km")
//                .setTelephone("20€")
//                .setBackground(R.drawable.lake)
//                .setCoords("47.421142", "0.700384");
        return travels.get(Strings.randInt(travels.size()));
    }


    public static Travel getMorning2() {
        return new Travel()
                .setName("Afternoon " + Strings.nextSessionId())
                .setDistance("10km")
                .setTelephone("20€")
                .setBackground(R.drawable.noel)
                .setCoords("47.412582", "0.68545");
    }


    public static Travel getLunch() {
        return new Travel()
                .setName("Launch " + Strings.nextSessionId())
                .setDistance("10km")
                .setTelephone("20€")
                .setBackground(R.drawable.vin)
                .setCoords("47.366353", "0.677934");
    }

    public static Travel getAfternoon1() {
        return new Travel()
                .setName("AfterNoon " + Strings.nextSessionId())
                .setDistance("10km")
                .setTelephone("20€")
                .setBackground(R.drawable.musee);
    }

    public static Travel getAfternoon2() {
        return new Travel()
                .setName("Afternoon " + Strings.nextSessionId())
                .setDistance("10km")
                .setTelephone("20€")
                .setBackground(R.drawable.shop);
    }
}
