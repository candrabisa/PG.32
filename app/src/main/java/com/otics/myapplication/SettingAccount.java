package com.otics.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingAccount extends AppCompatActivity {

    EditText et_changeLayout;

    AlertDialog.Builder builder;

    FirebaseDatabase fBase = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/");
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);

        builder = new AlertDialog.Builder(SettingAccount.this);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference dRef = fBase.getReference("Users").child(fUser.getUid());
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child("email").setValue(fUser.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.rl_btnGantiPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Ganti Password");
                View layoutChange = getLayoutInflater().inflate(R.layout.layout_change_password, null);
                EditText et_changePw1 = layoutChange.findViewById(R.id.et_changePw1);
                EditText et_changePw2 = layoutChange.findViewById(R.id.et_changePw2);
                builder.setView(layoutChange);
                builder.setCancelable(false);
                builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String password1 = et_changePw1.getText().toString();
                        final String password2 = et_changePw2.getText().toString();

                        if (password1.isEmpty()) {
                            Snackbar.make(view, "Password baru tidak boleh kosong", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (password2.isEmpty()) {
                            Snackbar.make(view, "Ulangi password tidak kosong", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (!password2.equals(password1)) {
                            Snackbar.make(view, "Password tidak sama", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (password2.length() < 6) {
                            Snackbar.make(view, "Password minimal 6 karakter", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else {
                            fUser.updatePassword(password2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(view, "Password berhasil diubah", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    } else {
                                        AuthCredential credential = EmailAuthProvider.getCredential(fUser.getEmail(), password2);
                                        fUser.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Snackbar.make(view, "Password berhasil diubah", BaseTransientBottomBar.LENGTH_SHORT).show();
                                                } else {
                                                    Snackbar.make(view, "Password gagal diubah", BaseTransientBottomBar.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        findViewById(R.id.rl_btnGantiEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Ganti Email");
                View layoutChange = getLayoutInflater().inflate(R.layout.layout_change_et, null);
                et_changeLayout = layoutChange.findViewById(R.id.et_changeLayout);
                builder.setView(layoutChange);
                builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String ganti_email = et_changeLayout.getText().toString();
                        if (ganti_email.isEmpty()) {
                            Snackbar.make(view, "Email tidak boleh kosong", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(ganti_email).matches()) {
                            Snackbar.make(view, "Email tidak sesuai!", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (ganti_email.equals(fUser.getEmail())) {
                            Snackbar.make(view, "Email tidak boleh sama", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else {
                            ProgressDialog progressDialog = new ProgressDialog(SettingAccount.this);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                            fUser.updateEmail(ganti_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(view, "Berhasil ganti email", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(view, "Email telah digunakan", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        int back = item.getItemId();
        if (back == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


