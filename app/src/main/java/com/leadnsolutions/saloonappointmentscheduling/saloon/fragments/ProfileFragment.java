package com.leadnsolutions.saloonappointmentscheduling.saloon.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gun0912.tedbottompicker.TedBottomPicker;

public class ProfileFragment<updateDialog> extends Fragment {

    ImageView saloonProfileImg;
    TextView profileName, profileNumber, profileAddress, editPrice;
    DatabaseReference reference;
    private View view;
    private AppCompatActivity mActivity;
    private Dialog updateDialog;
    private Dialog priceDialog;
    private Uri imageUri;
    private UploadTask mUploadTask;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews();
        return view;
    }

    private LinearLayout llVertical;

    private void initViews() {
        mActivity = (AppCompatActivity) getActivity();
        if (mActivity != null && mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().hide();
        }
        saloonProfileImg = view.findViewById(R.id.img_saloon_profile);
        profileName = view.findViewById(R.id.profile_saloon_saloon_name);
        profileNumber = view.findViewById(R.id.profile_saloon_phone);
        profileAddress = view.findViewById(R.id.profile_saloon_address);
        editPrice = view.findViewById(R.id.tv_edit_service_price);
        ImageButton btnEditProfile = view.findViewById(R.id.btn_edit_info);
        editPrice.setOnClickListener(v -> editServicePrice());


        btnEditProfile.setOnClickListener(v -> showUpdateDialog());

        loadProfileData();
    }

    private void loadProfileData() {
        if (SharedPrefHelper.getmHelper().getSaloonModel() != null) {
            SaloonModel sharedModel = new Gson()
                    .fromJson(SharedPrefHelper.getmHelper().getSaloonModel(), SaloonModel.class);

            reference = FirebaseDatabase.getInstance()
                    .getReference(AppConstant.SALOON).child(sharedModel.getId());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //saloonModelList = new ArrayList<>();
                    if (llVertical != null)
                        llVertical.removeAllViews();
                    model = snapshot.getValue(SaloonModel.class);
                    if (model != null) {
                        profileName.setText(model.getName());
                        profileNumber.setText(model.getPhone());
                        profileAddress.setText(model.getAddress());
                        Glide.with(mActivity).load(model.getProfile_image()).into(saloonProfileImg);
                        setUpSaloonServices(model);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    LinearLayout llHorizontal;

    private void setUpSaloonServices(SaloonModel model) {
        llVertical = view.findViewById(R.id.ll_vertical);
        for (SaloonModel.SaloonService saloonService : model.getSaloonService()) {
            TextView txtSaloonService, txtServicePrice;
            txtSaloonService = new TextView(mActivity);
            txtServicePrice = new TextView(mActivity);
            llHorizontal = new LinearLayout(mActivity);

            txtSaloonService.setText(saloonService.getName());
            txtServicePrice.setText(saloonService.getPrice());

            llHorizontal.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            llHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            layoutParams.setMargins(2, 2, 2, 2);
            txtSaloonService.setLayoutParams(layoutParams);
            txtServicePrice.setLayoutParams(layoutParams);
            txtSaloonService.setGravity(Gravity.CENTER);
            txtSaloonService.setTextSize(18);
            txtServicePrice.setTextSize(18);
            txtServicePrice.setGravity(Gravity.CENTER);

            txtSaloonService.setPadding(3, 3, 3, 3);
            txtServicePrice.setPadding(3, 3, 3, 3);
            llHorizontal.addView(txtSaloonService);
            llHorizontal.addView(txtServicePrice);
            llVertical.addView(llHorizontal);
        }
    }


    private EditText[] editTexts;
    private TextView[] textViews;
    private SaloonModel model;

    private void editServicePrice() {
        if (priceDialog == null) {

            priceDialog = new Dialog(mActivity);
            priceDialog.setCancelable(false);
            priceDialog.setContentView(R.layout.set_saloon_service_price_dialog);
            ImageView ivClose = priceDialog.findViewById(R.id.iv_edit_service_price_close);
            Button updatePrice = priceDialog.findViewById(R.id.btn_update_service_price);

            ivClose.setOnClickListener(v -> {
                priceDialog.dismiss();
                priceDialog = null;
            });

            Objects.requireNonNull(priceDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            priceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            LinearLayout linearLayout = priceDialog.findViewById(R.id.ll_edit_service_price);
            if (model != null) {
                textViews = new TextView[model.getSaloonService().size()];
                editTexts = new EditText[model.getSaloonService().size()];
                for (int i = 0; i < model.getSaloonService().size(); i++) {
                    textViews[i] = new TextView(mActivity);
                    editTexts[i] = new EditText(mActivity);

                    LinearLayout layout = new LinearLayout(mActivity);

                    textViews[i].setText(model.getSaloonService().get(i).getName());
                    editTexts[i].setText(model.getSaloonService().get(i).getPrice());

                    layout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams
                            = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

                    layoutParams.setMargins(2, 2, 2, 2);

                    textViews[i].setLayoutParams(layoutParams);
                    editTexts[i].setLayoutParams(layoutParams);
                    textViews[i].setGravity(Gravity.CENTER);
                    editTexts[i].setTextSize(18);
                    editTexts[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                    textViews[i].setTextColor(Color.BLACK);
                    textViews[i].setTextSize(18);
                    editTexts[i].setGravity(Gravity.CENTER);

                    textViews[i].setPadding(3, 3, 3, 3);
                    editTexts[i].setPadding(3, 3, 3, 3);
                    layout.addView(textViews[i]);
                    layout.addView(editTexts[i]);
                    linearLayout.addView(layout);
                }

            } else
                Toast.makeText(mActivity, "Model is Null!", Toast.LENGTH_SHORT).show();
            priceDialog.show();
            updatePrice.setOnClickListener(v -> {
                List<SaloonModel.SaloonService> mSaloonServicesList = new ArrayList<>();
                for (int i = 0; i < editTexts.length; i++) {
                    SaloonModel.SaloonService saloonService = new SaloonModel.SaloonService();
                    saloonService.setName(textViews[i].getText().toString());
                    saloonService.setPrice(editTexts[i].getText().toString());
                    mSaloonServicesList.add(saloonService);
                }
                model.setSaloonService(mSaloonServicesList);
                reference = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON)
                        .child(model.getId());

                reference.setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        priceDialog.dismiss();
                        priceDialog = null;
                        // setUpSaloonServices(model);
                        Toast.makeText(mActivity, "Service Price Updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            priceDialog.dismiss();
            priceDialog = null;
        }
    }

    private EditText updateName, updatePhone, updateAddress;
    private ImageView updateProfile;

    private void showUpdateDialog() {

        if (updateDialog == null) {
            updateDialog = new Dialog(mActivity);
            updateDialog.setCancelable(true);

            updateDialog.setContentView(R.layout.update_saloon_info_dialog);

            Objects.requireNonNull(updateDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView btnUpdateClose = updateDialog.findViewById(R.id.iv_update_close);
            Button btnUpdate = updateDialog.findViewById(R.id.btn_update_saloon_info);

            updateName = updateDialog.findViewById(R.id.update_username);
            updatePhone = updateDialog.findViewById(R.id.update_phone);
            updateAddress = updateDialog.findViewById(R.id.update_address);

            updateProfile = updateDialog.findViewById(R.id.update_profile_image);

            SaloonModel model = new Gson()
                    .fromJson(SharedPrefHelper.getmHelper().getSaloonModel(), SaloonModel.class);

            updateName.setText(model.getName());
            updatePhone.setText(model.getPhone());
            updateAddress.setText(model.getAddress());


            Glide.with(mActivity).load(model.getProfile_image()).into(updateProfile);

            btnUpdateClose.setOnClickListener(v -> updateDialog.dismiss());

            updateProfile.setOnClickListener(v -> {
                if (checkPermissions()) {

                    TedBottomPicker.with(mActivity)
                            .show(uri -> {
                                imageUri = uri;
                                Glide.with(mActivity).load(uri).into(updateProfile);
                                // updateProfile.setImageURI(uri);
                            });
                }
            });

            btnUpdate.setOnClickListener(v -> {

                if (imageUri != null && imageUri.getPath() != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(AppConstant.IMAGE_SALOON).child(new File(imageUri.getPath()).getName());

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

    private void updateDataToFirebase() {

        String name = updateName.getText().toString().trim();
        String phone = updatePhone.getText().toString().trim();
        String address = updateAddress.getText().toString().trim();

        if (SharedPrefHelper.getmHelper().getSaloonModel() != null) {
            SaloonModel saloonModel = new Gson().fromJson(SharedPrefHelper.getmHelper().getSaloonModel(),
                    SaloonModel.class);
            saloonModel.setName(name);
            saloonModel.setPhone(phone);
            saloonModel.setAddress(address);
            saloonModel.setProfile_image(imageUri.toString());

            reference = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON)
                    .child(saloonModel.getId());

            reference.setValue(saloonModel).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mActivity, "User updated!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}