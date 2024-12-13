package com.hasunemiku2015.metrofare.company;

import com.hasunemiku2015.metrofare.lookuptables.faretables.FareTable;
import com.hasunemiku2015.metrofare.lookuptables.faretables.FareTableStore;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class FareTableCompany extends AbstractCompany implements Serializable {
    @Serial
    private static final long serialVersionUID = 314159265L;

    private transient FareTable FareTableObject;
    private final String FareTableName;

    FareTableCompany(HashMap<String, Object> input) {
        super(input);

        FareTableName = (String) input.get("faretable");
        FareTableObject = FareTableStore.FareTables.get(FareTableName);
    }

    //Getter & Setter
    public FareTable getFareTable() {
        return FareTableObject;
    }

    String getFareTableName() {
        return FareTableName;
    }

    @Override
    public int computeFare(String from, String to) {
        if(FareTableObject.getFare1000(from,to) >= 0){
            return FareTableObject.getFare1000(from,to);
        }
        return -1;
    }

    @Override
    public void onLoad() {
        FareTableObject = FareTableStore.FareTables.get(FareTableName);
    }
}
