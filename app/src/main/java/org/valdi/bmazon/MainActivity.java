package org.valdi.bmazon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.valdi.bmazon.fragments.CartFragment;
import org.valdi.bmazon.fragments.HomeFragment;
import org.valdi.bmazon.fragments.NoConnectionFragment;
import org.valdi.bmazon.fragments.NotificationsFragment;
import org.valdi.bmazon.fragments.OrdersFragment;
import org.valdi.bmazon.fragments.ProfileFragment;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.LoggedUser;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Constants;
import org.valdi.bmazon.utils.Util;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements AuthProvider {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final ProfileFragment profileFragment = ProfileFragment.newInstance();
    private final HomeFragment homeFragment = HomeFragment.newInstance();
    private final NotificationsFragment notificationsFragment = NotificationsFragment.newInstance();
    private final CartFragment cartFragment = CartFragment.newInstance();
    private final OrdersFragment ordersFragment = OrdersFragment.newInstance();
    private CompositeSubscription subscriptions;
    private String token;

    private DrawerLayout drawer;
    private ImageView userAvatar;
    private TextView userName;
    private TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.subscriptions = new CompositeSubscription();
        this.token = getIntent().getStringExtra(Constants.TOKEN);
        // Setup app bar
        final MaterialToolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        this.drawer = findViewById(R.id.navigation_drawer);
        // Setup drawer
        final NavigationView navigation = findViewById(R.id.navigation);
        this.userAvatar = navigation.getHeaderView(0).findViewById(R.id.nav_user_avatar);
        this.userName = navigation.getHeaderView(0).findViewById(R.id.nav_user_name);
        this.userEmail = navigation.getHeaderView(0).findViewById(R.id.nav_user_email);
        navigation.setNavigationItemSelectedListener(item -> showFragment(item.getItemId()));
        // Handle data reload
        getSupportFragmentManager().setFragmentResultListener(
                ProfileFragment.UPDATE_KEY,
                this,
                (requestKey, result) -> loadUserData()
        );
        // Load all
        //this.loadUserData();
        if (Util.isNetworkConnected(this)) {
            this.loadUserData();
        }
        // Show home as default fragment
        if (savedInstanceState == null) {
            this.showFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.subscriptions.unsubscribe();
    }

    private boolean showFragment(final int id) {
        // Main
        if (id == R.id.nav_home) {
            this.showFragment(this.homeFragment, "home");
            if (this.drawer != null) {
                this.drawer.close();
            }
            return true;
        }
        if (id == R.id.nav_notifications) {
            this.showFragment(this.notificationsFragment, "notifications");
            if (this.drawer != null) {
                this.drawer.close();
            }
            return true;
        }
        if (id == R.id.nav_cart) {
            this.showFragment(this.cartFragment, "cart");
            if (this.drawer != null) {
                this.drawer.close();
            }
            return true;
        }
        if (id == R.id.nav_orders) {
            this.showFragment(this.ordersFragment, "orders");
            if (this.drawer != null) {
                this.drawer.close();
            }
            return true;
        }
        // Account
        if (id == R.id.nav_profile) {
            this.showFragment(this.profileFragment, "profile");
            if (this.drawer != null) {
                this.drawer.close();
            }
            return true;
        }
        if (id == R.id.nav_logout) {
            final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor editor = shared.edit();
            editor.remove(Constants.TOKEN);
            editor.apply();
            final Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        // Other
        if (id == R.id.nav_settings) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (id == R.id.nav_help) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void showFragment() {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (Util.isNetworkConnected(this)) {
            transaction.replace(R.id.main_fragment_container, homeFragment);
        } else {
            final NoConnectionFragment noConnectionFragment = NoConnectionFragment.newInstance(() -> {
                final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.addToBackStack("no-conn");
                t.replace(R.id.main_fragment_container, homeFragment);
                t.commit();
                this.loadUserData();
            });
            transaction.add(R.id.main_fragment_container, noConnectionFragment);
        }
        transaction.commit();
    }

    private void showFragment(final Fragment fragment, final String back) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (Util.isNetworkConnected(this)) {
            transaction.replace(R.id.main_fragment_container, fragment);
            if(this.userName.getText().length() == 0) {
                this.loadUserData();
            }
        } else {
            final NoConnectionFragment noConnectionFragment = NoConnectionFragment.newInstance(() -> {
                final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.addToBackStack("no-conn");
                t.replace(R.id.main_fragment_container, fragment);
                t.commit();
            });
            transaction.replace(R.id.main_fragment_container, noConnectionFragment);
        }
        if (back != null) {
            transaction.addToBackStack(back);
        }
        transaction.commit();
    }

    private void loadUserData() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit(this)
                .getUserData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleUserData, this::handleError)
        );
    }

    private void handleUserData(final LoggedUser user) {
        Util.setAvatar(this.userAvatar, user.getAvatar());
        this.userName.setText(user.getUsername());
        this.userEmail.setText(user.getEmail());
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(this, TAG, error);
    }

    @Override
    public boolean isLogged() {
        return true;
    }

    @Override
    public String getToken() {
        return this.token;
    }
}