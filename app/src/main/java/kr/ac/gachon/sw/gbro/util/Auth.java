package kr.ac.gachon.sw.gbro.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.AuthProvider;

import kr.ac.gachon.sw.gbro.LoginActivity;
import kr.ac.gachon.sw.gbro.R;

public class Auth {

    /**
     * Google Sign-In Client 객체를 얻어온다
     *
     * @param context Context
     * @return GoogleSignInClient
     * @author Minjae Seon
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
     *
     * @return FirebaseAuth Instance
     * @author Minjae Seon
     */
    public static FirebaseAuth getFirebaseAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuthInstance().getCurrentUser();
    }

    /**
     * 로그아웃 처리 후 LoginActivity로 이동
     *
     * @param activity 로그아웃을 하는 Activity
     * @author Minjae Seon
     */
    public static void signOut(Activity activity) {
        if (getCurrentUser() != null) {
            getFirebaseAuthInstance().signOut();
            getGoogleSignInClient(activity).signOut();
        }

        moveToLogin(activity);
    }

    /**
     * 회원 탈퇴 후 LoginActivity로 이동
     *
     * @param activity 탈퇴 하는 Activity
     * @author Minjae Seon
     */
    public static void deleteAccount(Activity activity) {
        FirebaseUser fbUser = getCurrentUser();
        GoogleSignInAccount gsoAccount = GoogleSignIn.getLastSignedInAccount(activity);

        if (fbUser != null && gsoAccount != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(gsoAccount.getIdToken(), null);

            fbUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // 데이터베이스 먼저 삭제
                        Firestore.removeUser(fbUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // 삭제 성공시 Firebase User 삭제
                                    fbUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // 성공시
                                                    if (task.isSuccessful()) {
                                                        // GSO 연결 해제
                                                        getGoogleSignInClient(activity).revokeAccess();

                                                        // 로그인 Activity로
                                                        moveToLogin(activity);
                                                    }
                                                    // 실패시
                                                    else {
                                                        // 로그아웃 시킴
                                                        signOut(activity);
                                                    }
                                                }
                                            });
                                }
                                // 데이터베이스 삭제 실패시
                                else {
                                    signOut(activity);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 모든 이전 작업들을 지우고 LoginActivity로 이동
     *
     * @param activity 작업하는 Activity
     */
    public static void moveToLogin(Activity activity) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(loginIntent);
        activity.finishAndRemoveTask();
    }
}
