package com.hasunemiku2015.metrofare.Company;

import java.io.Serializable;
import java.util.HashMap;

public class UniformCompany extends AbstractCompany implements Serializable {
    //Serialization ID
    private static final long serialVersionUID = 314159265L;

    //Vars
    private int Fare;

    //Constructor
    UniformCompany(HashMap<String, Object> input) {
        super(input);
        double fare = (double)input.get("fare");
        Fare = (int) (fare *1000);
    }

    //Inherit from Company
    @Override
    public int computeFare(String from, String to) {
        return Fare;
    }

    //Setter/Getter
    double getFare(){
        return Fare/1000.0;
    }
    void setFare(double fare) {
        Fare = (int) (fare *1000);
    }
}
