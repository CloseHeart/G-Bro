package kr.ac.gachon.sw.gbro.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    // Google Login Request Code
    public static final int RC_SIGN_IN = 1000;

    // FCM DEFAULT ID
    public static final int FCM_DEFAULT_ID = 0;

    // 키워드 Notification Service Foreground Notificaiton ID
    public static final String KEYWORD_FOREGROUND_ID = "keyword_foreground";

    // 키워드 Notification  ID
    public static final String KEYWORD_ID = "keyword";

    /**
     * Firebase Timestamp 정보를 Format에 맞춰 String 형태로 반환
     * @author Minjae Seon
     * @param timestamp com.google.firebase.Timestamp
     * @return Timestamp 정보 기반의 시간/날짜 정보 String
     */
    public static String timeStamptoString(Timestamp timestamp) {
        Date currentDate = new Date();
        Date targetDate = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREAN);

        int compareValue = dateFormat.format(currentDate).compareTo(dateFormat.format(targetDate));
        if(compareValue == 0) {
            return timeFormat.format(targetDate);
        }
        else {
            return dateFormat.format(targetDate);
        }
    }

    /**
     * Firebase Timestamp 정보를 Format에 맞춰 String 형태로 반환 (Detail Version)
     * @author Minjae Seon
     * @param timestamp com.google.firebase.Timestamp
     * @return Timestamp 정보 기반의 시간/날짜 정보 String
     */
    public static String timeStamptoDetailString(Timestamp timestamp) {
        Date currentDate = new Date();
        Date targetDate = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
        SimpleDateFormat detailDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREAN);

        int compareValue = dateFormat.format(currentDate).compareTo(dateFormat.format(targetDate));
        if(compareValue == 0) {
            return timeFormat.format(targetDate);
        }
        else {
            return detailDateFormat.format(targetDate);
        }
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

    /**
     * Bitmap Image를 Resize한다
     * 참조 Page : http://theeye.pe.kr/archives/1380
     * @author Minjae Seon
     * @param original 원본 Bitmap
     * @param maxRes 최대 해상도
     * @return Resize된 Bitmap Object
     */
    public static Bitmap resizeBitmap(Bitmap original, int maxRes) {
        int w = original.getWidth();
        int h = original.getHeight();
        int newW = w;
        int newH = h;
        float rate = 0.0f;

        if (w > h) {
            if (maxRes < w) {
                rate = maxRes / (float) w;
                newH = (int) (h * rate);
                newW = maxRes;
            }
        }
        else {
            if (maxRes < h) {
                rate = maxRes / (float) h;
                newW = (int) (w * rate);
                newH = maxRes;
            }
        }
        return Bitmap.createScaledBitmap(original, newW, newH, true);
    }

    /**
     * Drawable Image를 Bitmap으로 변환한다
     * @author Minjae Seon
     * @param context Context
     * @param drawable Drawable ID
     * @return Drawable을 Bitmap으로 변환한 객체
     */
    public static Bitmap drawableToBitmap(Context context, int drawable) {
        return BitmapFactory.decodeResource(context.getResources(), drawable);
    }

    /**
     * ByteArray로 된 Image를 Bitmap으로 변환한다
     * @author Minjae Seon
     * @param bytesImage Image 정보가 담긴 Byte Array
     * @return bytesImage로 넘어온 Image를 Bitmap으로 변환한 객체
     */
    public static Bitmap byteArrayToBitmap(byte[] bytesImage) {
        return BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
    }

    /**
     * Bitmap을 ByteArray로
     * @param bitmap 바꿀 Bitmap
     * @return ByteArray
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
