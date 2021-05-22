package kr.ac.gachon.sw.gbro;

import android.app.Activity;

import androidx.appcompat.app.ActionBar;

import kr.ac.gachon.sw.gbro.databinding.CustomactionbarBinding;

public class CustomActionBar {
    private CustomactionbarBinding binding;
    private final Activity activity;
    private final ActionBar actionBar;

    public CustomActionBar(Activity activity, ActionBar actionBar) {
        this.activity = activity;
        this.actionBar = actionBar;
    }

    public void setActionBar() {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


        binding = CustomactionbarBinding.inflate(activity.getLayoutInflater());
        actionBar.setCustomView(binding.getRoot());
    }

    public CustomactionbarBinding getBinding() { return this.binding; }

    public void hide() {
        actionBar.hide();
    }

    public void show() {
        actionBar.show();
    }
}
