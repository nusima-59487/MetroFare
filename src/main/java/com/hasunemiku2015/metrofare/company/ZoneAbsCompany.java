package com.hasunemiku2015.metrofare.company;

import java.io.Serializable;
import java.util.HashMap;

public class ZoneAbsCompany extends AbstractCompany implements Serializable {
    //Serialization ID
    private static final long serialVersionUID = 314159265L;

    //Vars
    private double Multiplier;
    private double Constant;

    //Constructors
    ZoneAbsCompany(HashMap<String, Object> input) {
        super(input);
        Multiplier = (double) input.get("multiplier");
        Constant = (double) input.get("constant");
    }

    //Getters
    double getMultiplier() {
        return Multiplier;
    }
    void setMultiplier(double multiplier) {
        Multiplier = multiplier;
    }
    double getConstant() {
        return Constant;
    }
    void setConstant(double constant) {
        Constant = constant;
    }

    //Inherit from Company
    @Override
    public int computeFare(String from, String to) {
        if(from.contains(" ")){
            //Abs Company
            try {
                double fromX = Double.parseDouble(from.split(" ")[0]);
                double fromZ = Double.parseDouble(from.split(" ")[1]);

                double toX = Double.parseDouble(to.split(" ")[0]);
                double toZ = Double.parseDouble(to.split(" ")[1]);

                double diff = Math.sqrt((fromX-toX)*(fromX-toX) + (fromZ-toZ)*(fromZ-toZ));
                return (int) ((Multiplier * diff + Constant)*1000);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return -1;
            }
        } else {
            //Zone Company
            try {
                int diff = Math.abs(Integer.parseInt(to) - Integer.parseInt(from));

                return (int) ((Multiplier * diff + Constant)*1000);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }
}
