package com.otics.myapplication.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.otics.myapplication.Pencatatan;
import com.otics.myapplication.R;
import com.otics.myapplication.model.ListCatatanModel;

import java.util.List;

public class ListCatatanAdapter extends RecyclerView.Adapter<ListCatatanAdapter.MyHolder> {

    Context context;
    List<ListCatatanModel> listCatatanModels;
    int tekan = -1;

    public ListCatatanAdapter(Context context, List<ListCatatanModel> listCatatanModels) {
        this.context = context;
        this.listCatatanModels = listCatatanModels;
    }

    @NonNull
    @Override
    public ListCatatanAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListCatatanAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {

        final String id_transaksi = listCatatanModels.get(position).getId_transaksi();
        final String tgl_catat =listCatatanModels.get(position).getTgl_pencatatan();
        final String nomc = listCatatanModels.get(position).getNo_mesin();
        final String jenis_tools = listCatatanModels.get(position).getJenis_tools();
        final String waktu_owa = listCatatanModels.get(position).getWaktu_owa();
        final String status_owa = listCatatanModels.get(position).getStatus_owa();
        final String waktu_hatsu = listCatatanModels.get(position).getWaktu_hatsu();
        final String status_hatsu = listCatatanModels.get(position).getStatus_hatsu();
        final String jumlah_counter = listCatatanModels.get(position).getJumlah_count();
        final String alasan = listCatatanModels.get(position).getAlasan_pencatatan();
        final String pic = listCatatanModels.get(position).getPic();

        holder.tv_tglCatat.setText(tgl_catat);
        holder.tv_nomc.setText(nomc);
        holder.tv_jenisTools.setText(jenis_tools);
        holder.tv_waktuOwa.setText(waktu_owa);
        holder.tv_waktuHatsu.setText(waktu_hatsu);
        holder.tv_statusOwa.setText(status_owa);
        holder.tv_statusHatsu.setText(status_hatsu);
        holder.tv_jumlahCounter.setText(jumlah_counter);
        holder.tv_alasan.setText(alasan);
        holder.tv_pic.setText(pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tekan = position;
                notifyDataSetChanged();
                String[] pilihan = {"Hapus Data", "Edit Data"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose Action");
                builder.setItems(pilihan, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle("Hapus data");
                            builder1.setMessage("Apakah anda yakin ingin menghapus data?");
                            builder1.setPositiveButton("Hapus Data", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference dRef = FirebaseDatabase.getInstance("https://pg-32-5fe27-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                            .getReference("Pencatatan").child(id_transaksi);
                                    dRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            builder1.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            builder1.create().show();
                        } else {
//                            ListCatatanModel catatanModel = new ListCatatanModel(id_transaksi, tgl_catat, nomc,
//                                    jenis_tools, waktu_owa, status_owa, waktu_hatsu, status_hatsu,
//                                    jumlah_counter, alasan, pic);

                            Intent intent = new Intent(context, Pencatatan.class);
                            intent.putExtra("id_transaksi", id_transaksi);
                            intent.putExtra("tgl_catat", tgl_catat);
                            intent.putExtra("nomc", nomc);
                            intent.putExtra("jenis_tools", jenis_tools);
                            intent.putExtra("waktu_owa", waktu_owa);
                            intent.putExtra("status_owa", status_owa);
                            intent.putExtra("waktu_hatsu", waktu_hatsu);
                            intent.putExtra("status_hatsu", status_hatsu);
                            intent.putExtra("jumlah_count", jumlah_counter);
                            intent.putExtra("alasan", alasan);
                            intent.putExtra("pic", pic);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });

        if (tekan == position){
            holder.tv_tglCatat.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_nomc.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_jenisTools.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_waktuOwa.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_statusOwa.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_waktuHatsu.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_statusHatsu.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_jumlahCounter.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_alasan.setBackgroundResource(R.drawable.bg_history_ditekan);
            holder.tv_pic.setBackgroundResource(R.drawable.bg_history_ditekan);
        } else {
            holder.tv_tglCatat.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_nomc.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_jenisTools.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_waktuOwa.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_statusOwa.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_waktuHatsu.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_statusHatsu.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_jumlahCounter.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_alasan.setBackgroundResource(R.drawable.bg_history_table_content);
            holder.tv_pic.setBackgroundResource(R.drawable.bg_history_table_content);
        }

    }

    @Override
    public int getItemCount() {
        return listCatatanModels.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_tglCatat, tv_nomc, tv_jenisTools, tv_waktuOwa, tv_statusOwa
                , tv_waktuHatsu, tv_statusHatsu, tv_jumlahCounter, tv_alasan, tv_pic;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tv_tglCatat = itemView.findViewById(R.id.tv_tglCatat);
            tv_nomc = itemView.findViewById(R.id.tv_nomc);
            tv_jenisTools = itemView.findViewById(R.id.tv_jenisTools);
            tv_waktuOwa = itemView.findViewById(R.id.tv_waktuOwa);
            tv_statusOwa = itemView.findViewById(R.id.tv_statusOwa);
            tv_waktuHatsu = itemView.findViewById(R.id.tv_waktuHatsu);
            tv_statusHatsu = itemView.findViewById(R.id.tv_statusHatsu);
            tv_jumlahCounter = itemView.findViewById(R.id.tv_jumlahCounter);
            tv_alasan = itemView.findViewById(R.id.tv_alasan);
            tv_pic = itemView.findViewById(R.id.tv_pic);
        }
    }
}
