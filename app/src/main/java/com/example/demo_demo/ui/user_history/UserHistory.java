package com.example.demo_demo.ui.user_history;

public class UserHistory {
    private String orderID, status;

    public UserHistory(){

    }

    public UserHistory(String orderID, String status) {
        this.orderID = orderID;
        this.status = status;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
