package com.valvrare.littlekai.valvraretranslation.helper;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;
import com.valvrare.littlekai.valvraretranslation.utils.Util;

import java.io.File;

public class DisplayNovelContentHtmlHelper {
    //    private static final String TAG = DisplayNovelContentHtmlHelper.class.toString();
    private static final String TAG = "Kai";

    public static String getViewPortMeta() {
        return "<meta name='viewport' content='width=device-width, minimum-scale=0.1, maximum-scale=10.0' id='viewport-meta'/>";
    }


    /**
     * Prepare javascript to enable highlighting and setting up bookmarks.
     *
     * @param lastPos        //     * @param bookmarks
     * @param enableBookmark
     * @return
     */
//    public static String prepareJavaScript(int lastPos, ArrayList<BookmarkModel> bookmarks, boolean enableBookmark) {
    public static String prepareJavaScript(int lastPos, boolean enableBookmark) {
        StringBuilder scriptBuilder = new StringBuilder();

        scriptBuilder.append("<script type='text/javascript'>");
//        scriptBuilder.append(String.format("var isBookmarkEnabled = %s;", enableBookmark));
//        scriptBuilder.append("\n");
//
//        String bookmarkJs = "var bookmarkCol = [%s];";
////        if (bookmarks != null && bookmarks.size() > 0) {
////            ArrayList<Integer> list = new ArrayList<Integer>();
////            for (BookmarkModel bookmark : bookmarks) {
////                list.add(bookmark.getpIndex());
////            }
////            bookmarkJs = String.format(bookmarkJs, Util.join(list, ","));
////        } else
////        {
//            bookmarkJs = String.format(bookmarkJs, "");
////        }
//        scriptBuilder.append(bookmarkJs);

        scriptBuilder.append("\n");

        String lastPosJs = String.format("var lastPos = %s;", lastPos);
        scriptBuilder.append(lastPosJs);
        scriptBuilder.append("\n");

        String js = LNReaderApplication.getInstance().ReadCss(R.raw.content_test);
        scriptBuilder.append(js);

        scriptBuilder.append("</script>");
        Log.d(TAG, "prepareJavaScript: " + scriptBuilder.toString());
        return scriptBuilder.toString();

    }

    public static String prepareJavaScript(int lastPos, boolean isTextChanged, double ratio) {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("<script type='text/javascript'>");
        scriptBuilder.append("\n");
        String lastPosJs = String.format("var lastPos = %s;", lastPos);
        scriptBuilder.append(lastPosJs);
        scriptBuilder.append("\n");

        lastPosJs = String.format("var ratio = %s;", ratio);
        scriptBuilder.append(lastPosJs);
        scriptBuilder.append("\n");
        String js;
        if (isTextChanged)
            js = LNReaderApplication.getInstance().ReadCss(R.raw.content_test2);
        else
            js = LNReaderApplication.getInstance().ReadCss(R.raw.content_test);
        scriptBuilder.append(js);

        scriptBuilder.append("</script>");
        return scriptBuilder.toString();
    }

    /**
     * getCSSSheet() method will generate the CSS data into the <style> elements.
     * At the current moment, it reads the external data line by line then applies
     * it directly to the header.
     *
     * @return
     */

