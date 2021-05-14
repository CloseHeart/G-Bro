package kr.ac.gachon.sw.gbro.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import kr.ac.gachon.sw.gbro.databinding.DialogLoadingBinding;

public class LoadingDialog extends Dialog {
    private DialogLoadingBinding viewBinding = null;

    public LoadingDialog(@NonNull Context context) {
        super(context);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewBinding = DialogLoadingBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
    }
}
