package kr.ac.gachon.sw.gbro.board;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.gachon.sw.gbro.R;
import kr.ac.gachon.sw.gbro.base.BaseActivity;
import kr.ac.gachon.sw.gbro.databinding.ActivityWriteBinding;
import kr.ac.gachon.sw.gbro.util.Util;

public class WriteActivity extends BaseActivity<ActivityWriteBinding> {
    private ActionBar actionBar;
    @Override
    protected ActivityWriteBinding getBinding() {
        return ActivityWriteBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.write);
        }

        // 날짜 및 시간 관련 설정
        setDateTime();

        // 버튼 이벤트 설정
        setButtonEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.writemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Google 정책에 따라 MenuItem에 Switch 사용하지 않고 if문 사용
        int itemId = item.getItemId();

        // 저장 버튼
        if (itemId == R.id.write_save) {
            Toast.makeText(this, "저장", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        closeDialog();
    }

    /**
     * 데이터가 날아갈 수 있다는 경고 Dialog를 출력한다 - 예를 누르면 Finish
     * @author Minjae Seon
     */
    private void closeDialog() {

        if(!binding.etContent.getText().toString().isEmpty() || !binding.etTitle.getText().toString().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.post_cancel_dialog_msg))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .create().show();
        }
        else {
            finish();
        }
    }

    /**
     * 버튼 이벤트 설정
     * @author Minjae Seon
     */
    private void setButtonEvent() {
        binding.btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WriteActivity.this, "사진", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 날짜 및 시간 관련 설정
     * @author Minjae Seon
     */
    private void setDateTime() {
        // Calendar Instance
        Calendar cal = Calendar.getInstance();

        // Date & Time Format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("a hh:mm", Locale.KOREA);

        // Date & Time EditText
        EditText etDate = binding.etMissingdate;
        etDate.setText(dateFormat.format(cal.getTime()));

        EditText etTime = binding.etMissingtime;
        etTime.setText(timeFormat.format(cal.getTime()));

        // DatePicker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Util.debugLog(WriteActivity.this, "onDateSet - " + year + "-" + month + "-" + dayOfMonth);
                cal.set(year, month, dayOfMonth);
                etDate.setText(dateFormat.format(cal.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        // Set Max Date
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        // TimePicker
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar currentCal = Calendar.getInstance();
                Calendar checkCal = Calendar.getInstance();
                checkCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                checkCal.set(Calendar.MINUTE, minute);

                Util.debugLog(WriteActivity.this, "currentCal - " + currentCal.get(Calendar.YEAR) + "-" + (currentCal.get(Calendar.MONTH) + 1) + "-" + currentCal.get(Calendar.DAY_OF_MONTH) + " "
                        + currentCal.get(Calendar.HOUR_OF_DAY) + ":" + currentCal.get(Calendar.MINUTE) + " / " + currentCal.getTimeInMillis() );
                Util.debugLog(WriteActivity.this, "checkCal - " + checkCal.get(Calendar.YEAR) + "-" + (checkCal.get(Calendar.MONTH) + 1) + "-" + checkCal.get(Calendar.DAY_OF_MONTH) + " "
                        + checkCal.get(Calendar.HOUR_OF_DAY) + ":" + checkCal.get(Calendar.MINUTE) + " / " + checkCal.getTimeInMillis());

                // 현재 시간보다 미래가 아니면
                if(currentCal.getTimeInMillis() >= checkCal.getTimeInMillis()) {
                    // 설정
                    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                    etTime.setText(timeFormat.format(cal.getTime()));
                }
                else {
                    // 미래면 토스트 출력
                    Toast.makeText(WriteActivity.this, getString(R.string.post_futureerror), Toast.LENGTH_SHORT).show();

                    view.setHour(currentCal.get(Calendar.HOUR_OF_DAY));
                    view.setMinute(currentCal.get(Calendar.MINUTE));
                }
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);

        binding.etMissingdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        binding.etMissingtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();

            }
        });
    }
}
