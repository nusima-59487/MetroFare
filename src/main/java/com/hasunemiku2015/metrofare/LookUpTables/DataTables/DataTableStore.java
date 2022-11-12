package com.hasunemiku2015.metrofare.LookUpTables.DataTables;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.response.Response;
import com.hasunemiku2015.metrofare.MTFA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class DataTableStore {
    public static HashMap<String, DataTable> DataTables = new HashMap<>();

    //Inits
    public static void init() {
        File dir = new File(MTFA.PLUGIN.getDataFolder(),"DataTables");
        dir.mkdirs();

        if(dir.listFiles() == null){
            return;
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            load(file);
        }
    }
    public static void deinit() throws IOException {
        for(DataTable io : DataTableStore.DataTables.values()){
            io.exportToCSV();
        }
    }

    protected static boolean load(File file){
        if (file.getName().endsWith((".csv"))) {
            DataTable dt;
            try {
                dt = new DataTable(file);
            } catch (IOException ex) {
                return false;
            }
            DataTables.put(file.getName().replace(".csv",""),dt);
            return true;
        }
        return false;
    }
    protected static void delFile(String FileName) {
        File file = new File(MTFA.PLUGIN.getDataFolder() + "/DataTables", FileName + ".csv");
        if(file.exists()){
            file.delete();
        }
    }

    protected static byte fromPasteBin(String pasteKey,String localFileName) throws IOException {
        //Error Codes:
        //0: No Error, 1: Cannot Read Paste Content, 2: File Name Already Exist
        //3: Unable to Create File

        final PastebinFactory factory = new PastebinFactory();
        final Pastebin pastebin = factory.createPastebin("hi");
        final Response<String> pasteResponse = pastebin.getRawPaste(pasteKey);
        if (pasteResponse.hasError()) return 1;

        File file = new File(MTFA.PLUGIN.getDataFolder() + "/DataTables", localFileName + ".csv");
        if(file.exists()) return 2;
        boolean b = file.createNewFile();
        if(!b) return 3;

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file,false))){
            bw.write(pasteResponse.get());
            return 0;
        }
    }
}
