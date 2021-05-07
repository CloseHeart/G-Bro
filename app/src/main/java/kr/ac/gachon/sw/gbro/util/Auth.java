package kr.ac.gachon.sw.gbro.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.ac.gachon.sw.gbro.LoginActivity;
import kr.ac.gachon.sw.gbro.R;

public class Auth {

    /**
     * Google Sign-In Client 객체를 얻어온다
     * @author Minjae Seon
     * @param context Context
     * @return GoogleSignInClient
     */
    public static GoogleSignInClient getGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }


    /**
     * FirebaseAuth Instance를 얻어온다
     * @author Minjae Seon
     * @return FirebaseAuth Instance
     */
    public static FirebaseAuth getFirebaseAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuthInstance().getCurrentUser();
    }

    /**
     * 로그아웃 처리 후 LoginActivity로 이동
     * @author Minjae Seon
     * @param activity 로그아웃을 하는 Activity
     */
    public static void signOut(Activity activity) {
        if(getCurrentUser() != null) {
            getFirebaseAuthInstance().signOut();
            getGoogleSignInClient(activity).signOut();
        }

        Intent loginIntent = new Intent(activity, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(loginIntent);
        activity.finishAndRemoveTask();
    }

    /**
     * 회원 탈퇴 후 LoginActivity로 이동
     * @author Minjae Seon
     * @param activity 로그아웃을 하는 Activity
     */
    public static void deleteAccount(Activity activity) {
        FirebaseUser user = getCurrentUser();

        if(user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // 성공시
                            if (task.isSuccessful()) {
                                // GSO 연결 해제
                                getGoogleSignInClient(activity).revokeAccess();

                                // TODO: Firestore 정보 삭제 필요
                            }
                            // 실패시
                            else {
                                // 로그아웃 시킴
                                signOut(activity);
                            }

                            // 로그인 Activity로
                            Intent loginIntent = new Intent(activity, LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(loginIntent);
                            activity.finishAndRemoveTask();
                        }
                    });
        }
    }
}
