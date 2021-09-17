package com.otics.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PreviewImages extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    Query query = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("Users").orderByChild("Uid").equalTo(firebaseUser.getUid());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image_users);

        ImageView iv_fotoProfil = findViewById(R.id.iv_fotoProfilDetail);
        ProgressBar pb_loadFoto = findViewById(R.id.pb_loadFoto);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String nama = "" + ds.child("nama_lengkap").getValue();
                    String foto = "" + ds.child("images_url").getValue();

                    getSupportActionBar().setTitle(nama);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    if (foto.isEmpty()){
                        Glide.with(PreviewImages.this).load(R.drawable.ic_baseline_account_circle_120)
                                .centerCrop().into(iv_fotoProfil);
                    } else {
                        Glide.with(PreviewImages.this).load(foto)
                                .centerCrop().into(iv_fotoProfil);
                    }
                }
                pb_loadFoto.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
