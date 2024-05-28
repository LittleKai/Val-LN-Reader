package com.valvrare.littlekai.valvraretranslation;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;

import java.io.File;
import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {
    private AppCompatDelegate mDelegate;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0)
            super.onBackPressed();
        else getFragmentManager().popBackStack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference != null)
            if (preference instanceof PreferenceScreen) {
                setUpNestedScreen((PreferenceScreen) preference);
            }
        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // HACK: Try to handle android.os.BadParcelableException: ClassNotFoundException when unmarshalling: android.support.v7.widget.Toolbar$SavedState
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception ex) {
            Log.e("Kai", "Failed to restore instance state.");
        }
    }

    /**
     * Enable toolbar on child screen
     * http://stackoverflow.com/a/27455330
     *
     * @param preferenceScreen
     */
    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        Toolbar bar = null;
        try {
            View tempView = dialog.findViewById(android.R.id.list);
            ViewParent viewParent = tempView.getParent();
            if (viewParent != null && viewParent instanceof LinearLayout) {
                LinearLayout root = (LinearLayout) viewParent;
                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                root.addView(bar, 0); // insert at top
            } else
                Log.i("Kai", "setUpNestedScreen() using unknown Layout: " + viewParent.getClass().toString());
        } catch (Exception ex) {
            Log.w("Kai", "Failed to get Toolbar on Settings Page", ex);
        }

        if (bar != null) {
            bar.setTitle(preferenceScreen.getTitle());
            bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setupActionBar();
        addPreferencesFromResource(R.xml.preferences);
        notification_pref();
        setCssPreferences();
        setStoragePreferences();
    }

    void notification_pref() {
        bindPreferenceSummaryToValue(findPreference("pref_check_frequency"));
    }

    ActionBar actionBar;

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
//        actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle("Thiết Lập");
//        }

        Toolbar toolbar = null;
        try {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.list).getParent().getParent().getParent();
            toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(toolbar, 0);
        } catch (Exception ex) {
            Log.w("Kai", "Failed to get Toolbar on Settings Page", ex);
        }
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setStoragePreferences() {
        Preference log = findPreference("update_log");
        log.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent = new Intent(getApplicationContext(), UpdateLogActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("Kai", "Update Logs", e);
                }
                return false;
            }
        });

        MemorySizeCalculator calculator = new MemorySizeCalculator(this);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        Log.d("Kai", "setStoragePreferences: " + defaultBitmapPoolSize + ", " + defaultMemoryCacheSize);

        Preference clearImages = findPreference("clear_image_cache");
        clearImages.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference p) {
                clearImages();
                return true;
            }
        });
    }

    private void clearImages() {
        final String imageRoot = UIHelper.getImageRoot(this);
        UIHelper.createYesNoDialog(this, "Bạn có chắc chắn muốn xóa Bộ Nhớ Đệm không?", "Xóa Image Cache", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    clearCache();
//                        DeleteRecursive(new File(imageRoot));
                }
            }
        }).show();
    }

    private void setCssPreferences() {
        final Preference lineSpacePref = findPreference(Constants.PREF_LINESPACING);
        final Preference marginPref = findPreference(Constants.PREF_MARGINS);

        // Retrieve inital values stored
        String currLineSpacing = getPreferenceScreen().getSharedPreferences().getString(Constants.PREF_LINESPACING, "120");
        String currMargin = getPreferenceScreen().getSharedPreferences().getString(Constants.PREF_MARGINS, "5");

        // Behaviour 3 (Activity first loaded)
        lineSpacePref.setSummary("Tăng độ rộng khoảng cách giữa các dòng. Giá trị càng cao, khoảng cách giữa các dòng càng lớn.\nGiá trị Hiện Tại: " + currLineSpacing);
        marginPref.setSummary("Thay đổi khoảng trống giữa chữ và hai cạnh bên của màn hình.\nGiá trị Hiện Tại: " + currMargin + "%");

        lineSpacePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String set = (String) newValue;
                preference.setSummary(getResources().getString(R.string.line_spacing_summary2) + " \nGiá trị Hiện Tại: " + set);
                return true;
            }
        });

        marginPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String set = (String) newValue;
                preference.setSummary(getResources().getString(R.string.margin_summary2) + " \nGiá trị Hiện Tại: " + set + "%");
                return true;
            }
        });

        final Preference css_backColorPref = findPreference(Constants.PREF_CSS_BACKGROUND);
        final Preference css_foreColorPref = findPreference(Constants.PREF_CSS_FOREGROUND);
        final Preference css_linkColorPref = findPreference(Constants.PREF_CSS_LINK_COLOR);
        final Preference css_tableBorderColorPref = findPreference(Constants.PREF_CSS_TABLE_BORDER);
        final Preference css_tableBackPref = findPreference(Constants.PREF_CSS_TABLE_BACKGROUND);

        css_backColorPref.setSummary(UIHelper.getBackgroundColor(this));
        css_backColorPref.setOnPreferenceChangeListener(colorChangeListener);
        setColorIcon(css_backColorPref, UIHelper.getBackgroundColor(this));

        css_foreColorPref.setSummary(UIHelper.getForegroundColor(this));
        css_foreColorPref.setOnPreferenceChangeListener(colorChangeListener);
        setColorIcon(css_foreColorPref, UIHelper.getForegroundColor(this));

        css_linkColorPref.setSummary(UIHelper.getLinkColor(this));
        css_linkColorPref.setOnPreferenceChangeListener(colorChangeListener);
        setColorIcon(css_linkColorPref, UIHelper.getLinkColor(this));

        css_tableBorderColorPref.setSummary(UIHelper.getThumbBorderColor(this));
        css_tableBorderColorPref.setOnPreferenceChangeListener(colorChangeListener);
        setColorIcon(css_tableBorderColorPref, UIHelper.getThumbBorderColor(this));

        css_tableBackPref.setSummary(UIHelper.getThumbBackgroundColor(this));
        css_tableBackPref.setOnPreferenceChangeListener(colorChangeListener);
        setColorIcon(css_tableBackPref, UIHelper.getThumbBackgroundColor(this));
    }

    private void setColorIcon(Preference colorPref, String hexColor) {
        int c = Color.parseColor(hexColor);
        Drawable d1 = getResources().getDrawable(R.drawable.ic_square);
        d1.mutate().setColorFilter(c, PorterDuff.Mode.MULTIPLY);
        colorPref.setIcon(d1);
    }

    private final Preference.OnPreferenceChangeListener colorChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String set = (String) newValue;
            try {
                int c = Color.parseColor(set);
                preference.setSummary(set);
                Drawable d = getResources().getDrawable(R.drawable.ic_square);
                d.mutate().setColorFilter(c, PorterDuff.Mode.MULTIPLY);
                preference.setIcon(d);
                return true;
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_color, set), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    };

    void clearCache() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Glide.get(context).clearDiskCache();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Glide.get(context).clearMemory();
                Toast.makeText(context, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

}
