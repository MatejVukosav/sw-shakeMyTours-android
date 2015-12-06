package com.codetroopers.shakemytours.service;

import com.codetroopers.shakemytours.core.entities.Travel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class JsonDirectionResponse {

    String status;
    List<JsonRoutes> routes;

    private class JsonRoutes {
        List<JsonLeg> legs;
        JsonPolyline overview_polyline;
    }

    private class JsonLeg {
        Travel.Coord end_location;
        Travel.Coord start_location;
        List<JsonStep> steps;
    }

    private class JsonStep {
        JsonPolyline polyline;
        Travel.Coord end_location;
        Travel.Coord start_location;
    }

    public List<LatLng> allCoords() {
        List<LatLng> out = new ArrayList<>();
        for (JsonLeg item : routes.get(0).legs) {
            out.add(item.end_location.toLatLng());
            out.add(item.start_location.toLatLng());
            out.add(item.steps.get(0).end_location.toLatLng());
            out.add(item.steps.get(0).start_location.toLatLng());
        }
        return out;
    }

    public List<String> allPolylines() {
        List<String> out = new ArrayList<>();
        for (JsonLeg item : routes.get(0).legs) {
            for (JsonStep subitem : item.steps) {
                out.add(subitem.polyline.points);
            }
        }
        return out;
    }

    public String getEncodedPolyline(){
        if(!routes.isEmpty()) {
            return routes.get(0).overview_polyline.points;
        }
        return "";
    }

    private class JsonPolyline {
        String points;
    }
}
