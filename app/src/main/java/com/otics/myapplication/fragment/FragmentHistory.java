package com.otics.myapplication.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.otics.myapplication.MainActivity;
import com.otics.myapplication.MonthYearPickerDialog;
import com.otics.myapplication.R;
import com.otics.myapplication.adapter.ListCatatanAdapter;
import com.otics.myapplication.model.ListCatatanModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class FragmentHistory extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText et_cariPakeTanggal, et_cariPakeBulan, et_cariPakeTahun;
    RecyclerView rv_history;
    ConstraintLayout constraint_history, rl_filterTglHistory,
            constraint_filterTahun ,constraint_filterBulan;
    Spinner sp_historyCatatan;
    SearchableSpinner sp_cariPakeBulan;
    Button btn_cetakKanriban, btn_bagikanKanriban;

    List<ListCatatanModel> listCatatanModels = new ArrayList<>();
    ListCatatanAdapter adapter;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();
    FirebaseDatabase fBase = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference dRef = fBase.getReference("Pencatatan");

    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    static final int REQUEST_CODE = 100;

    String nama;
    String tanggal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    String bulan = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
    String tahun = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        loadDataPencatatan();

        constraint_history = view.findViewById(R.id.constraint_history);
        rl_filterTglHistory = view.findViewById(R.id.rl_filterTglHistory);
        constraint_filterBulan = view.findViewById(R.id.constraint_filterBulan);
        constraint_filterTahun = view.findViewById(R.id.constraint_filterTahun);
        rl_filterTglHistory.setVisibility(View.GONE);
        constraint_filterBulan.setVisibility(View.GONE);
        constraint_filterTahun.setVisibility(View.GONE);

        btn_cetakKanriban = view.findViewById(R.id.btn_cetakKanriban);
        btn_bagikanKanriban = view.findViewById(R.id.btn_bagikanKanriban);

        sp_historyCatatan = view.findViewById(R.id.sp_historyCatatan);
        sp_historyCatatan.setOnItemSelectedListener(this);

        et_cariPakeTanggal = view.findViewById(R.id.et_cariPakeTanggal);
        et_cariPakeBulan = view.findViewById(R.id.et_cariPakeBulan);
        et_cariPakeTahun = view.findViewById(R.id.et_cariPakeTahun);

        rv_history = view.findViewById(R.id.rv_history);
        rv_history.setLayoutManager(new LinearLayoutManager(getContext()));

        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Perhatian!");
        builder.setMessage("Anda belum melengkapi data diri profil, tidak dapat melihat history data pencatatan");
        builder.setCancelable(false);
        DatabaseReference dRef1 =fBase.getReference("Users").child(fUser.getUid());
        dRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    nama = snapshot.child("nama_lengkap").getValue().toString();
                    final String idcard = snapshot.child("idcard").getValue().toString();
                    final String jabatan = snapshot.child("jabatan").getValue().toString();

                    if (nama.isEmpty()){
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                constraint_history.setVisibility(View.GONE);
                            }
                        });
                        builder.create().show();
                    } else if (idcard.isEmpty()){
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                constraint_history.setVisibility(View.GONE);
                            }
                        });
                        builder.create().show();
                    } else if (jabatan.isEmpty()){
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                constraint_history.setVisibility(View.GONE);
                            }
                        });
                        builder.create().show();
                    }
                } catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_bagikanKanriban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String namaLaporan ="Laporan Kanriban-"+tanggal+"-"+nama+".xls";
                File fileXls = Environment.getExternalStorageDirectory();
                File directory =new File(fileXls.getAbsolutePath() + "/Laporan PG.32 PT.Otics Indonesia");
                directory.mkdirs();
                File file = new File(directory, namaLaporan);

                WorkbookSettings wbSettings =new WorkbookSettings();
                wbSettings.setLocale(new Locale("id", "ID"));
                WritableWorkbook workbook;

                try {
                    workbook = Workbook.createWorkbook(file, wbSettings);
                    WritableSheet sheet =workbook.createSheet("Sheet1", 0);

                    sheet.addCell(new Label(0, 0, "Tanggal"));
                    sheet.addCell(new Label(1,0,"No Mesin"));
                    sheet.addCell(new Label(2,0,"Jenis Tools"));
                    sheet.addCell(new Label(3,0,"Waktu Owarimono"));
                    sheet.addCell(new Label(4,0,"OK/NG"));
                    sheet.addCell(new Label(5,0,"Waktu Hatsumono"));
                    sheet.addCell(new Label(6,0,"OK/NG"));
                    sheet.addCell(new Label(7,0,"Jumlah Counter"));
                    sheet.addCell(new Label(8,0,"Alasan"));
                    sheet.addCell(new Label(9,0,"PIC"));

                    for (int i = 0; i<listCatatanModels.size(); i++){
                        sheet.addCell(new Label(0,i+1, listCatatanModels.get(i).getTgl_pencatatan()));
                        sheet.addCell(new Label(1,i+1,listCatatanModels.get(i).getNo_mesin()));
                        sheet.addCell(new Label(2,i+1, listCatatanModels.get(i).getJenis_tools()));
                        sheet.addCell(new Label(3,i+1,listCatatanModels.get(i).getWaktu_owa()));
                        sheet.addCell(new Label(4,i+1, listCatatanModels.get(i).getStatus_owa()));
                        sheet.addCell(new Label(5,i+1, listCatatanModels.get(i).getWaktu_hatsu()));
                        sheet.addCell(new Label(6,i+1, listCatatanModels.get(i).getStatus_hatsu()));
                        sheet.addCell(new Label(7,i+1, listCatatanModels.get(i).getJumlah_count()));
                        sheet.addCell(new Label(8,i+1, listCatatanModels.get(i).getAlasan_pencatatan()));
                        sheet.addCell(new Label(9,i+1, listCatatanModels.get(i).getPic()));
                    }
                    workbook.write();

                    try {
                        workbook.close();
                    } catch (WriteException e){
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }

                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext()
                            .getPackageName() + ".provider", file);
                Intent berbagiLaporan =new Intent(Intent.ACTION_SEND);
                berbagiLaporan.setType("text/plain");
                berbagiLaporan.putExtra(Intent.EXTRA_STREAM, uri);
                berbagiLaporan.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(berbagiLaporan, "Berbagai Laporan Pencatatan Dengan"));

            }
        });

        et_cariPakeTahun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                View layout_tahun = getLayoutInflater().inflate(R.layout.month_year_picker_dialog, null);
                NumberPicker yearPicker = layout_tahun.findViewById(R.id.picker_year);

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);

                yearPicker.setMinValue(1900);
                yearPicker.setMaxValue(3500);
                yearPicker.setValue(year);

                builder.setView(layout_tahun);
                builder.setCancelable(false);
                builder.setPositiveButton(Html.fromHtml("<font color='#FF4081'>OK</font>"),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String ambil_tahun = String.valueOf(yearPicker.getValue());
                        et_cariPakeTahun.setText(ambil_tahun);
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#FF4081'>Batal</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });

        et_cariPakeBulan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                View layout_bulan = getLayoutInflater().inflate(R.layout.month_year_picker_dialog, null);
                TextView tv_monthYearDialog = layout_bulan.findViewById(R.id.tv_monthYearDialog);
                NumberPicker monthPicker = layout_bulan.findViewById(R.id.picker_month);
                NumberPicker yearPicker = layout_bulan.findViewById(R.id.picker_year);

                monthPicker.setVisibility(View.VISIBLE);
                yearPicker.setVisibility(View.GONE);
                tv_monthYearDialog.setText("Bulan");

                Calendar cal = Calendar.getInstance();
                int month = cal.get(Calendar.MONTH);

                monthPicker.setMinValue(1);
                monthPicker.setMaxValue(12);
                monthPicker.setValue(month +1);

                builder.setView(layout_bulan);
                builder.setCancelable(false);
                builder.setPositiveButton(Html.fromHtml("<font color='#FF4081'>OK</font>"),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("cek1", String.valueOf(monthPicker.getValue()));
                        final String ambil_bulan = String.valueOf(monthPicker.getValue());

                        SimpleDateFormat sdf = new SimpleDateFormat("MM", new Locale("in", "ID"));
                        et_cariPakeBulan.setText(ambil_bulan);

                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#FF4081'>Batal</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });

        et_cariPakeTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i, i1, i2);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("in", "ID"));
                        et_cariPakeTanggal.setText(sdf.format(calendar.getTime()));
                    }
                };

                new DatePickerDialog(getContext(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        return view;
    }

    public void filterDataHistoryPerhari(String filter){
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCatatanModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ListCatatanModel model =ds.getValue(ListCatatanModel.class);
                    if (model.getTgl_pencatatan().toLowerCase().contains(filter.toLowerCase())){
                        listCatatanModels.add(model);
                    }

                }
                adapter = new ListCatatanAdapter(getContext(), listCatatanModels);
                if (listCatatanModels.isEmpty()){
                    Snackbar.make(getView(), "Data History Kosong", BaseTransientBottomBar.LENGTH_SHORT).show();
                    constraint_history.setVisibility(View.GONE);
                } else {
                    constraint_history.setVisibility(View.VISIBLE);
                    rv_history.setAdapter(adapter);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadDataPencatatan(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCatatanModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ListCatatanModel model = ds.getValue(ListCatatanModel.class);
                    listCatatanModels.add(model);

                    adapter = new ListCatatanAdapter(getContext(), listCatatanModels);
                    rv_history.setAdapter(adapter);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        i = sp_historyCatatan.getSelectedItemPosition();
        if (i == 0) {
            filterDataHistoryPerhari(tanggal);
            rl_filterTglHistory.setVisibility(View.VISIBLE);
            constraint_filterBulan.setVisibility(View.GONE);
            constraint_filterTahun.setVisibility(View.GONE);
            et_cariPakeTanggal.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable.toString())){
                        filterDataHistoryPerhari(editable.toString());
                    }
                }
            });
        }else if (i == 1){
            filterDataHistoryPerhari(bulan);
            constraint_filterBulan.setVisibility(View.VISIBLE);
            constraint_filterTahun.setVisibility(View.GONE);
            rl_filterTglHistory.setVisibility(View.GONE);
            et_cariPakeBulan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable.toString())){
                        filterDataHistoryPerhari(editable.toString());
                    }
                }
            });


        } else {
            loadDataPencatatan();
            constraint_filterTahun.setVisibility(View.VISIBLE);
            constraint_filterBulan.setVisibility(View.GONE);
            rl_filterTglHistory.setVisibility(View.GONE);
            et_cariPakeTahun.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable.toString())){
                        filterDataHistoryPerhari(editable.toString());
                    }
                }
            });
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        loadDataPencatatan();
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity()
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity()
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Meminta izin membaca perangkat");
                builder.setMessage("Membaca dan menulis pada penyimpanan, menggunakan kamera");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Batal",null);
                builder.create().show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, REQUEST_CODE);
            }
        } else {
            Snackbar.make(getView(), "Permintaan izin telah disetujui", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] + grantResults[1] + grantResults[2]
                        == PackageManager.PERMISSION_GRANTED){
                    Snackbar.make(getView(), "Permintaan izin disetujui", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), "Permintaan izin disetujui", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
        }
    }
}