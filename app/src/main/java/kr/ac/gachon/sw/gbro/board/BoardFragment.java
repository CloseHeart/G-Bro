package kr.ac.gachon.sw.gbro.board;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import kr.ac.gachon.sw.gbro.MainActivity;
import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentBoardBinding;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class    BoardFragment extends BaseFragment<FragmentBoardBinding> {
    private BoardAdapter boardAdapter;
    private DocumentSnapshot last;
    private Boolean isScrolling = false;
    private Boolean isLastItemReached = false;
    private ArrayList<Post> postList;

    @Override
    protected FragmentBoardBinding getBinding() {
        return FragmentBoardBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setAdapter();
        setRefresh();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBoardData();
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
        boardAdapter = new BoardAdapter(getContext(), postList);
        binding.rvBoard.setAdapter(boardAdapter);

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
                    isScrolling = false;
                    Query nextQuery = Firestore.getFirestoreInstance().collection("post").orderBy("writeTime",Query.Direction.DESCENDING).limit(20).startAfter(last);
                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        }
                    });
                }
            }
        };
        binding.rvBoard.addOnScrollListener(onScrollListener);
    }

    /**
     * Refresh 설정
     * @author Minjae Seon
     */
    private void setRefresh() {
        binding.swipeBoard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBoardData();
            }
        });
    }

    /**
     * 게시판 Data 로드
     * @author Minjae Seon
     */
    private void getBoardData() {
        boardAdapter.clear();
        Firestore.getPostData(0).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size() > 0){
                        for(DocumentSnapshot doc : task.getResult()){
                            Post post = doc.toObject(Post.class);
                            postList.add(post);
                        }
                        boardAdapter.notifyDataSetChanged();
                        last = task.getResult().getDocuments().get(task.getResult().size()-1);

                    }
                }
                else {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                }

                binding.swipeBoard.setRefreshing(false);
            }
        });
    }
}
