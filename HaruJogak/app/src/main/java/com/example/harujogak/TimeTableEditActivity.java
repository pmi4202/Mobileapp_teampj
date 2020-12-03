package com.example.harujogak;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jaredrummler.android.colorpicker.ColorPanelView;
import com.jaredrummler.android.colorpicker.ColorPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

//import com.jaredrummler.android.colorpicker.ColorPanelView;
//import com.jaredrummler.android.colorpicker.ColorPickerView;

// Todo : spinner 만들기

public class TimeTableEditActivity extends AppCompatActivity {
    private EditText taskLabel;
    private Button dateButton;
    private TextView startTimeButton, endTimeButton;
    private int flag_time, flag_template;
    private ArrayList<Integer> table_background=new ArrayList<>();

    DateSetListener dateSetListener = new DateSetListener();
    TimeSetListener timeSetListener = new TimeSetListener();

    private PieChart pieChart;
    ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

    //리스너
    class DateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateButton.setText(year + " / " + month+1 + " / " + dayOfMonth);

            Description description = new Description();
            description.setText(year + " / " + month+1 + " / " + dayOfMonth);
            description.setTextSize(15);
            pieChart.setDescription(description);
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
        }

        int getmHour(){
            return mHour;
        }
        int getmMinute(){
            return mMinute;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_edit);

        dateButton = (Button) findViewById(R.id.date_set_button);

        pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);

        PieChart pieChart = findViewById(R.id.pieChart);
//        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setRotationEnabled(false);

        //전역으로 사용
        //ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        //24시간 = 1440분 //TimePicker로 시간을 분으로 받으니까 파이차트를 분단위로 계산
        yValues.add(new PieEntry(1440f, " "));//일정 없는 것

        Description description = new Description();
        description.setText("세계 국가"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues, "Countries");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(1f);
        table_background.add(Color.rgb(250, 250, 250));
        dataSet.setColors(table_background);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = pieChart.getData().getDataSetForEntry(e).getEntryIndex((PieEntry) e);
                Toast.makeText(TimeTableEditActivity.this, x, Toast.LENGTH_SHORT).show();
                onClickDecoTaskButton(pieChart, x);
                Log.i("onValueSelected", x + "/" + yValues.get(x).getLabel());
