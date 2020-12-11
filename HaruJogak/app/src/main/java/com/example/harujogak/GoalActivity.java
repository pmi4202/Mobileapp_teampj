package com.example.harujogak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GoalActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal);

        //목표 리스트 출력
        User user = User.getInstance();


        //'추가'버튼
        btn=(Button) findViewById(R.id.goal_btn);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(GoalActivity.this, GoalAddActivity.class);
                startActivity(intent);
            }
        });
    }

}
