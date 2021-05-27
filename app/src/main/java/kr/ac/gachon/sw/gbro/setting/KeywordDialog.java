package kr.ac.gachon.sw.gbro.setting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import kr.ac.gachon.sw.gbro.databinding.DialogKeywordBinding;

public class KeywordDialog extends Dialog {
    private ArrayList<String> keywordList;
    private DialogKeywordBinding viewBinding = null;
    private EditText[] editTexts;

    public KeywordDialog(@NonNull Context context) {
        super(context);
        keywordList = new ArrayList<>();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewBinding = DialogKeywordBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        setView();

        viewBinding.etKeyword2.setEnabled(false);
        viewBinding.etKeyword3.setEnabled(false);
    }

    public KeywordDialog(@NonNull Context context, ArrayList<String> keywordList) {
        super(context);
        this.keywordList = keywordList;

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewBinding = DialogKeywordBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        setView();
        setValue();
    }

    /**
     * View 설정
     */
    private void setView() {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = 900;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(params);

        editTexts = new EditText[]{ viewBinding.etKeyword1, viewBinding.etKeyword2, viewBinding.etKeyword3};

        viewBinding.etKeyword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty()) {
                    viewBinding.etKeyword2.setText("");
                    viewBinding.etKeyword2.setEnabled(false);
                }
                else {
                    viewBinding.etKeyword2.setEnabled(true);
                }
            }
        });

        viewBinding.etKeyword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty()) {
                    viewBinding.etKeyword3.setText("");
                    viewBinding.etKeyword3.setEnabled(false);
                }
                else {
                    viewBinding.etKeyword3.setEnabled(true);
                }
            }
        });
    }

    /**
     * 기존 값 설정
     */
    private void setValue() {
        int i = 0;
        for(i = 0; i < keywordList.size(); i++) {
            editTexts[i].setText(keywordList.get(i));
        }

        for(int j = i; j < editTexts.length; j++) {
            editTexts[j].setText("");
            editTexts[j].setEnabled(false);
        }
    }

    /**
     * 저장 버튼 가져오기
     * @return Save Button View
     */
    public Button getSaveButton() {
        return viewBinding.btnKeywordsave;
    }

    /**
     * 키워드 목록 가져오기
     * @return Keyword ArrayList
     */

    public ArrayList<String> getKeywordList() {
        keywordList.clear();
        for(EditText editText : editTexts) {
            if(!editText.getText().toString().trim().replaceAll("\n", "").isEmpty()) {
                keywordList.add(editText.getText().toString().trim().replaceAll("\n", ""));
            }
        }
        return keywordList;
    }
}
