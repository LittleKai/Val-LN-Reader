package com.valvrare.littlekai.valvraretranslation.utils;


import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment3;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import org.json.JSONObject;

/**
 * Created by Kai on 12/25/2016.
 */

public class FacebookLoginDialog extends DialogFragment {
    private static final String TAG = "Kai";
    public static String APP_KEY = "516731191856957";
    public static String BASE_DOMAIN = "http://www.valvrareteam.com";
    public static String url = "http://www.valvrareteam.com";
    private LoginButton loginButton;
    private ValvrareDatabaseHelper db;
    private CallbackManager callbackManager;
    private TextView tv_FacebookName;
    public static String userId = null;
    AccessTokenTracker tracker;
    ProfileTracker profileTracker;
    ProfilePictureView profilePictureView;
    ImageView im_faceAvatar;
    private static Profile profile;
    String JSONdata = null;
    private String shareUrl = null;
    Context context;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            profile = Profile.getCurrentProfile();
            displayWelcomeMsg(profile);
            getUserInfo(loginResult);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    public static FacebookLoginDialog newInstance(String mgs) {
        FacebookLoginDialog f = new FacebookLoginDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("mgs", mgs);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        context = getActivity();
//        db = new ValvrareDatabaseHelper(context);

        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newToken) {

            }
        };
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    tv_FacebookName.setText("Đăng Nhập:");
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_avatar);
                    Bitmap roundedIcon = MyCircleImageView.getRoundedCornerBitmap(bitmap);
//        roundedIcon = Bitmap.createScaledBitmap(roundedIcon, 100, 200, false);
                    im_faceAvatar.setImageBitmap(roundedIcon);
//                    db.deleteFacebookUser();
                }
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if (newProfile != null)
                    displayWelcomeMsg(newProfile);
            }
        };
        if (tracker != null)
            tracker.startTracking();
        if (profileTracker != null)
            profileTracker.startTracking();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon_facebook);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments()!=null)
            shareUrl = getArguments().getString("mgs");
        getDialog().setTitle("Đăng Nhập");
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.fragment_login_facebook, container, false);

        return view;
//        url = getArguments().getString("mgs");

    }

    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();
        displayWelcomeMsg(profile);

    }


    @Override
    public void onStop() {
        super.onStop();
        tracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization
        // Callback registration
        loginButton.registerCallback(callbackManager, callback);
        tv_FacebookName = (TextView) view.findViewById(R.id.tv_FaceName);
        //       profilePictureView = (ProfilePictureView) view.findViewById(R.id.faceAvatar);
        im_faceAvatar = (ImageView) view.findViewById(R.id.im_faceAvatar);
        if (profile == null) {
            tv_FacebookName.setText("Đăng Nhập:");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_avatar);
            Bitmap roundedIcon = MyCircleImageView.getRoundedCornerBitmap(bitmap);
//        roundedIcon = Bitmap.createScaledBitmap(roundedIcon, 100, 200, false);
            im_faceAvatar.setImageBitmap(roundedIcon);
            userId = null;
//            db.deleteFacebookUser();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        profile = Profile.getCurrentProfile();
        displayWelcomeMsg(profile);
    }

    public void displayWelcomeMsg(Profile profile) {

        if (profile != null) {
            tv_FacebookName.setText(profile.getName());
            userId = profile.getId();
//            profilePictureView.setProfileId(userId);

            String imageURL = "https://graph.facebook.com/" + userId + "/picture?type=large";
            Glide.with(this).load(imageURL).transform(new BigCircleTransform(getActivity())).into(im_faceAvatar);
        }
    }

    protected void getUserInfo(LoginResult login_result) {

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
//                        db.insertFacebookUser(json_object.toString());
                        Log.d(TAG, "onCompleted: "+shareUrl);
                        if(shareUrl!=null) {
                            NovelFragment3 f = (NovelFragment3) NovelDescriptionActivity.getViewPagerItem();
                            if(f!=null)
                            f.update();
                            getDialog().dismiss();
                            ShareDialog shareDialog = new ShareDialog(getActivity());  // intialize facebook shareDialog.
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(shareUrl))
                                        .build();
                                shareDialog.show(linkContent);  // Show facebook ShareDialog
                                Log.d(TAG, "onCompleted: ");

                            }
                        }
//                        startActivity(intent);
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        Log.e("data",data.toString());
//    }


}
