package com.example.harujogak;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import petrov.kristiyan.colorpicker.ColorPicker;

//import com.jaredrummler.android.colorpicker.ColorPanelView;
//import com.jaredrummler.android.colorpicker.ColorPickerView;

public class TableByDayEditActivity extends AppCompatActivity {
    private PieChart pieChart;
    private MyTimeTable myTimeTable; //PieData, MyTask(이름, 시작시간, 끝시간), MyBackground, OnWeek, OnDate
    float rotate = 0;
    private Button dateButton;
    private TextView startTimeButton, endTimeButton, edit_startTime, edit_endTime;
    private int flag_time;
    String start_times[], end_times[];

    User user = new User();

    int[] week = {0, 0, 0, 0, 0, 0, 0};

    private DateSetListener dateSetListener = new DateSetListener();
    private TimeSetListener timeSetListener = new TimeSetListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_edit_day);

        //title bar 제거하기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        int position = (int) intent.getIntExtra("byDate", -1);
        //Todo: 사용자 시간표 어레이리스트에서 position 값에 해당하는 시간표 가져옴.
        // day List 는 그냥 0~6 숫자만 받아옴
        myTimeTable = new MyTimeTable();

        dateButton = (Button) findViewById(R.id.date_set_button);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        Button DONE = (Button) findViewById(R.id.add_timeTable_done);

        pieChart.setUsePercentValues(false);
        pieChart.setRotationEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDrawMarkers(true);
//        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setData(myTimeTable.getPieData());

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = pieChart.getData().getDataSetForEntry(e).getEntryIndex((PieEntry) e);
                onClickDecoTaskButton(pieChart, x);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        DONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<7;i++){
                    if(week[i]==1)
                      user.addWeekTable(i, myTimeTable);
                }
            }
        });

    }

    //노티피케이션(푸시알림)
    void diaryNotification(Calendar calendar) {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {

            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }

    //리스너
    class DateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month += 1;
            dateButton.setText(year + " / " + month + " / " + dayOfMonth);
            myTimeTable.setDate(year + " / " + month + " / " + dayOfMonth);
            Log.i("set Date :", year + " / " + month + " / " + dayOfMonth);
        }
    }

    class TimeSetListener implements TimePickerDialog.OnTimeSetListener {
        int mHour, mMinute;

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            mHour = hour;
            mMinute = minute;
            String strHour = hour + "";
            String strMinute = minute + "";

            if (hour < 10)
                strHour = "0" + strHour;
            if (minute < 10)
                strMinute = "0" + strMinute;

            if (flag_time == 1)
                startTimeButton.setText(strHour + " : " + strMinute);
            else if (flag_time == 2)
                endTimeButton.setText(strHour + " : " + strMinute);
            else if (flag_time == 3)
                edit_startTime.setText(strHour + " : " + strMinute);
            else if (flag_time == 4)
                edit_endTime.setText(strHour + " : " + strMinute);
        }

        int getmHour() {
            return mHour;
        }

        int getmMinute() {
            return mMinute;
        }
    }


    //버튼 클릭시 add Task 다이얼로그 띄우는 함수
    public void onClickAddTaskButton(View v) {

        Log.i("Custom", "onClickAddTaskButton");
        Dialog addTaskDialog = new Dialog(this);

        addTaskDialog.setContentView(R.layout.add_task_dialog);
        addTaskDialog.setTitle("일정 추가");

        ImageButton exit = (ImageButton) addTaskDialog.findViewById(R.id.exit);
        startTimeButton = (TextView) addTaskDialog.findViewById(R.id.start_time_set_button);
        endTimeButton = (TextView) addTaskDialog.findViewById(R.id.end_time_set_button);
        EditText taskLabel = (EditText) addTaskDialog.findViewById(R.id.task_label_set);
        Button add_task_done = (Button) addTaskDialog.findViewById(R.id.add_task_done);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskDialog.dismiss();
            }
        });

        //Todo : 나중에 골 설정한거를 어레이리스트로 가져와야 함
        //ArrayList<String> GoalList = user.getGoalList(); ????????
        ArrayList<String> GoalList = new ArrayList<>();
        GoalList.add("토익 시험");
        GoalList.add("다이어트");
        GoalList.add("코딩테스트");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, GoalList);
        Spinner s = (Spinner) addTaskDialog.findViewById(R.id.goalSpinner);
        s.setAdapter(arrayAdapter); //adapter를 spinner에 연결

