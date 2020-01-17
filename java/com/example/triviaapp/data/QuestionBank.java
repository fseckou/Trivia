package com.example.triviaapp.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.triviaapp.controller.AppController;
import com.example.triviaapp.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.triviaapp.controller.AppController.TAG;

public class QuestionBank {

    ArrayList<Question> questionArrayList = new ArrayList<>();
    String s = "";
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, (JSONArray) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              for(int i =0; i<response.length(); i++){
                  try {
                      // getting questions and answers from the api and instantiating new objects for every
                      // question and answer

                      Question question = new Question();
                      question.setAnswer(response.getJSONArray(i).get(0).toString());
                      question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                      // populate the arrayList to be returned
                      questionArrayList.add(question);
                      //System.out.println(response);

                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }
              if(callBack != null){
                  callBack.processFinished(questionArrayList);
              }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return questionArrayList;
    }

}
