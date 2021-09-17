package com.otics.myapplication.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.otics.myapplication.GetStarted;
import com.otics.myapplication.PreviewImages;
import com.otics.myapplication.R;
import com.otics.myapplication.SettingAccount;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;


public class FragmentProfil extends Fragment {

    EditText et_namaLengkapProfil, et_idCardProfil, et_tglJoinPT,
            et_noHPProfil, et_alamatProfil;
    Spinner sp_Jabatan, sp_departmentProfil;
    Button btn_logout, btn_simpanDataProfil;
    ImageView iv_fotoProfil, iv_btnSettings;
    LinearLayout ll_buttonEditProfil;

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fBase = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/");
    StorageReference sPrefUsers = FirebaseStorage.getInstance("gs://pg-32-5fe27.appspot.com/").getReference("Users");

    Uri lokasi_foto;
    private ActivityResultLauncher<String[]> activityResultLauncher;

    class MyLifecycleObserver implements DefaultLifecycleObserver {
        private final ActivityResultRegistry mRegistry;
        private ActivityResultLauncher<String> mGetContent;


        MyLifecycleObserver(@NonNull ActivityResultRegistry registry) {
            mRegistry = registry;
        }

        public void onCreate(@NonNull LifecycleOwner owner) {

            mGetContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Mengupload...");
                            progressDialog.show();
                            final StorageReference storageReference = sPrefUsers.child(fUser.getUid())
                                    .child(System.currentTimeMillis() + ambilExtensiGambar(uri));
                            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @SuppressLint("WrongConstant")
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String uri_foto = uri.toString();
                                            fBase.getReference("Users").child(fUser.getUid()).child("images_url").setValue(uri_foto);
                                            Snackbar.make(getView(), "Behasil upload foto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
        }

        public void selectImage() {
            mGetContent.launch("image/*");
//            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Map<String, Boolean>>() {
//                @Override
//                public void onActivityResult(Map<String, Boolean> result) {
//                    Boolean areAllGranted = true;
//                    for (Boolean b : result.values()){
//                        areAllGranted = areAllGranted && b;
//                    }
//                    if (areAllGranted){
//                        // Open the activity to select an image
//                        mGetContent.launch("image/*");
//                    }
//                }
//            });

        }
    }

