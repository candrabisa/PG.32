package com.otics.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.otics.myapplication.fragment.FragmentBeranda;
import com.otics.myapplication.fragment.FragmentHistory;
import com.otics.myapplication.fragment.FragmentProfil;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        if (savedInstanceState == null){
            FragmentBeranda fragmentBeranda = new FragmentBeranda();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragmentBeranda)
                    .commit();
        }

        BottomNavigationView bottomNavigationView =findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment pilihFragment = null;
            switch (item.getItemId()){
                case R.id.navBeranda:
                    pilihFragment = new FragmentBeranda();
                    break;
                case R.id.navHistory:
                    pilihFragment = new FragmentHistory();
                    break;
                case R.id.navAkun:
                    pilihFragment = new FragmentProfil();
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, pilihFragment)
                    .commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        if (bottomNavigationView.getSelectedItemId() == R.id.navBeranda) {
            super.onBackPressed();
            finish();
//            System.exit(0);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navBeranda);
        }
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Meminta izin membaca perangkat");
                builder.setMessage("Membaca dan menulis pada penyimpanan, menggunakan kamera");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Batal",null);
                builder.create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, REQUEST_CODE);
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] + grantResults[1] + grantResults[2]
                        == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "Permintaan izin disetujui", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(MainActivity.this, "Permintaan izin disetujui", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permintaan izin ditolak", Toast.LENGTH_SHORT).show();

//                    Snackbar.make(getApplicationContext(), "Permintaan izin disetujui",BaseTransientBottomBar.LENGTH_SHORT).show();
                }
        }
    }
}