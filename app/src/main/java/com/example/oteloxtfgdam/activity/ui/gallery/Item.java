package com.example.oteloxtfgdam.activity.ui.gallery;

public class Item {
    private String name;
    private String date;
    private String amount;

    public Item(String name, String date, String amount) {
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }
}

