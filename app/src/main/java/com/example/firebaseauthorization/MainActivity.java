package com.example.firebaseauthorization;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivty";

    private Toolbar mToolbar;
    private TextView tv_toolbar;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    private FirebaseAuth mAuth;

    public static String CHANNEL_ID = "my_first_channel";
    private static final String TOPIC_WHEATHER = "whether";


    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private View headerView;
    private ViewPagerAdapter mViewPagerAdapter;

    private ControlFragment mControlFragment;
    private AlertListenerFragment mAlertListenerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(mToolbar);
        mToolbar = findViewById(R.id.toolbar_main);
        tv_toolbar = findViewById(R.id.toolbar_text);
        getSupportActionBar().setElevation(0);
        tv_toolbar.setText("TARP Project");
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open_drawer,R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = findViewById(R.id.navigation_view);
        headerView = mNavigationView.getHeaderView(0);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        if(!checkUserSignIn()){
            return;
        }

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mControlFragment = new ControlFragment();
        mAlertListenerFragment = new AlertListenerFragment();

        mViewPagerAdapter.addFragment(mControlFragment,"");
        mViewPagerAdapter.addFragment(mAlertListenerFragment,"");

        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Controls");
        mTabLayout.getTabAt(1).setText("Alerts");
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Toast.makeText(MainActivity.this,"Tab Position: "+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        checkUserSignIn();
//    }

    private boolean checkUserSignIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI(currentUser);
            return true;
        }else {
            Intent loginIntent = new Intent(this,LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            return false;
        }
    }


    private void updateUI(FirebaseUser user){
        Toast.makeText(this,"UI updated",Toast.LENGTH_SHORT).show();

        final TextView navUsername = headerView.findViewById(R.id.tv_header_name);
        final TextView navUserEmail = headerView.findViewById(R.id.tv_header_email);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "checkUserSignIn: 4");
                try{
                    String userName = dataSnapshot.getValue().toString().trim();
                    navUsername.setText(userName);
                    Toast.makeText(MainActivity.this,userName,Toast.LENGTH_SHORT).show();
                }catch (NullPointerException npe){
                    Toast.makeText(MainActivity.this,npe.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Something went wrong while fetching user name!!",Toast.LENGTH_SHORT).show();
            }
        });
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "checkUserSignIn: 5");
                try{
                    String userEmail = dataSnapshot.getValue().toString().trim();
                    navUserEmail.setText(userEmail);
                }catch (NullPointerException npe){
                    Toast.makeText(MainActivity.this,npe.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(this,"UI updated",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                mAuth.signOut();
                checkUserSignIn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