    public static String getCSSSheet() {
        Context ctx = LNReaderApplication.getInstance().getApplicationContext();

        // Default CSS start here
        String key = "";
        int styleId = -1;
//        StringBuilder css = null;
        StringBuilder css = new StringBuilder();
        int textSize = ChapterReadingActivity.getTextSizePref();
        String font = null;
        switch (ChapterReadingActivity.getBackgroundPref()) {
            case "WhiteBackground":
                styleId = R.raw.style_white;
                break;
            case "BlackBackground":
                styleId = R.raw.style_dark;
                break;
            case "CustomBackground":
                styleId = R.raw.style_custom_color;
//                    key = "style_custom_color" + UIHelper.getBackgroundColor(ctx) + UIHelper.getForegroundColor(ctx) + UIHelper.getLinkColor(ctx) + UIHelper.getThumbBorderColor(ctx) + UIHelper.getThumbBackgroundColor(ctx);
                break;
            case "SepiaBackground":
                styleId = R.raw.style_sepia;
                break;
        }
        css.append("<style type=\"text/css\">");

        // check if exists in css cache
//        if (UIHelper.CssCache.containsKey(key))
//            return UIHelper.CssCache.get(key);

        // build the css
        css.append(LNReaderApplication.getInstance().ReadCss(styleId));

        if (getUseJustifiedPreferences(ctx)) {
            css.append("\nbody { text-align: justify !important; }\n");
        }

        String Palatino = "font-family: Palatino;\n" +
                "src: url(\"fonts/palatino.otf\")\n";
        String Tahoma = "font-family: Tahoma;\n" +
                "src: url(\"fonts/tahoma.ttf\")\n";
        String Roboto = "font-family: Roboto;\n" +
                "src: url(\"fonts/Roboto-Regular.ttf\")\n";
        String Georgia = "font-family: Georgia;\n" +
                "src: url(\"fonts/georgia.tff\")\n";
        String Times = "font-family: Times;\n" +
                "src: url('fonts/times.ttf')\n";

        css.append("\n@font-face {\n");
        switch (ChapterReadingActivity.getFontNamePref()) {
            case "Palatino":
                font = "Palatino";
                css.append(Palatino);
                break;
            case "Roboto":
                font = "Roboto";
                css.append(Roboto);
                break;
            case "Tahoma":
                font = "Tahoma";
                css.append(Tahoma);
                break;
            case "Georgia":
                font = "Georgia";
                css.append(Georgia);
                break;
            case "Times N.Roman":
                font = "Times";
                css.append(Times);
                break;
        }

        css.append("\n}\n");
        css.append("\np { line-height:").append(getLineSpacingPreferences(ctx)).append("% !important; \n");

        css.append("font-family: ").append(font).append(";\n").append("\nfont-size: ")
                .append(textSize).append("px").append(";\n").append(" }\n");
        css.append("\nbody { margin: ").append(getMarginPreferences(ctx)).append("% !important; }\n");

        css.append("</style>");

        String cssStr = css.toString();
        key = ChapterReadingActivity.getBackgroundPref() + ChapterReadingActivity.getFontNamePref() + ChapterReadingActivity.getTextSizePref();

        // replace custom color if enabled.
        if (ChapterReadingActivity.getBackgroundPref().equals("CustomBackground")) {
            cssStr = cssStr.replace("@background@", UIHelper.getBackgroundColor(ctx));
            cssStr = cssStr.replace("@foreground@", UIHelper.getForegroundColor(ctx));
            cssStr = cssStr.replace("@link@", UIHelper.getLinkColor(ctx));
            cssStr = cssStr.replace("@thumb-border@", UIHelper.getThumbBorderColor(ctx));
            cssStr = cssStr.replace("@thumb-back@", UIHelper.getThumbBackgroundColor(ctx));
        }

//        UIHelper.CssCache.put(key, cssStr);
        return cssStr;
    }

    /**
     * link to external CSS file, not cached.
     *
     * @return <link rel="stylesheet" href="file://EXTERNAL-CSS-PATH">
     */
    public static String getExternalCss() {
        Context ctx = LNReaderApplication.getInstance().getApplicationContext();
        String cssPath = PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.PREF_CUSTOM_CSS_PATH, Environment.getExternalStorageDirectory().getPath() + "/custom.css");
        if (!Util.isStringNullOrEmpty(cssPath)) {
            File cssFile = new File(cssPath);
            if (cssFile.exists()) {
                String external = String.format("<link rel=\"stylesheet\" href=\"file://%s\">", cssPath);
                Log.d(TAG, "External CSS: " + external);
                return external;
            }
        }
        // should not hit this code, either external css not exists or failed to read.
        Toast.makeText(ctx, "CSS không tồn tại!", Toast.LENGTH_SHORT).show();
        return null;
    }

    public static boolean getUseCustomCSS(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(Constants.PREF_USE_CUSTOM_CSS, false);
    }

    private static float getLineSpacingPreferences(Context ctx) {
        return Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.PREF_LINESPACING, "120"));
    }

    private static boolean getUseJustifiedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(Constants.PREF_FORCE_JUSTIFIED, false);
    }

    private static float getMarginPreferences(Context ctx) {
        return Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.PREF_MARGINS, "3"));
    }

    private static String getHeadingFontPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.PREF_HEADING_FONT, "serif");
    }

    private static String getContentFontPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.PREF_CONTENT_FONT, "sans-serif");
    }
}
