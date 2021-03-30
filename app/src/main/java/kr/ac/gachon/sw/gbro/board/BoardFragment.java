package kr.ac.gachon.sw.gbro.board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentBoardBinding;

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
            postList.add(new Post(0, 0, "Test", "test", "test", 1, "Test"));
        }

        Log.d("BoardFragment", "postList Size : " + postList.size());

        binding.rvBoard.setHasFixedSize(true);
        binding.rvBoard.setLayoutManager(new LinearLayoutManager(getActivity()));

        boardAdapter = new BoardAdapter(postList);
        binding.rvBoard.setAdapter(boardAdapter);
    }
}
