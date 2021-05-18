package kr.ac.gachon.sw.gbro.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityPostContentBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.LoadingDialog;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.Post;
import kr.ac.gachon.sw.gbro.util.model.User;
import me.relex.circleindicator.CircleIndicator;

public class PostContentActivity extends BaseActivity<ActivityPostContentBinding>{
    private ActionBar actionBar;
    private LoadingDialog loadingDialog;
    private Post contentPost;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        // 작성자 아이디가 현재 로그인 ID와 같으면
        if(contentPost.getWriterId().equals(Auth.getCurrentUser().getUid())) {
            // Menu 보이기
            inflater.inflate(R.menu.postcontentmenu, menu);
        }
        return true;
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
        else if(itemId == R.id.postcontent_modify) {
            Intent writeModify = new Intent(this, WriteActivity.class);
            writeModify.putExtra("post", contentPost);
            writeModify.putExtra("image", adapter.getBitmapByteArrayList());
            startActivity(writeModify);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(loadingDialog.isShowing()) loadingDialog.dismiss();
        super.onBackPressed();
    }

    /**
     * 어댑터를 설정한다
     * @author Subin Kim, Minjae Seon
     */
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

    /**
     * 컨텐츠를 설정한다
     * @author Minjae Seon
     */
    private void setContent() {
        Firestore.getUserData(contentPost.getWriterId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    User chatUser = task.getResult().toObject(User.class);
                    binding.postWriter.setText(chatUser.getUserNickName());

                    // 이미지
                    if(chatUser.getUserProfileImgURL() != null) {
                        CloudStorage.getImageFromURL(chatUser.getUserProfileImgURL()).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> imgTask) {
                                if (imgTask.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgTask.getResult(), 0, imgTask.getResult().length);
                                    binding.postProfile.setImageBitmap(bitmap);
                                }
                                // 프로필 사진 가져오는데 실패하면 기본 사진
                                else {
                                    binding.postProfile.setImageResource(R.drawable.profile);
                                }
                            }
                        });
                    }
                    // 프로필사진 NULL 이면
                    else {
                        // 기본 사진
                        binding.postProfile.setImageResource(R.drawable.profile);
                    }
                }
            }
        });

        binding.postTitle.setText(contentPost.getTitle());
        binding.postContent.setText(contentPost.getContent());
        binding.postUploadtime.setText(Util.timeStamptoDetailString(contentPost.getWriteTime()));
        binding.postLocation.setText(contentPost.getSummaryBuildingName(getApplicationContext()));

        // 본인이 작성자면 채팅 버튼이 안나오게
        if(contentPost.getWriterId().equals(Auth.getCurrentUser().getUid())) {
            binding.contentToChat.setVisibility(View.GONE);
        }
    }

}