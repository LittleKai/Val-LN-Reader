package com.valvrare.littlekai.valvraretranslation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class UpdateLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_log);
      Toolbar  toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitle("Nhật Ký Cập Nhật");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        TextView txtLog = (TextView) findViewById(R.id.txtUpdateLog);
//        txtLog.setTextSize(18);
        // limited support on html tag, see: http://stackoverflow.com/a/3150456
//        txtLog.setText(Html.fromHtml("<h1>Credits</h1><h2>UI and Parsers</h2>" +
//                "- erakk<br />" +
//                "- <a href=\"https://nandaka.devnull.zone/\">nandaka</a><br />" +
//                "- Thatdot7<br />" +
//                "- <a href=\"http://nstranslation.blogspot.com\">freedomofkeima</a><br />" +
//                "<h2>UI Translations</h2>" +
//                "- English : erakk & nandaka<br />" +
//                "- Indonesian : freedomofkeima<br />" +
//                "- French : Lery<br />" +
//                "<br />" +
//                "And other people contributing through <a href=\"http://baka-tsuki.org/forums/\">baka-tsuki forum</a> :D<br />"));
//        // allow link to be clickable, see: http://stackoverflow.com/a/8722574
//        txtLog.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
