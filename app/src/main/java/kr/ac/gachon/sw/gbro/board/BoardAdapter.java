package kr.ac.gachon.sw.gbro.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.ItemBoardBinding;
import kr.ac.gachon.sw.gbro.util.CloudStorage;
import kr.ac.gachon.sw.gbro.util.Firestore;
import kr.ac.gachon.sw.gbro.util.Util;
import kr.ac.gachon.sw.gbro.util.model.Post;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private Context context;
    private ArrayList<Post> postList;

    public interface onItemClickListener {
        void onClick(View v, Post post);
    }

    private onItemClickListener listener = null;

    public BoardAdapter(Context context, ArrayList<Post> postList, onItemClickListener listener) {
        this.context = context;
        this.postList = postList;
        this.listener = listener;
    }

    public class BoardViewHolder extends RecyclerView.ViewHolder {
        private ItemBoardBinding itemBoardBinding;

        public BoardViewHolder(@NonNull ItemBoardBinding binding) {
            super(binding.getRoot());
            this.itemBoardBinding = binding;

            itemBoardBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, postList.get(getAdapterPosition()));
                }
            });
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
        binding.tvSummary.setText(currentPost.getContent().replaceAll("\n", ""));
        binding.tvUploadtime.setText(Util.timeStamptoString(currentPost.getWriteTime()));
        binding.tvLocation.setText(currentPost.getSummaryBuildingName(context));
        Firestore.getUserData(currentPost.getWriterId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    binding.tvWriter.setText((String) task.getResult().get("userNickName"));
                }
            }
        });

        CloudStorage.getImageFromURL(currentPost.getPhotoUrlList().get(0)).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                    binding.ivThumbnail.setImageBitmap(bitmap);
                }
                else {
                    binding.ivThumbnail.setImageResource(R.mipmap.ic_launcher);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     * Post 객체를 Adapter List에 추가
     * @author Minjae Seon
     * @param post Post Object
     */
    public void addItem(Post post) {
        if(postList.size() > 0 && post.getWriteTime().compareTo(postList.get(0).getWriteTime()) >= 0) {
            postList.add(0, post);
        } else postList.add(post);

        notifyDataSetChanged();
    }

    /**
     * ArrayList에 담긴 내용을 Adapter List에 전부 추가
     * @author Minjae Seon
     * @param post ArrayList<Post>
     */
    public void addAll(ArrayList<Post> post) {
        postList.clear();
        postList.addAll(post);
        notifyDataSetChanged();
    }

    /**
     * 포스트 삭제
     * @param post 삭제 대상 Post
      */
    public void removeItem(Post post) {
        for(Post p : postList) {
            if(p.getPostId() == post.getPostId()) {
                int removedIdx = postList.indexOf(p);
                postList.remove(p);
                notifyItemRemoved(removedIdx);
                break;
            }
        }
    }

    /**
     * 모든 List 정보 삭제
     * @author Minjae Seon
     */
    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }
}
