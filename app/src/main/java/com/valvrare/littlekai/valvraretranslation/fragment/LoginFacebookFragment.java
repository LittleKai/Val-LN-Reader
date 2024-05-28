package com.valvrare.littlekai.valvraretranslation.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.utils.BigCircleTransform;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import org.json.JSONObject;

public class LoginFacebookFragment extends Fragment {
    private LoginButton loginButton;
//    private ValvrareDatabaseHelper db;
    private CallbackManager callbackManager;
    private TextView tv_FacebookName;
    public String userId = null;
    AccessTokenTracker tracker;
    ProfileTracker profileTracker;
    ProfilePictureView profilePictureView;
    ImageView im_faceAvatar;
    private static Profile profile;
    String JSONdata = null;
    private String url;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

//        db = new ValvrareDatabaseHelper(getContext());

        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    tv_FacebookName.setText("Đăng Nhập:");
                    if(getActivity()!=null)
                    Glide.with(getActivity()).load(R.drawable.no_avatar).transform(new BigCircleTransform(getActivity())).into(im_faceAvatar);
                }
            }
        };
//        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(
//                    AccessToken oldAccessToken,
//                    AccessToken currentAccessToken) {
//                if (currentAccessToken == null) {
//                    tv_FacebookName.setText("Đăng Nhập:");
////                    Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.no_avatar);
////                    Bitmap roundedIcon = MyCircleImageView.getRoundedCornerBitmap(bitmap);
//////        roundedIcon = Bitmap.createScaledBitmap(roundedIcon, 100, 200, false);
////                    im_faceAvatar.setImageBitmap(roundedIcon);
//                    Glide.with(im_faceAvatar.getContext()).load(R.drawable.no_avatar).transform(new BigCircleTransform(getActivity())).into(im_faceAvatar);
//
////                    db.deleteFacebookUser();
//                }
//            }
//        };
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_facebook, container, false);
//        LinearLayout contain = (LinearLayout) view.findViewById(R.id.container);
//        Glide.with(contain.getContext()).load(R.drawable.pic_3).into(contain);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();
        displayWelcomeMsg(profile);

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        tracker.startTracking();
//        profileTracker.startTracking();
//        profile = Profile.getCurrentProfile();
//        displayWelcomeMsg(profile);
//        if (profile == null) {
//            tv_FacebookName.setText("Đăng Nhập:");
//            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_avatar);
//            Bitmap roundedIcon = MyCircleImageView.getRoundedCornerBitmap(bitmap);
////        roundedIcon = Bitmap.createScaledBitmap(roundedIcon, 100, 200, false);
//            im_faceAvatar.setImageBitmap(roundedIcon);
//            db.deleteFacebookUser();
//            Log.d("Kai", "Default Avatar Created: On Start ");
//        } else
//            Log.d("Kai", "Set Avatar: On Start ");
//
//        ShareDialog shareDialog = new ShareDialog(this);  // intialize facebook shareDialog.
////                String jsondata = db.getFacebookUser();
//        if (ShareDialog.canShow(ShareLinkContent.class)) {
//            ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                    .setContentUrl(Uri.parse("http://valvrareteam.com"))
//                    .build();
//
//            shareDialog.show(linkContent);  // Show facebook ShareDialog
//
//        }
//    }

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
//loginButton.
        // If using in a fragment
//        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.no_avatar);
//        Bitmap roundedIcon = MyCircleImageView.getRoundedCornerBitmap(bitmap);
        loginButton.setFragment(this);
        // Other app specific specialization
        // Callback registration
        loginButton.registerCallback(callbackManager, callback);
        tv_FacebookName = (TextView) view.findViewById(R.id.tv_FaceName);
        //       profilePictureView = (ProfilePictureView) view.findViewById(R.id.faceAvatar);
        im_faceAvatar = (ImageView) view.findViewById(R.id.im_faceAvatar);
        if (profile == null) {
            tv_FacebookName.setText("Đăng Nhập:");

//        roundedIcon = Bitmap.createScaledBitmap(roundedIcon, 100, 200, false);

//            im_faceAvatar.setImageBitmap(roundedIcon);
            Glide.with(this).load(R.drawable.no_avatar).transform(new BigCircleTransform(getActivity())).into(im_faceAvatar);
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
            if(userId !=null)
                if(!userId.isEmpty())
            Glide.with(this).load(imageURL.trim()).transform(new BigCircleTransform(getActivity())).into(im_faceAvatar);
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

//                        startActivity(intent);
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(150).height(150)");
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
