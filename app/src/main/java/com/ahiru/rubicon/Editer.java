package com.ahiru.rubicon;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Editer extends AppCompatActivity {
    ChoiceOpenHelper choiceOpenHelper = null;

    public final static String EXTRA_TEXT = "com.example_koumei.buttondbtexttest.rubicon";

    EditText input_et;
    String title;
   int  exists;
   String inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Toolbarに戻るボタンを表示する
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        input_et = (EditText)findViewById(R.id.inputText);
        //画面起動時にソフトウェアキーボードを表示する
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        input_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //EnterKey判定
                if(event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                    //処理
                    toChoiceList(v);

                    return true;
                }
                return false;
            }
        });
    }

    public void toChoiceList(View v){
        inputText = input_et.getText().toString();
        Intent gIntent =getIntent();

        // インテント受け取り元を判別
        int fromMainList = gIntent.getIntExtra(MainList.FROM_MAINLIST,0);
        // 遷移前のActivityによって別の処理を行う
        if(fromMainList == 0) {

            if (choiceOpenHelper == null){
                choiceOpenHelper = new ChoiceOpenHelper(this);
            }

            // Titleを受け取って送り返す
            title = gIntent.getStringExtra(MainList.TITLENAME);
            SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();

            //titleIDを取得
            Cursor cursorTitleID = db.rawQuery("select " + TestContract.Titles._ID + " from " +
                            TestContract.Titles.TABLE_NAME + " where " + TestContract.Titles.COL_NAME + "=?",
                    new String[]{title});
            boolean nexttitle = cursorTitleID.moveToFirst();

            // タイトルに対応するタイトルIDを変数に代入
            while(nexttitle) {
                int titleID = cursorTitleID.getInt(0);
                nexttitle = cursorTitleID.moveToNext();
            }
            cursorTitleID.close();

            // 要素の重複判定
            try {
                Cursor c = db.rawQuery(
                        "select " +  TestContract.Choices.COL_NAME +
                                " from " + TestContract.Choices.TABLE_NAME +
                                " where " + TestContract.Choices.COL_NAME + "=?" +
                                " and " + TestContract.Choices.COL_TITLEID + " = " + "'" +
                                 title + "'",
                        new String[]{inputText}
                );
                exists = c.getCount();
                c.close();
            }finally {
                db.close();
            }

            if (inputText.length() > 0 && exists == 0){
                Intent intentToCL = new Intent(this, com.ahiru.rubicon.MainActivity.class);
                intentToCL.putExtra(EXTRA_TEXT, inputText);
                intentToCL.putExtra(MainList.TITLENAME,title);
                startActivity(intentToCL);
            }else if(exists != 0){
                String message = "既に登録済みです";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else{
                String message = "テキストを入力してください";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }else if(fromMainList == 1){
            //inputTextを送る
            Intent intentToML = new Intent(this, com.ahiru.rubicon.MainActivity.class);
            intentToML.putExtra(MainList.TITLENAME,inputText);
            // DBへの保存までボタンでやるか？→MainListでの受け取り値の判定（getindentがnullかどうか）が必要なくなる
            // これをやっておくことで、MainActivityではDBを開いてリストビューに表示するだけでよい。
            if (choiceOpenHelper == null){
                choiceOpenHelper = new ChoiceOpenHelper(this);
            }
            // ContentValuesの用意
            ContentValues values = new ContentValues();
            // db open
            SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();

            // タイトルの重複防止処理
            // Edittextに入力された文字列と同一のタイトルがDBに存在するか判定
            try {
                Cursor c = db.rawQuery(
                        "select " +  TestContract.Titles.COL_NAME +
                                " from " + TestContract.Titles.TABLE_NAME +
                                " where " + TestContract.Titles.COL_NAME + "=?",
                        new String[]{inputText}
                );
                exists = c.getCount();
                c.close();

                if (inputText != null && exists == 0) {
                    if (inputText.length() != 0) {
                        values.put(TestContract.Titles.COL_NAME, inputText);
                        db.insert(TestContract.Titles.TABLE_NAME, null, values);
                        // ここまででdbへの保存は完了
                    }
                }
            }finally {
                db.close();
            }
            if (inputText.length() > 0 && exists == 0){
                startActivity(intentToML);
            }else if(exists != 0){
                String message = "既に登録済みのタイトルです";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else{
                String message = "タイトルを入力してください";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }else if (fromMainList == 2){
            String oldTitle = gIntent.getStringExtra(MainList.TITLENAME);
            inputText = input_et.getText().toString();

            String UPDATE_TITLE = "update " + TestContract.Titles.TABLE_NAME +
                    " set " + TestContract.Titles.COL_NAME  + " = "  + "'" + inputText + "'" +
                    " where " + TestContract.Titles.COL_NAME + " = " + "'" + oldTitle + "'";

            Intent intentToCL = new Intent(this, com.ahiru.rubicon.MainActivity.class);


            if (choiceOpenHelper == null){
                choiceOpenHelper = new ChoiceOpenHelper(this);
            }

            SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();

            // タイトルの重複防止処理
            // Edittextに入力された文字列と同一のタイトルがDBに存在するか判定
            try {
                Cursor c = db.rawQuery(
                        "select " +  TestContract.Titles.COL_NAME +
                                " from " + TestContract.Titles.TABLE_NAME +
                                " where " + TestContract.Titles.COL_NAME + "=?",
                        new String[]{inputText}
                );
                exists = c.getCount();
                c.close();

                if (inputText != null && exists == 0) {
                    if (inputText.length() != 0) {
                        db.execSQL(UPDATE_TITLE);
                    }
                }
            }finally {
                db.close();
            }

            if (inputText.length() > 0 && exists == 0){
                intentToCL.putExtra(MainList.TITLENAME,inputText);
                startActivity(intentToCL);
            }else if(exists != 0){
                String message = "既に登録済みのタイトルです";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else{
                String message = "タイトルを入力してください";
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == R.id.check){
            View check = (View)findViewById(R.id.check);
            toChoiceList(check);
            return true;
        }

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
