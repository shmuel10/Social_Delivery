package il.co.yedidia_shmuel.ddb_2.controller.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.controller.fragments.AllUsersFragment;
import il.co.yedidia_shmuel.ddb_2.controller.fragments.EditUserProfileFragment;
import il.co.yedidia_shmuel.ddb_2.controller.fragments.FriendRequestFragment;
import il.co.yedidia_shmuel.ddb_2.controller.fragments.MyFriendsFragment;
import il.co.yedidia_shmuel.ddb_2.model.authentication.AuthService;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;

public class UserProfileActivity extends AppCompatActivity {

    private AuthService _authService = DataSourceFactory.getAuthService();
    private DataSource _database = DataSourceFactory.getDataBase();
    private AppBarConfiguration _mAppBarConfiguration;
    private FirebaseAuth _mAuth;
    private String _currentUserMail;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        _currentUserMail = getIntent().getStringExtra("mail");
        TextView userMail = headerView.findViewById(R.id.userMail);
        userMail.setText(_currentUserMail);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        _mAppBarConfiguration = new
                AppBarConfiguration.Builder(R.id.all_packages, R.id.packages_i_can_collect, R.id.my_histroy_packages).
                setDrawerLayout(drawer).build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, _mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Menu m = navigationView.getMenu();
        SubMenu subMenu = m.addSubMenu("");

        subMenu.add("החברים שלי").setIcon(R.drawable.ic_view_headline_black_24dp).
                setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        MyFriendsFragment myFriendsFragment = new MyFriendsFragment();
                        FragmentManager fragmentManage = getSupportFragmentManager();
                        drawer.closeDrawers();
                        fragmentManage.beginTransaction().replace(R.id.nav_host_fragment, myFriendsFragment, "Tag").
                                addToBackStack(null).commit();
                        return true;
                    }
                });

        subMenu.add("בקשות חברות").setIcon(R.drawable.ic_view_headline_black_24dp).
                setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        FriendRequestFragment friendRequestFragment = new FriendRequestFragment();
                        FragmentManager fragmentManage = getSupportFragmentManager();
                        drawer.closeDrawers();
                        fragmentManage.beginTransaction().replace(R.id.nav_host_fragment, friendRequestFragment, "Tag").
                                addToBackStack(null).commit();
                        return true;
                    }
                });

        subMenu.add("כל המשתמשים").setIcon(R.drawable.ic_view_headline_black_24dp).
                setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        AllUsersFragment allUsersFragment = new AllUsersFragment();
                        FragmentManager fragmentManage = getSupportFragmentManager();
                        drawer.closeDrawers();
                        fragmentManage.beginTransaction().replace(R.id.nav_host_fragment, allUsersFragment, "Tag").
                                addToBackStack(null).commit();
                        return true;
                    }
                });

        SubMenu subMenu1 = m.addSubMenu("");

        subMenu1.add("הפרטים שלי").setIcon(R.drawable.ic_info_24dp).
                setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        EditUserProfileFragment editUserProfileFragment = new EditUserProfileFragment();
                        FragmentManager fragmentManage = getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("UserID", _currentUserMail);
                        editUserProfileFragment.setArguments(bundle);
                        drawer.closeDrawers();
                        fragmentManage.beginTransaction().replace(R.id.nav_host_fragment, editUserProfileFragment, "Tag").
                                addToBackStack(null).commit();
                        return true;
                    }
                });

        subMenu1.add("יציאה").setIcon(R.drawable.ic_exit_24dp).
                setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        _authService.logout();
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, _mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}
