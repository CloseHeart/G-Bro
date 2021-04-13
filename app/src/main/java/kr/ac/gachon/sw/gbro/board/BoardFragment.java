package kr.ac.gachon.sw.gbro.board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentBoardBinding;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class BoardFragment extends BaseFragment<FragmentBoardBinding> {
    private BoardAdapter boardAdapter;

    @Override
    protected FragmentBoardBinding getBinding() {
        return FragmentBoardBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setAdapter();
        return binding.getRoot();
    }

    private void setAdapter() {
        Log.d("BoardFragment", "Set Adapter Run");

        ArrayList<Post> postList = new ArrayList<>();

        for(int i = 0; i < 50; i++) {
            postList.add(new Post(0,
                    "에어팟 프로를 찾습니다",
                    "ㅠㅠ 에어팟이 없어졌어요",
                    null,
                    new Random().nextInt(24),
                    null,
                    "사용자",
                    new Timestamp(new Date()),
                    false));
        }

        Log.d("BoardFragment", "postList Size : " + postList.size());

        binding.rvBoard.setHasFixedSize(true);
        binding.rvBoard.setLayoutManager(new LinearLayoutManager(getActivity()));

        boardAdapter = new BoardAdapter(getContext(), postList);
        binding.rvBoard.setAdapter(boardAdapter);
    }
}
