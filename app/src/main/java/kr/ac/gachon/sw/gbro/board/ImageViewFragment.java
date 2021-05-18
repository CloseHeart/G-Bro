package kr.ac.gachon.sw.gbro.board;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.ac.gachon.sw.gbro.base.BaseFragment;
import kr.ac.gachon.sw.gbro.databinding.FragmentImageviewBinding;
import kr.ac.gachon.sw.gbro.util.Util;

public class ImageViewFragment extends BaseFragment<FragmentImageviewBinding> {
    Bitmap image = null;

    @Override
    protected FragmentImageviewBinding getBinding() {
        return FragmentImageviewBinding.inflate(getLayoutInflater());
    }

    public static ImageViewFragment newInstance(Bitmap bitmap) {
        ImageViewFragment imageViewFragment = new ImageViewFragment();
        Bundle bundle = new Bundle();
        bundle.putByteArray("image", Util.bitmapToByteArray(bitmap));
        imageViewFragment.setArguments(bundle);
        return imageViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            image = Util.byteArrayToBitmap(getArguments().getByteArray("image"));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(image != null) {
            binding.imageView.setImageBitmap(image);
        }
        return binding.getRoot();
    }
}
