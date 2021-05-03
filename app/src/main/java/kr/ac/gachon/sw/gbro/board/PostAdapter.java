package kr.ac.gachon.sw.gbro;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class PostAdapter extends PagerAdapter {

    // R.drawable.(사진파일이름)으로 images 배열 생성
    private int[] images = {R.drawable.one, R.drawable.two,R.drawable.three};
    private LayoutInflater inflater;
    private Context context;

    // 해당 context가 자신의 context 객체와 똑같이 되도록 생성자를 만듬
    public PostAdapter(Context context){
        this.context = context;
    }

    public PostAdapter(FragmentManager supportFragmentManager, List<Fragment> data) {
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // object를 LinearLayout 형태로 형변환했을 때 view와 같은지 여부를 반환
//        return view == ((LinearLayout)object); 으로 했을때 오류 나서 View 로 바꿈..
        return view == ((View)object);
    }

    // 각각의 item을 인스턴스 화
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //초기화
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.post_slider, container, false);
        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);

        /**
        //물품 사진에 라운딩 넣을까 했는데 유치해보여서 뺐습니다.
        //필요하면 넣는걸로 !
        GradientDrawable drawable=
                (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);
        **/
        imageView.setImageResource(images[position]);
        container.addView(v);
        return v;
    }

    //할당을 해제
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
//        super.destroyItem(container, position, object);
    }
}

