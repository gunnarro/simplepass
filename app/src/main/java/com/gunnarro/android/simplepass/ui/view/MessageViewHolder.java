package com.gunnarro.android.simplepass.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.Message;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final TextView messageTagHeaderView;
    @NonNull
    private final TextView messageContentView;


    private MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageTagHeaderView = itemView.findViewById(R.id.message_header_txt);
        messageContentView = itemView.findViewById(R.id.message_content);
    }

    public static MessageViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_message_item, parent, false);
        return new MessageViewHolder(view);
    }

    public void bindListLine(@NonNull Message message) {
        messageTagHeaderView.setText(message.getTag());
        messageContentView.setText(message.getContent());
    }
}
