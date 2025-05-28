package com.example.lab5_20206156;

import java.io.Serializable;
import java.util.Calendar;

public class Medication implements Serializable {
    private String id; // ID único para cada medicamento
    private String name;
    private String type; // Pastilla, Jarabe, Ampolla, Cápsula
    private String dosage;
    private int frequencyHours; // Cada X horas
    private long startDateMillis; // Fecha y Hora de inicio en milisegundos

    public Medication(String id, String name, String type, String dosage, int frequencyHours, long startDateMillis) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dosage = dosage;
        this.frequencyHours = frequencyHours;
        this.startDateMillis = startDateMillis;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDosage() {
        return dosage;
    }

    public int getFrequencyHours() {
        return frequencyHours;
    }

    public long getStartDateMillis() {
        return startDateMillis;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setFrequencyHours(int frequencyHours) {
        this.frequencyHours = frequencyHours;
    }

    public void setStartDateMillis(long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }
}