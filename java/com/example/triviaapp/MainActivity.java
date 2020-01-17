package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.data.AnswerListAsyncResponse;
import com.example.triviaapp.data.QuestionBank;
import com.example.triviaapp.model.Question;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DATA_ID = "data_prefs";
    private TextView txtQuestion;
    private TextView txtCounter;
    private Button true_btn;
    private Button false_btn;
    private ImageButton prev_btn;
    private ImageButton next_btn;
    private int currQuestionIndex = 0;
    private  List<Question> questionList;
    private TextView txtScore;
    private int score = 0;
    private int numberOfAnswer = 0;
    private TextView txtHighScore;
    private int intHighScore;
    private int intHighScoreChecker;
     CardView cardView;
     SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtCounter = findViewById(R.id.txtCounter);
        true_btn = findViewById(R.id.btnTrue);
        false_btn = findViewById(R.id.btnFalse);
        prev_btn = findViewById(R.id.btnPrev);
        next_btn = findViewById(R.id.btnNext);
        cardView = findViewById(R.id.cardView);
        next_btn.setOnClickListener(this);
        prev_btn.setOnClickListener(this);
        true_btn.setOnClickListener(this);
        false_btn.setOnClickListener(this);
        txtScore = findViewById(R.id.txtScore);
        txtHighScore = findViewById(R.id.txtHighScore);

        this.sharedPreferences = getSharedPreferences("com.example.triviaapp", MODE_PRIVATE);
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                if(sharedPreferences != null){
                    setText();
                    currQuestionIndex = sharedPreferences.getInt("counter",1);
                }else{
                    txtQuestion.setText(questionList.get(currQuestionIndex).getAnswer());
                    txtCounter.setText(MessageFormat.format("{0}/{1}", currQuestionIndex + 1, questionList.size()));
                }

            }
        });
        txtScore.setText(Integer.toString(score));
        txtHighScore.setText(MessageFormat.format("High Score: {0}", intHighScore));

    }
    public void saveText(){
        sharedPreferences.edit().putString("question", txtQuestion.getText().toString()).apply();
        sharedPreferences.edit().putInt("counter", currQuestionIndex).apply();
        if(score > intHighScore){
            sharedPreferences.edit().putInt("highScore", intHighScore).apply();

        }
    }

    public void setText(){

        String values = sharedPreferences.getString("question","");
        txtQuestion.setText(values);
        txtCounter.setText(MessageFormat.format("{0}/{1}", sharedPreferences.getInt("counter", 1) + 1, questionList.size()));
        int hsValues = sharedPreferences.getInt("highScore",1);
        String text = "High Score: "+ hsValues;
        txtHighScore.setText(text);
        intHighScore = hsValues;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPrev:
                currQuestionIndex--;
                if(currQuestionIndex < 0) currQuestionIndex++;
                nextQuestion(currQuestionIndex);

                break;
            case R.id.btnNext:
                currQuestionIndex++;
                nextQuestion(currQuestionIndex);
                break;
            case R.id.btnTrue:
                checkAnswer(true);

                break;
            case R.id.btnFalse:
                checkAnswer(false);
                break;

        }
    }

    public void checkAnswer(boolean userAnswer){
        boolean answer = questionList.get(currQuestionIndex).isAnswerTrue();

        if(userAnswer == answer){

            score+=100;
         //   numberOfAnswer++;
          //  intHighScoreChecker = score+2;
            Toast.makeText(this, "You are correct!!", Toast.LENGTH_SHORT).show();
            fadeAnimation();
            nextQuestion(currQuestionIndex);

        }else{
            Toast.makeText(this, "Wrong!!!", Toast.LENGTH_SHORT).show();
            shakeAnimation();
            nextQuestion(currQuestionIndex);
            //numberOfAnswer++;
            if(score > 0){
                score -=100;
            }

        }
        txtScore.setText(MessageFormat.format("Score: {0}", score));

        if(score > intHighScore){
            intHighScore = score;
            txtHighScore.setText(MessageFormat.format("High Score: {0}", intHighScore));
        }
    }

    public void nextQuestion(final int nextQ){
        if(nextQ < questionList.size()){
            txtQuestion.setText(questionList.get(nextQ).getAnswer());
            txtCounter.setText(MessageFormat.format("{0}/{1}", nextQ + 1, questionList.size()));

        }

        //        txtQuestion.setText(questionArrayList.get(currQuestionIndex).getAnswer());
    }

    public void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.DKGRAY);
                currQuestionIndex++;
                nextQuestion(currQuestionIndex);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    public void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shakeanimation);

        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.DKGRAY);
                currQuestionIndex++;
                nextQuestion(currQuestionIndex);
                //txtQuestion.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        saveText();
        super.onPause();
    }


}
