package kr.ac.gachon.sw.gbro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.regex.Pattern;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityLoginBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Util;

import static kr.ac.gachon.sw.gbro.util.Util.RC_SIGN_IN;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected ActivityLoginBinding getBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Google Login Client
        mGoogleSignInClient = Auth.getGoogleSignInClient(this);

        // Firebase Auth Instance
        mAuth = Auth.getFirebaseAuthInstance();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request Login Window
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 현재 유저가 null 아니면
        if(Auth.getCurrentUser() != null) {
            // 메인으로
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Request Code가 RC_SIGN_IN과 일치한다면
        if (requestCode == RC_SIGN_IN) {
            // 넘어온 정보로 로그인
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // 로그인 Result 가져옴
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(this.getLocalClassName(), "firebaseAuthWithGoogle:" + account.getId());

                // 이메일 정보 로드
                String emailAddress = account.getEmail();

                // 이메일 정상이면
                if(emailAddress != null) {
                    // 가천대 메일 체크 정규식
                    boolean isGachonMail = Pattern.matches("^[a-zA-Z0-9._%+-]+@gachon.ac.kr$", emailAddress);

                    // 가천대 메일 맞으면
                    if(isGachonMail) {
                        // Firebase 로그인 절차로
                        firebaseAuthWithGoogle(account);
                    }
                    // 아니라면
                    else {
                        // 토스트 띄우고 구글 로그아웃 처리
                        Toast.makeText(getApplicationContext(), R.string.login_invalid, Toast.LENGTH_LONG).show();
                        mGoogleSignInClient.signOut();
                    }
                }
                // 이메일 정보가 정상이 아니면
                else {
                    // 도스트 띄우고 구글 로그아웃 처리
                    Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                    mGoogleSignInClient.signOut();
                }
            } catch (ApiException e) {
                Log.w(this.getClass().getSimpleName(), "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
     * Google Login 정보를 Firebase Auth로 넘겨서 로그인한다
     * @author Suyeon Jung, Minjae Seon
     * @param idToken Google Login Token
     * @return Void
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        // Credential 정보로 Firebase Auth
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                    // Task 성공이라면
                    if (task.isSuccessful()) {
                        Log.d(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:success");
                        AdditionalUserInfo additionalUserInfo = task.getResult().getAdditionalUserInfo();

                        // User 정보가 정상이고, 추가 UserInfo도 잘 가져와졌다면
                        if(task.getResult().getUser() != null && additionalUserInfo != null) {
                            // 신규 유저라면
                            if (additionalUserInfo.isNewUser()) {
                                Util.debugLog(LoginActivity.this, "New User Detected");

                                // DB 생성 작업
                                createNewUserDatabase(task.getResult().getUser());
                            }
                            // 아니라면
                            else {
                                Util.debugLog(LoginActivity.this, "Already Registered User");

                                // MainActivity로
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                        // 하나라도 Null이라면
                        } else {
                            // 토스트 띄우고 로그아웃
                            Log.w(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:failure", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            mGoogleSignInClient.signOut();
                        }
                });
    }

    /**
     * 새로운 사용자의 DB 정보를 생성한다
     * @author Suyeon Jung, Minjae Seon
     */
    private void createNewUserDatabase(FirebaseUser user) {
        // 새 유저 정보 작성
        Firestore.writeNewUser(user.getUid(), user.getEmail(), user.getDisplayName())
                .addOnCompleteListener(documentTask -> {
                    // 성공했다면
                    if(documentTask.isSuccessful()) {
                        Log.d(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:success");
                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();

                        // MainActivity로
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    // 실패했다면
                    else {
                        Log.w(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:failure", documentTask.getException());
                        // 에러 메시지 띄우고 로그아웃
                        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        mGoogleSignInClient.signOut();
                    }
                });
    }
}