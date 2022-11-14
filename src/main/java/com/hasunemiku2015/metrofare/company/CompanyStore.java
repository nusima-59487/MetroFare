package com.hasunemiku2015.metrofare.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasunemiku2015.metrofare.MTFA;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class CompanyStore {
    public static HashMap<String, AbstractCompany> CompanyTable;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        File CompanyFolder;

        CompanyTable = new HashMap<>();

        //Get Company Files
        CompanyFolder = new File(MTFA.PLUGIN.getDataFolder(), "Companies");
        boolean created = CompanyFolder.mkdirs();

        //Load in Ser Files
        if (created) return;
        if (CompanyFolder.listFiles() == null) return;
        for (File f : Objects.requireNonNull(CompanyFolder.listFiles())) {
            if (f.getName().endsWith(".jsonc")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    String companyType = reader.readLine().substring(3);
                    Object o = GSON.fromJson(reader, Class.forName(companyType));

                    if (o instanceof AbstractCompany) {
                        AbstractCompany c = (AbstractCompany) o;
                        c.onLoad();
                        CompanyTable.put(c.getName(), c);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void deInit() throws IOException {
        if (CompanyTable != null) {
            for (String s : CompanyTable.keySet()) {
                File file = new File(MTFA.PLUGIN.getDataFolder() + "/Companies", s + ".jsonc");

                boolean canCreateFile = false;
                if (!file.exists()) {
                    canCreateFile = file.createNewFile();
                }
                if (canCreateFile || file.exists()) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                        writer.write( "///" + CompanyTable.get(s).getClass().getCanonicalName() + "\n");
                        GSON.toJson(CompanyTable.get(s), writer);
                    }
                }
            }
        }
    }

    public static void reload() {
        Bukkit.getScheduler().runTaskAsynchronously(MTFA.PLUGIN, () -> {
            try {
                deInit();
                init();
            } catch (Exception ignored) {
            }
        });
    }
    public static boolean newCompany(HashMap<String, Object> in) {
        AbstractCompany company;
        switch ((CompanyType) in.get("type")) {
            case ZONE:
            case ABS_COORDINATE:
                company = new ZoneAbsCompany(in);
                break;
            case DIJKSTRA:
                company = new DijkstraCompany(in);
                break;
            case UNIFORM:
                company = new UniformCompany(in);
                break;
            case FARE_TABLE:
                company = new FareTableCompany(in);
                break;
            default:
                return false;
        }

        CompanyTable.put(company.getName(), company);
        return true;
    }

    protected static boolean delCompany(String s) {
        File file = new File(MTFA.PLUGIN.getDataFolder() + "/Companies", s + ".jsonc");
        if (file.exists()) {
            boolean var = file.delete();
            CompanyStore.CompanyTable.remove(s);
            return var;
        }
        return false;
    }
}
