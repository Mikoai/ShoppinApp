package com.example.shoppinapp.entity;

public class Item {
    String name;
    String quantity;
    boolean isDone;

    public Item(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
        this.isDone = false;
    }

    public Item(String name) {
        this.name = name;
        this.quantity = "1 szt";
        this.isDone = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
