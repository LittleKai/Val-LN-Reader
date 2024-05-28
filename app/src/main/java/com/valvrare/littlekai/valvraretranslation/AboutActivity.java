package com.valvrare.littlekai.valvraretranslation;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitle("Thông Tin Ứng Dụng");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        String version = "N/A";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " (" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ")";
            tv_version.setText("Phiên Bản: " + version + " (Beta)");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Kai", "Cannot get version.", e);
        }

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

    public void facebook_feedback(View view) {
        Uri uri = Uri.parse("https://www.facebook.com/anhduc.nguyen.311"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void checkVersion(View view) {
        Toast.makeText(AboutActivity.this, "Chức năng đang được phát triển!", Toast.LENGTH_SHORT).show();
    }

    public void googleFeekback(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","littlekai.valvrare@gmail.com", null));
//        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

    public void rateApp(View view) {
        Toast.makeText(AboutActivity.this, "Chức năng đang được phát triển!", Toast.LENGTH_SHORT).show();
    }

    public void fanpageGoto(View view) {
        Uri uri = Uri.parse("https://www.facebook.com/H%E1%BB%99i-nh%E1%BB%AFng-ng%C6%B0%E1%BB%9Di-y%C3%AAu-th%C3%ADch-Light-Novel-563135230470838"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void introduce(View view) {
    final Dialog dialog = new Dialog(AboutActivity.this);
//        dialog.setTitle("Chọn Font Chữ:");
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.layout_val_team_introduce);
    dialog.show();
    }

    public void browser_feedback(View view) {
        Uri uri = Uri.parse("http://valvrareteam.com/apps.html"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
