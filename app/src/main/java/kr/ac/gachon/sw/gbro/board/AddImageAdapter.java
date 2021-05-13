package kr.ac.gachon.sw.gbro.board;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.databinding.ItemAddimageBinding;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.AddImageHolder> {
    private ArrayList<Bitmap> imageList;

    public interface OnImageAddItemClickListener {
        /**
         * 이미지 추가를 클릭했을 때 이벤트를 정의한다
         * @author Minjae Seon
         * @param v 누른 View
         */
        void onAddClick(View v);

        /**
         * 이미지 제거를 클릭했을 때 이벤트를 정의한다
         * @author Minjae Seon
         * @param v 누른 View
         * @param position 누른 View의 Position
         */
        void onRemoveClick(View v, int position);
    }

    private OnImageAddItemClickListener itemClickListener = null;

    public AddImageAdapter(OnImageAddItemClickListener itemClickListener) {
        this.imageList = new ArrayList<>();
        this.itemClickListener = itemClickListener;

        // 기본 Add 추가
        imageList.add(null);
    }

    public class AddImageHolder extends RecyclerView.ViewHolder {
        private ItemAddimageBinding addimageBinding;

        public AddImageHolder(@NonNull ItemAddimageBinding binding) {
            super(binding.getRoot());
            this.addimageBinding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 이미지 제거
                    if(binding.ivAddimage.getTag().equals("userImg")) {
                        itemClickListener.onRemoveClick(binding.getRoot(), getAdapterPosition());
                    }
                    // 이미지 추가
                    else {
                        itemClickListener.onAddClick(binding.getRoot());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AddImageAdapter.AddImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddImageAdapter.AddImageHolder(ItemAddimageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddImageAdapter.AddImageHolder holder, int position) {
        ItemAddimageBinding binding = holder.addimageBinding;
        Bitmap image = imageList.get(position);
        if(image != null) {
            binding.ivAddimage.setTag("userImg");
            binding.ivAddimage.setImageBitmap(image);
        }
        else {
            binding.ivAddimage.setTag("addImg");
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * Image를 추가한다
     * @author Minjae Seon
     * @param bitmap Bitmap Image
     */
    public void addImage(Bitmap bitmap) {
        imageList.add(bitmap);
        notifyDataSetChanged();
    }

    /**
     * 지정한 Position의 Image를 제거한다
     * @author Minjae Soen
     * @param position Position
     */
    public void removeImage(int position) {
        imageList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 모든 Bitmap 정보가 담긴 ArrayList를 반환한다.
     * @author Minjae Seon
     * @return ArrayList<Bitmap>
     */
    public ArrayList<Bitmap> getAllImageList() {
        return imageList;
    }

}