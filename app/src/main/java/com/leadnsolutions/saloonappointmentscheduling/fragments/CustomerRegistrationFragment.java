package com.leadnsolutions.saloonappointmentscheduling.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;

import java.io.File;

import gun0912.tedbottompicker.TedBottomPicker;


public class CustomerRegistrationFragment extends Fragment {

    EditText customerName, customerEmail, customerPassword, customerPhone, customerAddress;
    ImageView customerImage;
    Button btnAddUser;
    Spinner genderSpinner;
    String[] gender = {"Male", "Female"};
    String[] gender1 = {"Male", "Female","others"};

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    StorageReference storageReference;
    View view;
    private AppCompatActivity mActivity;

    private Uri imageUri;
    private UploadTask mUploadTask;

    public CustomerRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_customer_registration, container, false);

        initViews();


        return view;
    }

    private void initViews() {

        firebaseAuth = FirebaseAuth.getInstance();
        mActivity = (AppCompatActivity) getActivity();

        customerName = view.findViewById(R.id.et_customer_name);
        customerEmail = view.findViewById(R.id.et_customer_email);
        customerPassword = view.findViewById(R.id.et_customer_password);
        customerAddress = view.findViewById(R.id.et_customer_address);
        customerPhone = view.findViewById(R.id.et_customer_phone);
        customerImage = view.findViewById(R.id.iv_customer_img);

        btnAddUser = view.findViewById(R.id.btn_customer_register);
        genderSpinner = view.findViewById(R.id.customer_Gender_Spinner);

        storageReference = FirebaseStorage.getInstance().getReference(AppConstant.IMAGE_CUSTOMER);


        ArrayAdapter arrayAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, gender);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        genderSpinner.setAdapter(arrayAdapter);

        btnAddUser.setOnClickListener(v -> {

            String email = customerEmail.getText().toString();
            String pass = customerPassword.getText().toString();


            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(mActivity, "Password And Email is Required!", Toast.LENGTH_SHORT).show();
            } else if (pass.length() < 6) {
                Toast.makeText(mActivity, "Password must be greater than 6.", Toast.LENGTH_SHORT).show();
            } else {
                registerCustomer(email, pass);
            }
        });

        customerImage.setOnClickListener(v -> chooseImage());


    }

    private void chooseImage() {
        if (checkPermissions()) {

            TedBottomPicker.with(mActivity)
                    .show(uri -> {
                        imageUri = uri;
                        Glide.with(mActivity).load(uri).into(customerImage);
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



    private void registerCustomer(String email, String password) {

        //customerName.getText();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (mUploadTask != null && mUploadTask.isInProgress()) {
                            Toast.makeText(mActivity, "Upload in progress...", Toast.LENGTH_SHORT).show();
                        } else {

                            if (imageUri != null && imageUri.getPath() != null) {

                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReference(AppConstant.IMAGE_CUSTOMER).child(new File(imageUri.getPath()).getName());

                                /*getting downloadable link from uploaded image*/
                                mUploadTask = (UploadTask) storageReference.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot ->
                                                storageReference.getDownloadUrl()
                                                        .addOnSuccessListener(this::addCustomerToFirebase));

                            } else {
                                Toast.makeText(mActivity, "plz attach an image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(mActivity, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void addCustomerToFirebase(Uri uri) {

        String name = customerName.getText().toString();
        String email = customerEmail.getText().toString();
        String password = customerPassword.getText().toString();
        String loc = customerAddress.getText().toString();
        String phone = customerPhone.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference(AppConstant.CUSTOMER)
                .child(userId);
        CustomerModel model = new CustomerModel(userId, uri.toString(), name, email, password,
                phone, loc, gender);
        reference.setValue(model).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(mActivity, "User Added!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}