package com.cyph.somanlpannotator.Models;

public class Annotation {
    String intent, email, query, date, sortableDate;

    public Annotation() {
        this.intent = "";
        this.email = "";
        this.query = "";
        this.date = "";
        this.sortableDate = "";
    }

    public Annotation(String intent, String email, String query, String date, String sortableDate) {
        this.intent = intent;
        this.email = email;
        this.query = query;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }
}
