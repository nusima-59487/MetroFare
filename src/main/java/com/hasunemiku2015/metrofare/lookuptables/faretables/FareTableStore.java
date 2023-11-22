package com.hasunemiku2015.metrofare.lookuptables.faretables;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.response.Response;
import com.hasunemiku2015.metrofare.MTFA;

import java.io.*;
import java.util.*;

public class FareTableStore {
    public static HashMap<String, FareTable> FareTables;

    public static void init() throws FileNotFoundException, InvalidFareTableException {
        FareTables = new HashMap<>();
        File dir = new File(MTFA.PLUGIN.getDataFolder(), "FareTables");
        boolean b = dir.mkdirs();
        if (!b) {
            //Read the csv files
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                loadTable(file);
            }
        }
    }

    protected static void loadTable(File file) throws InvalidFareTableException, FileNotFoundException {
        if (!file.getName().endsWith(".csv")) return;

        try (Scanner sc = new Scanner(file)) {
            List<String> keyArr = Arrays.asList(sc.nextLine().split(","));
            List<String> keys = keyArr.subList(1, keyArr.size());

            List<int[]> data = new ArrayList<>();
            while (sc.hasNext()) {
                String[] row = sc.nextLine().split(",");

                List<Integer> rowData = new ArrayList<>();
                for (int i = 1; i < row.length; i++) {
                    int fare1000;
                    try {
                        fare1000 = Integer.parseInt(row[i]);
                    } catch (NumberFormatException e) {
                        throw new InvalidFareTableException("Invalid data!");
                    }
                    rowData.add(fare1000);
                }
                data.add(toIntArray(rowData));
            }

            String name = file.getName().replace(".csv", "");
            int[][] csvOut = data.toArray(new int[0][0]);
            FareTable ft = new FareTable(keys, csvOut);
            FareTables.put(name, ft);
        }
    }
    protected static byte fromPasteBin(String pasteKey, String localFileName) throws IOException {
        //Error Codes:
        //0: No Error, 1: Cannot Read Paste Content, 2: File Name Already Exist
        //3: Unable to Create File

        final PastebinFactory factory = new PastebinFactory();
        final Pastebin pastebin = factory.createPastebin("hi");
        final Response<String> pasteResponse = pastebin.getRawPaste(pasteKey);
        if (pasteResponse.hasError()) return 1;

        File file = new File(MTFA.PLUGIN.getDataFolder() + "/FareTables", localFileName + ".csv");
        if(file.exists()) return 2;
        boolean b = file.createNewFile();
        if(!b) return 3;

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file,false))){
            bw.write(pasteResponse.get());
            return 0;
        }
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];

        for(int i = 0; i < ret.length; ++i) {
            ret[i] = list.get(i);
        }

        return ret;
    }
}
