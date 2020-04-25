package com.example.android.thefactorfactor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private int num;
    private EditText number;
    private Button submit;
    private Button[] op = new Button[3];
    private TextView result;
    private int streak = 0;
    private int bestStreak = 0;
    private boolean flag = true;
    private TextView streakView;
    private TextView bestStreakView;
    private LinearLayout rootview;
    private SharedPreferences sharedpref;
    private Button reset;
    private TextView countDownText;
    private CountDownTimer countdown;
    private long timeLeftMS=10000;
    private boolean timeRunning;
    private int score=0;
    private TextView scoreView;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        op[0] = findViewById(R.id.option1);
        op[1] = findViewById(R.id.option2);
        op[2] = findViewById(R.id.option3);
        v=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        scoreView=findViewById(R.id.score_num);
        countDownText=findViewById(R.id.countdownText);
        rootview = findViewById(R.id.root);
        streakView = findViewById(R.id.streak_number);
        bestStreakView = findViewById(R.id.best_streak_num);
        result = findViewById(R.id.result);
        number = findViewById(R.id.input_number);
        submit = findViewById(R.id.submit_num);
        reset = findViewById(R.id.reset);
        Context context = getApplicationContext();

        sharedpref = context.getSharedPreferences(getString(R.string.sp_file), Context.MODE_PRIVATE);
        if (sharedpref.contains(getString(R.string.save_best_key))) {
            displayBestStreak();
        } else {
            SharedPreferences.Editor editor = sharedpref.edit();
            editor.putInt(getString(R.string.save_best_key), bestStreak);
            editor.apply();
            displayBestStreak();
        }
        displayStreak();
        displayScore();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num == Integer.parseInt(number.getText().toString())) ;
                else {
                    flag = true;
                    result.setText("");
                    rootview.setBackgroundColor(getResources().getColor(R.color.background));
                    result.setBackgroundColor(getResources().getColor(R.color.background));
                    num = Integer.parseInt(number.getText().toString());
                    if (isPrime(num)) {
                        Toast invalidNum;
                        invalidNum = Toast.makeText(getApplicationContext(), "Enter a number that is not prime ", Toast.LENGTH_SHORT);
                        invalidNum.show();
                    } else {
                        int[] options = makeOptions(num);
                        //Now show the options:
                        changeOptions(options);
                        resetTimer();
                        startTimer();

                        for (int i = 0; i < 3; i++) {
                            op[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (flag) {
                                        Button Check = findViewById(v.getId());
                                        if(timeRunning) {
                                            if (num % Integer.parseInt((String) Check.getText()) == 0) {
                                                rightResult();
                                                flag = false;
                                                resetOptions();
                                            } else {
                                                wrongResult();
                                                flag = false;
                                            }
                                            stopTimer();
                                            resetTimer();
                                            result.setTextColor(getResources().getColor(R.color.black));
                                            result.setTextSize(34);
                                            result.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
                                    }
                                }
                            });
                        }

                        reset.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bestStreak = 0;
                                saveBestStreak();
                                displayBestStreak();
                                streak = 0;
                                displayStreak();
                                rootview.setBackgroundColor(getResources().getColor(R.color.background));
                                result.setText("");
                                result.setBackgroundColor(getResources().getColor(R.color.background));
                                num=0;
                                score=0;
                                displayScore();
                                resetOptions();
                                resetButtonColors();
                                resetTimer();
                                stopTimer();
                            }
                        });
                    }
                }
            }
        });
    }

    private void displayRightAnswer() {
        for(int i =0 ;i<3;i++){
            if(num%(Integer.parseInt((String) op[i].getText()))==0){
                op[i].setBackgroundColor(getResources().getColor(R.color.right));
            }
        }
    }

    private void rightResult() {
        result.setText(R.string.right);
        rootview.setBackgroundColor(getResources().getColor(R.color.right));
        streak++;
        score+=20;
        displayStreak();
        compareStreaks();
        displayBestStreak();
        displayScore();
    }

    private void displayScore() {
        scoreView.setText(Integer.toString(score));
        scoreView.setBackgroundColor(getResources().getColor(R.color.best));
    }

    private void wrongResult() {
        result.setText(R.string.wrong);
        rootview.setBackgroundColor(getResources().getColor(R.color.wrong));
        compareStreaks();
        streak = 0;
        score-=10;
        displayStreak();
        displayBestStreak();
        displayRightAnswer();
        displayScore();
        v.vibrate(400);
    }

    private void displayBestStreak() {
        bestStreak = sharedpref.getInt(getString(R.string.save_best_key), 0);
        bestStreakView.setText(Integer.toString(bestStreak));
        bestStreakView.setBackgroundColor(getResources().getColor(R.color.best));
    }

    private void compareStreaks() {
        bestStreak = sharedpref.getInt(getString(R.string.save_best_key), 0);
        if (streak > bestStreak) {
            bestStreak = streak;
            saveBestStreak();
        }
    }

    private void saveBestStreak() {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt(getString(R.string.save_best_key), bestStreak);
        editor.apply();
    }

    private void displayStreak() {
        streakView.setText(Integer.toString(streak));
        streakView.setBackgroundColor(getResources().getColor(R.color.best));
    }
    private void changeOptions(int[] options) {
        op[0].setText(Integer.toString(options[0]));
        op[1].setText(Integer.toString(options[1]));
        op[2].setText(Integer.toString(options[2]));
    }

    private int[] makeOptions(int num) {
        int[] a = new int[3];
        int j = 0;
        int indexRandom = (int) Math.floor(Math.random() * 3);
        int crtOne = findFactor(num);
        int[] n = findNotFactor(num);

        for (int i = 0; i < 3; i++) {
            if (i == indexRandom) {
                a[i] = crtOne;
            } else {
                a[i] = n[j];
                j++;
            }
        }
        return a;
    }

    private int findFactor(int num) {
        int m;
        while (true) {
            m = (int) (Math.random() * Math.abs(num)) + 1;
            if (num % m == 0&&m!=1&&m!=num) {
                return m;
            }
        }
    }

    private int[] findNotFactor(int num) {
        int m, i = 0;
        int[] returnArray = new int[2];
        while (i < 2) {
            m = (int) (Math.random() *Math.abs(num))+ 1;
            if (num % m != 0) {
                if (i == 1) {
                    if (num != returnArray[0]) {
                        returnArray[i] = m;
                        i++;
                    }
                }
                else {
                    returnArray[i] = m;
                    i++;
                }
            }
        }
        return returnArray;
    }

    private boolean isPrime(int n) {
        boolean flag = true;
        n = Math.abs(n);
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void resetOptions() {
        number.setText("");
        op[0].setText(R.string.option_a);
        op[1].setText(R.string.option_b);
        op[2].setText(R.string.option_c);
    }

    private void startTimer(){
        countdown=new CountDownTimer(timeLeftMS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMS=millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                updateTimer();
                wrongResult();
                timeRunning=false;
            }
        }.start();
        timeRunning=true;
    }

    private void updateTimer() {
        int secs= (int) (timeLeftMS/1000);
        countDownText.setText(Integer.toString(secs));
        if(secs<4){
            countDownText.setTextColor(getResources().getColor(R.color.critical));
        }
    }

    private void resetTimer(){
        timeLeftMS=10000;
        countDownText.setTextColor(getResources().getColor(R.color.black));
        countDownText.setText(Integer.toString(10));
        timeRunning=false;
    }

    private void resetButtonColors(){
        for(int i=0;i<3;i++){
            op[i].setBackgroundColor(getResources().getColor(R.color.optionOrange));
        }
    }

    private void stopTimer(){
        countdown.cancel();
        timeRunning=false;
    }
}
