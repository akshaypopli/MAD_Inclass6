package com.example.inclass06;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//filename: MainActivity
//Group Number: groups1 3
//Members : Akshay Popli and Neel Solanki
//Assignment# InClas06

public class MainActivity extends AppCompatActivity {

    TextView tv_sc;
    TextView tv_title;
    TextView tv_date;
    ImageView iv_image;
    TextView tv_desc;
    ImageView iv_prev;
    ImageView iv_next;
    TextView tv_page;
    Button btn_select;
    int counter = 0;
    ProgressBar progressBar;
    ArrayList<News> result = new ArrayList<>();

    CharSequence[] items = {"Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");

        tv_sc = findViewById(R.id.tv_sc);
        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_date);
        iv_image = findViewById(R.id.iv_image);
        tv_desc = findViewById(R.id.tv_desc);
        iv_prev = findViewById(R.id.iv_prev);
        iv_next = findViewById(R.id.iv_next);
        tv_page = findViewById(R.id.tv_page);
        btn_select = findViewById(R.id.btn_select);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        iv_prev.setAlpha((float) 0.2);
        iv_next.setAlpha((float) 0.2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose Category").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CharSequence selectCat = items[i];
                Log.d("select category: ", selectCat.toString());
                tv_sc.setText(selectCat.toString());
                String url="https://newsapi.org/v2/top-headlines?category=" + selectCat.toString() + "&apiKey=dd7c77b2dba94fe4956539f81c86f967";

                new GetDataAsync().execute(url);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        final AlertDialog alert = builder.create();


        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(result.size() > 1){
                    counter += 1;
                    if(counter < result.size()){
                        tv_title.setText(result.get(counter).title);
                        tv_desc.setText(result.get(counter).desc);
                        tv_date.setText(result.get(counter).date);
                        Log.d("url", result.get(counter).urlToImage);
                        tv_page.setText(String.valueOf(counter+1) + " out of " + result.size());
                        Picasso.get().load(result.get(counter).urlToImage).into(iv_image);
                    } else {
                        counter = 0;
                        tv_title.setText(result.get(counter).title);
                        tv_desc.setText(result.get(counter).desc);
                        tv_date.setText(result.get(counter).date);
                        Log.d("url", result.get(counter).urlToImage);
                        tv_page.setText(String.valueOf(counter+1) + " out of " + result.size());
                        Picasso.get().load(result.get(counter).urlToImage).into(iv_image);
                    }
                }

            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.size() > 1){
                    counter -= 1;
                    if (counter >= 0) {
                        tv_title.setText(result.get(counter).title);
                        tv_desc.setText(result.get(counter).desc);
                        tv_date.setText(result.get(counter).date);
                        Log.d("url", result.get(counter).urlToImage);
                        tv_page.setText(String.valueOf(counter+1) + " out of " + result.size());
                        Picasso.get().load(result.get(counter).urlToImage).into(iv_image);
                    } else {
                        counter = result.size()-1;
                        tv_title.setText(result.get(counter).title);
                        tv_desc.setText(result.get(counter).desc);
                        tv_date.setText(result.get(counter).date);
                        Log.d("url", result.get(counter).urlToImage);
                        tv_page.setText(String.valueOf(counter+1) + " out of " + result.size());
                        Picasso.get().load(result.get(counter).urlToImage).into(iv_image);
                    }
                }

            }

        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public class GetDataAsync extends AsyncTask<String, Void, ArrayList<News>> {

        @Override
        protected ArrayList<News> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<News> result = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");
                    if(articles.length() == 0){
                        Toast.makeText(getApplicationContext(), "No News Found", Toast.LENGTH_SHORT).show();
                    }else if(articles.length() > 20){
                        for(int i=0; i< 20;i++){
                            JSONObject articlesJSON = articles.getJSONObject(i);
                            News news = new News();
                            news.title = articlesJSON.getString("title");
                            news.date = articlesJSON.getString("publishedAt");
                            news.desc= articlesJSON.getString("description");
                            news.urlToImage = articlesJSON.getString("urlToImage");
                            result.add(news);
                        }
                    }else{
                        for(int i=0; i< articles.length();i++){
                            JSONObject articlesJSON = articles.getJSONObject(i);
                            News news = new News();
                            news.title = articlesJSON.getString("title");
                            news.date = articlesJSON.getString("publishedAt");
                            news.desc= articlesJSON.getString("description");
                            news.urlToImage = articlesJSON.getString("urlToImage");
                            result.add(news);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<News> s) {
            result = s;
            tv_title.setText(s.get(counter).title);
            tv_desc.setText(s.get(counter).desc);
            tv_date.setText(s.get(counter).date);
            Log.d("url", s.get(counter).urlToImage);
            tv_page.setText(String.valueOf(counter+1) + " out of " + result.size());
            Picasso.get().load(s.get(counter).urlToImage).into(iv_image);
            progressBar.setVisibility(View.INVISIBLE);
            if(result.size() >1 ){
                iv_prev.setAlpha((float) 1);
                iv_next.setAlpha((float) 1);
            }
        }
    }
}
