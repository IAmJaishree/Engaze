package com.india.engaze.screens.Authentication.PhoneInput;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.india.engaze.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.phoneContainer)
    ConstraintLayout container;

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.tvMoving)
    TextView tvMoving;

    @BindView(R.id.tvPhoneNo)
    TextView tvPhoneNo;

    @BindView(R.id.llInfo)
    LinearLayout llInfo;

    @BindView(R.id.ivFlag)
    ImageView ivFlag;

    @BindView(R.id.tvCode)
    TextView tvCode;

    @BindView(R.id.ivback)
    ImageView ivBack;

    @BindView(R.id.greetText)
    TextView greetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setup();
    }

    private void setup() {
        String text = "Welcome to\n";
        String text2 = "Class";
        Spannable spannable = new SpannableString(text + text2);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), text.length(), (text + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        greetText.setText(spannable, TextView.BufferType.SPANNABLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ivBack.setImageAlpha(0);
    }


    @OnClick({R.id.phoneContainer, R.id.ivFlag, R.id.tvPhoneNo})
    void startTransition() {
        Intent intent = new Intent(LoginActivity.this, LoginWithPhone.class);
        Pair<View, String> p1 = Pair.create(ivBack, getString(R.string.transition_arrow));
        Pair<View, String> p2 = Pair.create(ivFlag, getString(R.string.transition_ivFlag));
        Pair<View, String> p3 = Pair.create(tvCode, getString(R.string.transition_tvCode));
        Pair<View, String> p4 = Pair.create(tvPhoneNo, getString(R.string.transition_tvPhoneNo));
        Pair<View, String> p5 = Pair.create(container, getString(R.string.transition_llPhone));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5);
        startActivity(intent, options.toBundle());
    }

}
