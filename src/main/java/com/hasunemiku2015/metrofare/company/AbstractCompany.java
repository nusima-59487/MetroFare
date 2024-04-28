package com.hasunemiku2015.metrofare.company;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


public abstract class AbstractCompany implements Serializable {
    //Serialization ID
    private static final long serialVersionUID = 314159265L;

    //Vars
    private final String name;
    private final CompanyType type;
    private List<String> owners;
    private int revenue;

    AbstractCompany(HashMap<String, Object> input) {
        name = (String) input.get("name");
        type = (CompanyType) input.get("type");
        owners = (List<String>) input.get("owners");
        revenue = 0;
    }

    public String getName() {
        return name;
    }

    public CompanyType getType() {
        return type;
    }

    public void addOwner(String Owner) {
        owners.add(Owner);
    }

    public void addOwner(List<String> Owner) {
        owners.addAll(Owner);
    }

    public synchronized boolean removeOwner(String Owner, String UID) {
        if (owners.contains(UID)) {
            if (owners.size() > 1) {
                owners.remove(Owner);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeOwner(List<String> owner, String UID) {
        List<String> localList = owners;
        if (!owners.contains(UID)) {
            return false;
        }

        localList.removeAll(owners);
        if (!localList.isEmpty()) {
            owners = localList;
            return true;
        }
        return false;
    }

    public boolean hasOwner(String owner) {
        if (owner == null) {
            return false;
        }
        return owners.contains(owner);
    }

    List<String> getOwners() {
        return owners;
    }

    public void addRevenue(double amount) {
        revenue = revenue + (int) (amount * 1000);
    }

    public boolean deductRevenue(double amount) {
        if (revenue - (int) amount * 1000 >= 0) {
            revenue = revenue - (int) (amount * 1000);
            return true;
        }
        return false;
    }

    double getRevenue() {
        return revenue / 1000.0;
    }

    public abstract int computeFare(String from, String to);

    public void onLoad() {}
}
