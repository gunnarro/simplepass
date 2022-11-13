package com.gunnarro.android.simplepass.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.ui.view.CredentialViewHolder;
import com.gunnarro.android.simplepass.utility.Utility;

public class CredentialListAdapter extends ListAdapter<Credential, CredentialViewHolder> implements AdapterView.OnItemClickListener {

    public CredentialListAdapter(@NonNull DiffUtil.ItemCallback<Credential> diffCallback) {
        super(diffCallback);
        Log.d("CredentialListAdapter", "init");
    }


    @NonNull
    @Override
    public CredentialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CredentialViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(CredentialViewHolder holder, int position) {
        Credential current = getItem(position);
        if (current != null) {
            holder.bindListLine(current);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(Utility.buildTag(getClass(), "onItemClick"), "position: " + position + ", id: " + id);
        notifyItemRangeRemoved(position, 1);
    }

    /**
     *
     */
    public static class CredentialDiff extends DiffUtil.ItemCallback<Credential> {
        @Override
        public boolean areItemsTheSame(@NonNull Credential oldItem, @NonNull Credential newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Credential oldItem, @NonNull Credential newItem) {
            return oldItem.equals(newItem);
        }
    }
}
