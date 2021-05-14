package kr.ac.gachon.sw.gbro.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityPostContentBinding;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.LoadingDialog;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.Post;
import me.relex.circleindicator.CircleIndicator;

public class PostContentActivity extends BaseActivity<ActivityPostContentBinding>{
    private ActionBar actionBar;
    private LoadingDialog loadingDialog;
    private Post contentPost;
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private PostAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected ActivityPostContentBinding getBinding() { return ActivityPostContentBinding.inflate(getLayoutInflater()); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(this);

        actionBar = getSupportActionBar();
        // 포스트 정보 가져오기
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            loadingDialog.show();
            contentPost = bundle.getParcelable("post");
            setAdapter();
            setContent();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(contentPost.getTitle());
            }

        }
        else {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Google 정책에 따라 MenuItem에 Switch 사용하지 않고 if문 사용
        int itemId = item.getItemId();

        // Actionbar 뒤로가기 버튼
        if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(loadingDialog.isShowing()) loadingDialog.dismiss();
        super.onBackPressed();
    }

    private void setAdapter() {
        // 아까 만든 view
        viewPager = binding.view;

        //adapter 초기화
        adapter = new PostAdapter(this);
        viewPager.setAdapter(adapter);

        CircleIndicator c_indicator = binding.indicator;
        c_indicator.setViewPager(viewPager);

        for(int i = 1; i <= contentPost.getPhotoNum(); i++) {
            int currentIndex = i;
            CloudStorage.getPostImage(contentPost.getPostId(), String.valueOf(i)).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        adapter.addImage(bitmap);
                    }
                    if(currentIndex == contentPost.getPhotoNum()) loadingDialog.dismiss();
                }
            });
        }
    }

    private void setContent() {
        Firestore.getUserData(contentPost.getWriterId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    binding.postWriter.setText((String) task.getResult().get("userNickName"));
                }
            }
        });

        binding.postTitle.setText(contentPost.getTitle());
        binding.postContent.setText(contentPost.getContent());
        binding.postUploadtime.setText(Util.timeStamptoString(contentPost.getWriteTime()));
        binding.postLocation.setText(contentPost.getSummaryBuildingName(getApplicationContext()));
    }

}