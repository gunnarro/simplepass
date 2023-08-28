package com.gunnarro.android.simplepass.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.simplepass.domain.entity.Message;
import com.gunnarro.android.simplepass.ui.view.MessageViewHolder;
import com.gunnarro.android.simplepass.utility.Utility;

public class MessageListAdapter extends ListAdapter<Message, MessageViewHolder> implements AdapterView.OnItemClickListener {

    public MessageListAdapter(@NonNull DiffUtil.ItemCallback<Message> diffCallback) {
        super(diffCallback);
        Log.d("MessageListAdapter", "init");
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MessageViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message current = getItem(position);
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
    public static class MessageDiff extends DiffUtil.ItemCallback<Message> {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.equals(newItem);
        }
    }
}
