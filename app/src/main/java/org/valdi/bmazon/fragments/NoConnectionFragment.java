package org.valdi.bmazon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import org.valdi.bmazon.R;
import org.valdi.bmazon.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoConnectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoConnectionFragment extends Fragment {
    private static final String TAG = NoConnectionFragment.class.getSimpleName();
    private DrawerLayout drawer;
    private Runnable action;

    public NoConnectionFragment() {
        // Required empty public constructor
    }

    public NoConnectionFragment(Runnable action) {
        // Required empty public constructor
        this.action = action;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param action the action to execute on retry
     * @return A new instance of fragment CartFragment.
     */
    public static NoConnectionFragment newInstance(Runnable action) {
        return new NoConnectionFragment(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.drawer = requireActivity().findViewById(R.id.navigation_drawer);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_no_connection, container, false);
        final Button button = view.findViewById(R.id.retry);
        button.setOnClickListener(v -> {
            if (Util.isNetworkConnected(requireActivity()) && this.action != null) {
                //requireActivity().onBackPressed();
                this.action.run();
            }
        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (this.drawer != null) {
                this.drawer.open();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}