package com.otics.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText et_emailResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle("Lupa Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        et_emailResetPassword = findViewById(R.id.et_emailResetPassword);

        findViewById(R.id.btn_resetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = et_emailResetPassword.getText().toString();

                if (email.isEmpty()){
                    et_emailResetPassword.setError("Belum diisi");
                    et_emailResetPassword.setFocusable(true);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    et_emailResetPassword.setError("Masukan email dengan benar");
                    et_emailResetPassword.setFocusable(true);
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(ForgotPassword.this);
                    progressDialog.setMessage("Mohon menunggu...");
                    progressDialog.show();
                    fAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                                        builder.setTitle("Berhasil");
                                        builder.setMessage("Reset password berhasil, apakah anda ingin membuka email sekarang?");
                                        builder.setPositiveButton("Buka Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent bukaEmail = new Intent(Intent.ACTION_MAIN);
                                                bukaEmail.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                startActivity(Intent.createChooser(bukaEmail, "BUKA EMAIL DENGAN"));

                                            }
                                        });
                                        builder.setNegativeButton("nanti", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.show();
                                    } else {
                                        progressDialog.dismiss();
                                        Snackbar.make(view, "Email tidak ditemukan", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int back = item.getItemId();
        if (back == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}