package com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.adapter.StaffAdapter;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.AddStaff;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gun0912.tedbottompicker.TedBottomPicker;


public class DashboardFragment extends Fragment {

    View view;
    private Uri imageUri;
    private AppCompatActivity mActivity;
    private UploadTask mUploadTask;
    boolean a100;
    private StaffAdapter adapter;

    private DatabaseReference mUserDatabase;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        intiViews();

        return view;
    }


    RecyclerView staffRecyclerView;
    FloatingActionButton fabAddStaff;


    private void intiViews() {
        mActivity = (AppCompatActivity) getActivity();
        fabAddStaff = view.findViewById(R.id.fab_add_staff);
        staffRecyclerView = view.findViewById(R.id.recycler_view_saloon_staff);
        staffRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        showStaff();

        fabAddStaff.setOnClickListener(v -> addSaloonStaff());

    }

    private List<AddStaff> listModel;
    private void showStaff() {
        if (SharedPrefHelper.getmHelper().getSaloonModel()!=null) {
            String uID = new Gson().fromJson(SharedPrefHelper.getmHelper().getSaloonModel(),SaloonModel.class).getId();
            mUserDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON).child(uID).child("staff");

            mUserDatabase.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        listModel = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AddStaff value = snapshot.getValue(AddStaff.class);
                            listModel.add(value);
                            // listModel.add(snapshot.getValue(SaloonModel.class));

                        }
                        Log.d("ListSaloon", listModel.toString());

                        adapter = new StaffAdapter(mActivity, listModel);
                        staffRecyclerView.setAdapter(adapter);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Dialog addStaffDialog;
    ImageView staffProfile;
    EditText staffName, staffPhone, staffAddress;
    Button staffAdd;
    Spinner designationSpinner;
    ArrayAdapter designationAdapter;
    String[] designation = {"Manager", "Hair Stylist", "Assistant", "Receptionist", "Massage Therapist"};

    private void addSaloonStaff() {
        mActivity = (AppCompatActivity) getActivity();
        Toast.makeText(mActivity, "Add Staff Dialog", Toast.LENGTH_SHORT).show();

        if (addStaffDialog == null) {
            addStaffDialog = new Dialog(mActivity);
            addStaffDialog.setCancelable(false);
            addStaffDialog.setContentView(R.layout.add_saloon_staff_dialog);

            staffProfile = addStaffDialog.findViewById(R.id.staff_profile_image);
            staffName = addStaffDialog.findViewById(R.id.add_staff_name);
            staffPhone = addStaffDialog.findViewById(R.id.add_staff_phone);
            staffAddress = addStaffDialog.findViewById(R.id.add_staff_address);
            staffAdd = addStaffDialog.findViewById(R.id.btn_add_saloon_staff);

            designationSpinner = addStaffDialog.findViewById(R.id.staff_designation);

            Objects.requireNonNull(addStaffDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            addStaffDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView ivClose = addStaffDialog.findViewById(R.id.iv_add_saloon_staff_close);
            ivClose.setOnClickListener(v -> {
                addStaffDialog.dismiss();
                addStaffDialog = null;
            });

            staffProfile.setOnClickListener(v -> {
                if (checkPermissions()) {

                    TedBottomPicker.with(mActivity)
                            .show(uri -> {
                                imageUri = uri;
                                Glide.with(mActivity).load(uri).into(staffProfile);
                                // updateProfile.setImageURI(uri);
                            });
                }
            });

            designationAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_item, designation);
            designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            designationSpinner.setAdapter(designationAdapter);


            staffAdd.setOnClickListener(v -> {

                if (imageUri != null && imageUri.getPath() != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(AppConstant.SALOON_STAFF_IMAGE).child(new File(imageUri.getPath()).getName());

                    /*getting downloadable link from uploaded image*/
                    mUploadTask = (UploadTask) storageReference.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot ->
                                    storageReference.getDownloadUrl()
                                            .addOnSuccessListener(uri -> addStaffToFirebase()));

                }
            });

            addStaffDialog.show();
        } else {
            addStaffDialog.dismiss();
            addStaffDialog = null;
        }

    }

    DatabaseReference reference;

    private void addStaffToFirebase() {

        String name = staffName.getText().toString().trim();
        String phone = staffPhone.getText().toString().trim();
        String address = staffAddress.getText().toString().trim();
        String designation = designationSpinner.getSelectedItem().toString();


        SaloonModel saloonModel = new Gson().fromJson(SharedPrefHelper.getmHelper().getSaloonModel(),
                SaloonModel.class);

        reference = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON)
                .child(saloonModel.getId()).child("staff").push();


        String free = "Free";
        AddStaff addStaffModel = new AddStaff(imageUri.toString(), name, phone, address, designation, free);

        reference.setValue(addStaffModel).addOnCompleteListener(task ->
                Toast.makeText(mActivity, "Saloon Staff Added!", Toast.LENGTH_SHORT).show());
        addStaffDialog.dismiss();
        addStaffDialog = null;

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