package com.valvrare.littlekai.valvraretranslation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.valvrare.littlekai.valvraretranslation.adapter.OptionMenuAdapter;
import com.valvrare.littlekai.valvraretranslation.fragment.HomePageFragment;
import com.valvrare.littlekai.valvraretranslation.fragment.LoginFacebookFragment;
import com.valvrare.littlekai.valvraretranslation.model.OptionMenu;
import com.valvrare.littlekai.valvraretranslation.service.FetchLatestService;
import com.valvrare.littlekai.valvraretranslation.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private static String TAG = "Kai";
    private List<OptionMenu> listSystem;
    private List<OptionMenu> listValMenu;
    private MyListView listViewOption, listValMenuSliding;
    private ImageView val_arrow, snk_arrow;
    private DrawerLayout drawerLayout;
    private LinearLayout drawerPane, main_screen, valClickOption, snkClickOption;
    //    private RelativeLayout main_screen;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static MenuItem searchItem, deleteItem, shareItem;
    private boolean isDrawerClosed = true;
    private float lastTranslate = 0.0f;
    boolean doubleBackToExitPressedOnce = false;
    public static String novelType = "Home";
    private HomePageFragment homePageFragment;
    private SharedPreferences prefs;
    public static final String KEY_UPDATE_FREQUENCY = "pref_check_frequency";
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case KEY_UPDATE_FREQUENCY:
                    System.out.println("Setting alarm");
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("Alarm1", false);
                    editor.apply();
                    setAlarmService();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(listener);
        setAlarmService();
    }

    void setAlarmService() {
        if (!prefs.getBoolean("Alarm", false)) {
//            Log.d(TAG, "setAlarmService: start");
            Intent i = new Intent(this, FetchLatestService.class);
            PendingIntent pi = PendingIntent.getService(this, 233, i, 0);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.cancel(pi);
            String frequency = prefs.getString(KEY_UPDATE_FREQUENCY, "180");
            int minute = Integer.parseInt(frequency);
//            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + minute * 60 * 1000,
//                    minute * 60 * 1000, pi);
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + minute * 60 * 1000,
                    minute * 60 * 1000, pi);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Alarm1", true);

            editor.putBoolean("Alarm", false);

            editor.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_activity);
        toolBar.setContentInsetStartWithNavigation(0);
        toolBar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolBar);

        //init component
//        facebookSDKInitialize();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        main_screen = (LinearLayout) findViewById(R.id.main_screen);
        drawerPane = (LinearLayout) findViewById(R.id.drawer_pane);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        listSystem = new ArrayList<>();
        listValMenu = new ArrayList<>();
        homePageFragment = new HomePageFragment();
//        toolBar.setTitleTextColor(0xffffffff);
        //main_content.setBackgroundColor(0xfffc544c);
        facebookSDKInitialize();
        listViewOption = (MyListView) findViewById(R.id.lvOptionMenu);
        listValMenuSliding = (MyListView) findViewById(R.id.lvValListMenu);

        setAlarmService();
        // add item for sliding list
        listSystem.add(new OptionMenu("Setting", "Thiết Lập", R.drawable.ic_settings_white_48dp));
        listSystem.add(new OptionMenu("About", "Thông Tin Ứng Dụng", R.drawable.ic_alert_circle_outline_white_48dp));

        OptionMenuAdapter adapter = new OptionMenuAdapter(this, R.layout.item_nav_list1, listSystem);

        listValMenu.add(new OptionMenu("Home", "Toàn Bộ Danh Sách Truyện", R.drawable.icon_home));
        listValMenu.add(new OptionMenu("Light Novel", "Danh Sách Active Light Novel", R.drawable.icon_books_stack_of_three));
        listValMenu.add(new OptionMenu("Original Novel", "Danh sách Original Novel", R.drawable.icon_book));
        listValMenu.add(new OptionMenu("Teaser Novel", "Danh sách Teaser Light Novel", R.drawable.icon_book_library));
        listValMenu.add(new OptionMenu("Manga", "Danh sách Manga", R.drawable.icon_book_cover));

        OptionMenuAdapter adapter2 = new OptionMenuAdapter(this, R.layout.item_nav_list, listValMenu);

        listViewOption.setAdapter(adapter);
        listValMenuSliding.setAdapter(adapter2);

        listViewOption.setFocusable(false);
        listValMenuSliding.setFocusable(false);
        listValMenuSliding.setActivated(true);