//        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                System.out.println("!!position : " + position + parent.getItemAtPosition(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        add_task_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_task_thread(taskLabel);
                addTaskDialog.dismiss();

                //알림 부분
                SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
                long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

                Calendar nextNotifyTime = new GregorianCalendar();
                nextNotifyTime.setTimeInMillis(millis);

                int Alarm_hour = Integer.parseInt(start_times[0]);
                int Alarm_min = Integer.parseInt(start_times[1]);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, Alarm_hour);
                calendar.set(Calendar.MINUTE, Alarm_min);
                calendar.set(Calendar.SECOND, 0);

                // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                Date currentDateTime = calendar.getTime();
                String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

                //  Preference에 설정한 값 저장
                SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                editor.putLong("nextNotifyTime", (long) calendar.getTimeInMillis());
                editor.apply();

                diaryNotification(calendar);
                //알림부분 끝

            }
        });

        addTaskDialog.show();
    }


    public void add_task_thread(EditText taskLabel) {
        //시간 문자열 => 분으로 계산
        String strt = (String) startTimeButton.getText();
        String endt = (String) endTimeButton.getText();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                PieEntry yValues_entry;
                int background_entry;

                start_times = strt.split(" : ");
                int new_str = (int) (Integer.parseInt(start_times[0]) * 60 + Integer.parseInt(start_times[1]) + rotate * 4) % 1440;

                end_times = endt.split(" : ");
                int new_end = (int) (Integer.parseInt(end_times[0]) * 60 + Integer.parseInt(end_times[1]) + rotate * 4) % 1440;

                boolean done = false;
                int entry_str = 0, entry_end = 0;

                //기존의 파이차트 정보와 추가할 일정 정보 합치기
                ArrayList<PieEntry> yValues_new = new ArrayList<PieEntry>();
                PieDataSet dataSet = (PieDataSet) myTimeTable.getPieData().getDataSet();
                PieData data = (PieData) myTimeTable.getPieData();
                Iterator<Integer> backgrounds_entries = myTimeTable.getMyBackground().iterator();
                Iterator<PieEntry> yValues_entries = dataSet.getValues().iterator();
                ArrayList<Integer> table_background_new = new ArrayList<>();

                while (yValues_entries.hasNext()) {
                    yValues_entry = yValues_entries.next();
                    background_entry = backgrounds_entries.next();
                    entry_str = entry_end;
                    entry_end += yValues_entry.getValue();

                    //Todo : 0시를 낀 일정 rotate 값 계산 -> pieChart.setRotateAngle()..
                    //새로운 일정 추가/폐기 이후의 기존 일정 추가
                    if (done) {
                        yValues_new.add(yValues_entry);
                        table_background_new.add(background_entry);
                        continue;
                    }
                    //0시를 낀 일정 추기
                    if (new_end < new_str) {//맨 첫번째와 마지막의 항목이 모두 빈칸이어야 하고 크기가 맞아야한다
                        if (dataSet.getValues().get(0).getValue() >= new_str
                                && dataSet.getValues().get(dataSet.getEntryCount() - 1).getValue() >= 1440 - new_str) {
                            //빈칸 -> 흰색
                            yValues_new.add(new PieEntry(0, " "));
                            table_background_new.add(Color.rgb(250, 250, 250));
                            //추가된 태스크 -> 조이풀
                            yValues_new.add(new PieEntry(1440 - new_str + new_end, taskLabel.getText().toString()));
                            table_background_new.add(ColorTemplate.JOYFUL_COLORS[myTimeTable.getTasksCount() % 5]);
                            myTimeTable.setTasksCount(myTimeTable.getTasksCount() + 1);
                            //빈칸 -> 흰색
                            yValues_new.add(new PieEntry(entry_end - new_end, " "));
                            table_background_new.add(Color.rgb(250, 250, 250));
                            done = true;
                            rotate = (1440 - new_str) / 4;
                            pieChart.setRotationAngle(270 - rotate);
                        } else {//기존 일정에 내용이 있을 경우 -> 새로운 일정을 폐기
                            Log.i("add task :", "type 32 0시낀 일정 겹침");
                            Toast.makeText(getApplicationContext(), "이미 존재하는 일정과 시간이 겹칩니다", Toast.LENGTH_LONG).show();
                            yValues_new = (ArrayList) dataSet.getValues();
                            entry_end += yValues_entry.getValue();
                        }
                    }
                    //새로운 일정과 겹치지 않는 이전의 기존 일정 추가
                    else if (entry_end <= new_str && entry_end <= new_end) {
                        Log.i("add task :", "type 1 이전 일정");
                        yValues_new.add(yValues_entry);
                        table_background_new.add(background_entry);
                    }
                    //새로운 일정이 하나의 기존 일정과 겹칠 때
                    else if (entry_str <= new_str && new_end <= entry_end) {
                        if (yValues_entry.getLabel().equals(" ")) {//기존 일정이 빈칸일 경우 -> 해당 일정을 [빈칸, 새로운 일정, 빈칸] 으로 바꿔 추가
                            Log.i("add task :", "type 2 정상적으로 추가");
                            //빈칸 -> 흰색
                            yValues_new.add(new PieEntry(new_str - entry_str, " "));
                            table_background_new.add(Color.rgb(250, 250, 250));
                            //추가된 태스크 -> 조이풀
                            yValues_new.add(new PieEntry(new_end - new_str, taskLabel.getText().toString()));
                            table_background_new.add(ColorTemplate.JOYFUL_COLORS[myTimeTable.getTasksCount() % 5]);
                            myTimeTable.setTasksCount(myTimeTable.getTasksCount() + 1);
                            //빈칸 -> 흰색
                            yValues_new.add(new PieEntry(entry_end - new_end, " "));
                            table_background_new.add(Color.rgb(250, 250, 250));
                            done = true;
                        } else {//기존 일정에 내용이 있을 경우 -> 새로운 일정을 폐기
                            Log.i("add task :", "type 3 일정 완전히 겹침");
                            Toast.makeText(getApplicationContext(), "이미 존재하는 일정과 시간이 겹칩니다", Toast.LENGTH_LONG).show();
                            yValues_new.add(yValues_entry);
                            table_background_new.add(background_entry);
                            entry_end += yValues_entry.getValue();
                        }
                    }
                    //새로운 일정이 여러 일정과 겹칠 때 -> 무조건 새로운 일정 폐기
                    else {
                        Log.i("add task :", "type 4 일정 부분 겹침");
                        Toast.makeText(getApplicationContext(), "이미 존재하는 일정과 시간이 겹칩니다", Toast.LENGTH_LONG).show();
                        yValues_new.add(yValues_entry);
                        table_background_new.add(background_entry);
                        entry_end += yValues_entry.getValue();
                    }
                }

                myTimeTable.setMyBackground(table_background_new);
                dataSet = new PieDataSet(yValues_new, "Tasks");
                dataSet.setSliceSpace(0.5f);
                dataSet.setSelectionShift(0f);
                dataSet.setColors(myTimeTable.getMyBackground());

                data.setDataSet(dataSet);
                data.setValueTextSize(0f);
                myTimeTable.setPieData(data);

                pieChart.notifyDataSetChanged();
                pieChart.setData(myTimeTable.getPieData());
                pieChart.invalidate();
                Log.i("add Tasks : ", "done");
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    //버튼 클릭시 decorate task 다이얼로그 띄우는 함수
    public void onClickDecoTaskButton(PieChart pieChart, int index) {
        Log.i("onClickDecoTaskButton", index+"");

        Dialog decoTaskDialog = new Dialog(this);
        decoTaskDialog.setContentView(R.layout.decorate_dialog);
        decoTaskDialog.setTitle("일정 추가");

        ImageButton exit = (ImageButton) decoTaskDialog.findViewById(R.id.exit);
        TextView taskLabelLine = (TextView) decoTaskDialog.findViewById(R.id.task_label_show);
        edit_startTime = (TextView) decoTaskDialog.findViewById(R.id.start_time);
        edit_endTime = (TextView) decoTaskDialog.findViewById(R.id.end_time);
        Button decorate_done = (Button) decoTaskDialog.findViewById(R.id.decorate_done);
        Button template = (Button) decoTaskDialog.findViewById(R.id.show_adapted_task);

        PieDataSet dataSet = (PieDataSet) myTimeTable.getPieData().getDataSet();
        taskLabelLine.setText(dataSet.getValues().get(index).getLabel());
        template.setBackgroundColor(myTimeTable.getMyBackground().get(index));

        List<PieEntry> yValues = ((PieDataSet) myTimeTable.getPieData().getDataSet()).getValues();
        int str_time = (int) (yValues.get(0).getValue() + 4 * rotate);
        int end_time, i;
        for (i = 1; i < index; i++) {
            str_time += (int) yValues.get(i).getValue();
        }

        String strHour, strMinute;
        if ((str_time / 60) < 10)
            strHour = "0" + (str_time / 60);
        else
            strHour = "" + (str_time / 60);
        if ((str_time % 60) < 10)
            strMinute = "0" + (str_time % 60);
        else
            strMinute = "" + (str_time % 60);

        edit_startTime.setText(strHour + " : " + strMinute);

        end_time = str_time + (int) yValues.get(i).getValue();
        if ((end_time / 60) < 10)
            strHour = "0" + (end_time / 60);
        else
            strHour = "" + (end_time / 60);
        if ((end_time % 60) < 10)
            strMinute = "0" + (end_time % 60);
        else
            strMinute = "" + (end_time % 60);

        edit_endTime.setText(strHour + " : " + strMinute);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decoTaskDialog.dismiss();
            }
        });

        int finalStr_time = str_time;
        int finalEnd_time = end_time;
        decorate_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "decorating done", Toast.LENGTH_SHORT).show();
                List<PieEntry> yValues = ((PieDataSet) myTimeTable.getPieData().getDataSet()).getValues();
                int prev_y = (int) yValues.get(index-1).getY();
                int y = (int) yValues.get(index).getY();
                int next_y = (int) yValues.get(index+1).getY();
                Log.i("onClickDecoTaskButton", prev_y+"/"+y+"/"+next_y);

                String str[];
                str = edit_startTime.getText().toString().split(" : ");
                int new_str = (int) (Integer.parseInt(str[0]) * 60 + Integer.parseInt(str[1]) + rotate * 4) % 1440;
                if(finalStr_time!=new_str){
                    yValues.get(index - 1).setY(prev_y + (new_str - finalStr_time));
                    y -= (new_str - finalStr_time);
                }

                str = edit_endTime.getText().toString().split(" : ");
                int new_end = (int) (Integer.parseInt(str[0]) * 60 + Integer.parseInt(str[1]) + rotate * 4) % 1440;
                if(finalEnd_time!=new_end){
                    yValues.get(index + 1).setY(next_y + (finalEnd_time- new_end));
                    y -= (finalEnd_time- new_end);
                }

                yValues.get(index).setY(y);
                Log.i("onClickDecoTaskButton", yValues.get(index - 1).getValue()+"/"
                        +yValues.get(index).getValue()+"/"+yValues.get(index + 1).getValue());

                PieDataSet pieDataSet = new PieDataSet(yValues, "Tasks");
                pieDataSet.setSliceSpace(0.5f);
                pieDataSet.setSelectionShift(0f);
                pieDataSet.setColors(myTimeTable.getMyBackground());

                PieData pieData = new PieData(pieDataSet);
                pieData.setDataSet(pieDataSet);
                pieData.setValueTextSize(0f);
                myTimeTable.setPieData(pieData);

                pieChart.notifyDataSetChanged();
                pieChart.setData(myTimeTable.getPieData());
                pieChart.invalidate();
                decoTaskDialog.dismiss(); // Cancel 버튼을 누르면 다이얼로그가 사라짐
            }
        });

        template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "pieColorButton done", Toast.LENGTH_SHORT).show();
                showColorPicker(template, index);
            }
        });

        decoTaskDialog.show();
    }

    //날짜, 시간 다이얼로그 띄우는 함수
    public void onClickSet(View view) {
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        String mTime, times[];

        if (view == dateButton) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, mYear, mMonth, mDay);
            datePickerDialog.show();
            Log.i("date button", mYear + "/" + mMonth + "/" + mDay);
        } else if (view == startTimeButton) {
            flag_time = 1;
            mTime = (String) startTimeButton.getText();
            times = mTime.split(" : ");
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener, Integer.parseInt(times[0]), Integer.parseInt(times[1]), true);
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();

        } else if (view == endTimeButton) {
            flag_time = 2;
            mTime = (String) endTimeButton.getText();
            times = mTime.split(" : ");
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener, Integer.parseInt(times[0]), Integer.parseInt(times[1]), true);
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
        } else if (view == edit_startTime) {
            flag_time = 3;
            mTime = (String) edit_startTime.getText();
            times = mTime.split(" : ");
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener, Integer.parseInt(times[0]), Integer.parseInt(times[1]), true);
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
        } else if (view == edit_endTime) {
            flag_time = 4;
            mTime = (String) edit_endTime.getText();
            times = mTime.split(" : ");
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener, Integer.parseInt(times[0]), Integer.parseInt(times[1]), true);
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
        }
    }

    public void checkDay(View view) {
        int i = 0;
        switch (view.getId()) {
            case R.id.mon_button:
                i = 0;
                break;
            case R.id.tue_button:
                i = 1;
                break;
            case R.id.wed_button:
                i = 2;
                break;
            case R.id.thr_button:
                i = 3;
                break;
            case R.id.fri_button:
                i = 4;
                break;
            case R.id.sat_button:
                i = 5;
                break;
            case R.id.sun_button:
                i = 6;
                break;
        }
        if (week[i] == 0) {
            week[i] = 1;
            Log.i("Checkbox i", "checked");
            view.setSelected(true);
        } else {
            week[i] = 0;
            Log.i("Checkbox i", "unchecked");
            view.setSelected(false);
        }
//        User user = new User();
//        user.getWeekTable().set(i, nowMyTimeTable);
    }

    public void showColorPicker(View view, int index) {
        final ColorPicker colorPicker = new ColorPicker(TableByDayEditActivity.this);
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            Button showTemplate = (Button) view.findViewById(R.id.show_adapted_task);

            @Override
            public void onChooseColor(int position, int color) {
                showTemplate.setBackgroundColor(color);
                myTimeTable.getMyBackground().set(index, color);
            }

            @Override
            public void onCancel() {
                colorPicker.dismissDialog();
            }
        })
                .setRoundColorButton(true)
                .setDefaultColorButton(Color.parseColor("#f84c44"))
                .setColumns(5)
                .show();
    }

}