package com.attitudetech.pawsroom.base;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class AbstractRecyclerViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public T binding;

    public AbstractRecyclerViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
