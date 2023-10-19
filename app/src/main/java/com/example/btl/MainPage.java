package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title);
        setSupportActionBar(toolbar);

        fAuth=FirebaseAuth.getInstance();

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment= new ExpenseFragment();

        bottomNavigationView= findViewById(R.id.bottomNavBar);
        frameLayout=findViewById(R.id.main_frame);

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView= findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        setFragment(dashboardFragment);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.dashboard){
                    setFragment(dashboardFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.baby_blue);
                    return true;
                }
                if (item.getItemId() == R.id.income){
                    setFragment(incomeFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                    return true;
                }
                if(item.getItemId()==R.id.expense){
                    setFragment(expenseFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemID){
        Fragment fragment = null;
        if(itemID == R.id.dashboard){
            setFragment(dashboardFragment);
            bottomNavigationView.setItemBackgroundResource(R.color.baby_blue);
        }
        if(itemID == R.id.income){
            setFragment(incomeFragment);
            bottomNavigationView.setItemBackgroundResource(R.color.income_color);
        }
        if(itemID == R.id.expense){
            setFragment(expenseFragment);
            bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
        }
        if(itemID==R.id.change_pass){
            startActivity(new Intent(getApplicationContext(), changePass.class));
        }
        if(itemID == R.id.log_out){
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
        }
        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return false;
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }
}