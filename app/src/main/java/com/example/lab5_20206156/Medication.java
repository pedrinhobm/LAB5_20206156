package com.example.lab5_20206156;
import java.io.Serializable;

public class Medication implements Serializable {
    private String id;
    private String name;
    private String type;
    private String dosage;
    private int frequencyHours;
    private long startDateMillis;
    public Medication(String id, String name, String type, String dosage, int frequencyHours, long startDateMillis) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dosage = dosage;
        this.frequencyHours = frequencyHours;
        this.startDateMillis = startDateMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getFrequencyHours() {
        return frequencyHours;
    }

    public void setFrequencyHours(int frequencyHours) {
        this.frequencyHours = frequencyHours;
    }

    public long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }
}