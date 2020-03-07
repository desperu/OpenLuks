package org.desperu.openluks;

import android.os.Build;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.desperu.openluks.ui.Base.BaseActivity;
import org.desperu.openluks.view.ViewPagerAdapter;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    // FOR DESIGN
    @BindView(R.id.activity_main_view_pager) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.activity_main_drawer_layout) DrawerLayout drawerLayout;
//    @BindView(R.id.toolbar_search_view) SearchView searchView;
    @BindView(R.id.activity_main_nav_view) NavigationView navigationView;

    // FOR DATA
//    @State int currentFragment = -1;
//    private Fragment fragment;

    // --------------
    // BASE METHODS
    // --------------

    @Override
    protected int getActivityLayout() { return R.layout.activity_main; }

    @Override
    protected void configureDesign() {
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureViewPagerAndTabs();
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//        ViewPager viewPager = findViewById(R.id.view_pager);
//        viewPager.setAdapter(sectionsPagerAdapter);
//        TabLayout tabs = findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);
//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // Menu drawer
//            case R.id.activity_main_menu_drawer_your_lunch:
//                this.onClickYourLunch();
//                break;
//            case R.id.activity_main_menu_drawer_settings:
//                this.showSettingsActivity();
//                break;
//            case R.id.activity_main_menu_drawer_log_out:
//                this.logOut();
//                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        if (this.drawerLayout.isDrawerOpen(GravityCompat.START))
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//        else if (this.searchView != null && this.searchView.isShown()) {
//            this.hideSearchViewIfVisible();
//        } else
//            super.onBackPressed();
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                onSearchTextChange(query);
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String s) {
//                onSearchTextChange(s);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
//        if (item.getItemId() == R.id.activity_main_menu_search) {
//            searchView.setVisibility(View.VISIBLE);
//            searchView.onActionViewExpanded();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//        // To check nav drawer item when view pager change page.
//        navigationView.getMenu().getItem(viewPager.getCurrentItem()).setChecked(true);
//    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure Tab layout and View pager.
     */
    private void configureViewPagerAndTabs() {
        viewPager.setAdapter(new ViewPagerAdapter(getBaseContext(), getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));

        TabLayout tabLayout = findViewById(R.id.activity_main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    /**
     * Configure Drawer layout.
     */
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Configure Navigation Drawer.
     */
    private void configureNavigationView() {
        // Support status bar for KitKat.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            navigationView.setPadding(0, 0, 0, 0);
        navigationView.setNavigationItemSelectedListener(this);
        // Disable check item
//        navigationView.getMenu().setGroupCheckable(R.id.activity_main_menu_drawer_group, false, false);
    }

//    /**
//     * Configure and show corresponding fragment.
//     * @param fragmentKey Key for fragment.
//     */
//    private void configureAndShowFragment(int fragmentKey) {
//        String titleActivity = getString(R.string.title_activity_main);
//        Bundle bundle = new Bundle();

//        fragment = getSupportFragmentManager()
//                .findFragmentById(R.id.activity_main_frame_layout);
//
//        if (currentFragment != fragmentKey) {
//            switch (fragmentKey) {
//                case MAP_FRAGMENT:
//                    fragment = MapsFragment.newInstance();
//                    bundle.putStringArrayList(PLACE_ID_LIST_MAPS, placeIdList);
//                    if (this.bounds != null)
//                        bundle.putParcelable(CAMERA_POSITION, cameraPosition);
//                    if (this.queryTerm != null && !this.queryTerm.isEmpty())
//                        bundle.putString(QUERY_TERM_MAPS, queryTerm);
//                    bundle.putBoolean(MapsFragment.IS_QUERY_FOR_RESTAURANT_MAPS, isQueryForRestaurant);
//                    fragment.setArguments(bundle);
//                    break;
//                case LIST_FRAGMENT:
//                    fragment = RestaurantListFragment.newInstance();
//                    bundle.putStringArrayList(PLACE_ID_LIST_RESTAURANT_LIST, placeIdList);
//                    bundle.putParcelable(BOUNDS, bounds);
//                    if (this.queryTerm != null && !this.queryTerm.isEmpty())
//                        bundle.putString(QUERY_TERM_LIST, queryTerm);
//                    bundle.putBoolean(IS_QUERY_FOR_RESTAURANT_LIST, isQueryForRestaurant);
//                    bundle.putParcelable(NEARBY_BOUNDS, nearbyBounds);
//                    bundle.putParcelable(USER_LOCATION, userLocation);
//                    fragment.setArguments(bundle);
//                    break;
//                case WORKMATES_FRAGMENT:
//                    fragment = WorkmatesFragment.newInstance();
//                    if (this.queryTerm != null && !this.queryTerm.isEmpty())
//                        bundle.putString(QUERY_TERM_WORKMATES, queryTerm);
//                    fragment.setArguments(bundle);
//                    titleActivity = getString(R.string.title_fragment_workmates);
//                    break;
//                case CHAT_FRAGMENT:
//                    fragment = ChatFragment.newInstance();
//                    this.hideSearchViewIfVisible();
//                    titleActivity = getString(R.string.title_fragment_chat);
//                    break;
//            }

//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.activity_main_frame_layout, fragment)
//                    .commit();

//            this.setTitleActivity(titleActivity);
//            if (toolbar.findViewById(R.id.activity_main_menu_search) != null)
//                toolbar.findViewById(R.id.activity_main_menu_search).setVisibility(
//                        fragmentKey != CHAT_FRAGMENT ? View.VISIBLE : View.GONE);
//        }
//        currentFragment = fragmentKey;
//    }
}