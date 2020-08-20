package com.example.banner;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BannerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected abstract ArrayList<? extends IBannerData> getDataList();
}
