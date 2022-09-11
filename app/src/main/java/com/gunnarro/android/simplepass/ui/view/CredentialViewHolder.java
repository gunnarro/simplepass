package com.gunnarro.android.simplepass.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.Credential;

public class CredentialViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final TextView credentialHeaderView;
    @NonNull
    private final TextView usernameView;
    @NonNull
    private final TextView passwordView;
    @NonNull
    private final TextView passwordValidationView;

    private CredentialViewHolder(@NonNull View itemView) {
        super(itemView);
        credentialHeaderView = itemView.findViewById(R.id.credential_header_txt);
        usernameView = itemView.findViewById(R.id.credential_username_txt);
        passwordView = itemView.findViewById(R.id.credential_password_txt);
        passwordValidationView = itemView.findViewById(R.id.credential_password_validation_txt);
    }

    public static CredentialViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_credential_item_material, parent, false);
        return new CredentialViewHolder(view);
    }

    public void bindListLine(Credential credential) {
        credentialHeaderView.setText(credential.getSystem());
        usernameView.setText(credential.getUsername());
        passwordView.setText(credential.getPassword().getValue());
        passwordValidationView.setText(credential.getPasswordStatus());
    }
}
