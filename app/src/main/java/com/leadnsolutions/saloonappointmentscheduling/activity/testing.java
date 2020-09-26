package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments.SaloonProfileFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.adapter.SaloonAdapter;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testing extends AppCompatActivity
        implements OnMapReadyCallback, SaloonAdapter.OnSaloonClickListener {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private SearchView mSearchView;

    private ImageView imgFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_maps);
        initViews();
        //Objects.requireNonNull(getSupportActionBar()).hide();
        mSearchView = findViewById(R.id.search_map);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String query = mSearchView.getQuery().toString();
                searchAreaOnMap(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        imgFilter = findViewById(R.id.img_filter);
        imgFilter.setOnClickListener(view -> {
            showFilterDialog();
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

    }

    private BottomSheetBehavior mBottomSheetBehavior;
    private RecyclerView mRecyclerView;

    private void initViews() {
        View BottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(BottomSheet);

        mRecyclerView = findViewById(R.id.rv_saloon);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private Dialog mFilterDialog;
    private Long minPrice, maxPrice;

    private void showFilterDialog() {
        if (mFilterDialog == null) {
            mFilterDialog = new Dialog(this);
            mFilterDialog.setCancelable(false);

            mFilterDialog.setContentView(R.layout.dialog_filter_saloon);
            assert mFilterDialog.getWindow() != null;
            mFilterDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView txtMin = (TextView) mFilterDialog.findViewById(R.id.txt_min_value);
            TextView txtMax = (TextView) mFilterDialog.findViewById(R.id.txt_max_value);

            TextView txtClose = mFilterDialog.findViewById(R.id.txt_close);
            txtClose.setOnClickListener(view -> {
                mFilterDialog.dismiss();
                mFilterDialog = null;
            });


            CrystalRangeSeekbar rangeSeekBar = mFilterDialog.findViewById(R.id.range_seek_bar);
            rangeSeekBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                minPrice = (Long) minValue;
                maxPrice = (Long) maxValue;
                txtMax.setText(String.valueOf(maxValue));
                txtMin.setText(String.valueOf(minValue));


            });

            Button btnFilter = mFilterDialog.findViewById(R.id.btn_send_request);
            btnFilter.setOnClickListener(view -> {
                getSaloonByPrice(minPrice, maxPrice);
                mFilterDialog.dismiss();
                mFilterDialog = null;
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            });


            // set final value listener
            /*rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                @Override
                public void finalValue(Number minValue, Number maxValue) {
                    Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                }
            });*/

            mFilterDialog.show();
        } else {
            mFilterDialog.dismiss();
            mFilterDialog = null;
        }
    }


    private void getSaloonByPrice(Long minPrice, Long maxPrice) {
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance()
                .getReference(AppConstant.SALOON);
        mUserDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<SaloonModel> mSaloonModelList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SaloonModel saloonModel = snapshot.getValue(SaloonModel.class);
                        if (saloonModel != null)
                            for (SaloonModel.SaloonService saloonService : saloonModel.getSaloonService()) {
                                Long price = Long.parseLong(saloonService.getPrice());
                                if (price <= maxPrice && price >= minPrice && !mSaloonModelList.contains(saloonModel)) {
                                    mSaloonModelList.add(saloonModel);
                                }
                            }

                    }

                    SaloonAdapter saloonAdapter = new SaloonAdapter(testing.this, mSaloonModelList, testing.this::onSaloonClick);
                    mRecyclerView.setAdapter(saloonAdapter);
                    saloonAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchAreaOnMap(String query) {
        Geocoder geocoder = new Geocoder(this);
        try {
            mGoogleMap.clear();
            List<Address> addressList = geocoder.getFromLocationName(query, 1);
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(query));
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            Log.e("city", address.getLocality());
            getSaloonByCity(address.getLocality(), mLastLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

                getSaloonByCity("nearBy", mLastLocation);
            }
        }
    };

    private void getSaloonByCity(String city, Location mLastLocation) {

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance()
                .getReference(AppConstant.SALOON);
        if (city.toLowerCase().equalsIgnoreCase("nearby")) {
            if (mLastLocation != null) {
                try {
                    Geocoder geocoder = new Geocoder(this);
                    List<Address> addressList;
                    double lat = mLastLocation.getLatitude();
                    double lng = mLastLocation.getLongitude();
                    Log.e("LatLng", String.valueOf(lat).concat(",").concat(lng + ""));
                    addressList = geocoder.getFromLocation(lat, lng, 1);
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        city = address.getLocality();
                        //getSaloonByCity(address.getLocality());
                        //city = mAddress.getLocality();

                    } else {
                        Toast.makeText(this, "error in geocoder", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Toast.makeText(this, "showAllSaloon!", Toast.LENGTH_SHORT).show();
        //final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("data");
        mUserDatabase.orderByChild("city").equalTo(city).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<SaloonModel> mSaloonModelList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SaloonModel saloonModel = snapshot.getValue(SaloonModel.class);

                        String[] value = saloonModel.getLoc().split(",");
                        double lat = Double.parseDouble(value[0]);
                        double lng = Double.parseDouble(value[1]);
                        LatLng saloonLatLang = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions().position(saloonLatLang).title(saloonModel.getName());
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.saloon_icon));

                        mGoogleMap.addMarker(markerOptions);

                        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(saloonLatLang));
                        // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(saloonLatLang, 15F));

                    }
                    // Log.d("ListSaloon", listModel.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onSaloonClick(SaloonModel saloonModel) {
        UtilClass.loadFragment(new SaloonProfileFragment(saloonModel), this, R.id.frame_container);
    }
}