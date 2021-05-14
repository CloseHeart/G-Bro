package kr.ac.gachon.sw.gbro.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.PostSliderBinding;

public class PostAdapter extends PagerAdapter {
    private PostSliderBinding binding;
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    // 해당 context가 자신의 context 객체와 똑같이 되도록 생성자를 만듬
    public PostAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // object를 LinearLayout 형태로 형변환했을 때 view와 같은지 여부를 반환
//        return view == ((LinearLayout)object); 으로 했을때 오류 나서 View 로 바꿈..
        return view == ((View)object);
    }

    // 각각의 item을 인스턴스 화
    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = PostSliderBinding.inflate(inflater);
        View v = binding.getRoot();
        ImageView imageView = binding.imageView;

        /**
        //물품 사진에 라운딩 넣을까 했는데 유치해보여서 뺐습니다.
        //필요하면 넣는걸로 !
        GradientDrawable drawable=
                (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);
        **/

        imageView.setImageBitmap(bitmapList.get(position));
        container.addView(v);
        return v;
    }

    //할당을 해제
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 이미지 추가
     * @param bitmap bitmap
     */
    public void addImage(Bitmap bitmap) {
        bitmapList.add(bitmap);
        notifyDataSetChanged();
    }
}

