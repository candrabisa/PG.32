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
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.otics.myapplication.adapter.ListCatatanAdapter;
import com.otics.myapplication.model.ListCatatanModel;

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
    String id_transaksi;
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

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                id_transaksi = bundle.getString("id_transaksi");
                final String tgl_catat = bundle.getString("tgl_catat");
                final String nomc = bundle.getString("nomc");
                final String jenis_tools = bundle.getString("jenis_tools");
                final String waktu_owa = bundle.getString("waktu_owa");
                final String status_owa = bundle.getString("status_owa");
                final String waktu_hatsu = bundle.getString("waktu_hatsu");
                final String status_hatsu = bundle.getString("status_hatsu");
                final String jumlah_count = bundle.getString("jumlah_count");
                final String alasan = bundle.getString("alasan");
                final String pic = bundle.getString("pic");

                et_ambilTglPencatatan.setText(tgl_catat);
                et_ambilTglPencatatan.setEnabled(false);
                et_waktuOwaPencatatan.setText(waktu_owa);
                et_waktuHatsuPencatatan.setText(waktu_hatsu);
                et_jumlahCountPencatatan.setText(jumlah_count);
                et_alasanPencatatan.setText(alasan);

                switch (nomc){
                    case "#20" :
                        sp_nomc.setSelection(0);
                        break;
                    case "#30" :
                        sp_nomc.setSelection(1);
                        break;
                    case "#40" :
                        sp_nomc.setSelection(2);
                        break;
                    case "#50" :
                        sp_nomc.setSelection(3);
                        break;
                    case "#60" :
                        sp_nomc.setSelection(4);
                        break;
                    case "70" :
                        sp_nomc.setSelection(5);
                        break;
                    case "#80" :
                        sp_nomc.setSelection(6);
                    default:
                        break;
                }

                if (status_owa.equals("OK")){
                    sp_statusOwa.setSelection(0);
                } else {
                    sp_statusOwa.setSelection(1);
                }

                if (jenis_tools.equals("New")){
                    sp_pilihJenisTool.setSelection(0);
                } else {
                    sp_pilihJenisTool.setSelection(1);
                }
                if (status_hatsu.equals("OK")){
                    sp_statusHatsu.setSelection(0);
                } else {
                    sp_statusHatsu.setSelection(1);
                }
            } else {
                et_ambilTglPencatatan.setText("");
                et_waktuOwaPencatatan.setText("");
                et_waktuHatsuPencatatan.setText("");
                et_jumlahCountPencatatan.setText("");
                et_alasanPencatatan.setText("");
            }

        } catch (Exception e){
            Toast.makeText(Pencatatan.this, "Kesalahan", Toast.LENGTH_SHORT).show();
        }

        progressDialog = new ProgressDialog(Pencatatan.this);
        progressDialog.setMessage("Menyimpan data");
        progressDialog.setCancelable(false);

        Query query = dRef1.orderByChild("Uid").equalTo(fUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    nama_pic = "" + ds.child("nama_lengkap").getValue();
                    final String idcard = "" + ds.child("idcard").getValue();
                    final String jabatan = "" + ds.child("jabatan").getValue();
                    final String tgl_join = ""+ds.child("tgl_join_pt").getValue();
                    final String department = ""+ds.child("department").getValue();
                    final String alamat = ""+ds.child("alamat").getValue();
                    final String nohp = ""+ds.child("nohp").getValue();

                    if (idcard.isEmpty() && jabatan.isEmpty() && tgl_join.isEmpty() && department.isEmpty() && alamat.isEmpty() && nohp.isEmpty()){
                        AlertDialog.Builder alert = new AlertDialog.Builder(Pencatatan.this);
                        alert.setTitle("Perhatian!!");
                        alert.setMessage("Anda harus melengkapi data pada account untuk melakukan pencatatan data");
                        alert.setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        alert.create().show();
                    }
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
                            if (id_transaksi == null){
                                dRef.child(tanggal_catatan+"-"+transaksi).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().child("id_transaksi").setValue(tanggal_catatan+"-"+transaksi);
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
                            } else {
                                dRef.child(id_transaksi).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().child("id_transaksi").setValue(id_transaksi);
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