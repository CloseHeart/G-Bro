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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.regex.Pattern;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityLoginBinding;
import kr.ac.gachon.sw.gbro.util.Firestore;

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

        // Google Login Builder
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Google Login Client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();

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
        checkUser();
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
                        firebaseAuthWithGoogle(account.getIdToken());
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
                Log.w(this.getLocalClassName(), "Google sign in failed", e);
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
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // Credential 정보로 Firebase Auth
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    // Task 성공이라면
                    if (task.isSuccessful()) {
                        Log.d(LoginActivity.this.getLocalClassName(), "signInWithCredential:success");
                        checkUser();
                    // 실패라면
                    } else {
                        // 토스트 띄우고 로그아웃
                        Log.w(LoginActivity.this.getLocalClassName(), "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                        mGoogleSignInClient.signOut();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        // 이미 로그인했다면
        if(user != null) {
            // 이메일 인증 절차가 됐다면
            if(user.isEmailVerified()) {
                Log.d(LoginActivity.this.getLocalClassName(), "checkUser:emailverified");
                // 유저 데이터 가져오기
                Firestore.getUserData(user.getUid())
                        .addOnCompleteListener(userDataTask -> {
                            // 데이터 가져오는데 성공했다면
                            if(userDataTask.isSuccessful()) {
                                // Result 가져오고
                                DocumentSnapshot document = userDataTask.getResult();

                                // Intent 미리 씀
                                Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);

                                // 문서가 존재하지 않으면
                                if(!document.exists()) {
                                    Log.d(LoginActivity.this.getLocalClassName(), "getUserData:notexists");

                                    // 새 유저 정보 작성
                                    Firestore.writeNewUser(user.getUid(), user.getEmail(), user.getDisplayName())
                                            .addOnCompleteListener(documentTask -> {
                                                // 성공했다면
                                                if(documentTask.isSuccessful()) {
                                                    Log.d(LoginActivity.this.getLocalClassName(), "writeNewUser:success");
                                                    // MainActivity로
                                                    startActivity(mainActivityIntent);
                                                    finish();
                                                }
                                                // 실패했다면
                                                else {
                                                    Log.w(LoginActivity.this.getLocalClassName(), "writeNewUser:failure", documentTask.getException());
                                                    // 에러 메시지 띄우고 로그아웃
                                                    Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                    mGoogleSignInClient.signOut();
                                                }
                                            });
                                }
                                // 문서가 존재하면
                                else {
                                    Log.d(LoginActivity.this.getLocalClassName(), "getUserData:exists");

                                    // 바로 MainActivity
                                    startActivity(mainActivityIntent);
                                    finish();
                                }
                            }
                            // 데이터 로드 실패라면
                            else {
                                Log.w(LoginActivity.this.getLocalClassName(), "getUserData:failure", userDataTask.getException());

                                // 로그인 에러 토스트 및 로그아웃
                                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                mGoogleSignInClient.signOut();
                            }
                        });
            }
            else {
                // 이메일 인증 전송 및 토스트
                Log.d(LoginActivity.this.getLocalClassName(), "checkUser:needverifiy");
                user.sendEmailVerification();
                Toast.makeText(getApplicationContext(), R.string.login_requireverify, Toast.LENGTH_LONG).show();
            }
        }
    }
}