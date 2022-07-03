package org.valdi.bmazon.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.notifications.NotificationAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.Notification;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    private static final String TAG = NotificationsFragment.class.getSimpleName();
    public static final String UPDATE_KEY = "update_notifications_amount";
    private CompositeSubscription subscriptions;
    private NotificationAdapter adapter;

    private DrawerLayout drawer;
    private TextView noNotifications;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationsFragment.
     */
    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.subscriptions = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final MaterialToolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setTitle(R.string.nav_notifications);
        this.drawer = requireActivity().findViewById(R.id.navigation_drawer);
        final NavigationView navigation = requireActivity().findViewById(R.id.navigation);
        navigation.setCheckedItem(R.id.nav_notifications);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        this.noNotifications = view.findViewById(R.id.no_notifications);
        // Notifications
        this.adapter = new NotificationAdapter(requireContext(), this.subscriptions);
        final RecyclerView products = view.findViewById(R.id.notifications_list);
        products.setAdapter(this.adapter);
        // Load all
        this.loadNotifications();
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
        this.subscriptions.unsubscribe();
    }

    private void loadNotifications() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .getNotifications()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleNotifications, this::handleError)
        );
    }

    private void handleNotifications(final List<Notification> notifications) {
        if (notifications.size() > 0) {
            this.noNotifications.setVisibility(ImageView.GONE);
        }
        this.adapter.addAll(notifications);
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(requireContext(), TAG, error);
    }
}