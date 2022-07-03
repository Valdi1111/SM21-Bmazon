package org.valdi.bmazon.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.JsonObject;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.cart.CartProductAdapter;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.CartProduct;
import org.valdi.bmazon.model.Payment;
import org.valdi.bmazon.model.Shipment;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    private static final String TAG = CartFragment.class.getSimpleName();
    private CompositeSubscription subscriptions;
    private CartProductAdapter adapter;
    private TextView total;
    private MaterialAutoCompleteTextView shipment;
    private MaterialAutoCompleteTextView payment;
    private List<Shipment> shipments;
    private List<Payment> payments;

    //private MaterialToolbar toolbar;
    private DrawerLayout drawer;
    private TextView noProducts;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartFragment.
     */
    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.subscriptions = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //this.toolbar = requireActivity().findViewById(R.id.app_bar);
        //this.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        //this.toolbar.setTitle(R.string.nav_cart);
        final MaterialToolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setTitle(R.string.nav_cart);
        this.drawer = requireActivity().findViewById(R.id.navigation_drawer);
        final NavigationView navigation = requireActivity().findViewById(R.id.navigation);
        navigation.setCheckedItem(R.id.nav_cart);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_cart, container, false);
        this.noProducts = view.findViewById(R.id.no_products);
        // Products
        this.adapter = new CartProductAdapter(requireContext(), this.subscriptions, this::updateTotal, this::updateToolbar);
        final RecyclerView products = view.findViewById(R.id.products_list);
        products.setAdapter(this.adapter);
        this.total = view.findViewById(R.id.cart_total);
        final Button checkout = view.findViewById(R.id.checkout);
        checkout.setOnClickListener(v -> checkout());
        this.shipments = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.shipment = view.findViewById(R.id.shipment);
        this.payment = view.findViewById(R.id.payment);
        // Load all
        this.loadProducts();
        this.loadShipments();
        this.loadPayments();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //if (this.adapter.isSelected()) {
            //    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
            //    this.toolbar.setTitle(R.string.nav_cart);
            //    this.toolbar.getMenu().clear();
            //    return true;
            //}
            if (this.drawer != null) {
                this.drawer.open();
            }
            return true;
        }
        //if (item.getItemId() == R.id.cart_increment) {
        //    return true;
        //}
        //if (item.getItemId() == R.id.cart_decrement) {
        //    return true;
        //}
        //if (item.getItemId() == R.id.cart_delete) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.subscriptions.unsubscribe();
    }

    private void loadProducts() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .getCart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleProducts, this::handleError)
        );
    }

    private void handleProducts(final List<CartProduct> products) {
        if (products.size() > 0) {
            this.noProducts.setVisibility(ImageView.GONE);
        }
        this.adapter.addAll(products);
    }

    private void loadShipments() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .getShipments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleShipments, this::handleError)
        );
    }

    private void handleShipments(final List<Shipment> shipments) {
        this.shipments.addAll(shipments);
        final String[] items = new String[shipments.size()];
        for (int i = 0; i < shipments.size(); i++) {
            items[i] = shipments.get(i).prettyString();
        }
        this.shipment.setSimpleItems(items);
    }

    private void loadPayments() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .getPayments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlePayments, this::handleError)
        );
    }

    private void handlePayments(final List<Payment> payments) {
        this.payments.addAll(payments);
        final String[] items = new String[payments.size()];
        for (int i = 0; i < payments.size(); i++) {
            items[i] = payments.get(i).prettyString();
        }
        this.payment.setSimpleItems(items);
    }

    private void checkout() {
        if (this.adapter.getItems().isEmpty()) {
            Toast.makeText(requireContext(), R.string.products_required, Toast.LENGTH_SHORT).show();
            return;
        }
        Shipment shipment = null;
        for (final Shipment s : this.shipments) {
            if (s.prettyString().equals(this.shipment.getText().toString())) {
                shipment = s;
            }
        }
        if (shipment == null) {
            Toast.makeText(requireContext(), R.string.shipment_required, Toast.LENGTH_SHORT).show();
            return;
        }
        Payment payment = null;
        for (final Payment p : this.payments) {
            if (p.prettyString().equals(this.payment.getText().toString())) {
                payment = p;
            }
        }
        if (payment == null) {
            Toast.makeText(requireContext(), R.string.payment_required, Toast.LENGTH_SHORT).show();
            return;
        }
        final JsonObject request = new JsonObject();
        request.addProperty("shipment", shipment.getId());
        request.addProperty("payment", payment.getId());
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .postCheckout(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCheckout, this::handleError)
        );
    }

    private void handleCheckout(final Void unused) {
        Toast.makeText(requireContext(), R.string.order_placed, Toast.LENGTH_SHORT).show();
        this.noProducts.setVisibility(ImageView.VISIBLE);
        this.adapter.clear();
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(requireContext(), TAG, error);
    }

    private void updateTotal() {
        double total = 0;
        for (final CartProduct p : this.adapter.getItems()) {
            total += Util.discount(p.getPrice(), p.getDiscount()) * p.getAmount();
        }
        this.total.setText(Util.formatCurrency(total));
    }

    private void updateToolbar() {
        //this.toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        //this.toolbar.setTitle(R.string.nav_product);
        //this.toolbar.inflateMenu(R.menu.app_bar_cart_menu);
    }
}