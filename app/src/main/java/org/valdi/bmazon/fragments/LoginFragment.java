package org.valdi.bmazon.fragments;

import static org.valdi.bmazon.utils.Validation.validateEmail;
import static org.valdi.bmazon.utils.Validation.validatePassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.valdi.bmazon.MainActivity;
import org.valdi.bmazon.R;
import org.valdi.bmazon.network.ErrorResponse;
import org.valdi.bmazon.network.LoginRequest;
import org.valdi.bmazon.network.LoginResponse;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.model.UserType;
import org.valdi.bmazon.utils.Constants;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass for login view.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private CompositeSubscription subscriptions;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.subscriptions = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        this.emailInput = view.findViewById(R.id.login_email);
        this.passwordInput = view.findViewById(R.id.login_password);
        final Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::onLogin);
        if (savedInstanceState != null) {
            this.emailInput.setText(savedInstanceState.getString(KEY_EMAIL));
            this.passwordInput.setText(savedInstanceState.getString(KEY_PASSWORD));
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.emailInput != null) {
            outState.putString(KEY_EMAIL, this.emailInput.getText().toString());
            outState.putString(KEY_PASSWORD, this.passwordInput.getText().toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.subscriptions.unsubscribe();
    }

    private void onLogin(final View view) {
        final String email = this.emailInput.getText().toString();
        final String password = this.passwordInput.getText().toString();
        this.emailInput.setError(null);
        this.passwordInput.setError(null);

        boolean err = false;
        if (!validateEmail(email)) {
            this.emailInput.setError(getString(R.string.invalid_email));
            err = true;
        }
        if (!validatePassword(password)) {
            this.passwordInput.setError(getString(R.string.invalid_password));
            err = true;
        }
        if (err) {
            Toast.makeText(requireContext(), R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
            return;
        }
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .login(new LoginRequest(email, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
        //mProgressBar.setVisibility(View.VISIBLE);
    }

    private void handleResponse(final LoginResponse response) {
        //mProgressBar.setVisibility(View.GONE);
        if (response.getType() != UserType.buyer) {
            Toast.makeText(requireContext(), R.string.login_not_buyer, Toast.LENGTH_SHORT).show();
            return;
        }
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        final SharedPreferences.Editor editor = shared.edit();
        editor.putString(Constants.TOKEN, response.getToken());
        editor.apply();
        final Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("token", response.getToken());
        this.startActivity(intent);
    }

    private void handleError(final Throwable error) {
        //mProgressBar.setVisibility(View.GONE);
        if (error instanceof HttpException) {
            final Gson gson = new GsonBuilder().create();
            try {
                final String errorBody = ((HttpException) error).response().errorBody().string();
                final ErrorResponse response = gson.fromJson(errorBody, ErrorResponse.class);
                Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Error loading data: " + error.getMessage(), error);
    }
}