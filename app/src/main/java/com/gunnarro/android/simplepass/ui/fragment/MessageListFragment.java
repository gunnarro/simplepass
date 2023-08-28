package com.gunnarro.android.simplepass.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.Message;
import com.gunnarro.android.simplepass.ui.adapter.MessageListAdapter;
import com.gunnarro.android.simplepass.ui.login.LoginActivity;
import com.gunnarro.android.simplepass.ui.swipe.SwipeCallback;
import com.gunnarro.android.simplepass.ui.view.MessageViewModel;
import com.gunnarro.android.simplepass.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MessageListFragment extends Fragment {
    public static final String MESSAGE_JSON_INTENT_KEY = "message_as_json";
    public static final String MESSAGE_REQUEST_KEY = "3";
    public static final String MESSAGE_ACTION_KEY = "13";
    public static final String MESSAGE_ACTION_SAVE = "message_save";
    public static final String MESSAGE_ACTION_DELETE = "message_delete";
    private MessageViewModel messageViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a new or existing ViewModel from the ViewModelProvider.
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        // Pick up callback from add message view
        getParentFragmentManager().setFragmentResultListener(MESSAGE_REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), "intent: " + requestKey + "json:  + " + bundle.getString(MESSAGE_JSON_INTENT_KEY));
                try {
                    Message message = Utility.gsonMapper().fromJson(bundle.getString(MESSAGE_JSON_INTENT_KEY), Message.class);
                    handleButtonActions(message, bundle.getString(MESSAGE_ACTION_KEY));
                    Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, message: %s", bundle.getString(MESSAGE_ACTION_KEY), message));
                } catch (Exception e) {
                    Log.e("", e.toString());
                    showInfoDialog(String.format("Application error!%s Error: %s%sErrorCode: 5001%sPlease report.", e.getMessage(), System.lineSeparator(), System.lineSeparator(), System.lineSeparator()), getActivity());
                }
            }
        });
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_message_list);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_message_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.message_recyclerview);
        final MessageListAdapter adapter = new MessageListAdapter(new MessageListAdapter.MessageDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Long loggedInUserId = getArguments() != null ? getArguments().getLong(LoginActivity.LOGGED_IN_USER_ID_INTENT_KEY) : 1L;
        // Add an observer on the LiveData returned by getCredentialLiveData.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground. Update the cached copy of the message in the adapter.
        messageViewModel.getMessageLiveData().observe(requireActivity(), adapter::submitList);
        FloatingActionButton addButton = view.findViewById(R.id.add_message);
        addButton.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            try {
                Message newMessage = new Message();
                newMessage.setFkUserId(loggedInUserId);
                arguments.putString(MESSAGE_JSON_INTENT_KEY, Utility.gsonMapper().toJson(newMessage));
            } catch (Exception e) {
                Log.e(Utility.buildTag(getClass(), "addBtn.setOnClickListener"), e.toString());
                showInfoDialog(String.format("Application error!\n Error: %s\nErrorCode: 5000\nPlease report.", e.getMessage()), getActivity());
            }

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, MessageAddFragment.class, arguments).setReorderingAllowed(true).commit();
        });
        // enable swipe
        enableSwipeToLeftAndDeleteItem(recyclerView);
        enableSwipeToRightAndViewItem(recyclerView);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void handleButtonActions(Message message, String action) {
        if (MESSAGE_ACTION_SAVE.equals(action)) {
            try {
                messageViewModel.save(message);
                if (message.getId() == null) {
                    showSnackbar("Added message", R.color.color_snackbar_text_add);
                } else {
                    showSnackbar("Updated message", R.color.color_snackbar_text_update);
                }
            } catch (Exception ex) {
                showInfoDialog(String.format("Application error! %s Error: %s %s Please report.", ex.getMessage(), System.lineSeparator(), System.lineSeparator()), getActivity());
            }
        } else if (MESSAGE_ACTION_DELETE.equals(action)) {
            messageViewModel.delete(message);
            showSnackbar("Deleted message", R.color.color_snackbar_text_delete);
        } else {
            Log.w(Utility.buildTag(getClass(), "onFragmentResult"), "unknown action: " + action);
            showInfoDialog(String.format("Application error! %s Unknown action: %s %s Please report.", action, System.lineSeparator(), System.lineSeparator()), getActivity());
        }
    }

    /**
     *
     */
    private void enableSwipeToLeftAndDeleteItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAbsoluteAdapterPosition();
                messageViewModel.delete(messageViewModel.getMessageLiveData().getValue().get(position));
                showSnackbar("Deleted message", R.color.color_snackbar_text_delete);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToLeftAndDeleteItem"), "enabled swipe handler for delete message list item");
    }

    /**
     *
     */
    private void enableSwipeToRightAndViewItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.RIGHT, getResources().getColor(R.color.color_bg_swipe_right, null), R.drawable.ic_add_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                openViewMessage(messageViewModel.getMessageLiveData().getValue().get(viewHolder.getAbsoluteAdapterPosition()));
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToRightAndAdd"), "enabled swipe handler for message list item");
    }

    private void openViewMessage(Message message) {
        Bundle arguments = new Bundle();
        try {
            arguments.putString(MESSAGE_JSON_INTENT_KEY, Utility.gsonMapper().toJson(message));
        } catch (Exception e) {
            Log.e(Utility.buildTag(getClass(), "openViewMessage"), e.toString());
            showInfoDialog(String.format("Application error!%s Error: %s%sErrorCode: 5002%sPlease report.", e.getMessage(), System.lineSeparator(), System.lineSeparator(), System.lineSeparator()), getActivity());
        }

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, MessageAddFragment.class, arguments).setReorderingAllowed(true).commit();
    }

    private void showSnackbar(String msg, @ColorRes int bgColor) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(requireView().findViewById(R.id.message_list_layout), msg, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(bgColor, theme));
        snackbar.show();
    }

    private void showInfoDialog(String infoMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Login failed!");
        builder.setMessage(infoMessage);
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
