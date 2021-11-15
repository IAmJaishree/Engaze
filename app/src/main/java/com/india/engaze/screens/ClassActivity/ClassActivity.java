package com.india.engaze.screens.ClassActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.india.engaze.AppController;
import com.india.engaze.R;
import com.india.engaze.screens.Chat.PublicChatActivity;
import com.india.engaze.screens.adapter.ClassMenuFragment;
import com.india.engaze.screens.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.india.engaze.screens.adapter.ClassMenuFragment.newInstance;


public class ClassActivity extends BaseActivity implements ClassContract.View {


    @Inject
    ClassContract.Presenter<ClassContract.View> presenter;

    @BindView(R.id.chat)
    FloatingActionButton chatButton;

    @BindView(R.id.toolbar_text)
    TextView toolbarText;

    @BindView(R.id.backButton)
    ImageButton backButton;


    @BindView(R.id.bottom_app_bar)
    ConstraintLayout bottomAppBar;

    @BindView(R.id.teacherName)
    TextView teacherName;

    @BindView(R.id.timeTable)
    TextView timeTable;

    @BindView(R.id.maxStrength)
    TextView maxStrength;

    @BindView(R.id.memberCount)
    TextView memberCount;


    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.classImage)
    CircleImageView classImage;

    @BindView(R.id.updatesLayout)
    LinearLayout updatesLayout;


    String classId;

    Boolean isTeacher = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.onAttach(this);

        classId = getIntent().getExtras().getString("classId");
        presenter.getClassDetails();

        init();
    }

    private void init() {



        backButton.setOnClickListener(v -> onBackPressed());
        toolbarText.setText("Class Details");
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PublicChatActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void showClassDetails(DataSnapshot ds) {

        try{
            toolbarText.setText(ds.child("name").getValue().toString());
            status.setText(ds.child("status").getValue().toString());
            maxStrength.setText(ds.child("physicalStrength").getValue().toString());
            timeTable.setText(ds.child("timeTable").getValue().toString());
            status.setText(ds.child("status").getValue().toString());
            status.setText(ds.child("status").getValue().toString());

            memberCount.setText("" + ds.child("members").getChildrenCount());


            String uid = AppController.getInstance().getFirebaseUser().getUid();
            for(DataSnapshot d : ds.child("members").getChildren()){
                if(d.getKey().equals(uid)){
                    isTeacher =  d.child("as").getValue().toString().equals("teacher");
                    break;
                }
            }

            updatesLayout.removeAllViews();
            if(ds.child("updates").exists()){
                for(DataSnapshot d : ds.child("updates").getChildren()){
                    String update = d.getValue().toString();
                    View view = LayoutInflater.from(this).inflate(R.layout.update_view, null);
                    TextView editText = view.findViewById(R.id.itemText);
                    editText.setText(update);
                    View removeButton = view.findViewById(R.id.removeButton);

                    if(isTeacher){
                        removeButton.setOnClickListener(v-> presenter.removeUpdate(d.getKey()));
                    }else{
                        removeButton.setVisibility(View.GONE);
                    }
                    updatesLayout.addView(view);
                }
            }

            if(isTeacher){
                View view = LayoutInflater.from(this).inflate(R.layout.add_updates_view, null);
                EditText editText = view.findViewById(R.id.itemText);
                View addButton = view.findViewById(R.id.addButton);
                addButton.setOnClickListener(v-> presenter.addUpdate(editText.getText().toString()));
                updatesLayout.addView(view);
            }

            AppController.getInstance().getmSessionManager().setIsTeacher(isTeacher);


            bottomAppBar.setOnClickListener(v -> {
                ClassMenuFragment bottomSheetDialogFragment = newInstance(isTeacher);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            });

            String imageUrl = ds.child("image").getValue().toString();
            Glide.with(this).load(imageUrl).into(classImage);
        }catch (Exception e){
            showMessage(e.getMessage());
        }

    }

    @Override
    public void updated() {

    }
}
