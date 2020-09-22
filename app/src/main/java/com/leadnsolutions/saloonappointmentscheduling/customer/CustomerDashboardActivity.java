package com.leadnsolutions.saloonappointmentscheduling.customer;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.customer.fragments.HomeMapsFragment;
import com.leadnsolutions.saloonappointmentscheduling.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.saloon.adapter.SaloonAdapter;
import com.leadnsolutions.saloonappointmentscheduling.saloon.fragments.ProfileFragment;
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {


    private List<SaloonModel> listModel;
    private RecyclerView mResultList;
    private SaloonAdapter adapter;

    private EditText searchSaloon;

    private DatabaseReference mUserDatabase;

    private StorageReference storageReference;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:

                fragment = new HomeMapsFragment();
                UtilClass.loadFragment(fragment, this, R.id.frame_container);
                return true;
            case R.id.navigation_profile:

                fragment = new ProfileFragment();
                UtilClass.loadFragment(fragment, this, R.id.frame_container);
                return true;

        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        getSupportActionBar().setTitle("Customer Dashboard");

        UtilClass.loadFragment(new HomeMapsFragment(), this, R.id.frame_container);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        /*mResultList = findViewById(R.id.rv_saloons);
        searchSaloon = findViewById(R.id.ed_saloon_search);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new GridLayoutManager(this, 2));


        showAllSaloon();

        searchSaloon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(CustomerDashboardActivity.this, "Searching ...", Toast.LENGTH_SHORT).show();
                String searchName = searchSaloon.getText().toString().trim();
                searchUserFirebase(searchName);
            }

            @Override
            public void afterTextChanged(Editable s) {
                UtilClass.hideSoftKeyboard(CustomerDashboardActivity.this);
            }
        });*/


    }


    private void showAllSaloon() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference("saloon");

        //Toast.makeText(this, "showAllSaloon!", Toast.LENGTH_SHORT).show();
        //final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("data");
        mUserDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listModel = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SaloonModel value = snapshot.getValue(SaloonModel.class);
                        listModel.add(value);
                        // listModel.add(snapshot.getValue(SaloonModel.class));

                    }
                    Log.d("ListSaloon", listModel.toString());

           /*         adapter = new SaloonAdapter(CustomerDashboardActivity.this, listModel);
                    mResultList.setAdapter(adapter);*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerDashboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUserFirebase(String name) {

        Query query = mUserDatabase.orderByChild("name").startAt(name).endAt(name + "\uf8ff");
/*
        FirebaseRecyclerOptions<SaloonModel> options =
                new FirebaseRecyclerOptions.Builder<SaloonModel>()
                        .setQuery(query, SaloonModel.class).build();

        FirebaseRecyclerAdapter<SaloonModel, SaloonAdapter.SaloonViewHolder> adapter =
                new FirebaseRecyclerAdapter<SaloonModel, SaloonAdapter.SaloonViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SaloonAdapter.SaloonViewHolder holder,
                                                    int position, @NonNull SaloonModel model) {
                        holder.setData(model, CustomerDashboardActivity.this);
                    }

                    @NonNull
                    @Override
                    public SaloonAdapter.SaloonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(CustomerDashboardActivity.this)
                                .inflate(R.layout.registered_saloon_list, parent, false);
                        return new SaloonAdapter.SaloonViewHolder(view);
                    }
                };*/


        //adapter = new SaloonAdapter(CustomerDashboardActivity.this,listModel);
        mResultList.removeAllViews();
        mResultList.setAdapter(adapter);
        // adapter.startListening();

    }

}