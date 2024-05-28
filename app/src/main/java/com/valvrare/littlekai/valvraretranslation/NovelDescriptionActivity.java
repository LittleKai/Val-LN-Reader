package com.valvrare.littlekai.valvraretranslation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.aakira.expandablelayout.Utils;
import com.valvrare.littlekai.valvraretranslation.adapter.ViewPagerNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.task.GetSnkNovelChaptersListTask;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment1;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment2;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment3;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.FacebookLoginDialog;
import com.valvrare.littlekai.valvraretranslation.widget.CustomViewPager;

import java.io.IOException;
import java.util.ArrayList;

public class NovelDescriptionActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "Kai";
    CallbackManager callbackManager;
    private Context context;
    private ValvrareDatabaseHelper db;
    private LNReaderApplication lnReaderApplication;
    private String novelName = "1", image;
    private String title = "Nội Dung Truyện";
    private Toolbar toolbar;
    private LinearLayout novelMainLayout, layoutMenu, llout_descripTitle, download_button;
    private RelativeLayout triangle_button;
    private boolean isTitleHide;
    private int titleLLout;
    private TabLayout tabLayout;
    private Novel novel;
    private CustomViewPager viewPager;
    private ImageView iv_followedNovel, iv_novel_avatar;
    private TextView tv_novelName, tv_chapterNumber, tv_View, tv_LastestChap, tv_rateCount, tv_rate, tv_updateDateDescrip;
    private MenuItem searchItem;
    private Profile profile;
    private static Object obj_frag;
    private NovelFragment2 novelFragment2;
    private AppBarLayout appbar;
    private boolean isDownloadedNovel = false;
    private ProgressDialog progressDialog;
    private ViewPagerNovelAdapter viewPagerNovelAdapter;
    private GetSnkNovelChaptersListTask getSnkNovelChaptersListTask;

    public boolean isDownloadedNovel() {
        return isDownloadedNovel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_description);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
//        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lnReaderApplication = (LNReaderApplication) getApplicationContext();

        facebookSDKInitialize();
        lnReaderApplication.setNovelDescriptionActivity(this);

        isTitleHide = false;
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        llout_descripTitle = (LinearLayout) findViewById(R.id.llout_descripTitle);
        novelMainLayout = (LinearLayout) findViewById(R.id.novelMainLayout);
        layoutMenu = (LinearLayout) findViewById(R.id.llout_novelTitle);
        iv_novel_avatar = (ImageView) findViewById(R.id.im_novel_avatar);
        download_button = (LinearLayout) findViewById(R.id.download_button);
        download_button.setClickable(false);
        tv_rate = (TextView) findViewById(R.id.tv_RateDescrip);
        tv_rateCount = (TextView) findViewById(R.id.tv_rateCountDescrip);
        tv_novelName = (TextView) findViewById(R.id.tv_novelNameDescirption);
        tv_chapterNumber = (TextView) findViewById(R.id.tv_chapterCountDescrip);
        tv_View = (TextView) findViewById(R.id.tv_ViewDescrip);
        iv_followedNovel = (ImageView) findViewById(R.id.iv_followedNovel);
        tv_LastestChap = (TextView) findViewById(R.id.tv_LastestChapterDescrip);
        triangle_button = (RelativeLayout) findViewById(R.id.triangle_btn);

        context = this.getApplicationContext();
        db = new ValvrareDatabaseHelper(context);

        novel = getIntent().getParcelableExtra("novel");
        isDownloadedNovel = getIntent().getBooleanExtra("downloaded", false);
        if (novel != null) {
            novelName = novel.getNovelName();
            //check and set set Favarite Image
            if (db.isNovelFavorited(novel)) {
                iv_followedNovel.setImageResource(R.drawable.ic_heart_outline_red_48dp);
            } else {
                iv_followedNovel.setImageResource(R.drawable.ic_heart_outline_white_48dp);
            }

            tv_updateDateDescrip = (TextView) findViewById(R.id.tv_updateDateDescrip);
            image = novel.getImage();
            String lastestChap = novel.getLatestChapName();
            tv_novelName.setText(novelName);

            tv_chapterNumber.setText("Số Chương: 0");
            if (lastestChap != null)
                tv_LastestChap.setText(Html.fromHtml("Chương Mới Nhất: " + "\"" + "<b>" + lastestChap + "</b>" + "\""));
            else
                tv_LastestChap.setText("Chương Mới Nhất:");

            getInfoNovel();

        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout_novel);

        viewPager = (CustomViewPager) findViewById(R.id.view_pager_novel);
        viewPager.setActivity(this);
        viewPagerNovelAdapter = new ViewPagerNovelAdapter(getSupportFragmentManager());
        viewPagerNovelAdapter.addFragments(new NovelFragment1(), "GIỚI THIỆU");
        novelFragment2 = new NovelFragment2();
        viewPagerNovelAdapter.addFragments(novelFragment2, "CHƯƠNG");
        viewPagerNovelAdapter.addFragments(new NovelFragment3(), "BÌNH LUẬN");
        obj_frag = viewPagerNovelAdapter.getItem(2);
