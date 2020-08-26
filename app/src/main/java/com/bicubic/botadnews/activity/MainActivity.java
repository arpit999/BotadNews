package com.bicubic.botadnews.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.fragment.AboutusFragment;
import com.bicubic.botadnews.fragment.ContactusFragment;
import com.bicubic.botadnews.fragment.FragmentDrawer;
import com.bicubic.botadnews.fragment.GalleryFragment;
import com.bicubic.botadnews.fragment.HomeFragment;
import com.bicubic.botadnews.fragment.SettingFragment;

import java.util.Stack;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    TextView mTitle;
    private Stack<Fragment> fragmentStack;
    FragmentManager fragmentManager;
    Fragment fragment = null;
    String backStateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);

            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.bicubic.botadnews");
            startActivity(Intent.createChooser(intent, "Share"));
            return true;
        }


      /*  if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                mTitle.setText(title);
                break;
            case 1:
                fragment = new GalleryFragment();
                title = getString(R.string.title_gallery);
                mTitle.setText(title);
                break;
            case 2:
                fragment = new SettingFragment();
                title = getString(R.string.title_setting);
                mTitle.setText(title);
                break;
            case 3:
                fragment = new AboutusFragment();
                title = getString(R.string.title_aboutus);
                mTitle.setText(title);
                break;
            case 4:
                fragment = new ContactusFragment();
                title = getString(R.string.title_contactus);
                mTitle.setText(title);
                break;
            default:
                break;
        }

        if (fragment != null) {

            backStateName = fragment.getClass().getName();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();

//            getSupportFragmentManager().beginTransaction().replace(R.id.container_body, fragment).addToBackStack(backStateName).commit();

           /* if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.container_body, fragment);
                ft.addToBackStack(backStateName);
                ft.commit();
            }*/

            // set the toolbar title
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public void onBackPressed() {

       /* if (getFragmentManager().getBackStackEntryCount() == 1) {
            super.onBackPressed();
        } else */

        if (getSupportFragmentManager().getBackStackEntryCount() !=  0) {
            getSupportFragmentManager().popBackStack();

            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
//            finish();
        }

    }


}