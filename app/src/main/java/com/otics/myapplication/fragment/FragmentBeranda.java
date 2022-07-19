package com.otics.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.otics.myapplication.JadwalSholat;
import com.otics.myapplication.KelolaData;
import com.otics.myapplication.Pencatatan;
import com.otics.myapplication.R;

public class FragmentBeranda extends Fragment {


    AlertDialog.Builder builder;
    TextView tv_waktuNow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        tv_waktuNow = view.findViewById(R.id.tv_waktuNow);

        tv_waktuNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), JadwalSholat.class));
            }
        });


        view.findViewById(R.id.ll_kelolaDataHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), KelolaData.class));
            }
        });
        view.findViewById(R.id.iv_safety3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View previewImage = getLayoutInflater().inflate(R.layout.preview_image, null);
                ImageView imageView = previewImage.findViewById(R.id.iv_preview);
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(previewImage);
                builder.setTitle("Safety");
                Glide.with(getContext()).load(R.drawable.safety3)
                        .fitCenter().into(imageView);
                builder.create().show();
            }
        });

        view.findViewById(R.id.iv_safety2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View previewImage = getLayoutInflater().inflate(R.layout.preview_image, null);
                ImageView imageView = previewImage.findViewById(R.id.iv_preview);
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(previewImage);
                builder.setTitle("Safety");
                Glide.with(getContext()).load(R.drawable.safety2)
                        .fitCenter().into(imageView);
                builder.create().show();
            }
        });

        view.findViewById(R.id.iv_safety1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View previewImage = getLayoutInflater().inflate(R.layout.preview_image, null);
                ImageView imageView = previewImage.findViewById(R.id.iv_preview);
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(previewImage);
                builder.setTitle("Safety");
                Glide.with(getContext()).load(R.drawable.safety1)
                        .fitCenter().into(imageView);
                builder.create().show();
            }
        });

        view.findViewById(R.id.iv_motivasi3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View previewImage = getLayoutInflater().inflate(R.layout.preview_image, null);
                ImageView imageView = previewImage.findViewById(R.id.iv_preview);
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(previewImage);
                builder.setTitle("Motivasi");
                Glide.with(getContext()).load(R.drawable.motivasi3)
                        .fitCenter().into(imageView);
                builder.create().show();
            }
        });

        view.findViewById(R.id.iv_motivasi2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View previewImage = getLayoutInflater().inflate(R.layout.preview_image, null);
                ImageView imageView = previewImage.findViewById(R.id.iv_preview);
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(previewImage);
                builder.setTitle("Motivasi");
                Glide.with(getContext()).load(R.drawable.motivasi2)
                        .fitCenter().into(imageView);
                builder.create().show();
            }
        });

        view.findViewById(R.id.iv_motivasi1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View previewImage = getLayoutInflater().inflate(R.layout.preview_image, null);
                ImageView imageView = previewImage.findViewById(R.id.iv_preview);
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(previewImage);
                builder.setTitle("Motivasi");
                Glide.with(getContext()).load(R.drawable.motivasi1)
                        .fitCenter().into(imageView);
                builder.create().show();
            }
        });

        view.findViewById(R.id.ll_pencatatanHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Pencatatan.class));
            }
        });

        return view;
    }


}