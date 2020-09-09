package com.leadnsolutions.saloonappointmentscheduling.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gun0912.tedbottompicker.TedBottomPicker;


public class SaloonRegistrationFragment extends Fragment {

    EditText edSaloonName, edSaloonEmail, edSaloonPassword, edSaloonPhone, edSaloonAddress;
    Button btnSaloonReg;
    ImageView saloonImage;
    TextView saloonService;


    FirebaseAuth auth;
    DatabaseReference reference;

    View view;
    Spinner genderSpinner, timeFromSpinner, timeToSpinner, dayFromSpinner, dayToSpinner;
    String[] genderSaloon = {"Male", "Female"};
    String[] daySaloon = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String[] timeSaloon = {"01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00"};
    CheckBox[] checkBox = null;
    StringBuilder stringBuilder;
    List<String> addedServices;
    List<SaloonModel.SaloonService> mSaloonServicesList = null;
    private AppCompatActivity mActivity;
    private Uri imageUri;
    private Dialog mServiceDialog;
    private String[] saloonServices = {"Waxing", "Treatment", "Hair Dressing", "Make-Up", "Facial", "Massage"};
    private UploadTask mUploadTask;

    ArrayAdapter arrayAdapter, arrayAdapterDayFrom, arrayAdapterDayTo, arrayAdapterTimeFrom, arrayAdapterTimeTo;

    public SaloonRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_saloon_registration, container, false);

