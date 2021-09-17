package com.otics.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;


public class Login extends AppCompatActivity {

    private static final String TAG = "Login";
    EditText et_emailLogin, et_pwLogin;
    CircularProgressButton btn_login;
    SignInButton btn_loginUseGoogle;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private BeginSignInRequest signUpRequest;
    private static final int masukgoogle = 100;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        et_emailLogin = findViewById(R.id.et_emailLogin);
        et_pwLogin = findViewById(R.id.et_passwordLogin);
        btn_login = findViewById(R.id.btn_login);
        btn_loginUseGoogle = findViewById(R.id.btn_loginUseGoogle);
        btn_loginUseGoogle.setSize(SignInButton.SIZE_STANDARD);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = et_emailLogin.getText().toString();
                final String password = et_pwLogin.getText().toString();

                if (email.isEmpty()) {
                    et_emailLogin.setFocusable(true);
                    et_emailLogin.setError("Belum diisi");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    et_emailLogin.setFocusable(true);
                    et_emailLogin.setError("Email tidak sesuai");
                    return;
                } else if (password.isEmpty()) {
                    et_pwLogin.setFocusable(true);
                    et_pwLogin.setError("Belum diisi");
                    return;
                } else if (password.length() < 6) {
                    et_pwLogin.setFocusable(true);
                    et_pwLogin.setError("Password minimal 6");
                    return;
                } else {
                    btn_login.startAnimation();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                btn_login.revertAnimation();

                                Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();
                                    }
                                }, 800);

                            } else {
                                btn_login.revertAnimation();
                                Toast.makeText(Login.this, "Email atau password salah", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        findViewById(R.id.tv_daftarFromLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        findViewById(R.id.tv_lupaPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        btn_loginUseGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneTapClient = Identity.getSignInClient(Login.this);
                signInRequest = BeginSignInRequest.builder()
                        .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true).build())
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true).setServerClientId(getString(R.string.web_client_google_signin))
                                .setFilterByAuthorizedAccounts(false).build())
                        .setAutoSelectEnabled(true)
                        .build();

                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try {
                            startIntentSenderForResult(beginSignInResult.getPendingIntent()
                                    .getIntentSender(), masukgoogle,null, 0,0,0);
                        }catch (IntentSender.SendIntentException e){
                            Log.e(TAG, "gagal masuk google "+ e.getLocalizedMessage());
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == masukgoogle) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                final String idToken = credential.getGoogleIdToken();
                final String username = credential.getId();
                final String password = credential.getPassword();

                firebaseAuthWithGoogle(idToken);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            fAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                assert user != null;
                                if (!user.isEmailVerified()){
                                    user.sendEmailVerification();
                                }
                                final String Uid = user.getUid();
                                HashMap<String, String> input = new HashMap<>();
                                input.put("nama_lengkap", user.getDisplayName());
                                input.put("Uid", Uid);
                                input.put("email", user.getEmail());
                                input.put("nohp", "");
                                input.put("images_url", "");
                                input.put("idcard", "");
                                input.put("department", "");
                                input.put("jabatan", "");
                                input.put("tgl_join_pt", "");
                                input.put("alamat", "");

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("Users").child(Uid);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()){
                                            databaseReference.setValue(input);
                                        }
                                        Toast.makeText(Login.this, "Login dengan google berhasil", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(Login.this, "wkwoakoawk", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                            // ...
                        }
                    });
        } catch (Exception e){
            Log.d("cekLoginGoogle", "firebaseAuthWithGoogle: " + e.getMessage());
        }

    }

}