package com.otics.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ListCatatanModel implements Parcelable {

    String id_transaksi, alasan_pencatatan, jenis_tools, jumlah_count
            , no_mesin, pic, status_owa, status_hatsu
            , tgl_pencatatan, waktu_owa, waktu_hatsu;

    public ListCatatanModel() {
    }

    public ListCatatanModel(String id_transaksi, String alasan_pencatatan, String jenis_tools, String jumlah_count, String no_mesin, String pic, String status_owa, String status_hatsu, String tgl_pencatatan, String waktu_owa, String waktu_hatsu) {
        this.id_transaksi = id_transaksi;
        this.alasan_pencatatan = alasan_pencatatan;
        this.jenis_tools = jenis_tools;
        this.jumlah_count = jumlah_count;
        this.no_mesin = no_mesin;
        this.pic = pic;
        this.status_owa = status_owa;
        this.status_hatsu = status_hatsu;
        this.tgl_pencatatan = tgl_pencatatan;
        this.waktu_owa = waktu_owa;
        this.waktu_hatsu = waktu_hatsu;
    }

    protected ListCatatanModel(Parcel in) {
        id_transaksi = in.readString();
        alasan_pencatatan = in.readString();
        jenis_tools = in.readString();
        jumlah_count = in.readString();
        no_mesin = in.readString();
        pic = in.readString();
        status_owa = in.readString();
        status_hatsu = in.readString();
        tgl_pencatatan = in.readString();
        waktu_owa = in.readString();
        waktu_hatsu = in.readString();
    }

    public static final Creator<ListCatatanModel> CREATOR = new Creator<ListCatatanModel>() {
        @Override
        public ListCatatanModel createFromParcel(Parcel in) {
            return new ListCatatanModel(in);
        }

        @Override
        public ListCatatanModel[] newArray(int size) {
            return new ListCatatanModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_transaksi);
        parcel.writeString(alasan_pencatatan);
        parcel.writeString(jenis_tools);
        parcel.writeString(jumlah_count);
        parcel.writeString(no_mesin);
        parcel.writeString(pic);
        parcel.writeString(status_owa);
        parcel.writeString(status_hatsu);
        parcel.writeString(tgl_pencatatan);
        parcel.writeString(waktu_owa);
        parcel.writeString(waktu_hatsu);
    }

    public String getId_transaksi() {
        return id_transaksi;
    }

    public void setId_transaksi(String id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public String getAlasan_pencatatan() {
        return alasan_pencatatan;
    }

    public void setAlasan_pencatatan(String alasan_pencatatan) {
        this.alasan_pencatatan = alasan_pencatatan;
    }

    public String getJenis_tools() {
        return jenis_tools;
    }

    public void setJenis_tools(String jenis_tools) {
        this.jenis_tools = jenis_tools;
    }

    public String getJumlah_count() {
        return jumlah_count;
    }

    public void setJumlah_count(String jumlah_count) {
        this.jumlah_count = jumlah_count;
    }

    public String getNo_mesin() {
        return no_mesin;
    }

    public void setNo_mesin(String no_mesin) {
        this.no_mesin = no_mesin;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getStatus_owa() {
        return status_owa;
    }

    public void setStatus_owa(String status_owa) {
        this.status_owa = status_owa;
    }

    public String getStatus_hatsu() {
        return status_hatsu;
    }

    public void setStatus_hatsu(String status_hatsu) {
        this.status_hatsu = status_hatsu;
    }

    public String getTgl_pencatatan() {
        return tgl_pencatatan;
    }

    public void setTgl_pencatatan(String tgl_pencatatan) {
        this.tgl_pencatatan = tgl_pencatatan;
    }

    public String getWaktu_owa() {
        return waktu_owa;
    }

    public void setWaktu_owa(String waktu_owa) {
        this.waktu_owa = waktu_owa;
    }

    public String getWaktu_hatsu() {
        return waktu_hatsu;
    }

    public void setWaktu_hatsu(String waktu_hatsu) {
        this.waktu_hatsu = waktu_hatsu;
    }

    public static Creator<ListCatatanModel> getCREATOR() {
        return CREATOR;
    }
}
