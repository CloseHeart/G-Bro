package kr.ac.gachon.sw.gbro.board;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.chat.ChatActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityPostContentBinding;
import kr.ac.gachon.sw.gbro.map.MapFragment;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.LoadingDialog;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.Post;
import kr.ac.gachon.sw.gbro.util.model.User;

public class PostContentActivity extends BaseActivity<ActivityPostContentBinding>{
    private ActionBar actionBar;
    private LoadingDialog loadingDialog;
    private Post contentPost;
    private Bitmap userImage;

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
        ViewPager2 vpImageSlide = binding.vpImageSlide;
        TabLayout tlImageSlide = binding.tlImageSlide;
        PostContentAdapter postContentAdapter = new PostContentAdapter(this);
        vpImageSlide.setAdapter(postContentAdapter);
        new TabLayoutMediator(tlImageSlide, vpImageSlide, ((tab, position) -> {})).attach();

        // 경로 가져오기
        ArrayList<Integer> mapPath = contentPost.getSavePath();

        // 경로 null 아니면
        if(mapPath != null) {
            // 경로 지도 추가
            MapFragment fragment = MapFragment.getPathInstance(mapPath);
            postContentAdapter.addNewFragment(fragment);
        }

        // 사진 가져오기
        for(String photoUrl : contentPost.getPhotoUrlList()) {
            CloudStorage.getImageFromURL(photoUrl)
                    .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            if(task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                postContentAdapter.addNewFragment(ImageViewFragment.newInstance(bitmap));
                            }
                            else {
                                Log.e(PostContentActivity.this.getClass().getSimpleName(), "Get Post Photo Error!", task.getException());
                                Toast.makeText(PostContentActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                                finish();
                            }
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
                                    userImage = Util.byteArrayToBitmap(imgTask.getResult());
                                }
                                // 프로필 사진 가져오는데 실패하면 기본 사진
                                else {
                                    userImage = Util.drawableToBitmap(PostContentActivity.this, R.drawable.profile);
                                }
                                binding.postProfile.setImageBitmap(userImage);
                            }
                        });
                    }
                    // 프로필사진 NULL 이면
                    else {
                        // 기본 사진
                        userImage = Util.drawableToBitmap(PostContentActivity.this, R.drawable.profile);
                    }
                    binding.postProfile.setImageBitmap(userImage);
                }
                else {
                    Log.e(PostContentActivity.this.getClass().getSimpleName(), "Get User Data Error!", task.getException());
                    Toast.makeText(PostContentActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    finish();
                }
                loadingDialog.dismiss();
            }
        });

        binding.postTitle.setText(contentPost.getTitle());
        binding.postContent.setText(contentPost.getContent());
        binding.postUploadtime.setText(Util.timeStamptoDetailString(contentPost.getWriteTime()));
        binding.postLocation.setText(contentPost.getSummaryBuildingName(getApplicationContext()));

        setChat();
        // 본인이 작성자면
        if(contentPost.getWriterId().equals(Auth.getCurrentUser().getUid())) {
            // 채팅버튼 삭제
            binding.contentToChat.setVisibility(View.GONE);
        }
        else {
            // 채팅 설정
            setChat();
        }
    }

    /**
     * 채팅 설정
     */
    private void setChat() {
        binding.contentToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미 방 있는지 검색
                Firestore.searchChatRoom(Auth.getCurrentUser().getUid(), contentPost.getWriterId())
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    Intent chatActivity = new Intent(PostContentActivity.this, ChatActivity.class);
                                    chatActivity.putExtra("targetid", contentPost.getWriterId());

                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                    // 채팅방이 하나도 없으면
                                    if(documents.size() <= 0) {
                                        // 새 채팅방 생성
                                        Firestore.createChatRoom(Auth.getCurrentUser().getUid(), contentPost.getWriterId())
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> createTask) {
                                                        if(createTask.isSuccessful()) {
                                                            chatActivity.putExtra("chatid", createTask.getResult().getId());
                                                            startActivity(chatActivity);
                                                            Log.d(PostContentActivity.this.getClass().getSimpleName(), "Start Chat " + createTask.getResult().getId() + " with " + contentPost.getWriterId());
                                                        }
                                                    }
                                                });
                                    }
                                    // 있다면
                                    else {
                                        chatActivity.putExtra("chatid", documents.get(0).getId());
                                        startActivity(chatActivity);
                                        Log.d(PostContentActivity.this.getClass().getSimpleName(), "Start Chat " + documents.get(0).getId() + " with " + contentPost.getWriterId());
                                    }

                                    Log.d(PostContentActivity.this.getClass().getSimpleName(), "Chat Extra Data " + chatActivity.getExtras());
                                }
                            }
                        });
            }
        });
    }
}