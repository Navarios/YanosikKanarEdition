package com.example.kamilandrusiewicz.yanosikkanaredition.Models;

public class StopModel {
    private String id;
    private String lat;
    private String lon;
    private String nazwa;
    private String nrzespolu;
    private String nrslupka;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNrzespolu() {
        return nrzespolu;
    }

    public void setNrzespolu(String nrzespolu) {
        this.nrzespolu = nrzespolu;
    }

    public String getNrslupka() {
        return nrslupka;
    }

    public void setNrslupka(String nrslupka) {
        this.nrslupka = nrslupka;
    }
}
