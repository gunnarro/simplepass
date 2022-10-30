package com.gunnarro.android.simplepass.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.exception.SimpleCredStoreApplicationException;
import com.gunnarro.android.simplepass.ui.adapter.CredentialListAdapter;
import com.gunnarro.android.simplepass.ui.login.LoginActivity;
import com.gunnarro.android.simplepass.ui.swipe.SwipeCallback;
import com.gunnarro.android.simplepass.ui.view.CredentialViewModel;
import com.gunnarro.android.simplepass.utility.Utility;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CredentialListFragment extends Fragment {
    public static final String CREDENTIALS_JSON_INTENT_KEY = "credentials_as_json";
    public static final String CREDENTIALS_REQUEST_KEY = "2";
    public static final String CREDENTIALS_ACTION_KEY = "12";
    public static final String CREDENTIALS_ACTION_SAVE = "credentials_save";
    public static final String CREDENTIALS_ACTION_DELETE = "credentials_delete";
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private CredentialViewModel credentialsViewModel;
    private Long loggedInUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a new or existing ViewModel from the ViewModelProvider.
        credentialsViewModel = new ViewModelProvider(this).get(CredentialViewModel.class);
        // Pick up callback from add credentials view
        getParentFragmentManager().setFragmentResultListener(CREDENTIALS_REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), "intent: " + requestKey + "json:  + " + bundle.getString(CREDENTIALS_JSON_INTENT_KEY));
                try {
                    Credential credential = mapper.readValue(bundle.getString(CREDENTIALS_JSON_INTENT_KEY), Credential.class);
                    if (bundle.getString(CREDENTIALS_ACTION_KEY).equals(CREDENTIALS_ACTION_SAVE)) {
                        try {
                            credentialsViewModel.save(credential);
                            if (credential.getId() == null) {
                                showSnackbar("Added credential");
                            } else {
                                showSnackbar("Updated credential");
                            }
                        } catch (SimpleCredStoreApplicationException ex) {
                            Toast.makeText(getContext(), ex.getErrorCode(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (bundle.getString(CREDENTIALS_ACTION_KEY).equals(CREDENTIALS_ACTION_DELETE)) {
                        credentialsViewModel.delete(credential);
                        showSnackbar("Deleted credential");
                    } else {
                        Log.w(Utility.buildTag(getClass(), "onFragmentResult"), "unknown action: " + (bundle.getString(CREDENTIALS_ACTION_KEY)));
                        Toast.makeText(getContext(), "unknown action " + bundle.getString(CREDENTIALS_ACTION_KEY), Toast.LENGTH_SHORT).show();
                    }
                    Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, credentials: %s", bundle.getString(CREDENTIALS_ACTION_KEY), credential));
                } catch (Exception e) {
                    Log.e("", e.toString());
                    throw new RuntimeException("Application Error: " + e);
                }
            }
        });
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_credential_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.credential_recyclerview);
        final CredentialListAdapter adapter = new CredentialListAdapter(getParentFragmentManager(), new CredentialListAdapter.CredentialDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setOnClickListener(v -> Log.d("", "clicked on list item...."));

        loggedInUserId = getArguments() != null ? getArguments().getLong(LoginActivity.LOGGED_IN_USER_ID_INTENT_KEY) : 1L;
        // Add an observer on the LiveData returned by getCredentialLiveData.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground. Update the cached copy of the credentials in the adapter.
        credentialsViewModel.getCredentialLiveData().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.add_credential);
        addButton.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            try {
                Credential newCredential = new Credential();
                newCredential.setFkUserId(loggedInUserId);
                arguments.putString(CREDENTIALS_JSON_INTENT_KEY, Utility.getJsonMapper().writeValueAsString(newCredential));
            } catch (JsonProcessingException e) {
                Log.e(Utility.buildTag(getClass(), "addBtn.setOnClickListener"), e.toString());
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, CredentialAddFragment.class, arguments)
                    .setReorderingAllowed(true)
                    .commit();
        });
        // enable swipe
        enableSwipeToLeftAndDeleteItem(recyclerView);
        enableSwipeToRightAndViewItem(recyclerView);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //loggedInUserId = getArguments().getLong(LoginActivity.LOGGED_IN_USER_ID_INTENT_KEY);
        //Log.d("onViewCreated", "args" + getArguments().getBundle(LoginActivity.LOGGED_IN_USER_ID_INTENT_KEY));
    }

    /*
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_list) {
           Log.d("onOptionsItemSelected", "sort list");
        }
        return super.onOptionsItemSelected(item);
    }
 */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     *
     */
    private void enableSwipeToLeftAndDeleteItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(getContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAbsoluteAdapterPosition();
                //   final Credential credential = credentialsViewModel.getCredentialLiveData().getValue().get(position);
                credentialsViewModel.delete(credentialsViewModel.getCredentialLiveData().getValue().get(position));
                showSnackbar("Deleted credential");
                //  mAdapter.removeItem(position);

                /*
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.restoreItem(credential, position);
                        recyclerView.scrollToPosition(position);
                    }
                });
                 */
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToLeftAndDeleteItem"), "enabled swipe handler for delete list item");
    }

    /**
     *
     */
    private void enableSwipeToRightAndViewItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(getContext(), ItemTouchHelper.RIGHT, getResources().getColor(R.color.color_bg_swipe_right, null), R.drawable.ic_add_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                openViewCredential(credentialsViewModel.getCredentialLiveData().getValue().get(viewHolder.getAbsoluteAdapterPosition()));
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToRightAndAdd"), "enabled swipe handler for view list item");
    }

    private void openViewCredential(Credential credential) {
        Bundle arguments = new Bundle();
        try {
            arguments.putString(CREDENTIALS_JSON_INTENT_KEY, Utility.getJsonMapper().writeValueAsString(credential));
        } catch (JsonProcessingException e) {
            Log.e(Utility.buildTag(getClass(), "openViewCredential"), e.toString());
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, CredentialAddFragment.class, arguments)
                .setReorderingAllowed(true)
                .commit();
    }

    private void showSnackbar(String msg) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.list_layout), msg, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(R.color.color_snackbar_background, theme));
        if (msg.contains("Delete")) {
            snackbar.setTextColor(getResources().getColor(R.color.color_snackbar_text_delete, theme));
        } else if (msg.contains("Add")) {
            snackbar.setTextColor(getResources().getColor(R.color.color_snackbar_text_add, theme));
        }
        snackbar.show();
    }
}