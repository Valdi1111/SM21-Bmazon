package org.valdi.bmazon.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import org.valdi.bmazon.R;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.BuyerData;
import org.valdi.bmazon.model.LoggedUser;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    public static final String UPDATE_KEY = "update_user_data";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_FISCAL_CODE = "fiscal_code";
    private static final String KEY_GENDER = "gender";
    private CompositeSubscription subscriptions;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cropLauncher;
    private String avatarUrl;

    private DrawerLayout drawer;
    private ImageView avatar;
    private TextInputEditText name;
    private TextInputEditText surname;
    private TextInputEditText fiscal_code;
    private MaterialAutoCompleteTextView gender;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.subscriptions = new CompositeSubscription();
        // Handle camera
        this.cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    final File file = new File(requireContext().getCacheDir(), "temp_avatar.png");
                    final Bitmap avatar = result.getData().getParcelableExtra("data");
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        avatar.compress(Bitmap.CompressFormat.PNG, 100, out);
                        cropPhoto(Uri.fromFile(file));
                    } catch (Exception e) {
                        Log.w(TAG, "Error saving image to disk", e);
                    }
                }
        );
        this.galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    cropPhoto(result.getData().getData());
                }
        );
        this.cropLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    final CropImage.ActivityResult res = CropImage.getActivityResult(result.getData());
                    changeAvatar(res.getUri());
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final MaterialToolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setTitle(R.string.nav_profile);
        this.drawer = requireActivity().findViewById(R.id.navigation_drawer);
        final NavigationView navigation = requireActivity().findViewById(R.id.navigation);
        navigation.setCheckedItem(R.id.nav_profile);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.avatar = view.findViewById(R.id.user_avatar);
        final Button take = view.findViewById(R.id.avatar_take_picture);
        take.setOnClickListener(v -> takePicture());
        final Button select = view.findViewById(R.id.avatar_select_photo);
        select.setOnClickListener(v -> selectPhoto());
        this.name = view.findViewById(R.id.user_name);
        this.surname = view.findViewById(R.id.user_surname);
        this.fiscal_code = view.findViewById(R.id.user_fiscal_code);
        this.gender = view.findViewById(R.id.user_gender);
        final Button save = view.findViewById(R.id.user_save);
        save.setOnClickListener(v -> changeUserData());
        if(savedInstanceState != null) {
            this.avatarUrl = savedInstanceState.getString(KEY_AVATAR);
            Util.setAvatar(this.avatar, this.avatarUrl);
            this.name.setText(savedInstanceState.getString(KEY_NAME));
            this.surname.setText(savedInstanceState.getString(KEY_SURNAME));
            this.fiscal_code.setText(savedInstanceState.getString(KEY_FISCAL_CODE));
            this.gender.setText(savedInstanceState.getString(KEY_GENDER), false);
        } else {
            // Load all
            this.loadUserData();
        }
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(this.name != null) {
            outState.putString(KEY_AVATAR, this.avatarUrl);
            outState.putString(KEY_NAME, this.name.getText().toString());
            outState.putString(KEY_SURNAME, this.surname.getText().toString());
            outState.putString(KEY_FISCAL_CODE, this.fiscal_code.getText().toString());
            outState.putString(KEY_GENDER, this.gender.getText().toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.subscriptions.unsubscribe();
    }

    private void takePicture() {
        if (!requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(requireContext(), R.string.no_camera, Toast.LENGTH_SHORT).show();
            return;
        }
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent chooser = Intent.createChooser(intent, getString(R.string.action_take_picture));
        this.cameraLauncher.launch(chooser);
    }

    private void selectPhoto() {
        //final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        final Intent chooser = Intent.createChooser(intent, getString(R.string.action_select_photo));
        this.galleryLauncher.launch(chooser);
    }

    private void cropPhoto(final Uri uri) {
        final Intent intent = CropImage
                .activity(uri)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setAllowRotation(true)
                .setAllowCounterRotation(true)
                .setAllowFlipping(true)
                .getIntent(requireContext());
        this.cropLauncher.launch(intent);
    }

    private void loadUserData() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .getUserData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleUserData, this::handleError)
        );
    }

    private void handleUserData(final LoggedUser user) {
        this.avatarUrl = user.getAvatar();
        Util.setAvatar(this.avatar, this.avatarUrl);
        this.name.setText(user.getName());
        this.surname.setText(user.getSurname());
        this.fiscal_code.setText(user.getFiscalCode());
        this.gender.setText(this.gender.getAdapter().getItem(user.getGender().ordinal()).toString(), false);
    }

    private void changeAvatar(final Uri uri) {
        final File file = new File(uri.getPath());
        final RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        final MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(), body);
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .postUserAvatar(part)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        unused -> {
                            Toast.makeText(requireContext(), R.string.user_avatar_updated, Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().setFragmentResult(ProfileFragment.UPDATE_KEY, new Bundle());
                            this.avatar.setImageURI(uri);
                        },
                        this::handleError
                )
        );
    }

    private void changeUserData() {
        final BuyerData data = new BuyerData();
        data.setName(this.name.getText().toString());
        data.setSurname(this.surname.getText().toString());
        data.setFiscalCode(this.fiscal_code.getText().toString());
        final String[] genders = getResources().getStringArray(R.array.user_genders);
        final String gender = this.gender.getText().toString();
        final int pos = Arrays.asList(genders).indexOf(gender);
        data.setGender(LoggedUser.Gender.values()[pos]);
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .postBuyerData(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        unused -> {
                            Toast.makeText(requireContext(), R.string.user_data_updated, Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().setFragmentResult(ProfileFragment.UPDATE_KEY, new Bundle());
                        },
                        this::handleError
                )
        );
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(requireContext(), TAG, error);
    }
}