package com.vinitagarwal.DemoLaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    cart cart;
    orderhistory orderhistory;
    Home home;
    profile profile;
    String address = "";
    boolean newuser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS) {
            // The SafetyNet Attestation API is available.
        } else {
            Toast.makeText(this, "Please Update/Install Google Play Services", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meowBottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(0,R.drawable.homebottom));
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.historybottom));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.shopping_ca));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.profilebottom));
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

            }
        });
        ViewPager viewPager2 = findViewById(R.id.framelayout);
        vpadapter vpadapter1 = new vpadapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        home = new Home(this);
        vpadapter1.addfragments(home);
        cart = new cart(this);
        vpadapter1.addfragments(cart);
        orderhistory = new orderhistory(home,this);
        vpadapter1.addfragments(orderhistory);
        profile = new profile(this);
        vpadapter1.addfragments(profile);
        viewPager2.setAdapter(vpadapter1);
        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (position == 1){
                    cart.updatecart();
                }
                bottomNavigation.show(position,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                if (item.getId() == 1){
                    cart.updatecart();
                }
                if (item.getId() == 2){
                    orderhistory.updateorderlist();
                }
                if (item.getId() == 3){

                }
                viewPager2.setCurrentItem(item.getId());
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                
            }
        });
        bottomNavigation.show(0,true);
        Toast.makeText(this, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
    }
}