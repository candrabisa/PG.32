package com.otics.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class Pencatatan extends AppCompatActivity {

    EditText et_ambilTglPencatatan, et_waktuOwaPencatatan,
            et_waktuHatsuPencatatan, et_jumlahCountPencatatan,
            et_alasanPencatatan;
    Spinner sp_nomc, sp_pilihJenisTool, sp_statusOwa, sp_statusHatsu;
    AppCompatButton btn_simpanData;

    FirebaseDatabase fBase = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference dRef = fBase.getReference("Pencatatan");
    DatabaseReference dRef1 = fBase.getReference("Users");
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();

    Calendar calendar;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;

    String nama_pic;
    int transaksi = new Random().nextInt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencatatan);

        et_ambilTglPencatatan = findViewById(R.id.et_ambilTglPencatatan);
        et_waktuOwaPencatatan = findViewById(R.id.et_waktuOwaPencatatan);
        et_waktuHatsuPencatatan = findViewById(R.id.et_waktuHatsuPencatatan);
        et_jumlahCountPencatatan = findViewById(R.id.et_jumlahCountPencatatan);
        et_alasanPencatatan = findViewById(R.id.et_alasanPencatatan);
        sp_nomc = findViewById(R.id.sp_pilihNoMc);
        sp_pilihJenisTool = findViewById(R.id.sp_pilihJenisTool);
        sp_statusOwa = findViewById(R.id.sp_statusOwa);
        sp_statusHatsu = findViewById(R.id.sp_statusHatsu);
        btn_simpanData = findViewById(R.id.btn_inputDataPencatatan);

        progressDialog = new ProgressDialog(Pencatatan.this);
        progressDialog.setMessage("Menyimpan data");
        progressDialog.setCancelable(false);

        Query query = dRef1.orderByChild("Uid").equalTo(fUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    nama_pic = "" + ds.child("nama_lengkap").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_simpanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tanggal_catatan = et_ambilTglPencatatan.getText().toString();
                final String waktu_owa = et_waktuOwaPencatatan.getText().toString();
                final String waktu_hatsu = et_waktuHatsuPencatatan.getText().toString();
                final String jumlah_count = et_jumlahCountPencatatan.getText().toString();
                final String alasan = et_alasanPencatatan.getText().toString();
                final String nomc = sp_nomc.getSelectedItem().toString();
                final String jenis_tools = sp_pilihJenisTool.getSelectedItem().toString();
                final String status_owa = sp_statusOwa.getSelectedItem().toString();
                final String status_hatsu = sp_statusHatsu.getSelectedItem().toString();

                if (tanggal_catatan.isEmpty()){
                    et_ambilTglPencatatan.setError("Belum diisi");
                    et_ambilTglPencatatan.setFocusable(true);
                } else if (waktu_owa.isEmpty()){
                    et_waktuOwaPencatatan.setError("Belum diisi");
                    et_waktuOwaPencatatan.setFocusable(true);
                } else if (waktu_hatsu.isEmpty()){
                    et_waktuHatsuPencatatan.setError("Belum diisi");
                    et_waktuHatsuPencatatan.setFocusable(true);
                } else if (jumlah_count.isEmpty()){
                    et_jumlahCountPencatatan.setError("Belum diisi");
                    et_jumlahCountPencatatan.setFocusable(true);
                } else if (alasan.isEmpty()) {
                    et_alasanPencatatan.setError("Belum diisi");
                    et_alasanPencatatan.setFocusable(true);
                } else {
                    builder = new AlertDialog.Builder(Pencatatan.this);
                    builder.setTitle("Perhatian!");
                    builder.setMessage("Apakah yakin ingin menyimpan data?");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog.show();
                            dRef.child(tanggal_catatan+"-"+transaksi).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().child("tgl_pencatatan").setValue(tanggal_catatan);
                                    snapshot.getRef().child("waktu_owa").setValue(waktu_owa);
                                    snapshot.getRef().child("waktu_hatsu").setValue(waktu_hatsu);
                                    snapshot.getRef().child("jumlah_count").setValue(jumlah_count);
                                    snapshot.getRef().child("alasan_pencatatan").setValue(alasan);
                                    snapshot.getRef().child("no_mesin").setValue(nomc);
                                    snapshot.getRef().child("jenis_tools").setValue(jenis_tools);
                                    snapshot.getRef().child("status_owa").setValue(status_owa);
                                    snapshot.getRef().child("status_hatsu").setValue(status_hatsu);
                                    snapshot.getRef().child("pic").setValue(nama_pic);

                                    et_ambilTglPencatatan.setText("");
                                    et_waktuOwaPencatatan.setText("");
                                    et_waktuHatsuPencatatan.setText("");
                                    et_jumlahCountPencatatan.setText("");
                                    et_alasanPencatatan.setText("");
                                    transaksi = new Random().nextInt();

                                    Snackbar.make(view, "Data berhasil disimpan", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Snackbar.make(view, "Gagal menyimpan karena "+error.getMessage()
                                            ,BaseTransientBottomBar.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create().show();
                }
            }
        });

        et_waktuHatsuPencatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "fragment_tag");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        int jamBaru = materialTimePicker.getHour();
                        int menitBaru = materialTimePicker.getMinute();

                        calendar.set(Calendar.HOUR_OF_DAY, jamBaru);
                        calendar.set(Calendar.MINUTE, menitBaru);

                        String format = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("id", "ID"));

                        et_waktuHatsuPencatatan.setText(sdf.format(calendar.getTime()) + " WIB");
                    }
                });
            }
        });

        et_waktuOwaPencatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "wkwkwk");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        int jamBaru = materialTimePicker.getHour();
                        int menitBaru = materialTimePicker.getMinute();

                        calendar.set(Calendar.HOUR_OF_DAY, jamBaru);
                        calendar.set(Calendar.MINUTE, menitBaru);

                        String format = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("id", "ID"));
                        et_waktuOwaPencatatan.setText(sdf.format(calendar.getTime()) +" WIB");
                    }
                });
            }
        });

        et_ambilTglPencatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("id", "ID"));
                        et_ambilTglPencatatan.setText(sdf.format(calendar.getTime()));
                    }
                };

                new DatePickerDialog(Pencatatan.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }
}