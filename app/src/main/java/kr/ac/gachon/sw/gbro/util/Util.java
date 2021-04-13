package kr.ac.gachon.sw.gbro.util;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static String timeStamptoString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        Date date = timestamp.toDate();
        return dateFormat.format(date);
    }
}
