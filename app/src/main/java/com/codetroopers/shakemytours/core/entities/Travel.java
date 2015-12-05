package com.codetroopers.shakemytours.core.entities;

public class Travel {

    public String name;
    public String distance;
    public String tarif;


    public Travel() {
    }

    public Travel setName(String name) {
        this.name = name;
        return this;
    }

    public Travel setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public Travel setTarif(String tarif) {
        this.tarif = tarif;
        return this;
    }
}
