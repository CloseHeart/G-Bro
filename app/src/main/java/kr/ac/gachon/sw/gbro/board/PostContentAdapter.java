package kr.ac.gachon.sw.gbro.board;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class PostContentAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public PostContentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void addNewFragment(Fragment fragment) {
        fragmentList.add(fragment);
        notifyDataSetChanged();
    }

    public void removeFragment(int position) {
        fragmentList.remove(position);
        notifyDataSetChanged();
    }
}
