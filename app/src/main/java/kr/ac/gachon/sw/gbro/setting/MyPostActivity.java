package kr.ac.gachon.sw.gbro.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.board.BoardAdapter;
import kr.ac.gachon.sw.gbro.board.PostContentActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityMypostBinding;
import kr.ac.gachon.sw.gbro.util.Auth;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.LoadingDialog;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class MyPostActivity extends BaseActivity<ActivityMypostBinding> implements BoardAdapter.onItemClickListener {
    ActionBar actionBar;
    private LoadingDialog loadingDialog;
    private BoardAdapter boardAdapter;
    private DocumentSnapshot last;
    private Boolean isScrolling = false;
    private Boolean isLastItemReached = false;
    private ArrayList<Post> postList;

    @Override
    protected ActivityMypostBinding getBinding() {
        return ActivityMypostBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(this);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.setting_mypost);
        }

        setAdapter();
        setRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBoardData();
    }

    private void setAdapter() {
        RecyclerView myPostRecyclerView = binding.rvMypost;
        postList = new ArrayList<>();
        myPostRecyclerView.setHasFixedSize(true);
        myPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        boardAdapter = new BoardAdapter(this, postList, this);
        myPostRecyclerView.setAdapter(boardAdapter);

        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();

                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                    loadingDialog.show();
                    isScrolling = false;
                    Firestore.getMyPostData(Auth.getCurrentUser().getUid(), last).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                            if (t.isSuccessful()) {
                                if(t.getResult().size() > 0){
                                    for(DocumentSnapshot doc : t.getResult()){
                                        Post post = doc.toObject(Post.class);
                                        postList.add(post);
                                    }
                                    boardAdapter.notifyDataSetChanged();
                                    last = t.getResult().getDocuments().get(t.getResult().size()-1);
                                }

                                if (t.getResult().size() < 20) {
                                    isLastItemReached = true;
                                }
                            }
                            else {
                                Toast.makeText(MyPostActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }
            }
        };
        myPostRecyclerView.addOnScrollListener(onScrollListener);
    }

    /**
     * 게시판 Data 로드
     * @author Minjae Seon
     */
    private void getBoardData() {
        loadingDialog.show();
        boardAdapter.clear();
        Firestore.getMyPostData(Auth.getCurrentUser().getUid(), null).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size() > 0){
                        for(DocumentSnapshot doc : task.getResult()){
                            Post post = doc.toObject(Post.class);
                            post.setPostId(doc.getId());
                            postList.add(post);
                        }
                        boardAdapter.notifyDataSetChanged();
                        last = task.getResult().getDocuments().get(task.getResult().size()-1);

                    }
                }
                else {
                    Toast.makeText(MyPostActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    finish();
                }
                loadingDialog.dismiss();
                binding.swipeMypost.setRefreshing(false);
            }
        });
    }

    /**
     * Refresh 설정
     * @author Minjae Seon
     */
    private void setRefresh() {
        binding.swipeMypost.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBoardData();
            }
        });
    }

    @Override
    public void onClick(View v, Post post) {
        Intent contentIntent = new Intent(this, PostContentActivity.class);
        contentIntent.putExtra("post", post);
        startActivity(contentIntent);
    }
}
