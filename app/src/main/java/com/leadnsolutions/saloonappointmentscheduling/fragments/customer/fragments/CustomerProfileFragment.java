package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.activity.SplashActivity;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.io.File;
import java.util.Objects;

import gun0912.tedbottompicker.TedBottomPicker;

public class CustomerProfileFragment extends Fragment {


    ImageView cProfileImg;
    Button btnLogout;
    TextView cProfileName, cProfileNumber, cProfileAddress, cProfileGender;
    DatabaseReference reference;
    private View view;
    private AppCompatActivity mActivity;
    private Uri imageUri;
    private UploadTask mUploadTask;


    public CustomerProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        mActivity = (AppCompatActivity) getActivity();
        initViews();
        return view;
    }

    private void initViews() {

        cProfileImg = view.findViewById(R.id.customer_profile_pic);
        cProfileName = view.findViewById(R.id.profile_customer_name);
        cProfileNumber = view.findViewById(R.id.profile_customer_phone);
        cProfileAddress = view.findViewById(R.id.profile_customer_address);
        cProfileGender = view.findViewById(R.id.profile_customer_gender);
        btnLogout = view.findViewById(R.id.btn_customer_logout);
        ImageButton btnCEditProfile = view.findViewById(R.id.btn_customer_info_edit);
        btnCEditProfile.setOnClickListener(v -> showUpdateDialog());

        customerProfileData();

        btnLogout.setOnClickListener(view -> Logout());
    }


    private void Logout() {

        SharedPrefHelper.getmHelper().clearPreferences();
        AuthUI.getInstance()
                .signOut(mActivity)
                .addOnCompleteListener(task -> {
                    mActivity.startActivity(new Intent(mActivity, SplashActivity.class));
                });
    }


    private CustomerModel model;

    private void customerProfileData() {
        if (SharedPrefHelper.getmHelper().getCustomerModel() != null) {
            CustomerModel customerModel = new Gson()
                    .fromJson(SharedPrefHelper.getmHelper().getCustomerModel(), CustomerModel.class);

            reference = FirebaseDatabase.getInstance()
                    .getReference(AppConstant.CUSTOMER).child(customerModel.getId());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    model = snapshot.getValue(CustomerModel.class);
                    if (model != null) {

                        cProfileName.setText(model.getName());
                        cProfileNumber.setText(model.getPhone());
                        cProfileAddress.setText(model.getLocation());
                        cProfileGender.setText(model.getGender());
                        Glide.with(mActivity).load(model.getProfile_image()).into(cProfileImg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mActivity, (CharSequence) error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Dialog updateDialog;
    private EditText updateCName, updateCPhone, updateCAddress;
    private ImageView updateCProfileImage;

    private void showUpdateDialog() {

        if (updateDialog == null) {
            updateDialog = new Dialog(mActivity);
            updateDialog.setCancelable(false);

            updateDialog.setContentView(R.layout.update_customer_info_dialog);

            Objects.requireNonNull(updateDialog.getWindow())
                    .setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

            ImageView btnUpdateClose = updateDialog.findViewById(R.id.iv_customer_update_close);
            Button btnUpdate = updateDialog.findViewById(R.id.btn_update_customer_info);

            updateCName = updateDialog.findViewById(R.id.update_customer_name);
            updateCPhone = updateDialog.findViewById(R.id.update_customer_phone);
            updateCAddress = updateDialog.findViewById(R.id.update_customer_address);


            updateCProfileImage = updateDialog.findViewById(R.id.update_customer_profile_image);

            CustomerModel model = new Gson()
                    .fromJson(SharedPrefHelper.getmHelper().getCustomerModel(), CustomerModel.class);

            updateCName.setText(model.getName());
            updateCPhone.setText(model.getPhone());
            updateCAddress.setText(model.getLocation());

            Glide.with(mActivity).load(model.getProfile_image()).into(updateCProfileImage);

            btnUpdateClose.setOnClickListener(v -> updateDialog.dismiss());

            updateCProfileImage.setOnClickListener(v -> {
                if (checkPermissions()) {

                    TedBottomPicker.with(mActivity)
                            .show(uri -> {
                                imageUri = uri;
                                Glide.with(mActivity).load(uri).into(updateCProfileImage);
                                // updateProfile.setImageURI(uri);
                            });
                }
            });

            btnUpdate.setOnClickListener(v -> {

                updateDialog.dismiss();
                if (imageUri != null && imageUri.getPath() != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(AppConstant.IMAGE_CUSTOMER).child(new File(imageUri.getPath()).getName());

                    /*getting downloadable link from uploaded image*/
                    mUploadTask = (UploadTask) storageReference.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot ->
                                    storageReference.getDownloadUrl()
                                            .addOnSuccessListener(uri -> updateDataToFirebase()));

                }
            });


            updateDialog.show();

        } else {
            updateDialog.dismiss();
            updateDialog = null;
        }

    }

    private void updateDataToFirebase() {

        String name = updateCName.getText().toString().trim();
        String phone = updateCPhone.getText().toString().trim();
        String address = updateCAddress.getText().toString().trim();


        if (SharedPrefHelper.getmHelper().getCustomerModel() != null) {
            CustomerModel model = new Gson()
                    .fromJson(SharedPrefHelper.getmHelper().getCustomerModel(), CustomerModel.class);
            model.setName(name);
            model.setPhone(phone);
            model.setLocation(address);
            model.setProfile_image(imageUri.toString());

            reference = FirebaseDatabase.getInstance().getReference(AppConstant.CUSTOMER)
                    .child(model.getId());

            reference.setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mActivity, "Customer Updated!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            return false;
        } else {
            Toast.makeText(mActivity, "Permissions Allowed!", Toast.LENGTH_LONG).show();
            return true;
        }
    }

}