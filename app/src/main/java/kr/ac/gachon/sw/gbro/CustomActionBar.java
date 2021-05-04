package kr.ac.gachon.sw.gbro;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.ActionBar;

public class CustomActionBar {
    private Activity activity;
    private ActionBar actionBar;

    public CustomActionBar(Activity activity, ActionBar actionBar) {
        this.activity = activity;
        this.actionBar = actionBar;
    }

    public void setActionBar() {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View customView = LayoutInflater.from(activity)
                .inflate(R.layout.customactionbar, null);
        actionBar.setCustomView(customView);
    }

    public void hide() {
        actionBar.hide();
    }

    public void show() {
        actionBar.show();
    }
}
