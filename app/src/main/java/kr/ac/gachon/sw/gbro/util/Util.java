package kr.ac.gachon.sw.gbro.util;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    // Google Login Request Code
    public static final int RC_SIGN_IN = 1000;

    /**
     * Firebase Timestamp 정보를 Format에 맞춰 String 형태로 반환
     * @author Minjae Seon
     * @param timestamp com.google.firebase.Timestamp
     * @return Timestamp 정보 기반의 시간/날짜 정보 String
     */
    public static String timeStamptoString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        Date date = timestamp.toDate();
        return dateFormat.format(date);
    }

    /**
     * Debug Log 출력
     *      * @author Minjae Seon
     * @param activity 로그 띄울 Activity
     * @param msg 메시지
     */
    public static void debugLog(Activity activity, String msg) {
        Log.d(activity.getClass().getSimpleName(), msg);
    }

    /**
     * Debug Log 출력
     * @author Minjae Seon
     * @param activity 로그 띄울 Activity
     * @param msg 메시지
     * @param exception Exception
     */
    public static void errorLog(Activity activity, String msg, Exception exception) {
        Log.e(activity.getClass().getSimpleName(), msg, exception);
    }

    /**
     * Debug Log 출력
     * @author Minjae Seon
     * @param activity 로그 띄울 Activity
     * @param msg 메시지
     * @param exception Exception
     */
    public static void infoLog(Activity activity, String msg, Exception exception) {
        Log.i(activity.getClass().getSimpleName(), msg, exception);
    }

    /**
     * Debug Log 출력
     * @author Minjae Seon
     * @param activity 로그 띄울 Activity
     * @param msg 메시지
     */
    public static void infoLog(Activity activity, String msg) {
        Log.i(activity.getClass().getSimpleName(), msg);
    }
}
