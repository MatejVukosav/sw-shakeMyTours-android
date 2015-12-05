package com.codetroopers.shakemytours.util;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.core.entities.Travel;

public class TravelItemFactory {


    public static Travel getRandomFoodEvent(int i) {
        switch (i) {
            case 0:
                return getMorning1();
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

    public static Travel getRandomTravel(int position) {
        switch (position) {
            case 0:
                return getMorning1();
            case 1:
                return getLunch();
            case 2:
                return getAfternoon1();
            case 3:
                return getAfternoon2();
            case 4:
                return getMorning2();
        }
        return null;
    }

    public static Travel getMorning1() {
        return new Travel()
                .setName("Morning" + Strings.nextSessionId())
                .setDistance("10km")
                .setTelephone("20€")
                .setBackground(R.drawable.lake)
                .setCoords("47.421142", "0.700384");
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
