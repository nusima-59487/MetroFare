package com.hasunemiku2015.metrofare.company;

import com.hasunemiku2015.metrofare.lookuptables.datatables.DataTable;
import com.hasunemiku2015.metrofare.lookuptables.datatables.DataTableStore;
import java.io.Serializable;
import java.util.HashMap;

public class DijkstraCompany extends AbstractCompany implements Serializable {
    //Serialization ID
    private static final long serialVersionUID = 314159265L;

    private transient DataTable DataTableObject;
    private final String DataTableName;

    DijkstraCompany(HashMap<String, Object> input) {
        super(input);
        DataTableName = (String) input.get("datatable");
        DataTableObject = DataTableStore.DataTables.get(DataTableName);
    }

    public DataTable getDataTable() {
        return DataTableObject;
    }

    @Override
    public int computeFare(String from, String to) {
        return (int)(DataTableObject.ComputeFare(from,to) * 1000);
    }

    @Override
    public void onLoad() {
        DataTableObject = DataTableStore.DataTables.get(DataTableName);
    }
}
