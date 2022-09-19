package com.gunnarro.android.simplepass.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.gunnarro.android.simplepass.ui.adapter.CredentialListAdapter;
import com.gunnarro.android.simplepass.ui.swipe.SwipeCallback;
import com.gunnarro.android.simplepass.ui.view.CredentialViewModel;
import com.gunnarro.android.simplepass.utility.Utility;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CredentialStoreListFragment extends Fragment {
    public static final String CREDENTIALS_JSON_INTENT_KEY = "credentials_as_json";
    public static final String CREDENTIALS_REQUEST_KEY = "2";
    public static final String CREDENTIALS_ACTION_KEY = "12";
    public static final String CREDENTIALS_ACTION_SAVE = "credentials_save";
    public static final String CREDENTIALS_ACTION_DELETE = "credentials_delete";
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private CredentialViewModel credentialsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                        credentialsViewModel.save(credential);
                        // Toast.makeText(getContext(), "saved credentials " + credential.getWorkdayDate(), Toast.LENGTH_SHORT).show();
                    } else if (bundle.getString(CREDENTIALS_ACTION_KEY).equals(CREDENTIALS_ACTION_DELETE)) {
                        credentialsViewModel.delete(credential);
                        Toast.makeText(getContext(), "deleted credential " + credential.getUsername(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(Utility.buildTag(getClass(), "onFragmentResult"), "unknown action: " + (bundle.getString(CREDENTIALS_ACTION_KEY)));
                        Toast.makeText(getContext(), "unknown action " + bundle.getString(CREDENTIALS_ACTION_KEY), Toast.LENGTH_SHORT).show();
                    }
                    Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, credentials: %s", bundle.getString(CREDENTIALS_ACTION_KEY), credential));
                } catch (JsonProcessingException e) {
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

        // Add an observer on the LiveData returned by getCredentialLiveData.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground. Update the cached copy of the credentials in the adapter.
        credentialsViewModel.getCredentialLiveData().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.add_credential);
        addButton.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            try {
                arguments.putString(CREDENTIALS_JSON_INTENT_KEY, Utility.getJsonMapper().writeValueAsString(new Credential()));
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
        enableSwipeToLeftAndDeleteItem(view.findViewById(R.id.list_layout), recyclerView);
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
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     *
     */
    private void enableSwipeToLeftAndDeleteItem(ConstraintLayout constraintLayout, RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(getContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAbsoluteAdapterPosition();
                //   final Credential credential = credentialsViewModel.getCredentialLiveData().getValue().get(position);
                credentialsViewModel.delete(credentialsViewModel.getCredentialLiveData().getValue().get(position));
                //  mAdapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(constraintLayout, "Credential was removed from the list.", Snackbar.LENGTH_LONG);
                /*
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.restoreItem(credential, position);
                        recyclerView.scrollToPosition(position);
                    }
                });
                 */
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
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
}
