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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
//import com.jaredrummler.android.colorpicker.ColorPanelView;
//import com.jaredrummler.android.colorpicker.ColorPickerView;

import java.util.ArrayList;
import java.util.Calendar;

import petrov.kristiyan.colorpicker.ColorPicker;

public class TimeTableEditActivity extends AppCompatActivity {
    private EditText taskLabel;
    private Button dateButton;
    private TextView startTimeButton, endTimeButton;
    private ColorPickerView colorPickerView;
    private ColorPanelView newColorPanelView;
    private int flag_time, flag_template;
    DateSetListener dateSetListener = new DateSetListener();
    TimeSetListener timeSetListener = new TimeSetListener();
    private PieChart pieChart;

    ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

    //리스너
    class DateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateButton.setText(year + " / " + month + " / " + dayOfMonth);

            Description description = new Description();
            description.setText(year + " / " + month + " / " + dayOfMonth);
            description.setTextSize(15);
            pieChart.setDescription(description);
        }
    }

    class TimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (flag_time == 1)
                startTimeButton.setText(hourOfDay + " : " + minute);
            if (flag_time == 2)
                endTimeButton.setText(hourOfDay + " : " + minute);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_edit);

        dateButton = (Button) findViewById(R.id.date_set_button);

        pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(34f, "Japan"));
        yValues.add(new PieEntry(23f, "USA"));
        yValues.add(new PieEntry(14f, "UK"));
        yValues.add(new PieEntry(35f, "India"));
        yValues.add(new PieEntry(40f, "Russia"));
        yValues.add(new PieEntry(40f, "Korea"));

        Description description = new Description();
        description.setText("세계 국가"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues, "Countries");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(1f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                PieData x = pieChart.getData();
                Log.i("onValueSelected", "어캐해야하노ㅠㅠ");
//                Log.i("onValueSelected", x+"/"+yValues.size());
//                onClickDecoTaskButton(x);
            }

            @Override
            public void onNothingSelected() {

            }
        });

//        //여기서부터 테스트용 코드
//        fillRegionalSalesArrayList();
//
//        PieDataSet pieDataSet = new PieDataSet(pieEntries,"Regional Sales");
//        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setValueTextSize(16);
//
//        PieData pieData = new PieData(pieDataSet);
//        pieChart.setData(pieData);
//
//        //legend : 밑에 목록만 적은 것
//        Legend legend = pieChart.getLegend();
//        legend.setTextSize(13);
//        legend.setDrawInside(false);
//        legend.setWordWrapEnabled(true);
//        pieChart.animateXY(2000,2000);
//        pieChart.invalidate();
//
//        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                int x = pieChart.getData().getDataSetForEntry(e).getEntryIndex((PieEntry)e);
//                String region = regionalSalesDataArrayList.get(x).getRegion();
//                String sales = NumberFormat.getCurrencyInstance().format(regionalSalesDataArrayList.get(x).getSales());
//                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableEditActivity.this);
//                builder.setCancelable(true);
//
//                View view = LayoutInflater.from(TimeTableEditActivity.this).inflate(R.layout.regional_sales_layout,null);
//                TextView regionTxtView = view.findViewById(R.id.region);
//                TextView salesTxtView = view.findViewById(R.id.sales);
//                regionTxtView.setText(region);
//                salesTxtView.setText(sales);
//                builder.setView(view);
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
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

        add_task_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "" + taskLabel.getText().toString().trim(), Toast.LENGTH_LONG).show();
                addTaskDialog.dismiss(); // Cancel 버튼을 누르면 다이얼로그가 사라짐
            }
        });

        addTaskDialog.show();
    }

    //버튼 클릭시 decorate task 다이얼로그 띄우는 함수
    public void onClickDecoTaskButton(int index) {
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

        PieEntry pieEntry = yValues.get(index);
        taskLabelLine.setText(pieEntry.getLabel());
        taskTimeLine.setText(pieEntry.getValue()+"");

        decorate_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "decorating done", Toast.LENGTH_LONG).show();
                decoTaskDialog.dismiss(); // Cancel 버튼을 누르면 다이얼로그가 사라짐
            }
        });

        backgroundColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "pieColorButton done", Toast.LENGTH_LONG).show();
                flag_template = 1;
                showColorPicker(showTemplate);
            }
        });
        textColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "textColorButton done", Toast.LENGTH_LONG).show();
                flag_template = 2;
                showColorPicker(showTemplate);
            }
        });

        decoTaskDialog.show();
    }

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
            Log.i("date button", mYear+"/"+mMonth+"/"+mDay);
        } else if (view == startTimeButton) {
            flag_time = 1;
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, mHour, mMinute, false);
            timePickerDialog.show();
        } else if (view == endTimeButton) {
            flag_time = 2;
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    public void showColorPicker(View view) {
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
                if (flag_template == 1)
                    showTemplate.setBackgroundColor(color);
                else if (flag_template == 2)
                    showTemplate.setTextColor(color);
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