//        toolbar.setNavigationOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        initViews();
        return view;
    }

    private void initViews() {

        mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        edSaloonName = view.findViewById(R.id.et_saloon_name);
        edSaloonEmail = view.findViewById(R.id.et_saloon_email);
        edSaloonPassword = view.findViewById(R.id.et_saloon_password);
        edSaloonPhone = view.findViewById(R.id.et_saloon_phone);
        edSaloonAddress = view.findViewById(R.id.et_saloon_address);
        saloonImage = view.findViewById(R.id.iv_saloon_img);
        genderSpinner = view.findViewById(R.id.saloon_gender_spinner);
        saloonService = view.findViewById(R.id.tv_add_saloon_service);
        btnSaloonReg = view.findViewById(R.id.btn_saloon_registration);
        dayFromSpinner = view.findViewById(R.id.spinner_day_from);
        dayToSpinner = view.findViewById(R.id.spinner_day_to);
        timeFromSpinner = view.findViewById(R.id.spinner_time_from);
        timeToSpinner = view.findViewById(R.id.spinner_time_to);

        auth = FirebaseAuth.getInstance();

        arrayAdapter = new ArrayAdapter(mActivity, R.layout.spinner_item, genderSaloon);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        genderSpinner.setAdapter(arrayAdapter);

        arrayAdapterDayFrom = new ArrayAdapter(mActivity, R.layout.spinner_item, daySaloon);
        arrayAdapterDayFrom.setDropDownViewResource(R.layout.spinner_item);

        dayFromSpinner.setAdapter(arrayAdapterDayFrom);

        arrayAdapterDayTo = new ArrayAdapter(mActivity, R.layout.spinner_item, daySaloon);
        arrayAdapterDayTo.setDropDownViewResource(R.layout.spinner_item);

        dayToSpinner.setAdapter(arrayAdapterDayTo);

        arrayAdapterTimeFrom = new ArrayAdapter(mActivity, R.layout.spinner_item, timeSaloon);
        arrayAdapterTimeFrom.setDropDownViewResource(R.layout.spinner_item);

        timeFromSpinner.setAdapter(arrayAdapterTimeFrom);

        arrayAdapterTimeTo = new ArrayAdapter(mActivity, R.layout.spinner_item, timeSaloon);
        arrayAdapterTimeTo.setDropDownViewResource(R.layout.spinner_item);

        timeToSpinner.setAdapter(arrayAdapterTimeTo);

        btnSaloonReg.setOnClickListener(v -> {

            String email = edSaloonEmail.getText().toString();
            String password = edSaloonPassword.getText().toString();

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(email)) {
                Toast.makeText(mActivity, "Email and password are required!", Toast.LENGTH_SHORT).show();
            } else {
                saloonRegister(email, password);
            }
            //Toast.makeText(mActivity, "Saloon Registered!", Toast.LENGTH_SHORT).show();
        });

        saloonService.setOnClickListener(v -> showSaloonServiceDialog());

        saloonImage.setOnClickListener(v -> chooseImage());
    }

    private void chooseImage() {
        if (checkPermissions()) {
            TedBottomPicker.with(mActivity)
                    .show(uri -> {
                        imageUri = uri;
                        Glide.with(mActivity).load(uri).into(saloonImage);
                    });
        }
    }

    private void showSaloonServiceDialog() {
        if (mServiceDialog == null) {
            mServiceDialog = new Dialog(mActivity);
            mServiceDialog.setCancelable(false);

            mServiceDialog.setContentView(R.layout.dialoglayout);
            int width = (int) (mActivity.getResources().getDisplayMetrics().widthPixels * 0.7);
            int height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * 0.5);

            Objects.requireNonNull(mServiceDialog.getWindow()).setLayout(width, height);

            mServiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            LinearLayout linearLayout = mServiceDialog.findViewById(R.id.linearLayout_Dialog);
            stringBuilder = new StringBuilder();
            addedServices = new ArrayList<>();
            for (int i = 0; i < saloonServices.length; i++) {
                checkBox = new CheckBox[saloonServices.length];
                checkBox[i] = new CheckBox(mActivity);
                checkBox[i].setText(saloonServices[i]);
                //checkBox.setId();
                checkBox[i].setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                checkBox[i].setPadding(5, 5, 5, 5);

                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 10, 5, 2);
                checkBox[i].setLayoutParams(layoutParams);


                linearLayout.addView(checkBox[i]);

               /* if (checkBox.isChecked()) {
                    stringBuilder.append(checkBox.getText().toString()).append(",");
                }*/

                final int finalI = i;
                checkBox[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
//                           stringBuilder.append(checkBox[finalI].getText().toString()).append(",");
//                           stringBuilder.append(saloonServices[finalI]).append(",");
                        addedServices.add(saloonServices[finalI]);

                    } else {
                        addedServices.remove(saloonServices[finalI]);
                    }
                    Log.e("StringBuilder", addedServices.toString());
                });
                /*if (checkBox[i].isChecked()){
                    stringBuilder.append(checkBox[i].getText().toString()).append(",");
                    Log.e("StringBuilder",stringBuilder.toString());
                }*/

            }
            Button btnAddService = mServiceDialog.findViewById(R.id.btn_add_service);
            ImageView ivClose = mServiceDialog.findViewById(R.id.iv_close);

            ivClose.setOnClickListener(v -> mServiceDialog.dismiss());
            mSaloonServicesList = new ArrayList<>();
            btnAddService.setOnClickListener(v -> {
                if (addedServices.size() > 0) {
                    for (String addedService : addedServices) {
                        mSaloonServicesList.add(new SaloonModel.SaloonService(addedService, "0"));
                        stringBuilder.append(addedService).append(",");
                    }
                    mServiceDialog.dismiss();
                    mServiceDialog = null;
                    saloonService.setText(stringBuilder.toString());
                } else
                    Toast.makeText(mActivity, "Plz select at least one service!", Toast.LENGTH_SHORT).show();
            });
            Toast.makeText(mActivity, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            //let suppose inside okay
            /*if (checkBox != null && !checkBox.isChecked()) {
                //Plz select any check box
            }*/


            mServiceDialog.show();

        } else {
            mServiceDialog.dismiss();
            mServiceDialog = null;
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mActivity, "Permissions Allowed!", Toast.LENGTH_LONG).show();
            return true;
        } else {

            ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    0);
            return false;
        }
    }

    private void saloonRegister(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (mUploadTask != null && mUploadTask.isInProgress()) {
                            Toast.makeText(mActivity, "Upload in progress...", Toast.LENGTH_SHORT).show();
                        } else {

                            if (imageUri != null && imageUri.getPath() != null) {

                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReference(AppConstant.IMAGE_SALOON).child(new File(imageUri.getPath()).getName());

                                /*getting downloadable link from uploaded image*/
                                mUploadTask = (UploadTask) storageReference.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot ->
                                                storageReference.getDownloadUrl()
                                                        .addOnSuccessListener(this::addSaloonToFirebase));

                            } else {
                                Toast.makeText(mActivity, "plz attach an image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(mActivity, "Image is not uploaded!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /* protected LocationManager locationManager;
     LocationListener locationListener;
     protected Context context;
 */
    protected double latitude, longitude;


    FusedLocationProviderClient fusedLocationClient;

    private void addSaloonToFirebase(Uri uri) {

       /* locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, mActivity);


        }*/

        /*if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
            LocationRequest mLocationRequest = new LocationRequest();
           *//* mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(0);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);

            fusedLocationClient.requestLocationUpdates()*//*
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener((Executor) this, location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        });

        }*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        LocationRequest mLocationRequest = new LocationRequest();
           /* mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(0);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);

            fusedLocationClient.requestLocationUpdates()*/
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},1000);
        }else{
            fusedLocationClient.getLastLocation().addOnSuccessListener(mActivity, location -> {
                    if (location!=null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
            });
        }
        String name = edSaloonName.getText().toString();
        String email = edSaloonEmail.getText().toString();
        String password = edSaloonPassword.getText().toString();
        String phone = edSaloonPhone.getText().toString();
        String address = edSaloonAddress.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String dayFrom = dayFromSpinner.getSelectedItem().toString();
        String dayTo = dayToSpinner.getSelectedItem().toString();
        String timeFrom = timeFromSpinner.getSelectedItem().toString();
        String timeTo = timeToSpinner.getSelectedItem().toString();
        String loc = latitude + "," + longitude;

        String workTime = dayFrom + "-" + dayTo + "," + timeFrom + "-" + timeTo;
        Toast.makeText(mActivity, workTime, Toast.LENGTH_SHORT).show();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON)
                .child(userId);

        SaloonModel saloonModel = new SaloonModel(userId, uri.toString(), name, email, password,
                phone, address, gender, loc, workTime, mSaloonServicesList);

        reference.setValue(saloonModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            /*    HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("SaloonService", mSaloonServicesList);
                reference = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON)
                        .child(userId).child("SaloonService").push();
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {

                for (SaloonModel.SaloonService Service1 : mSaloonServicesList) {
                    reference = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON)
                            .child(userId).child("SaloonService").push();
                    reference.setValue(Service1).addOnCompleteListener(task1 -> {
                        Toast.makeText(mActivity, "User Added!", Toast.LENGTH_SHORT).show();
                    });

                }


                });*/


                if (task.isSuccessful()) {
                    Toast.makeText(mActivity, "Saloon Added", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(mActivity, "Users not Added!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(mActivity, "permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
/*
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }*/

}