    private MyLifecycleObserver mObserver;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mObserver = new MyLifecycleObserver(requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver((LifecycleObserver) mObserver);

        String[] appPerms;
        appPerms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        loadUser();

        et_namaLengkapProfil = view.findViewById(R.id.et_namaLengkapProfil);
        et_idCardProfil = view.findViewById(R.id.et_idCardProfil);
        et_tglJoinPT = view.findViewById(R.id.et_tglJoinPT);
        et_noHPProfil = view.findViewById(R.id.et_noHPProfil);
        et_alamatProfil = view.findViewById(R.id.et_alamatProfil);
        sp_Jabatan = view.findViewById(R.id.sp_Jabatan);
        sp_departmentProfil = view.findViewById(R.id.sp_departmentProfil);
        iv_fotoProfil = view.findViewById(R.id.iv_fotoProfil);
        iv_btnSettings = view.findViewById(R.id.iv_btnSettings);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_simpanDataProfil = view.findViewById(R.id.btn_simpanDataProfil);
        ll_buttonEditProfil = view.findViewById(R.id.ll_buttonEditProfil);

        //matiin edittext dan spinner
        et_namaLengkapProfil.setEnabled(false);
        et_idCardProfil.setEnabled(false);
        et_tglJoinPT.setEnabled(false);
        et_noHPProfil.setEnabled(false);
        et_alamatProfil.setEnabled(false);
        sp_departmentProfil.setEnabled(false);
        sp_Jabatan.setEnabled(false);

        //umpetin button simpan
        btn_simpanDataProfil.setVisibility(View.GONE);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Do your code from onActivityResult
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            lokasi_foto = data.getData();
                            Glide.with(getContext()).load(lokasi_foto)
                                    .centerCrop().into(iv_fotoProfil);
                        }
                    }
                });

        ll_buttonEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_namaLengkapProfil.setEnabled(true);
                et_idCardProfil.setEnabled(true);
                et_tglJoinPT.setEnabled(true);
                et_noHPProfil.setEnabled(true);
                et_alamatProfil.setEnabled(true);
                sp_departmentProfil.setEnabled(true);
                sp_Jabatan.setEnabled(true);
                btn_logout.setVisibility(View.GONE);
                btn_simpanDataProfil.setVisibility(View.VISIBLE);
            }
        });

        btn_simpanDataProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_simpanDataProfil.setVisibility(View.GONE);
                btn_logout.setVisibility(View.VISIBLE);

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Mohon menunggu...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                Query query = fBase.getReference("Users").child(fUser.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String namalengkap = et_namaLengkapProfil.getText().toString();
                        final String idcard = et_idCardProfil.getText().toString();
                        final String jabatan = sp_Jabatan.getSelectedItem().toString();
                        final String tgl_join = et_tglJoinPT.getText().toString();
                        final String nohp = et_noHPProfil.getText().toString();
                        final String department = sp_departmentProfil.getSelectedItem().toString();
                        final String alamat = et_alamatProfil.getText().toString();

                        snapshot.getRef().child("nama_lengkap").setValue(namalengkap);
                        snapshot.getRef().child("idcard").setValue(idcard);
                        snapshot.getRef().child("jabatan").setValue(jabatan);
                        snapshot.getRef().child("tgl_join_pt").setValue(tgl_join);
                        snapshot.getRef().child("nohp").setValue(nohp);
                        snapshot.getRef().child("department").setValue(department);
                        snapshot.getRef().child("alamat").setValue(alamat);

                        et_namaLengkapProfil.setEnabled(false);
                        et_idCardProfil.setEnabled(false);
                        et_tglJoinPT.setEnabled(false);
                        et_noHPProfil.setEnabled(false);
                        et_alamatProfil.setEnabled(false);
                        sp_departmentProfil.setEnabled(false);
                        sp_Jabatan.setEnabled(false);

                        btn_logout.setVisibility(View.VISIBLE);
                        btn_simpanDataProfil.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        iv_fotoProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pilihan[] = {"Ganti Foto", "Lihat Foto"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Item");
                builder.setItems(pilihan, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
//                            mObserver.selectImage();

                            mObserver.selectImage();

                        } else {
                            startActivity(new Intent(getContext(), PreviewImages.class));
                        }
                    }
                });
                builder.show();
            }
        });
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);

                String formatTgl = "dd-MM-yyyy";
                SimpleDateFormat sdf =new SimpleDateFormat(formatTgl, Locale.US);
                et_tglJoinPT.setText(sdf.format(calendar.getTime()));
            }
        };

        et_tglJoinPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), GetStarted.class));
                getActivity().finish();
            }
        });

        iv_btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingAccount.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        return view;
    }

    String ambilExtensiGambar(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void loadUser(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Query query = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").orderByChild("Uid").equalTo(fUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    final String nama = "" + ds.child("nama_lengkap").getValue();
                    final String idcard = "" +ds.child("idcard").getValue();
                    final String jabatan = "" + ds.child("jabatan").getValue();
                    final String tgl_joinPT = "" + ds.child("tgl_join_pt").getValue();
                    final String department = "" +ds.child("department").getValue();
                    final String nohp = ""+ds.child("nohp").getValue();
                    final String alamat = "" + ds.child("alamat").getValue();
                    final String foto = "" +ds.child("images_url").getValue();

                    if (nama.isEmpty()){
                        et_namaLengkapProfil.setHint("Belum diisi");
                    } else {
                        et_namaLengkapProfil.setText(nama);
                    }

                    if (idcard.isEmpty()){
                        et_idCardProfil.setHint("Belum diisi");
                    } else {
                        et_idCardProfil.setText(idcard);
                    }

                    if (tgl_joinPT.isEmpty()) {
                        et_tglJoinPT.setHint("Belum diisi");
                    } else {
                        et_tglJoinPT.setText(tgl_joinPT);
                    }

                    if (nohp.isEmpty()){
                        et_noHPProfil.setHint("Belum diisi");
                    } else {
                        et_noHPProfil.setText(nohp);
                    }

                    if (alamat.isEmpty()){
                            et_alamatProfil.setHint("Belum diisi");
                    } else {
                        et_alamatProfil.setText(alamat);
                    }


                    switch (jabatan) {
                        case "PIC":
                            sp_Jabatan.setSelection(0);
                            break;
                        case "Supervisor":
                            sp_Jabatan.setSelection(1);
                            break;
                        default:
                            break;
                    }

                    switch (department) {
                        case "PG.32 - Yanmar Machining B":
                            sp_departmentProfil.setSelection(0);
                            break;
                        case "PG.32 - Yanmar Machining C":
                            sp_departmentProfil.setSelection(1);
                            break;
                        case "PG.32 - Yanmar Machining D":
                            sp_departmentProfil.setSelection(2);
                            break;
                        case "PG.32 - Yanmar Packing":
                            sp_departmentProfil.setSelection(3);
                            break;
                        case "PG.32 - Suzuki 09j":
                            sp_departmentProfil.setSelection(4);
                            break;
                        case "PG.32 - Suzuki 22j":
                            sp_departmentProfil.setSelection(5);
                            break;
                        case "PG.32 - Lifter Valve":
                            sp_departmentProfil.setSelection(6);
                            break;
                        default:
                            break;
                    }

                    if (foto.equals("")){
                        Glide.with(getContext()).load(R.drawable.ic_baseline_account_circle_120)
                                .centerCrop().into(iv_fotoProfil);
                    } else {
                        Glide.with(getContext()).load(foto).centerCrop().into(iv_fotoProfil);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}