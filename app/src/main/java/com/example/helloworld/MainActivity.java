package com.example.helloworld;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, RecognitionListener {

    private static final String TAG = "AndroidSpeech";
    private Button mStartBtn;
    private TextView mLogTv;
    private SpeechRecognizer mRecognizer;
    private long mStartTime;

    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private int status = STATUS_None;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mStartBtn = (Button) findViewById(R.id.and_speech_btn);
        mLogTv = (TextView) findViewById(R.id.and_speech_tv);

        mStartBtn.setOnClickListener(this);


        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(this);


    }
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 4321;

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.and_speech_btn) {
//            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//
//            // Display an hint to the user about what he should say.
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说标准普通话");//注意不要硬编码
//
//            // Given an hint to the recognizer about what the user is going to say
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//
//            // Specify how many results you want to receive. The results will be sorted
//            // where the first result is the one with higher confidence.
//            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);//通常情况下，第一个结果是最准确的。
//
//            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (activities.size() != 0) {
            /*Activity  存在*/
                try
                {

                    //通过Intent传递语音识别的模式,开启语音
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    //语言模式和自由形式的语音识别
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //提示语音开始
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"开始语音");
                    //开始执行我们的Intent、语音识别
                    startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
                }
                catch (ActivityNotFoundException e)
                {
                    //找不到语音设备装置
                    Toast.makeText(MainActivity.this,
                            "ActivityNotFoundException",
                            Toast.LENGTH_LONG).show();
                }
            } else {
            /*  Activity  Not  Found   未判断会抛出ActivityNotFoundException*/
                Toast.makeText(MainActivity.this,
                        "activity 不存在",
                        Toast.LENGTH_LONG).show();
            }

//            switch (status) {
//                case STATUS_None:
//                    start();
//                    mStartBtn.setText("取消");
//                    status = STATUS_WaitingReady;
//                    break;
//                case STATUS_WaitingReady:
//                    cancel();
//                    status = STATUS_None;
//                    mStartBtn.setText("开始");
//                    break;
//                case STATUS_Ready:
//                    cancel();
//                    status = STATUS_None;
//                    mStartBtn.setText("开始");
//                    break;
//                case STATUS_Speaking:
//                    stop();
//                    status = STATUS_Recognition;
//                    mStartBtn.setText("识别中");
//                    break;
//                case STATUS_Recognition:
//                    cancel();
//                    status = STATUS_None;
//                    mStartBtn.setText("开始");
//                    break;
//            }
        }
    }
    //当语音结束时的回调函数onActivityResult
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        // 判断是否是我们执行的语音识别
        if(requestCode==VOICE_RECOGNITION_REQUEST_CODE&&resultCode==RESULT_OK)
        {
            // 取得语音的字符
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //设置视图更新
            //mList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,results));
            String resultsString = "";
            for (int i = 0; i < results.size(); i++)
            {
                resultsString += results.get(i);
            }
            Toast.makeText(this,resultsString,Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void start() {
        promptSpeechInput();
    }

    private void stop() {
        mRecognizer.stopListening();
    }

    private void cancel() {
        mRecognizer.cancel();
        status = STATUS_None;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecognizer.destroy();
    }

    private void promptSpeechInput() {
        mStartTime = System.currentTimeMillis();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speech_prompt");
                //getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mRecognizer.startListening(intent);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
        status = STATUS_Ready;
        print("准备完毕");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
        mStartBtn.setText("说完了");
        print("开始录音");
        status = STATUS_Speaking;
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
        mStartBtn.setText("识别中");
        print("开始识别");
        status = STATUS_Recognition;
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "error code: " + error + " msg: " + getErrorMsg(error));
        mStartBtn.setText("开始");
        status = STATUS_None;
    }


    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults: " + results.toString());
        dump(results);
        mStartBtn.setText("开始");
        status = STATUS_None;
        stop();
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("翻译最终结果: " + nbest.get(0));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "partialResults: " + partialResults.toString());
        dump(partialResults);
        ArrayList<String> nbest = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");
        if(nbest!= null){
            if (nbest.size() > 0) {
                print("翻译部分结果: " + nbest.get(0));
            }
        }

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "type: " + eventType + "params: " + params.toString());
        dump(params);
    }

    private void print(final String msg) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long t = System.currentTimeMillis() - mStartTime;
                mLogTv.append(t + "ms ---- " + msg + "\n");
                ScrollView sv = (ScrollView) mLogTv.getParent();
                sv.smoothScrollTo(0, 1000000);
                Log.d(TAG, "---- " + t + "ms ---- " + msg);
            }
        });
    }

    private void dump(Bundle bundle) {
        if (bundle != null) {
            Log.d(TAG, "--- dumping " + bundle.toString());
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);

                    Log.d(TAG, String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));


            }
        }
    }
    public static String getErrorMsg(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network operation timed out.";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Other network related errors.";
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error.";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server sends error status.";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Other client side errors.";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input.";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No recognition result matched.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "RecognitionService busy.";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions.";
            default:
                return "Unknown error.";
        }
    }


}
