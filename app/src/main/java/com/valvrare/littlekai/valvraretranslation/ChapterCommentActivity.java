package com.valvrare.littlekai.valvraretranslation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment3;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;

public class ChapterCommentActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_comment);
        Chapter chapter = getIntent().getParcelableExtra("chapter");
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity);
        toolbar.setTitle(chapter.getNovelName());
        toolbar.setSubtitle(chapter.getName());
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setSubtitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RelativeLayout fb_container = (RelativeLayout) findViewById(R.id.fb_container);


//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
        String url = getIntent().getStringExtra("url");
//        }
        Log.d("Kai", "onCreate: " + url + ", " + chapter.getNovelName() + ", " + chapter.getName());


        Fragment fragment = null;
        fragment = new NovelFragment3();
        final Bundle args = new Bundle();
        args.putString("url", url);

        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fb_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onBackPressed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                this.finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }
}