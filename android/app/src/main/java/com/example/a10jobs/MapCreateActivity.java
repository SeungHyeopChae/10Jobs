package com.example.a10jobs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class MapCreateActivity extends AppCompatActivity {
    Button btn_start, btn_stop;
    ImageButton btnUp, btnDown, btnLeft, btnRight;
    Bitmap bitmap;
    ImageView mapImg;
    Switch switchAuto;

    String url = "http://j5d201.p.ssafy.io:12001";
    Socket socket;
    {
        try{
            socket = IO.socket(url);
        } catch(URISyntaxException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_create);
        mapImg = (ImageView) findViewById(R.id.map_img);
        socket.connect();

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        btnUp = (ImageButton) findViewById(R.id.arrowkeys_up);
        btnDown = (ImageButton) findViewById(R.id.arrowkeys_down);
        btnLeft = (ImageButton) findViewById(R.id.arrowkeys_left);
        btnRight = (ImageButton) findViewById(R.id.arrowkeys_right);

        switchAuto = (Switch) findViewById(R.id.switch_auto);

        btnUp.setOnTouchListener(onTouchListener);
        btnDown.setOnTouchListener(onTouchListener);
        btnLeft.setOnTouchListener(onTouchListener);
        btnRight.setOnTouchListener(onTouchListener);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("start_createmap");
                btn_start.setVisibility(btn_start.GONE);
                btn_stop.setVisibility(btn_stop.VISIBLE);
                switchAuto.setVisibility(switchAuto.VISIBLE);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MapCreateActivity.this);
                dlg.setTitle("??? ??????"); //??????
                dlg.setMessage("?????? ????????????????????????? \n?????? ?????? ?????? ?????? ??? ????????????."); // ?????????
                dlg.setIcon(R.drawable.robot); // ????????? ??????
//                ?????? ????????? ??????
                dlg.setPositiveButton("??????",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // imgview??? drawable??? ????????????
                        Drawable tmpImg = mapImg.getDrawable();

                        // bitmap?????? ???????????????
                        Bitmap bitmap = ((BitmapDrawable)tmpImg).getBitmap();

                        // ????????? bitmap??? jpg??? ????????? ????????? ???????????????.
                        saveBitmapToJpeg(bitmap);
                        
                        Toast.makeText(MapCreateActivity.this,"?????????????????????.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        socket.emit("stop_createmap");

                        switchAuto.setChecked(false);
                    }
                });
                dlg.setNegativeButton("??????",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("check", "????????????");
                    }
                });
                dlg.show();
//                msocket.emit("stop_createmap");
//                btn_stop.setVisibility(btn_start.GONE);
//                btn_start.setVisibility(btn_stop.VISIBLE);
            }
        });


        switchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // ????????? ????????? ?????????????????? ???????????? ??????????????? ??? ????????? ?????? ???????????????.
                if (isChecked){
                    Log.d("check","?????? ?????????");
                    socket.emit("mapAutoOnToServer");

                }else{
                    Log.d("check","?????? ????????????");
                    socket.emit("mapAutoOffToServer");
                }
            }
        });

        socket.on("sendMapStreaming", getImg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        socket.off("sendMapStreaming");
        socket.disconnect();
//        Log.v("msg", "pause ?????? ?????? ??????");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        socket.on("sendMapStreaming", getImg);
        socket.connect();
//        Log.v("msg", "restart ????????? ?????? ?????????");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("sendMapStreaming");
        socket.disconnect();
//        Log.v("msg", "destroy ?????? ?????? ??????");
    }

    ImageButton.OnTouchListener onTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:   // ????????? ????????? ???
                case MotionEvent.ACTION_MOVE:   // ????????? ????????? ?????? ???
//                  view.setBackgroundColor(Color.parseColor("#757575"));
//                  ???-1/???-2/???-3/???-4
                    if(view == btnUp){
                        socket.emit("gostraightToServer", 2);
                        Log.v("click", "up");
                    }
                    else if(view == btnDown){
                        socket.emit("gobackToServer", 3);
                        Log.v("click", "down");
                    }
                    else if(view == btnLeft){
                        socket.emit("turnleftToServer", 1);
                        Log.v("click", "left");
                    }
                    else if(view == btnRight){
                        socket.emit("turnrightToServer", 4);
                        Log.v("click", "right");
                    }
                    break;
                case MotionEvent.ACTION_UP:     // ?????? ??? ???
                    view.setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
            return false;
        }
    };

    private Emitter.Listener getImg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("map", "?????????");
                    String data = (String)args[0];
                    bitmap = StringToBitmap(data);
                    mapImg.setImageBitmap(bitmap);
                }
            });
        }
    };

    private void saveBitmapToJpeg(Bitmap bitmap) {
        // ?????? ????????? ?????? ????????? ????????????
        File storage = getCacheDir();
        Log.d("test", "" + storage);

        // ????????? ?????? ??????
        String fileName = "map.jpg";

        // storage??? ?????? ??????????????? ????????????
        File tempFile = new File(storage, fileName);
        try {
            // ???????????? ??? ????????? ???????????????.
            tempFile.createNewFile();

            // ????????? ??? ??? ?????? ???????????? ???????????????.
            FileOutputStream out = new FileOutputStream(tempFile);

            // compress ????????? ????????? ???????????? ???????????? ???????????????.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // ????????? ????????? ???????????????.
            out.close();
        
        } catch (FileNotFoundException e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }

    }

    public static Bitmap StringToBitmap(String encodedString){
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}



