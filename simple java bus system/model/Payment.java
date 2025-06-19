package model;

import java.util.Date;

public class Payment {
    private int paymentId;
    private float amount;
    private String method;
    private Date date;
    private String status;

    public Payment(int id, float amt, String meth) {
        this.paymentId = id;
        this.amount = amt;
        this.method = meth;
        this.date = new Date();
        this.status = "Pending";
    }

    public int getPaymentId() {
        return paymentId;
    }

    public float getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void process() {
        this.status = "Completed";
    }
}