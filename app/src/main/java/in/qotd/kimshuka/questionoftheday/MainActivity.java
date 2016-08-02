package in.qotd.kimshuka.questionoftheday;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {

                OkHttpClient client = new OkHttpClient();
                String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                Log.d(TAG,curDate);
                Request request = new Request.Builder()
                        .url("http://kimshuka.in/qotd/questions/get_questions/"+curDate)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String respJSon = response.body().string();
                    Log.d(TAG,respJSon);
//                    JSONArray JSArr = new JSONArray(respJSon);
                  return respJSon;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String res) {
                ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
                bar.setVisibility(View.INVISIBLE);
                if(res != null && !res.isEmpty()){
                try {
                    JSONArray JSArr = new JSONArray(res);
                    String questStr = JSArr.getJSONObject(0).optString("question");
                    String answerStr = JSArr.getJSONObject(0).optString("answer");
                    String dateStr = JSArr.getJSONObject(0).optString("answer_date");
                    TextView qustView = (TextView) findViewById(R.id.question);
                    TextView answer_info = (TextView) findViewById(R.id.timer_info);
                    qustView.setText(questStr);
                    answer_info.setVisibility(View.VISIBLE);
                    int curretn = 0;
                    try {
                        curretn = GetTimeInMiliSec(dateStr);
                        MyCount timerCount = new MyCount(curretn, 1000);
                            timerCount.setAnswer(answerStr);
                        timerCount.start();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                else {
                    TextView qustView = (TextView) findViewById(R.id.question);
                    qustView.setText(R.string.no_question);
                }
            }
        }.execute();


    }

//    public interface onResp(String quest){
//         TextView qustView = (TextView) findViewById(R.id.question);
//        qustView.setText(quest);
//    }

    public int GetTimeInMiliSec(String dateInString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
//        dateInString = dateInString+" 14:00:00";
//        String dateInString = "25-06-2016 00:50:56";
        Date date = sdf.parse(dateInString);
        long millis = System.currentTimeMillis();
        long ans_time = date.getTime();
        return (int) (ans_time - millis );
    }

    public class MyCount extends CountDownTimer {
        private TextView counterView = (TextView) findViewById(R.id.answer_countdown);
        private TextView timerInfo = (TextView) findViewById(R.id.timer_info);
        public String answerTxt = "";
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        public void setAnswer(String answer) {
            this.answerTxt = answer;
        }
        @Override
        public void onFinish() {
            //some script here
            timerInfo.setText("Answer");
            counterView.setText(this.answerTxt);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //some script here
            int sec = (int) (millisUntilFinished / 1000);
            int seconds = (int) (millisUntilFinished / 1000) % 60 ;
            int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
            int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
            counterView.setText(String.format("%d:%d:%d",hours,minutes,seconds));
        }
    }
}