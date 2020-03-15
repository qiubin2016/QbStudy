package com.qb.toolbox.demo01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.qb.toolbox.R;
import com.qb.toolbox.demo01.objectBox.Note;
import com.qb.toolbox.demo01.objectBox.NoteActivity;
import com.qb.toolbox.demo01.objectBox.ObjectBox;

//import org.json.JSONObject;
//import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import io.objectbox.Box;

public class Demo01Activity extends AppCompatActivity {
    private static final String TAG = Demo01Activity.class.getSimpleName();

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;

    private BeepManager beepManager;
    private View mLayout;

    private Box<Note> userBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo01);

        InitView();
        InitVariable();
//        GetTtyUsbPath();
        Snackbar.make(mLayout, "Snackbar test ......",
                Snackbar.LENGTH_SHORT)
                .show();

        userBox = ObjectBox.get().boxFor(Note.class);
    }

    private void InitView() {
        mButton1 = findViewById(R.id.button1);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "button1 OnClick, enter");
                beepManager.playBeepSoundAndVibrate();
                Log.i(TAG, "button1 OnClick, leave");
            }
        });

        mLayout = findViewById(R.id.linearLayout);
        mButton2 = findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "button2 OnClick, enter");
                //Snackbar用法
                Snackbar.make(mLayout, "Snackbar test ......",
                        Snackbar.LENGTH_SHORT)
                        .show();
                Log.i(TAG, "button2 OnClick, leave");
            }
        });

        mButton3 = findViewById(R.id.button3);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mButton4 = findViewById(R.id.button4);
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "this:" + this);
                startActivity(new Intent(Demo01Activity.this, NoteActivity.class));
            }
        });
    }

    private void InitVariable() {
        beepManager = new BeepManager(this);
        beepManager.update();
    }

    @Override
    protected void onPause() {
        super.onPause();

        beepManager.close();
    }

    public String GetTtyUsbPath(){
        String root = "/sys/bus/usb/devices/";
        File dev = new File(root);
        File[] files = dev.listFiles();
        for(int i = 0;i < files.length;++i){
            if(files[i].isDirectory()){
                File fProduct = new File(files[i].getAbsolutePath() + "/idProduct");
                File fVendor = new File(files[i].getAbsolutePath() + "/idVendor");
                if(fProduct.exists() && fVendor.exists()){
                    try {
                        LineNumberReader readerProduct = new LineNumberReader(new FileReader(fProduct));
                        String version = readerProduct.readLine();
                        if(version != null && version.equals("2303")){
                        }else{
                            continue;
                        }
                        readerProduct.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        LineNumberReader readerVendor = new LineNumberReader(new FileReader(fVendor));
                        String version = readerVendor.readLine();
                        if(version != null && version.equals("067b")){
                        }else{
                            continue;
                        }
                        readerVendor.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    File fdev = new File(files[i].getAbsolutePath());
                    File[] f = fdev.listFiles();
                    for(int j = 0;j < f.length;++j){
                        if(f[j].isDirectory() && f[j].getName().startsWith(files[i].getName())){
                            File ttyDev = new File(f[j].getAbsolutePath());
                            File[] fTty = ttyDev.listFiles();
                            for (int k = 0;k < fTty.length;++k){
                                if(fTty[k].getName().startsWith("ttyUSB")){
                                    Log.e(TAG, "Find GPS USB Dev Path:" + fTty[k].getName());
                                    return "/dev/" + fTty[k].getName();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public class DeviceInfoData  {
        public String password = null;
        public String username = null;
        public String clientId = null;

        @Override
        public String toString() {
//            return JSONObject.toJSONString(this);
//            JSONObject
            return "";
        }
    }

    public void removeUser(Note note) {
        userBox.remove(note);

//        updateUsers();
    }
}
