package com.brtvsk.noter;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

public class Note {

    private String note;
    private String description;
    private final UUID id;
    private Date date;
    private Markers marker;
    private int position;

    public Note(int position) {
        this(UUID.randomUUID(), position);
    }

    public Note(UUID id, int position) {
        this.id = id;
        marker = Markers.DEFAULT;
        date = new Date();
        note = "Note was created " + date.toString();
        description = "New Note";
        this.position = position;
    }

    public String getNote() {
        return note;
    }

    public UUID getId() {
        return id;
    }

    public Markers getMarker() {
        return marker;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getPosition() {
        return position;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void mark(Markers mark) {
        this.marker = mark;
    }

    @NonNull
    @Override
    public String toString() {
        return id.toString();
    }
}
