package com.ahiru.rubicon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainResult extends AppCompatActivity {

    private Random randomGenerator;
    ArrayList choiceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Toolbarに戻るボタンを表示する
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        randomGenerator = new Random();
        //インテントを取得
        Intent gintent = getIntent();

        choiceList = gintent.getStringArrayListExtra(MainActivity.EXTRA_RANDOM);
        //リストの要素数からランダムな数字を抽出
        int index = randomGenerator.nextInt(choiceList.size());
        String result = (String) choiceList.get(index);

        //テキストビューに表示
        TextView tv_result = findViewById(R.id.tv_result);
        tv_result.setText(result);
    }

    //  もう一度ボタン
    public void oneMore(View v){
        //リストの要素数からランダムな数字を抽出
        int index = randomGenerator.nextInt(choiceList.size());
        String result = (String) choiceList.get(index);

        //テキストビューに表示
        TextView tv_result = findViewById(R.id.tv_result);
        tv_result.setText(result);
    }

    // トップに戻るボタン
    public void toMainList(View v){
        Intent intent = new Intent(this, com.ahiru.rubicon.MainList.class);
        startActivity(intent);
    }

    public void toChoiceList(View v){
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean result = true;

        switch (id){
            case android.R.id.home:
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }
}