//        expandableListView1.setAdapter(adapter);
//        expandableListView2.setAdapter(adapter2);

        loginFacebookFragmentActive();

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        //Display icon to open/ close sliding list
        //set title
        setTitle(listValMenu.get(0).getTitle());

        //item selected
        listViewOption.setItemChecked(0, true);
        //Close Menu
        drawerLayout.closeDrawer(drawerPane);
        //Display Fragment 1 when start
        if (null != homePageFragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            homePageFragment.setActivity(this);
            transaction.replace(R.id.main_content, homePageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        //Handle on Item Click
        listViewOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewOption.setItemChecked(position, true);
                replaceSystemFragment(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(drawerPane);
                    }
                }, 100);
            }
        });

        listValMenuSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                view.setSelected(true);

                //set title
                setTitle(listValMenu.get(position).getTitle());
                //item selected
                //Replace Fragment
                replaceMenuFragment(position);
                //close Menu
                setBackgroundSelectedValvrareItem(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(drawerPane);
                    }
                }, 100);

            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.drawer_opened, R.string.drawer_closed) {
            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                final float moveFactor = (drawerPane.getWidth() * slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    main_screen.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    main_screen.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
            }

            //Override Methods >> OndrawOpen & OnDrawClose
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                isDrawerClosed = true;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                isDrawerClosed = false;
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        listValMenuSliding.post(new Runnable() {
            @Override
            public void run() {
                if (listValMenuSliding.getChildAt(0) != null) {
                    listValMenuSliding.setItemChecked(0, true);
                }
            }
        });

    }

    private void setBackgroundSelectedValvrareItem(int pos) {
        for (int i = 0; i < listValMenuSliding.getChildCount(); i++)
            listValMenuSliding.setItemChecked(i, false);
        listValMenuSliding.setItemChecked(pos, true);
    }

    public void showMenuBar(View view, int time) {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0);
        animSet.playTogether(anim1);
        animSet.setDuration(time);
        animSet.start();
    }

    public void hideMenuBar(final View view, int time) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -view.getHeight());
        animSet.playTogether(anim1);
        animSet.setDuration(time);
        animSet.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (!isDrawerClosed) {
                    drawerLayout.closeDrawer(drawerPane);
                } else {
                    if (doubleBackToExitPressedOnce) {
                        this.finish();
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Nhấn Back lần nữa để thoát!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   getMenuInflater().inflate(R.menu.setting_menu,menu);
        getMenuInflater().inflate(R.menu.activity_actions, menu);
        searchItem = menu.findItem(R.id.action_search);
        shareItem = menu.findItem(R.id.action_share);
        deleteItem = menu.findItem(R.id.action_delete);
        deleteItem.setVisible(false);
        shareItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
//                toggleSearch(false);
                Intent intent = new Intent(MainActivity.this, NovelSearchActivity.class);
//                intent.putParcelableArrayListExtra("novels", novels);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                return true;
            case R.id.action_delete:
                homePageFragment.deleteHistoryTask();
                return true;
        }
//        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    //Tao hieu ung tren nut sliding
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //Create Method replace fragment
    public void replaceSystemFragment(int pos) {
        switch (pos) {
            case 0:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
//                fragment = new SettingFragment();
                break;
            case 1:
                Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }

    }

    public void replaceMenuFragment(int pos) {

        switch (pos) {
            case 0:
                novelType = "Home";
                setTitle("Danh Sách Truyện");
                break;
            case 1:

                novelType = "LightNovel";
                //main_content.setBackgroundColor(0xff5affef);
                break;
            case 2:
                novelType = "OriginalNovel";
                //main_content.setBackgroundColor(0xffffd04e);
                break;
            case 3:
                novelType = "TeaserNovel";
                //main_content.setBackgroundColor(0xffe24bff);
                break;
            case 4:
                novelType = "Manga";
                //main_content.setBackgroundColor(0xffe24bff);
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        homePageFragment = new HomePageFragment();
        transaction.replace(R.id.main_content, homePageFragment);
        transaction.addToBackStack(null);
        transaction.commit();

//        homePageFragment.changeTypeList();
    }

    public void loginFacebookFragmentActive() {
        Fragment fragment = new LoginFacebookFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.rlout_loginface, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void hideDeleteOption() {
        if (deleteItem != null)
            deleteItem.setVisible(false);
    }

    public void showDeleteOption() {
        if (deleteItem != null)
            deleteItem.setVisible(true);
    }

    public void hideSearchOption() {
        searchItem.setVisible(false);
    }

    public void showSearchOption() {
        searchItem.setVisible(true);
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }


}
