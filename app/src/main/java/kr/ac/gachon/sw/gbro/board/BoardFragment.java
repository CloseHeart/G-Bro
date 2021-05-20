package kr.ac.gachon.sw.gbro.board;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import kr.ac.gachon.sw.gbro.MainActivity;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentBoardBinding;
import kr.ac.gachon.sw.gbro.setting.MyPostActivity;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.LoadingDialog;
import kr.ac.gachon.sw.gbro.util.model.ChatData;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class BoardFragment extends BaseFragment<FragmentBoardBinding> implements BoardAdapter.onItemClickListener {
    private LoadingDialog loadingDialog;
    private BoardAdapter boardAdapter;
    private DocumentSnapshot last;
    private Boolean isScrolling = false;
    private Boolean isLastItemReached = false;
    private ArrayList<Post> postList;
    private ListenerRegistration postListener;
    private String searchName = null;
    private int spinner = 0;

    @Override
    protected FragmentBoardBinding getBinding() {
        return FragmentBoardBinding.inflate(getLayoutInflater());
    }

    public static BoardFragment newInstance(String searchName, int spinner){
        Log.d("searchName", "Search Name : " + searchName);
        BoardFragment bf = new BoardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", searchName);
        bundle.putInt("spinner", spinner);
        bf.setArguments(bundle);
        return bf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            searchName = getArguments().getString("title");
            spinner = getArguments().getInt("spinner");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(getActivity());
        setAdapter();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        postListener.remove();
    }

    /**
     * Adapter 설정
     * @author Minjae Seon, Taehyun Park
     */
    private void setAdapter() {
        Log.d("BoardFragment", "Set Adapter Run");

        postList = new ArrayList<>();
        binding.rvBoard.setHasFixedSize(true);
        binding.rvBoard.setLayoutManager(new LinearLayoutManager(getActivity()));
        boardAdapter = new BoardAdapter(getContext(), postList, this);
        binding.rvBoard.setAdapter(boardAdapter);

        setSnapshot();

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

                    Firestore.getPostData(spinner, last).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                            if (t.isSuccessful()) {
                                if (t.getResult().size() > 0) {
                                    for (DocumentChange doc : t.getResult().getDocumentChanges()) {
                                        Post post = doc.getDocument().toObject(Post.class);
                                        post.setPostId(doc.getDocument().getId());
                                        if(searchName != null && !searchName.trim().isEmpty()) {
                                            if (post.getTitle().contains(searchName)) {
                                                postList.add(post);
                                            }
                                        }
                                        else {
                                            postList.add(post);
                                        }
                                    }
                                    boardAdapter.notifyDataSetChanged();
                                    last = t.getResult().getDocumentChanges().get(t.getResult().getDocumentChanges().size() - 1).getDocument();
                                }

                                if (t.getResult().size() < 20) {
                                    isLastItemReached = true;
                                }
                            } else {
                                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }
            }
        };
        binding.rvBoard.addOnScrollListener(onScrollListener);
    }

    /**
     * Snapshot 설정
     * @author Minjae Seon
     */
    private void setSnapshot() {
        postListener =
                Firestore.getPostData(spinner, null).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // 에러 존재시
                if(error != null) {
                    // Snapshot 에러 로그 출력
                    Log.w(this.getClass().getSimpleName(), "Snapshot Error!", error);
                }

                if(value != null) {
                    // 변경된 리스트 가져옴
                    List<DocumentChange> changeList = value.getDocumentChanges();
                    // 변경 리스트 전체 반복
                    for(DocumentChange change : changeList) {
                        // Post로 변환
                        Post postData = change.getDocument().toObject(Post.class);

                        if(searchName != null && !searchName.trim().isEmpty()) {
                            if (postData.getTitle().contains(searchName)) {
                                boardAdapter.addItem(postData);
                            }
                        }
                        else {
                            boardAdapter.addItem(postData);
                        }
                    }

                    last = value.getDocumentChanges().get(value.getDocumentChanges().size() - 1).getDocument();

                    // Refresh
                    boardAdapter.notifyDataSetChanged();
                }
                // Snapshot에서 넘어온 데이터가 NULL이라면
                else {
                    // 에러 로그
                    Log.w(this.getClass().getSimpleName(), "Snapshot Data NULL!");
                }
            }
        });
    }

    @Override
    public void onClick(View v, Post post) {
        Intent contentIntent = new Intent(getActivity(), PostContentActivity.class);
        contentIntent.putExtra("post", post);
        startActivity(contentIntent);
    }
}
