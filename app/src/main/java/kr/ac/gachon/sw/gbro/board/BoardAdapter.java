package kr.ac.gachon.sw.gbro.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.ItemBoardBinding;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private Context context;
    private ArrayList<Post> postList;

    public BoardAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public static class BoardViewHolder extends RecyclerView.ViewHolder {
        private ItemBoardBinding itemBoardBinding;

        public BoardViewHolder(@NonNull ItemBoardBinding binding) {
            super(binding.getRoot());
            this.itemBoardBinding = binding;
        }
    }

    @NonNull
    @Override
    public BoardAdapter.BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoardViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardAdapter.BoardViewHolder holder, int position) {
        ItemBoardBinding binding = holder.itemBoardBinding;
        Post currentPost = postList.get(position);
        binding.ivThumbnail.setImageResource(R.mipmap.ic_launcher);
        binding.tvTitle.setText(currentPost.getTitle());
        binding.tvSummary.setText(currentPost.getContent());
        binding.tvUploadtime.setText(Util.timeStamptoString(currentPost.getWriteTime()));
        binding.tvLocation.setText(currentPost.getSummaryBuildingName(context));
        binding.tvWriter.setText(currentPost.getWriterId());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addItem(Post post) {
        postList.add(post);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Post> post) {
        postList.clear();
        postList.addAll(post);
        notifyDataSetChanged();
    }
}
