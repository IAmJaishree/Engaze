package com.india.engaze.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.india.engaze.R;
import com.india.engaze.screens.Splash.SplashActivity;
import com.india.engaze.service.AppSignatureHelper;

import java.util.ArrayList;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;

public class Functions {


    public static int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }

    public static boolean isLocationServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.india.engaze.service.LocationService".equals(service.service.getClassName())) {
                Timber.e("isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Timber.e("isLocationServiceRunning: location service is not running.");
        return false;
    }


    public static boolean isPermissionGranted(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static int dpToPx(float dpValue, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dpValue * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String getHash(Context context) {
        String hash = "";
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(context);
        for (String s :
                appSignatureHelper.getAppSignatures()) {
            hash = s;
        }

        return hash;
    }

    public static void shakeView(View view, Context context) {
        Animation shake;
        shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(shake); // starts animation
    }


    public static boolean isValidEmail(String email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobile(String mobile) {

        return TextUtils.isDigitsOnly(mobile) && mobile.length() == 10;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }



    public static CountDownTimer startTimer(int seconds, ProgressBar progressBar, TextView timerText, CallBacks.CountDownCall call) {
        return new CountDownTimer(seconds * 1000, 500) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds1 = leftTimeInMilliseconds / 1000;
                progressBar.setProgress((int) seconds1);
                timerText.setText(String.format("%02d", seconds1 % 60));
            }

            @Override
            public void onFinish() {
                call.finish();
                timerText.setText("0");
                progressBar.setProgress(seconds);
            }
        }.start();
    }


    public static boolean ifNotPermissionGrantedOpenSplash(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startSplash(activity);
        }
        return true;
    }

    public static void startSplash(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }




    public static void askPermission(Activity context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));
        context.startActivityForResult(intent, Constants.SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    public static void loadNavigationView(double lat, double lng, Activity activity) {
        Uri navigation = Uri.parse("google.navigation:q=" + lat + "," + lng + "");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(navigationIntent);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static void call(Long number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(number), null));
        context.startActivity(intent);
    }




    public static int findBestRoute(Route route, ArrayList<Route> routes) {
        int bestIndex = 0;
        int bestIntersections = 0;
        for (int i = 0; i < routes.size(); i++) {
            Route rout = routes.get(i);
            int intersections = 0;
            for (LatLng point : route.getPoints()) {
                if (PolyUtil.isLocationOnPath(point, rout.getPoints(), true, 50)) {
                    intersections++;

                }
            }
            if (intersections > bestIntersections) {
                bestIndex = i;
                bestIntersections = intersections;
            }
        }
        return bestIndex;
    }

    public static String getNewRandomId() {
        return UUID.randomUUID().toString();
    }



}