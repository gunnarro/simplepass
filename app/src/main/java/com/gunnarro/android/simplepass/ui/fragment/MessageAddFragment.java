package com.gunnarro.android.simplepass.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.Message;
import com.gunnarro.android.simplepass.exception.SimpleCredStoreApplicationException;
import com.gunnarro.android.simplepass.utility.Utility;

import java.time.LocalDateTime;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MessageAddFragment extends Fragment implements View.OnClickListener {

    // Match a not-empty string. A string with only spaces or no characters is an empty-string.
    public static final String HAS_TEXT_REGEX = "\\w.*+";
    // match one or more withe space
    public static final String EMPTY_TEXT_REGEX = "\\s+";

    @Inject
    public MessageAddFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_message_add);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_add, container, false);
        Message message = new Message();
        // check if this is an existing or a new message
        String messageJson = getArguments() != null ? getArguments().getString(MessageListFragment.MESSAGE_JSON_INTENT_KEY) : null;
        if (messageJson != null) {
            try {
                message = Utility.gsonMapper().fromJson(messageJson, Message.class);
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, message: %s", messageJson, message));
            } catch (Exception e) {
                Log.e("", e.toString());
                throw new SimpleCredStoreApplicationException("Application Error!", "5000", e);
            }
        }

        updateAddMessageView(view, message);
        // disable save button as default
        view.findViewById(R.id.btn_message_register_save).setEnabled(false);
        view.findViewById(R.id.btn_message_register_save).setOnClickListener(v -> {
            view.findViewById(R.id.btn_message_register_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(MessageListFragment.MESSAGE_JSON_INTENT_KEY, getMessageAsJson());
            result.putString(MessageListFragment.MESSAGE_ACTION_KEY, MessageListFragment.MESSAGE_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(MessageListFragment.MESSAGE_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new item intent");
            returnToMessageList();
        });

        view.findViewById(R.id.btn_message_register_delete).setOnClickListener(v -> {
            view.findViewById(R.id.btn_message_register_delete).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(MessageListFragment.MESSAGE_JSON_INTENT_KEY, getMessageAsJson());
            result.putString(MessageListFragment.MESSAGE_ACTION_KEY, MessageListFragment.MESSAGE_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(MessageListFragment.MESSAGE_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new item intent");
            returnToMessageList();
        });

        view.findViewById(R.id.btn_message_register_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_message_register_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to message list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_message_list));
            returnToMessageList();
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void returnToMessageList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, MessageListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void updateAddMessageView(View view, Message message) {
        TextView id = view.findViewById(R.id.message_entity_id);
        id.setText(String.valueOf(message.getId()));

        TextView userId = view.findViewById(R.id.message_user_id);
        userId.setText(String.valueOf(message.getFkUserId()));

        EditText createdDateView = view.findViewById(R.id.message_created_date);
        createdDateView.setText(Utility.formatDateTime(message.getCreatedDate()));

        EditText lastModifiedDateView = view.findViewById(R.id.message_last_modified_date);
        lastModifiedDateView.setText(Utility.formatDateTime(message.getLastModifiedDate()));

        EditText tagView = view.findViewById(R.id.message_tag);
        tagView.setText(message.getTag());
        tagView.addTextChangedListener(createEmptyTextValidator(tagView, HAS_TEXT_REGEX, "Can not be empty!"));
        tagView.requestFocus();

        EditText contentView = view.findViewById(R.id.message_content);
        contentView.addTextChangedListener(createEmptyTextValidator(contentView, HAS_TEXT_REGEX, "Can not be empty!"));
        contentView.setText(message.getContent());
        contentView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // means that user is finished to type text, the check if save button should be enabled
                view.findViewById(R.id.btn_message_register_save).setEnabled(isInputFormDataValid());
            }
        });

        // hide fields if this is a new
        if (message.getId() == null) {
            view.findViewById(R.id.message_created_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.message_last_modified_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.btn_message_register_delete).setVisibility(View.GONE);
        } else {
            // change button icon to from add new to save
            ((MaterialButton)view.findViewById(R.id.btn_message_register_save)).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_black_24dp));
        }
        Log.d(Utility.buildTag(getClass(), "updateAddMessageView"), String.format("updated %s ", message));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for any permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.btn_message_register_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.btn_message_register_cancel) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to message list view");
        }
    }

    private String getMessageAsJson() {
        Message message = new Message();
        TextView idView = requireView().findViewById(R.id.message_entity_id);
        Log.d("getMessageAsJson", "idView=" + idView.getText() + ", is=" + (idView.getText().length()));
        message.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        TextView userIdView = requireView().findViewById(R.id.message_user_id);
        message.setFkUserId(Utility.isInteger(userIdView.getText().toString()) ? Long.parseLong(userIdView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.message_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            message.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.message_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            message.setLastModifiedDate(lastModifiedDateTime);
        }

        EditText tagView = requireView().findViewById(R.id.message_tag);
        message.setTag(tagView.getText().toString());

        EditText contentView = requireView().findViewById(R.id.message_content);
        message.setContent(contentView.getText().toString());

        try {
            return Utility.gsonMapper().toJson(message);
        } catch (Exception e) {
            Log.e("getCredentialsAsJson", e.toString());
            throw new SimpleCredStoreApplicationException("unable to parse object to json!,", "5000", e);
        }
    }

    /**
     *
     */
    private TextWatcher createEmptyTextValidator(EditText editText, String regexp, String validationErrorMsg) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().matches(regexp)) {
                    editText.setError(validationErrorMsg);
                }
            }
        };
    }

    private boolean isInputFormDataValid() {
        return true;
    }
}
