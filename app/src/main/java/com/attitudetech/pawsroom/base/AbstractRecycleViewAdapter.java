package com.attitudetech.pawsroom.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class AbstractRecycleViewAdapter<T, B extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> listObjects;
    protected View view;
    Context context;
    protected B binding;

    protected abstract int layoutResourceForItem(int position);

    protected abstract void fillHolder(RecyclerView.ViewHolder abstractHolder, int position);

    protected abstract RecyclerView.ViewHolder inflateView(B binding, int position);

    public AbstractRecycleViewAdapter(List<T> listObjects) {
        this.listObjects = listObjects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutResourceForItem(viewType), viewGroup, false);
        return inflateView(binding, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder abstractHolder, int position) {
        fillHolder(abstractHolder, position);
    }

    @Override
    public int getItemCount() {
        return listObjects.size();
    }

    protected T getItem(int position) {
        return listObjects.get(position);
    }


}
