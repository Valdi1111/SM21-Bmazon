package org.valdi.bmazon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.orders.OrderAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.order.Order;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {
    private static final String TAG = OrdersFragment.class.getSimpleName();
    private CompositeSubscription subscriptions;
    private OrderAdapter adapter;

    private DrawerLayout drawer;
    private TextView noOrders;

    public OrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrdersFragment.
     */
    public static OrdersFragment newInstance() {
        return new OrdersFragment();
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
        toolbar.setTitle(R.string.nav_orders);
        this.drawer = requireActivity().findViewById(R.id.navigation_drawer);
        final NavigationView navigation = requireActivity().findViewById(R.id.navigation);
        navigation.setCheckedItem(R.id.nav_orders);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_orders, container, false);
        this.noOrders = view.findViewById(R.id.order_no_orders);
        // Orders
        this.adapter = new OrderAdapter(requireContext(), this.subscriptions);
        final RecyclerView products = view.findViewById(R.id.orders_list);
        products.setAdapter(this.adapter);
        // Load all
        this.loadOrders();
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

    private void loadOrders() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .getOrders()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleOrders, this::handleError)
        );
    }

    private void handleOrders(final List<Order> orders) {
        if (orders.size() > 0) {
            this.noOrders.setVisibility(ImageView.GONE);
        }
        this.adapter.addAll(orders);
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(requireContext(), TAG, error);
    }
}