//                onClickDecoTaskButton(x);
            }

            @Override
            public void onNothingSelected() {            }
        });

    }

    //버튼 클릭시 add Task 다이얼로그 띄우는 함수
    public void onClickAddTaskButton(View v) {
        Log.i("Custom", "onClickAddTaskButton");
        Dialog addTaskDialog = new Dialog(this);
        addTaskDialog.setContentView(R.layout.add_task_dialog);
        addTaskDialog.setTitle("일정 추가");

        Button add_task_done = (Button) addTaskDialog.findViewById(R.id.add_task_done);
        startTimeButton = (TextView) addTaskDialog.findViewById(R.id.start_time_set_button);
        endTimeButton = (TextView) addTaskDialog.findViewById(R.id.end_time_set_button);
        taskLabel = (EditText) addTaskDialog.findViewById(R.id.task_label_set);

//        // 목표 스피너
//        Spinner spinner = (Spinner)findViewById(R.id.goalSpinner);
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("토익 시험"); arrayList.add("다이어트"); arrayList.add("코딩테스트");
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
//                android.R.layout.simple_spinner_dropdown_item, arrayList);
//
////        spinner.setAdapter(arrayAdapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),arrayList.get(i)+"가 선택되었습니다.",
//                        Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });

        add_task_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //파이차트 객체 생성
                PieChart pieChart = findViewById(R.id.pieChart);

                pieChart.getDescription().setEnabled(false);
                pieChart.setExtraOffsets(5, 10, 5, 5);

                pieChart.setDrawHoleEnabled(false);
                pieChart.setRotationEnabled(false);

                //시간 문자열 => 분으로 계산
                String startTime = (String) startTimeButton.getText();
                String endTime = (String) endTimeButton.getText();

                String start_times[] = startTime.split(" : ");
                int start_time = Integer.parseInt(start_times[0]) * 60 + Integer.parseInt(start_times[1]);
                String end_times[] = endTime.split(" : ");
                int end_time = Integer.parseInt(end_times[0]) * 60 + Integer.parseInt(end_times[1]);

                //기존의 파이차트 정보와 추가할 일정 정보 합치기
                boolean done = false;
                ArrayList<PieEntry> yValues_new = new ArrayList<PieEntry>();
                Iterator<PieEntry> yValues_entries = yValues.iterator();
                int total_time = 0;
                while (yValues_entries.hasNext()) {
                    PieEntry yValues_entry = yValues_entries.next();
                    total_time += yValues_entry.getValue();
                    if (!done && total_time >= start_time && total_time >= end_time) {//선택한 시간
                        if (yValues_entry.getLabel().equals(" ")) {//선택한 시간에 일정이 없는 경우
                            total_time -= yValues_entry.getValue();
                            yValues_new.add(new PieEntry(start_time - total_time, " "));
                            yValues_new.add(new PieEntry(end_time - start_time, taskLabel.getText().toString()));
                            yValues_new.add(new PieEntry(yValues_entry.getValue() - (end_time - total_time), " "));
                            total_time += yValues_entry.getValue();
                            done = true; //추가 완료함을 표시
                        } else {//선택한 시간에 일정이 있는 경우
                            Toast.makeText(getApplicationContext(), "schedule already exists", Toast.LENGTH_LONG).show();
                            total_time += yValues_entry.getValue();
                        }
                    } else {//나머지 시간
                        yValues_new.add(yValues_entry);
                    }
                }
                yValues = yValues_new;

                Description description = new Description();
                description.setText("계획표"); //라벨
                description.setTextSize(15);
                pieChart.setDescription(description);

                PieDataSet dataSet = new PieDataSet(yValues_new, "Countries");
                dataSet.setSliceSpace(2f);
                dataSet.setSelectionShift(1f);

                //이게.. 일정을 추가해서 빈 칸이 생기면 두개 추가해야하고 빈 칸 없이 바로 이어붙인거면 하나만 추가..
                //현재 추가된 task 백그라운드 색상 추가
                table_background.add(Color.rgb(250, 250, 250));
                dataSet.setColors(table_background);
                //

                PieData data = new PieData((dataSet));
                data.setValueTextSize(10f);
                data.setValueTextColor(Color.YELLOW);

                pieChart.setData(data);

                Toast.makeText(getApplicationContext(), "" + taskLabel.getText().toString().trim(), Toast.LENGTH_LONG).show();
                addTaskDialog.dismiss(); // Cancel 버튼을 누르면 다이얼로그가 사라짐
            }
        });

        addTaskDialog.show();
    }

    //버튼 클릭시 decorate task 다이얼로그 띄우는 함수
    public void onClickDecoTaskButton(PieChart pieChart, int index) {
        Log.i("Custom", "onClickDecoTaskButton");

        Dialog decoTaskDialog = new Dialog(this);
        decoTaskDialog.setContentView(R.layout.decorate_dialog);
        decoTaskDialog.setTitle("일정 추가");

        TextView taskLabelLine = (TextView) decoTaskDialog.findViewById(R.id.task_label_show);
        TextView taskTimeLine = (TextView) decoTaskDialog.findViewById(R.id.task_time_show);
        Button decorate_done = (Button) decoTaskDialog.findViewById(R.id.decorate_done);
        Button showTemplate = (Button) decoTaskDialog.findViewById(R.id.show_adapted_task);
        TextView backgroundColorButton = (TextView) decoTaskDialog.findViewById(R.id.set_background);
        TextView textColorButton = (TextView) decoTaskDialog.findViewById(R.id.set_text_color);

        taskLabelLine.setText(yValues.get(index).getLabel());
        taskTimeLine.setText(yValues.get(index).getValue() + "");

        decorate_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "decorating done", Toast.LENGTH_SHORT).show();
                decoTaskDialog.dismiss(); // Cancel 버튼을 누르면 다이얼로그가 사라짐
            }
        });

        backgroundColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "pieColorButton done", Toast.LENGTH_SHORT).show();
                flag_template = 1;
                showColorPicker(showTemplate, index);

            }
        });
        textColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "textColorButton done", Toast.LENGTH_SHORT).show();
                flag_template = 2;
                showColorPicker(showTemplate, index);
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
        int mHour = cal.get(Calendar.HOUR_OF_DAY);
        int mMinute = cal.get(Calendar.MINUTE);

        if (view == dateButton) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, mYear, mMonth, mDay);
            datePickerDialog.show();
            Log.i("date button", mYear + "/" + mMonth + "/" + mDay);
        } else if (view == startTimeButton) {
            flag_time = 1;
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeSetListener, mHour, mMinute, true);
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
            
        } else if (view == endTimeButton) {
            flag_time = 2;
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeSetListener, mHour, mMinute, true);
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
        }
    }

    public void showColorPicker(View view, int index) {
//        ColorPickerView colorPickerView;
//        ColorPanelView newColorPanelView;

//        Dialog colorPickerDialog = new Dialog(this);
//        colorPickerDialog.setContentView(R.layout.color_picker_dialog);
//        Button colorPickDoneButton = (Button) findViewById(R.id.okButton);
//
//        colorPickDoneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "decorating done", Toast.LENGTH_LONG).show();
//                colorPickerDialog.dismiss(); // Cancel 버튼을 누르면 다이얼로그가 사라짐
//            }
//        });
//
//        colorPickerDialog.show();

        final ColorPicker colorPicker = new ColorPicker(TimeTableEditActivity.this);
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            Button showTemplate = (Button) view.findViewById(R.id.show_adapted_task);

            @Override
            public void onChooseColor(int position, int color) {
                if (flag_template == 1) {
                    showTemplate.setBackgroundColor(color);
                    // TODO : 백그라운드 색상, 텍스트 색상 변경하도록..
                    pieChart.getPaint(PieChart.PAINT_DESCRIPTION).setColor(color);

                } else if (flag_template == 2) {
                    showTemplate.setTextColor(color);
                    pieChart.getPaint(Chart.PAINT_DESCRIPTION).setColor(color);
                }
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