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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.regex.Pattern;

import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityLoginBinding;
import kr.ac.gachon.sw.gbro.service.LocalNotiService;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Preferences;
import kr.ac.gachon.sw.gbro.util.Util;

import static kr.ac.gachon.sw.gbro.util.Util.RC_SIGN_IN;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Preferences prefs;

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
        // SharedPreferences
        prefs = new Preferences(getApplicationContext());

        // ?????? ????????? null ?????????
        if(Auth.getCurrentUser() != null) {
            // CurrentChat??? null??? ???????????? null??? ??????
            if(prefs.getString("currentchat", null) != null) {
                prefs.getString("currentchat", null);
            }

            // ????????????
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Request Code??? RC_SIGN_IN??? ???????????????
        if (requestCode == RC_SIGN_IN) {
            // ????????? ????????? ?????????
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // ????????? Result ?????????
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(this.getLocalClassName(), "firebaseAuthWithGoogle:" + account.getId());

                // ????????? ?????? ??????
                String emailAddress = account.getEmail();

                // ????????? ????????????
                if(emailAddress != null) {
                    // ????????? ?????? ?????? ?????????
                    boolean isGachonMail = Pattern.matches("^[a-zA-Z0-9._%+-]+@gachon.ac.kr$", emailAddress);

                    // ????????? ?????? ?????????
                    if(isGachonMail) {
                        // Firebase ????????? ?????????
                        firebaseAuthWithGoogle(account);
                    }
                    // ????????????
                    else {
                        // ????????? ????????? ?????? ???????????? ??????
                        Toast.makeText(getApplicationContext(), R.string.login_invalid, Toast.LENGTH_LONG).show();
                        mGoogleSignInClient.signOut();
                    }
                }
                // ????????? ????????? ????????? ?????????
                else {
                    // ????????? ????????? ?????? ???????????? ??????
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
     * Google Login ????????? Firebase Auth??? ????????? ???????????????
     * @author Suyeon Jung, Minjae Seon
     * @param idToken Google Login Token
     * @return Void
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        // Credential ????????? Firebase Auth
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                    // Task ???????????????
                    if (task.isSuccessful()) {
                        Log.d(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:success");
                        AdditionalUserInfo additionalUserInfo = task.getResult().getAdditionalUserInfo();

                        // User ????????? ????????????, ?????? UserInfo??? ??? ??????????????????
                        if(task.getResult().getUser() != null && additionalUserInfo != null) {
                            // ?????? ????????????
                            if (additionalUserInfo.isNewUser()) {
                                Util.debugLog(LoginActivity.this, "New User Detected");

                                // DB ?????? ??????
                                createNewUserDatabase(task.getResult().getUser());
                            }
                            // ????????????
                            else {
                                Util.debugLog(LoginActivity.this, "Already Registered User");

                                // MainActivity???
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                        // ???????????? Null?????????
                        } else {
                            // ????????? ????????? ????????????
                            Log.w(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:failure", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            mGoogleSignInClient.signOut();
                        }
                });
    }

    /**
     * ????????? ???????????? DB ????????? ????????????
     * @author Suyeon Jung, Minjae Seon
     */
    private void createNewUserDatabase(FirebaseUser user) {
        // FCM Token ?????? ??????
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> tokenTask) {
                        if(tokenTask.isSuccessful()) {
                            // ??? ?????? ?????? ??????
                            Firestore.writeNewUser(user.getUid(), user.getEmail(), user.getDisplayName(), tokenTask.getResult())
                                    .addOnCompleteListener(documentTask -> {
                                        // ???????????????
                                        if(documentTask.isSuccessful()) {
                                            Log.d(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:success");
                                            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();

                                            // MainActivity???
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        }
                                        // ???????????????
                                        else {
                                            Log.w(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:failure", documentTask.getException());
                                            // ?????? ????????? ????????? ????????????
                                            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                            mGoogleSignInClient.signOut();
                                        }
                                    });
                        }
                        // ???????????????
                        else {
                            Log.w(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:failure", tokenTask.getException());
                            // ?????? ????????? ????????? ????????????
                            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            mGoogleSignInClient.signOut();
                        }
                    }
                });
    }
}