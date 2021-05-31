package kr.ac.gachon.sw.gbro.board;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.databinding.DialogPathSelectionBinding;

public class PathDialog extends Dialog {
    private DialogPathSelectionBinding viewBinding = null;
    private ArrayList<Integer> pathList = null;
    
    public PathDialog(@NonNull Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // 타이틀 제거
        viewBinding = DialogPathSelectionBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        pathList = new ArrayList<>();
        setDialog();
    }

    public PathDialog(@NonNull Context context, ArrayList<Integer> pathList){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // 타이틀 제거
        viewBinding = DialogPathSelectionBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        this.pathList = pathList;

        setDialog();
    }

    /**
     * 다이얼로그 기본 설정
     */
    private void setDialog() {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        Spinner[] spinnerItem = { viewBinding.spinnerBuildingFirst,
                viewBinding.spinnerBuildingSecond,
                viewBinding.spinnerBuildingThird,
                viewBinding.spinnerBuildingFourth,
                viewBinding.spinnerBuildingFifth};

        if(pathList != null && pathList.size() > 1) {
            for (int i = 0; i < spinnerItem.length; i++) {
                if (pathList.size() > i) {
                    spinnerItem[i].setSelection(pathList.get(i));
                } else {
                    spinnerItem[i].setSelection(25);
                }
            }
        }
        else {
            for(Spinner item : spinnerItem) {
                item.setSelection(25);
            }
        }

        // 없음을 선택한다면 아래 스피너는 선택 못하게
        viewBinding.spinnerBuildingFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if(parent.getItemAtPosition(position).toString().equals("없음")){
                    viewBinding.spinnerBuildingSecond.setEnabled(false);
                    viewBinding.spinnerBuildingSecond.setSelection(25);
                }else{
                    viewBinding.spinnerBuildingSecond.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        viewBinding.spinnerBuildingSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if(parent.getItemAtPosition(position).toString().equals("없음")){
                    viewBinding.spinnerBuildingThird.setEnabled(false);
                    viewBinding.spinnerBuildingThird.setSelection(25);
                }else{
                    viewBinding.spinnerBuildingThird.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        viewBinding.spinnerBuildingThird.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if(parent.getItemAtPosition(position).toString().equals("없음")){
                    viewBinding.spinnerBuildingFourth.setEnabled(false);
                    viewBinding.spinnerBuildingFourth.setSelection(25);
                }else{
                    viewBinding.spinnerBuildingFourth.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        viewBinding.spinnerBuildingFourth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if(parent.getItemAtPosition(position).toString().equals("없음")){
                    viewBinding.spinnerBuildingFifth.setEnabled(false);
                    viewBinding.spinnerBuildingFifth.setSelection(25);
                }else{
                    viewBinding.spinnerBuildingFifth.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // 완료 버튼을 누른다면
        viewBinding.btnPathFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathList = new ArrayList<>();

                // 첫번째 Item과 두번째 Item이 없음이 아니면
                if(spinnerItem[0].getSelectedItemPosition() != 25 && spinnerItem[1].getSelectedItemPosition() != 25) {
                    for (Spinner item : spinnerItem) {
                        // 없음 선택한거 아니면
                        if (item.getSelectedItemPosition() != 25) {
                            Log.d("PathDialog", "Added " + item.getSelectedItemPosition());
                            pathList.add(item.getSelectedItemPosition());
                        }
                        // 없음 선택시
                        else {
                            break;
                        }
                    }
                }

                PathDialog.this.dismiss(); // 다이얼로그 닫기
            }
        });
    }

    /**
     * 경로 목록을 가져온다
     * @return ArrayList<Integer>
     */
    public ArrayList<Integer> getPathList() {
        return pathList;
    }
}
