package com.hasunemiku2015.metrofare.Ticketing.Types;

import java.io.*;
import java.util.*;

public class PaymentRecord {

    private Queue<String> record;
    private static final int maxSize = 10;

    static PaymentRecord newInstance() {
        PaymentRecord r = new PaymentRecord();
        r.record = new LinkedList<>();

        return r;
    }

    private PaymentRecord() {
        record = new LinkedList<>();
    }

    PaymentRecord(String in) {
        byte[] dat = Base64.getDecoder().decode(in);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(dat);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            record = (Queue<String>) ois.readObject();
        } catch (Exception ex) {
            record = new LinkedList<>();
        }
    }

    void addPaymentRecord(String comp, String value) {
        record.add(comp + "," + value);

        while (record.size() > maxSize) {
            record.remove();
        }
    }

    List<String[]> getPaymentRecords() {
        List<String[]> out = new ArrayList<>();

        while (record.size() > maxSize) {
            record.remove();
        }

        String[] recordArr = record.toArray(new String[0]);
        for (int i = 0; i < recordArr.length; i++) {
            // Inverse Traversal of Array
            String s = recordArr[recordArr.length - 1 - i];
            out.add(s.split(",", 2));
        }

        while (out.size() < maxSize) {
            out.add(new String[]{"", ""});
        }

        return out;
    }

    String[] getPaymentRecord(int index) {
        if (index < 0 || index > maxSize - 1) {
            throw new IndexOutOfBoundsException("Value should be between 0 to " + (maxSize - 1));
        }

        String[] args = record.toArray(new String[0]);

        if (args.length < index) {
            return new String[]{"", ""};
        }
        return args[index].split(",", 1);
    }

    public String toString() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(record);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException ignored) {}

        return "";
    }
}
