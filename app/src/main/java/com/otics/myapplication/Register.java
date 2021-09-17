package com.otics.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Register extends AppCompatActivity {

    EditText et_namaLengkapRegister, et_emailRegister,
            et_nohpRegister, et_passwordRegister;
    CircularProgressButton btn_signUpRegister;

    FirebaseAuth fAuth;
    FirebaseDatabase fDbase;
    DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fDbase = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dRef = fDbase.getReference();

        et_namaLengkapRegister = findViewById(R.id.et_namaLengkapRegister);
        et_emailRegister = findViewById(R.id.et_emailRegister);
        et_nohpRegister = findViewById(R.id.et_nohpRegister);
        et_passwordRegister = findViewById(R.id.et_passwordRegister);
        btn_signUpRegister = findViewById(R.id.btn_signUpRegister);

        btn_signUpRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nama_lengkap = et_namaLengkapRegister.getText().toString();
                final String email = et_emailRegister.getText().toString();
                final String nohp = et_nohpRegister.getText().toString();
                final String password = et_passwordRegister.getText().toString();

                if (nama_lengkap.isEmpty()){
                    et_namaLengkapRegister.setError("Belum diisi");
                    et_namaLengkapRegister.setFocusable(true);
                    return;
                } else if (email.isEmpty()){
                    et_emailRegister.setError("Belum diisi");
                    et_emailRegister.setFocusable(true);
                    return;
                } else if (nohp.isEmpty()){
                    et_nohpRegister.setError("Belum diisi");
                    et_nohpRegister.setFocusable(true);
                    return;
                } else if (password.isEmpty()){
                    et_passwordRegister.setError("Belum diisi");
                    et_passwordRegister.setFocusable(true);
                    return;
                } else if (password.length()<6){
                    et_passwordRegister.setError("Password minimum 6");
                    et_passwordRegister.setFocusable(true);
                    return;
                } else {
                    btn_signUpRegister.startAnimation();
                    fAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        fAuth.getCurrentUser().sendEmailVerification();
                                        final String Uid = fAuth.getCurrentUser().getUid();
                                        dRef.child("Users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                snapshot.getRef().child("Uid").setValue(Uid);
                                                snapshot.getRef().child("nama_lengkap").setValue(nama_lengkap);
                                                snapshot.getRef().child("email").setValue(email);
                                                snapshot.getRef().child("nohp").setValue(nohp);
                                                snapshot.getRef().child("images_url").setValue("");
                                                snapshot.getRef().child("idcard").setValue("");
                                                snapshot.getRef().child("department").setValue("");
                                                snapshot.getRef().child("jabatan").setValue("");
                                                snapshot.getRef().child("tgl_join_pt").setValue("");
                                                snapshot.getRef().child("alamat").setValue("");

                                                et_namaLengkapRegister.setText("");
                                                et_emailRegister.setText("");
                                                et_nohpRegister.setText("");
                                                et_passwordRegister.setText("");
                                                btn_signUpRegister.revertAnimation();
                                                Toast.makeText(Register.this, "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(new Intent(Register.this, Login.class));
                                                        finish();
                                                    }
                                                }, 1000);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                                        btn_signUpRegister.revertAnimation();
                                        Toast.makeText(Register.this, "Masukan email dengan benar!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        btn_signUpRegister.revertAnimation();
                                        Toast.makeText(Register.this, "Email telah digunakan", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}