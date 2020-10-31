package com.kushal.kotlinuniversalvideoplayer.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TheUtility {

    public static String humanReadableByteCount(long j, boolean z) {
        if (j < 1000) {
            StringBuilder sb = new StringBuilder();
            sb.append(j);
            sb.append(" B");
            return sb.toString();
        }
        double d = (double) j;
        int log = (int) (Math.log(d) / Math.log(1024.0d));
        new StringBuilder();
        double pow = Math.pow(1024.0d, (double) log);
        Double.isNaN(d);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("kMGTPE".charAt(log - 1));
        sb2.append("");
        Double.isNaN(d);
        return String.format(Locale.US, "%.1f %sB", new Object[]{Double.valueOf(d / pow), sb2.toString()});
    }

    public static String humanReadableDate(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy hh:mm:ss", Locale.US);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        return simpleDateFormat.format(instance.getTime());
    }

    static String parseTimeFromMilliseconds(String str) {
        if (str == null) {
            return "";
        }
        long floor = (long) Math.floor((double) (Long.parseLong(str.trim()) / 1000));
        if (floor <= 59) {
            StringBuilder sb = new StringBuilder();
            sb.append(prependZero((int) floor));
            sb.append("s");
            return sb.toString();
        }
        long floor2 = (long) Math.floor((double) (floor / 60));
        if (floor2 <= 59) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(prependZero((int) floor2));
            sb2.append(":");
            sb2.append(prependZero((int) (floor % 60)));
            return sb2.toString();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(prependZero((int) ((long) Math.floor((double) (floor2 / 60)))));
        sb3.append(":");
        sb3.append(prependZero((int) (floor2 % 60)));
        sb3.append(":");
        sb3.append(prependZero((int) (floor % 60)));
        return sb3.toString();
    }

    private static String prependZero(int i) {
        if (i < 10) {
            StringBuilder sb = new StringBuilder();
            sb.append("0");
            sb.append(i);
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(i);
        sb2.append("");
        return sb2.toString();
    }
}
