package com.leadnsolutions.saloonappointmentscheduling.customer;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.customer.fragments.CustomerDashboradFragment;
import com.leadnsolutions.saloonappointmentscheduling.customer.fragments.CustomerHistoryFragment;
import com.leadnsolutions.saloonappointmentscheduling.customer.fragments.CustomerNotificationFragment;
import com.leadnsolutions.saloonappointmentscheduling.customer.fragments.CustomerProfileFragment;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;

import java.util.Objects;

public class CustomerDashboardActivity extends AppCompatActivity {


   /* private List<SaloonModel> listModel;
    private RecyclerView mResultList;
    private SaloonAdapter adapter;

    private EditText searchSaloon;

    private DatabaseReference mUserDatabase;

    private StorageReference storageReference;*/

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        Objects.requireNonNull(getSupportActionBar()).hide();


        BottomNavigationView navigation = findViewById(R.id.customer_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FrameLayout frameLayout = findViewById(R.id.frame_container_customer);

        fragment = new CustomerDashboradFragment();
        UtilClass.loadFragment(fragment, CustomerDashboardActivity.this, R.id.frame_container_customer);


        /*mResultList = findViewById(R.id.show_all_saloons);
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


   /* private void showAllSaloon() {

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

                    adapter = new SaloonAdapter(CustomerDashboardActivity.this, listModel);
                    mResultList.setAdapter(adapter);

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
                };
        //adapter = new SaloonAdapter(CustomerDashboardActivity.this,listModel);
        mResultList.removeAllViews();
        mResultList.setAdapter(adapter);
        adapter.startListening();

    }*/


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.customer_dashboard:

                fragment = new CustomerDashboradFragment();
                UtilClass.loadFragment(fragment, CustomerDashboardActivity.this, R.id.frame_container_customer);
                return true;
            case R.id.customer_profile:

                fragment = new CustomerProfileFragment();
                UtilClass.loadFragment(fragment, CustomerDashboardActivity.this, R.id.frame_container_customer);
                return true;
            case R.id.customer_history:
                fragment = new CustomerHistoryFragment();
                UtilClass.loadFragment(fragment, CustomerDashboardActivity.this, R.id.frame_container_customer);

                return true;
            case R.id.customer_notification:
                fragment = new CustomerNotificationFragment();
                UtilClass.loadFragment(fragment, CustomerDashboardActivity.this, R.id.frame_container_customer);
                return true;
        }
        return false;
    };

}