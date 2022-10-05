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
    private final View passwordStatusView;
    @NonNull
    private final View usernameStatusView;

    private CredentialViewHolder(@NonNull View itemView) {
        super(itemView);
        credentialHeaderView = itemView.findViewById(R.id.credential_header_txt);
        usernameView = itemView.findViewById(R.id.credential_username_value);
        passwordView = itemView.findViewById(R.id.credential_password_value);
        passwordStatusView = itemView.findViewById(R.id.credential_password_status);
        usernameStatusView = itemView.findViewById(R.id.credential_username_status);
    }

    public static CredentialViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_credential_item, parent, false);
        return new CredentialViewHolder(view);
    }

    public void bindListLine(Credential credential) {
        credentialHeaderView.setText(credential.getSystem());
        usernameView.setText(credential.getUsername());
        passwordView.setText(credential.getPassword() != null ? credential.getPassword().getValue() : null);
        passwordStatusView.setBackgroundResource(mapPasswordStatusToColor(credential.getPasswordStatus()));
        usernameStatusView.setBackgroundResource(mapPasswordStatusToColor(credential.getPasswordStatus()));
    }

    private int mapPasswordStatusToColor(String status) {
        if ("STRONG".equals(status)) {
            return R.color.color_password_strength_strong;
        } else if ("GOOD".equals(status)) {
            return R.color.color_password_strength_good;
        } else if ("FAIR".equals(status)) {
            return R.color.color_password_strength_fair;
        } else if ("WEAK".equals(status)) {
            return R.color.color_password_strength_weak;
        }
        return R.color.color_password_strength_default;
    }
}
