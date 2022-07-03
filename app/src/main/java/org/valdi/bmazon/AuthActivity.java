package org.valdi.bmazon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.valdi.bmazon.fragments.LoginFragment;
import org.valdi.bmazon.utils.Constants;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = AuthActivity.class.getSimpleName();;
    private final LoginFragment loginFragment = new LoginFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        final String token = shared.getString(Constants.TOKEN, "");
        if (!token.isEmpty()) {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.TOKEN, token);
            startActivity(intent);
            finish();
            return;
        }

        // Show login as default fragment
        if (savedInstanceState == null) {
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.auth_fragment_container, this.loginFragment);
            transaction.commit();
        }
    }
}