package com.gunnarro.android.simplepass.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.Credential;

public class CredentialViewHolder extends RecyclerView.ViewHolder {
    private final TextView credentialLineHeaderView;
    private final TextView credentialLine1ValueView;
    private final TextView credentialLine2ValueView;


    private CredentialViewHolder(View itemView) {
        super(itemView);
        credentialLineHeaderView = itemView.findViewById(R.id.credential_line_header);
        credentialLine1ValueView = itemView.findViewById(R.id.credential_line_1_value);
        credentialLine2ValueView = itemView.findViewById(R.id.credential_line_2_value);
    }

    public static CredentialViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_credential_item, parent, false);
        return new CredentialViewHolder(view);
    }

    public void bindListLine(Credential credential) {
        credentialLineHeaderView.setText(credential.getSystem());
        credentialLine1ValueView.setText(credential.getUsername());
        credentialLine2ValueView.setText(credential.getPassword().getValue());
    }
}