//        viewPager.requestDisallowInterceptTouchEvent(true);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerNovelAdapter);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager)

                {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        switch (tabLayout.getSelectedTabPosition()) {
                            case 0:
                                title = "Giới Thiệu Chung";
                                if (searchItem != null)
                                    searchItem.setVisible(false);
                                break;
                            case 1:
                                title = "Danh Sách Chương";
                                if (searchItem != null)
                                    searchItem.setVisible(true);
                                break;
                            case 2:
                                title = "Bình Luận";
                                if (searchItem != null)
                                    searchItem.setVisible(false);
                                break;
                        }
                        setTitle(title);
                    }
                });
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab != null)
            tab.select();
        viewPager.requestDisallowInterceptTouchEvent(true);
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            final int DISTANCE = 150;
//            float startY = 0;
//            float distY = 0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (!isTitleHide) {
//                    int action = event.getAction();
//                    if (action == MotionEvent.ACTION_DOWN) {
//                        startY = event.getY();
//                    } else if (action == MotionEvent.ACTION_MOVE) {
//                        distY = event.getY() - startY;
//                        if (pxToDp((int) distY) < DISTANCE) {
//                            hideTitleNovel();
//                        }
//                    } else if (action == MotionEvent.ACTION_UP) {
//                        startY = 0;
//                    }
//                }
//                return true;
//            }
//        });
        setTitle(title);
// inflate tab item into viewpager

