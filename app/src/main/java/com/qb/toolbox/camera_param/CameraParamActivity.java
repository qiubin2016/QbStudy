package com.qb.toolbox.camera_param;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ResourceManagerInternal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qb.toolbox.R;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CameraParamActivity extends AppCompatActivity {
    private static final String TAG = CameraParamActivity.class.getSimpleName();

    private static final String STRING_CAMERA_ID = "camera id";
    private static final String STRING_SUPPORTED_HARDWARE_LEVEL = "supported hardware level";
    private static final String STRING_FPS_RANGE = "fps range";
    private static final String STRING_AF_MODE = "AF mode";
    private static final String STRING_FLASH_MODE = "flash mode";
    private static final String STRING_APERTURES = "apertures";
    private static final String STRING_FOCAL_LENGTHS = "focal lengths";
    private static final String STRING_CAPABILITIES = "capabilities";
    private static final String STRING_REQUEST_KEYS= "request keys";


    private Button mButton;
    private TextView mLogTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_param_activity);

        initView();
    }

    void initView() {
        mButton = findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCameraParam();
            }
        });
        mLogTextview = findViewById(R.id.logTextview);
        //设置后进度条才能手动拖动
        mLogTextview.setMovementMethod(ScrollingMovementMethod.getInstance());
        mLogTextview.setScrollbarFadingEnabled(false);  //滚动条时刻显示
    }

    void getCameraParam() {
        String logText;
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();  //可以看作HashMap+LinkedList，用于保证插入顺序

        CameraManager manager = (CameraManager)this.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String camraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(camraId);

                linkedHashMap.clear();
                //camere id
                linkedHashMap.put(STRING_CAMERA_ID, camraId.toString());
                //相机类型
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                linkedHashMap.put(STRING_SUPPORTED_HARDWARE_LEVEL, facingToString(facing));
                //获取相机功能支持的情况
                Integer supportedHardwareLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                linkedHashMap.put(STRING_SUPPORTED_HARDWARE_LEVEL, supportedHardwareLevelToString(supportedHardwareLevel));
                //获取相机支持的配置属性
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //将"],"替换成"],\n"，即增加了换行符，使打印的数据更易懂
                linkedHashMap.put("map", map.toString().replace("],", "],\n"));
                int[] inputFormats = map.getInputFormats();
                linkedHashMap.put("input formats", Arrays.toString(inputFormats));
                int[] outputFormats = map.getOutputFormats();
                linkedHashMap.put("output formats", Arrays.toString(outputFormats));
                //获取相机帧率范围
                Range<Integer>[] fpsRanges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                linkedHashMap.put(STRING_FPS_RANGE, Arrays.toString(fpsRanges));
                //获取自动对焦（AF）模式列表
                int[] afRanges = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
                linkedHashMap.put(STRING_AF_MODE, Arrays.toString(afRanges));
                //获取相机闪光灯
                boolean flashMode = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                linkedHashMap.put(STRING_FLASH_MODE, flashMode ? "1" : "0");
                //获取光圈尺寸值列表
                float[] apertures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                linkedHashMap.put(STRING_APERTURES, Arrays.toString(apertures));
                //获取焦距列表
                float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                linkedHashMap.put(STRING_FOCAL_LENGTHS, Arrays.toString(focalLengths));
                //获取相机设备功能列表
                int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                linkedHashMap.put(STRING_CAPABILITIES, Arrays.toString(focalLengths));
                //获取相机设备可用的所有键的列表
//                int[] requestKeys = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_REQUEST_KEYS);
//                linkedHashMap.put(STRING_REQUEST_KEYS, Arrays.toString(requestKeys));
                //
//                ReprocessFormatsMap SCALER_AVAILABLE_INPUT_OUTPUT_FORMATS_MAP
                //显示到日志文本框
                showLogTextview(linkedHashMap);
            }
        }catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
//            ErrorDialog.newInstance(getString(R.string.camera_error))
//                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            Log.e(TAG, getString(R.string.camera_error));
        }
    }

    private String supportedHardwareLevelToString(Integer level) {
        String str = "get failed";

        if (null != level) {
            switch (level) {
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                    str = "limited";
                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                    str = "full";
                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                    str = "legacy";
                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                    str = "level 3";
                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
                    str = "external";
                    break;
                default:
                    str = "not found";
                    break;
            }
        }

        return str;
    }
    private String facingToString(Integer facing) {
        String str = "get failed";

        if (null != facing) {
            switch (facing) {
                case CameraCharacteristics.LENS_FACING_FRONT:
                    str = "front";
                    break;
                case CameraCharacteristics.LENS_FACING_BACK:
                    str = "back";
                    break;
                case CameraCharacteristics.LENS_FACING_EXTERNAL:
                    str = "external";
                    break;
                default:
                    str = "not found";
                    break;
            }
        }

        return str;
    }

    private void showLogTextview(final LinkedHashMap<String, String> map) {
        String logText = "---------------------\n";
        String tmp = "";

        //遍历
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            tmp = entry.getKey() + ": " + entry.getValue() + '\n';
            logText += tmp;
            Log.i(TAG, tmp);
        }
        logText += "---------------------\n";
        mLogTextview.append(logText);
    }

    /**
     * Shows an error message dialog.
     */
//    public static class ErrorDialog extends DialogFragment {
//
//        private static final String ARG_MESSAGE = "message";
//
//        public static ErrorDialog newInstance(String message) {
//            ErrorDialog dialog = new ErrorDialog();
//            Bundle args = new Bundle();
//            args.putString(ARG_MESSAGE, message);
//            dialog.setArguments(args);
//            return dialog;
//        }
//
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            Log.e(TAG, "requestPermission, onCreateDialog");
//            return new AlertDialog.Builder(this)
//                    .setMessage(getArguments().getString(ARG_MESSAGE))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Log.e(TAG, "requestPermission, onCreateDialog,onClick");
//                            activity.finish();
//                        }
//                    })
//                    .create();
//        }
//    }
}
