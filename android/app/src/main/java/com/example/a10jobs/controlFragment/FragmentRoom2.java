package com.example.a10jobs.controlFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a10jobs.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentRoom2 extends Fragment {
    int[] applianceStatus = new int[17];
    ImageView airconImg, curtainImg, lightImg;
    ImageView airconButton, curtainButton, lightButton;
    TextView aircon, curtain, light;

    String url = "http://j5d201.p.ssafy.io:12001";
    Socket socket;
    {
        try {
            socket = IO.socket(url);
            Log.v("socket", String.valueOf(socket));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    JSONObject data = new JSONObject();     // socket.emit할 때 필요한 JSON객체

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_room2, container, false);
        socket.on("sendApplianceStatus", onAppliance);
        socket.connect();

        airconButton = (ImageView) v.findViewById(R.id.airconButton_room2);
        curtainButton = (ImageView) v.findViewById(R.id.curtainButton_room2);
        lightButton = (ImageView) v.findViewById(R.id.lightButton_room2);

        aircon = (TextView) v.findViewById(R.id.airconState_room2);
        airconImg = (ImageView) v.findViewById(R.id.aircon_room2);

        curtain = (TextView) v.findViewById(R.id.curtainState_room2);
        curtainImg = (ImageView) v.findViewById(R.id.curtain_room2);

        light = (TextView) v.findViewById(R.id.lightState_room2);
        lightImg = (ImageView) v.findViewById(R.id.light_room2);

        airconButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(applianceStatus[8] == 2){
                    Toast.makeText(getActivity(), "에어컨을 작동시킵니다!", Toast.LENGTH_SHORT).show();
                    try {
                        data.put("ctr_cmd", 1);
                        data.put("ctr_num", 8);
                        socket.emit("AirConditionerOnToServer", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(applianceStatus[8] == 1){
                    Toast.makeText(getActivity(), "에어컨 작동을 멈춥니다!", Toast.LENGTH_SHORT).show();
                    try {
                        data.put("ctr_cmd", 2);
                        data.put("ctr_num", 8);
                        socket.emit("AirConditionerOffToServer", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        lightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(applianceStatus[2] == 2){
                    Toast.makeText(getActivity(), "불을 켭니다!", Toast.LENGTH_SHORT).show();
                    try {
                        data.put("ctr_cmd", 1);
                        data.put("ctr_num", 2);
                        socket.emit("LightOnToServer", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(applianceStatus[2] == 1){
                    Toast.makeText(getActivity(), "불을 끕니다!", Toast.LENGTH_SHORT).show();
                    try {
                        data.put("ctr_cmd", 2);
                        data.put("ctr_num", 2);
                        socket.emit("LightOffToServer", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        curtainButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(applianceStatus[14] == 2){
                    Toast.makeText(getActivity(), "커튼을 엽니다!", Toast.LENGTH_SHORT).show();
                    try {
                        data.put("ctr_cmd", 1);
                        data.put("ctr_num", 14);
                        socket.emit("CurtainOnToServer", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(applianceStatus[14] == 1){
                    Toast.makeText(getActivity(), "커튼을 닫습니다!", Toast.LENGTH_SHORT).show();
                    try {
                        data.put("ctr_cmd", 2);
                        data.put("ctr_num", 14);
                        socket.emit("CurtainOffToServer", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return v;
    }

    // 리스너 -> 이벤트를 보냈을 때 이 리스너가 실행됨
    private Emitter.Listener onAppliance = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
//                   Log.v("data", data);
                        String[] tmp = data.split(",");
                        for (int i = 0; i < tmp.length; i++) {
//                        Log.v("data", tmp[i]);
                            String[] tmp2 = tmp[i].split(":");
                            String num = tmp2[1].trim();
                            num = num.replace("}", "");
                            num = num.replace("{", "");
                            applianceStatus[i] = Integer.parseInt(num);
                            Log.v("data", i + " : " + String.valueOf(applianceStatus[i]));
                        }
                        if (applianceStatus[8] == 1) {
                            airconButton.setImageResource(R.drawable.power_on);
                            aircon.setText("켜짐");
                            airconImg.setImageResource(R.drawable.airconon);
                        } else if (applianceStatus[8] == 2) {
                            airconButton.setImageResource(R.drawable.power_off);
                            aircon.setText("꺼짐");
                            airconImg.setImageResource(R.drawable.airconoff);
                        }

                        if (applianceStatus[2] == 1) {
                            light.setText("켜짐");
                            lightImg.setImageResource(R.drawable.lamps_on);
                        } else if (applianceStatus[2] == 2) {
                            light.setText("꺼짐");
                            lightImg.setImageResource(R.drawable.lamps_off);
                        }

                        if (applianceStatus[14] == 1) {
                            curtain.setText("닫힘");
                            curtainImg.setImageResource(R.drawable.curtain_on);
                        } else if (applianceStatus[14] == 2) {
                            curtain.setText("열림");
                            curtainImg.setImageResource(R.drawable.curtain_off);
                        }
                    }
                });
            }
        }
    };
}