//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            LinearLayout relativeLayout = (LinearLayout)
//                    LayoutInflater.from(this).inflate(R.layout.tab_layout, tabLayout, false);
//
//            View view = relativeLayout.findViewById(R.id.view_verticalLine);
//
//            if(view!=null&&i==0)
//            view.setVisibility(View.GONE);
//
//            TextView tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
//            tabTextView.setText(tab.getText());
//            tab.setCustomView(relativeLayout);
//            tab.select();
//        }
        tv_novelName.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llout_descripTitle.getLayoutParams();
                params.height = tv_novelName.getMeasuredHeight() + 2;
                llout_descripTitle.setLayoutParams(params);
            }
        });

        viewPager.setOnViewPagerScrollListener(new CustomViewPager.onViewPagerScrollListener() {
            @Override
            public void viewPagerScrollListener() {
                hideTitleNovel();
            }
        });
        lnReaderApplication.setNovelFragment2(novelFragment2);
    }


    private void getInfoNovel() {
        if (novel.getUrl().contains(Constants.VAL_URL_ROOT)) {

            new AsyncTask<Novel, Void, Novel>() {
                @Override
                protected Novel doInBackground(Novel... params) {
                    Novel result = null;
                    Novel novelGet = params[0];
                    NovelLibrary novelLibrary = new NovelLibrary(novelGet.getUrl());
                    try {
                        result = novelLibrary.getNovelStatus(novelGet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Novel aVoid) {
                    super.onPostExecute(aVoid);
                    if (aVoid == null) {
                        String updateDate = novel.getUpdateDate();
                        int view = novel.getView();
                        double rateValue = novel.getRate();
                        int rateCount = novel.getRateCount();
                        if (updateDate != null)
                            tv_updateDateDescrip.setText(Html.fromHtml("Ngày Cập Nhật: " + "<b>" + updateDate + "</b>"));
                        else tv_updateDateDescrip.setText("Ngày Cập Nhật: ");
                        tv_rate.setText(rateValue + "/5");
                        tv_rateCount.setText(rateCount + " Rates");
                        tv_View.setText(Html.fromHtml("Lượt Xem: " + "<b>" + view + "</b>"));
                    } else {
                        String updateDate = aVoid.getUpdateDate();
                        int view = aVoid.getView();
                        double rateValue = aVoid.getRate();
                        int rateCount = aVoid.getRateCount();
                        novel.setUpdateDate(updateDate);
                        novel.setView(view);
                        novel.setRate(rateValue);
                        novel.setRateCount(rateCount);
                        if (updateDate != null)
                            tv_updateDateDescrip.setText(Html.fromHtml("Ngày Cập Nhật: " + "<b>" + updateDate + "</b>"));
                        else tv_updateDateDescrip.setText("Ngày Cập Nhật: ");
                        tv_rate.setText(rateValue + "/5");
                        tv_rateCount.setText(rateCount + " Rates");
                        tv_View.setText(Html.fromHtml("Lượt Xem: " + "<b>" + view + "</b>"));
                    }
                }
            }.execute(novel);

            setAvatarNovel(novel);

        } else if (novel.getUrl().contains(Constants.SNK_URL_ROOT)) {
            tv_View.setText("Thể Loại: ");
            tv_updateDateDescrip.setVisibility(View.GONE);
            if (!db.getInfoSnkNovel(novel)) {
                progressDialog = new ProgressDialog(NovelDescriptionActivity.this);
                progressDialog.setMessage("Đang lấy danh sách chương");
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setCancelable(false);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (getSnkNovelChaptersListTask != null) getSnkNovelChaptersListTask.setStop(true);
                        NovelDescriptionActivity.this.finish();
                    }
                });
                getSnkNovelChaptersListTask = new GetSnkNovelChaptersListTask(NovelDescriptionActivity.this, novel);
                progressDialog.show();
            } else {
                tv_View.setText(Html.fromHtml("Thể Loại: " + "<b>" + novel.getCatelogies() + "</b>"));
                setAvatarNovel(novel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
    }

    private boolean isActive;

    public void getSnkNovelUpdateProgressCallback(String update) {
        if (update != null)
            progressDialog.setMessage("Đang lấy danh sách chương (" + update + " %)");
    }

    public void getSnkNovelCallback(Novel novel) {
        if (isActive)
            if (novel.getChapterList() == null) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(context, "Lỗi Lấy Danh Sách Chương hoặc Số Chương Trong Truyện = 0!", Toast.LENGTH_LONG).show();
            } else {
                if (db.isNovelFavorited(novel)){
                    db.updateSnkFavNovelDate(novel);
                    db.insertFavNovel(novel);
                }
                this.novel = novel;
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (viewPager != null & viewPagerNovelAdapter != null) {
                    viewPager.setAdapter(viewPagerNovelAdapter);
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    if (tab != null)
                        tab.select();
                }
                tv_View.setText(Html.fromHtml("Thể Loại: " + "<b>" + novel.getCatelogies() + "</b>"));
                setAvatarNovel(novel);
            }
    }

    public static Object getViewPagerItem() {
        return obj_frag;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
//        getMenuInflater().inflate(R.menu.activity_actions,menu);
        searchItem = menu.findItem(R.id.action_descrip_search);
//        searchItem.setVisible(false);
        MenuItem updateItem = menu.findItem(R.id.action_descrip_update);

        if (novel != null)
            if (novel.getUrl().contains(Constants.SNK_URL_ROOT))
                updateItem.setVisible(true);
            else updateItem.setVisible(false);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Nhập Tên Chương Cần Tìm");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_descrip_update:
                if (novel.getUrl().contains(Constants.SNK_URL_ROOT)) {
                    progressDialog = new ProgressDialog(NovelDescriptionActivity.this);
                    progressDialog.setMessage("Đang lấy danh sách chương");
                    progressDialog.setCanceledOnTouchOutside(false);

                    new GetSnkNovelChaptersListTask(NovelDescriptionActivity.this, novel);

                    progressDialog.show();
                }
                return true;
            case R.id.action_descrip_share:
                if (FunctionHelper.isNetworkAvailable(context)) {

                    profile = Profile.getCurrentProfile();
                    if (profile == null) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        DialogFragment newFragment = FacebookLoginDialog.newInstance(novel.getUrl());
                        newFragment.show(ft, "dialog");
                    } else {
                        ShareDialog shareDialog = new ShareDialog(this);  // intialize facebook shareDialog.
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(novel.getUrl()))
                                    .build();

                            shareDialog.show(linkContent);  // Show facebook ShareDialog
                        }
                    }
                } else
                    Toast.makeText(context, "Xin hãy kiểm tra lại kết nối!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public Novel getNovel() {
        return novel;
    }


    public void showTitleLayout() {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, 0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(tabLayout, View.TRANSLATION_Y, 0);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(viewPager, View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1, anim2, anim3);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideTitleLayout() {
        AnimatorSet animSet = new AnimatorSet();
        titleLLout = layoutMenu.getHeight();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, -titleLLout);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(tabLayout, View.TRANSLATION_Y, -titleLLout);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(viewPager, View.TRANSLATION_Y, -titleLLout);
        animSet.playTogether(anim1, anim2, anim3);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideTitleNovel() {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(triangle_button, "rotation", 0f, 180f);
        animator.setDuration(300);
        animSet.playTogether(animator);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        animSet.start();

        hideTitleLayout();
        int viewPagerHeight = viewPager.getHeight();
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        titleLLout = layoutMenu.getHeight();
        params.height = viewPagerHeight + titleLLout;
        viewPager.setLayoutParams(params);
        isTitleHide = true;
    }

    public void showTitleNovel() {

        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(triangle_button, "rotation", 180f, 360f);
        animator.setDuration(300);
        animSet.playTogether(animator);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        animSet.start();

        showTitleLayout();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int viewPagerHeight = viewPager.getHeight();
                ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                titleLLout = layoutMenu.getHeight();
                params.height = viewPagerHeight - titleLLout;
                viewPager.setLayoutParams(params);
            }
        }, 300);

        isTitleHide = false;
    }

    public void expandTitleNovel(View view) {
        if (!isTitleHide)
            hideTitleNovel();
        else
            showTitleNovel();
    }

    public void novelRate(View view) {
        Toast.makeText(NovelDescriptionActivity.this, "Chức năng đang được phát triển!", Toast.LENGTH_SHORT).show();
    }

    public void novelFollow(View view) {
        if (!db.isNovelFavorited(novel)) {
            Toast.makeText(NovelDescriptionActivity.this, "Thêm vào mục Truyện Yêu Thích", Toast.LENGTH_SHORT).show();
            iv_followedNovel.setImageResource(R.drawable.ic_heart_outline_red_48dp);
            novel.setFav(true);
            db.getSnkNovelLastCheck(novel);
            db.insertFavNovel(novel);
        } else {
            Toast.makeText(NovelDescriptionActivity.this, "Xóa khỏi mục Truyện Yêu Thích", Toast.LENGTH_SHORT).show();
            iv_followedNovel.setImageResource(R.drawable.ic_heart_outline_white_48dp);
            novel.setFav(false);
            db.unfavoritedNovel(novel.getNovelName());
        }
    }

    public void novelDownload(View view) {
        ArrayList<Chapter> chapters = novelFragment2.getListChapters();
        if (lnReaderApplication.isDownloading()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Thông Báo")
                    .setMessage("Hiện đang tải chương, hãy đợi đến khi đã Hoàn Thành Tải Chương hoặc chọn Hủy Tiến Trình Tải.")
                    .setCancelable(false)
                    .setNegativeButton("Đã Hiểu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (chapters != null) {
            Intent intent = new Intent(NovelDescriptionActivity.this, DownloadActivity.class);
            intent.putParcelableArrayListExtra("chapters", chapters);
            intent.putExtra("novel", novel);
            startActivity(intent);
        } else
            Toast.makeText(NovelDescriptionActivity.this, "Không Có Chương hoặc Không Có Dữ Liệu!", Toast.LENGTH_SHORT).show();
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
//        if(callbackManager!=null)
//        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
//        else
    }

    public void setNovelLastest(String lastest, boolean saveAllowed) {
        novel.setLatestChapName(lastest);
        tv_LastestChap.setText(Html.fromHtml("Chương Mới Nhất: " + "\"" + "<b>" + lastest + "</b>" + "\""));
        if (saveAllowed)
            if (db.isNovelFavorited(novel))
                db.insertFavNovel(novel);
    }

    public void changeMaxChapter(int chapCount) {
        download_button.setClickable(true);
        tv_chapterNumber.setText(Html.fromHtml("Số Chương: " + "<b>" + chapCount + "</b>"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        profile = Profile.getCurrentProfile();
    }

    public boolean isTitleHide() {
        return isTitleHide;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String key) {
        novelFragment2.searchChapter(key);
        return true;
    }

    void setAvatarNovel(Novel novel) {
        String image = null;
        if (novel.getUrl().contains(Constants.VAL_URL_ROOT))
            image = novel.getImage();
        else if (novel.getUrl().contains(Constants.SNK_URL_ROOT))
            image = db.getSnkSecondImage(novel);

        Glide.with(this).load(image).
                listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        layoutMenu.setBackgroundColor(0x504f88a6);
//                        toolbar.setBackgroundColor(0x005088a6);
//                        llout_descripTitle.setBackgroundColor(0x504f88a6);
//                        tabLayout.setBackgroundColor(0x504f88a6);
//                        appbar.setBackgroundColor(0x504f88a6);

                        layoutMenu.setBackgroundColor(0x694f88a6);
                        toolbar.setBackgroundColor(0x694f88a6);
                        llout_descripTitle.setBackgroundColor(0x694f88a6);
                        tabLayout.setBackgroundColor(0x694f88a6);
//                        tabLayout.setTabTextColors(0xff0070a8,0xffffffff);
                        appbar.setBackgroundColor(0x694f88a6);
                        return false;
                    }
                }).into(iv_novel_avatar);
    }

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        return Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}


