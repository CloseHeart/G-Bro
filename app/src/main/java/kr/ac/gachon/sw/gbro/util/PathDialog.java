package kr.ac.gachon.sw.gbro.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.databinding.DialogLoadingBinding;
import kr.ac.gachon.sw.gbro.databinding.DialogPathSelectionBinding;

public class PathDialog extends Dialog {
    public DialogPathSelectionBinding viewBinding = null;
    
    public PathDialog (@NonNull Context context){
        super(context);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // 타이틀 제거
        viewBinding = DialogPathSelectionBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

}
