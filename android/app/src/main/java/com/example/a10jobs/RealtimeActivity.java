package com.example.a10jobs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class RealtimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        ImageButton btnUp = (ImageButton) findViewById(R.id.arrowkeys_up);
        ImageButton btnDown = (ImageButton) findViewById(R.id.arrowkeys_down);
        ImageButton btnLeft = (ImageButton) findViewById(R.id.arrowkeys_left);
        ImageButton btnRight = (ImageButton) findViewById(R.id.arrowkeys_right);

        btnUp.setOnTouchListener(onTouchListener);
        btnDown.setOnTouchListener(onTouchListener);
        btnLeft.setOnTouchListener(onTouchListener);
        btnRight.setOnTouchListener(onTouchListener);
    }

    // Touch 이벤트
    ImageButton.OnTouchListener onTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:   // 버튼이 눌렸을 때
                case MotionEvent.ACTION_MOVE:   // 버튼이 눌려져 있을 때
                    view.setBackgroundColor(Color.parseColor("#757575"));
                    break;
                case MotionEvent.ACTION_UP:     // 버튼 뗄 때
                    view.setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
            return false;
        }
    };

}
