package com.cryptonice.revue;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.*;

import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private static final String url = "jdbc:mysql://192.168.162.134:3306/revue";
    private static final String user = "admin";
    private static final String pass = "admin";
    public static Connection connection;
    public static String selected_item = "";

    public void connectToDatabase() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            Toast.makeText(getApplicationContext(), "Connected to database.", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setTabsAdapter() {
        List<Fragment> fragments = new Vector<>();

        TopFragment top_fragment = new TopFragment();
        CategoriesFragment categories_fragment = new CategoriesFragment();
        FavoritesFragment favorites_fragment = new FavoritesFragment();

        top_fragment.connection = connection;

        fragments.add(top_fragment);
        fragments.add(categories_fragment);
        fragments.add(favorites_fragment);

        MainActivity.TabsPageAdapter tabs_adapter = new MainActivity.TabsPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager tabs_pager = (ViewPager) findViewById(R.id.tabs_pager);
        tabs_pager.setAdapter(tabs_adapter);
        tabs_pager.setPageTransformer(false, new AnimatePageTransformer(AnimatePageTransformer.TransformType.ZOOM));
        TabLayout booksTabs = (TabLayout) findViewById(R.id.tab_layout);
        booksTabs.setupWithViewPager(tabs_pager);
        booksTabs.getTabAt(0).setIcon(R.drawable.icon_top);
        booksTabs.getTabAt(1).setIcon(R.drawable.icon_categories);
        booksTabs.getTabAt(2).setIcon(R.drawable.icon_favorites);

        int tabIconUnselected = ContextCompat.getColor(getApplicationContext(), R.color.colorUnselected);
        int tabIconSelected = ContextCompat.getColor(getApplicationContext(), R.color.colorSelected);
        booksTabs.getTabAt(0).getIcon().setColorFilter(tabIconSelected, PorterDuff.Mode.SRC_IN);
        booksTabs.getTabAt(1).getIcon().setColorFilter(tabIconUnselected, PorterDuff.Mode.SRC_IN);
        booksTabs.getTabAt(2).getIcon().setColorFilter(tabIconUnselected, PorterDuff.Mode.SRC_IN);

        booksTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorSelected);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorUnselected);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
        TextView toolbar_title = (TextView) toolbar_main.findViewById(R.id.txt_app_name);
        Typeface font_coves_bold = Typeface.createFromAsset(getAssets(), "coves_bold.otf");

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        toolbar_title.setTypeface(font_coves_bold);
        connectToDatabase();
        setTabsAdapter();
    }

    public class TabsPageAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;
        private long baseId = 0;

        public TabsPageAdapter (FragmentManager fragManager, List<Fragment> fragmentList) {
            super(fragManager);

            this.fragments = fragmentList;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

        @Override
        public Fragment getItem (int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                case 1:
                case 2:
                {
                    return "";
                }
            }
            return null;
        }
    }
}
