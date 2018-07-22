package com.ahiru.rubicon;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.ahiru.rubicon.MainList.FROM_MAINLIST;

public class MainActivity extends AppCompatActivity {

    EditText et_1;
    ChoiceOpenHelper choiceOpenHelper = null;
    ArrayAdapter<String> adapter;
    ArrayList<String> choiceList;
    public final static String EXTRA_RANDOM = "com.example_koumei.rubicontest.MainResult";
    Intent gintent;
    String edittitle;
    int titleID;
    TextView emptyTextView;
    String inputtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Toolbarに戻るボタンを表示する
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        if (choiceOpenHelper == null){
            choiceOpenHelper = new ChoiceOpenHelper(this);
        }

        // インテントを受け取る
        gintent = getIntent();
        inputtext = gintent.getStringExtra(Editer.EXTRA_TEXT);
//      edittitle = gintent.getStringExtra(TitleEditer.EXTRA_TITLE);

        // タイトル表示
        edittitle = gintent.getStringExtra(MainList.TITLENAME);
        TextView titletv = findViewById(R.id.titletv);
        titletv.setText(edittitle);

        // 要素をデータベースに追加
        insertChoiceData();

        // アダプター作成
        makeAdapter();

        // ListviewにAdapterを関連付ける
        ListView listView = findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        // 長押し時の削除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             * @return
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String dereteItem = (String)((TextView)view).getText();


                // 項目をリスト上から削除する
                adapter.remove(dereteItem);
                // 削除を反映する
                adapter.notifyDataSetChanged();
                // Toastで○○を削除しましたと表示する
                String messsage = dereteItem + "を削除しました";
                Toast toast = Toast.makeText(MainActivity.this, messsage, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
                // choiceテーブルからデータを削除する
                String deleteRecordSQL = "delete from " + TestContract.Choices.TABLE_NAME +
                        " where " + TestContract.Choices.COL_NAME + " = '" +  dereteItem + "'";
                SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();
                db.execSQL(deleteRecordSQL);
                db.close();
                return false;
            }
        });

        // 要素がない場合に別のViewを表示
        emptyTextView = (TextView)findViewById(R.id.emptyTextView2);
        listView.setEmptyView(emptyTextView);
    }

    // 以下ボタンメソッド
    public void toMainList(View v){
        Intent intent = new Intent(this, com.ahiru.rubicon.MainList.class);
        startActivity(intent);
    }

    public void toEditer(View v){
        Intent intentTE = new Intent(this, com.ahiru.rubicon.Editer.class);
        intentTE.putExtra(MainList.TITLENAME, edittitle);
        startActivity(intentTE);
    }

    public void toMainResult(View v){
        if (choiceList.size() != 0) {
            Intent intentTR = new Intent(this, com.ahiru.rubicon.MainResult.class);
            intentTR.putStringArrayListExtra(EXTRA_RANDOM, choiceList);
            startActivity(intentTR);
        }else {
            //要素を入力してくださいとエラーメッセージで表示
            String message = "要素を登録してください";
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 300);
            toast.show();
        }
    }

    public void titleEditButton(View v){
        Intent intentTE = new Intent(this, com.ahiru.rubicon.Editer.class);
        intentTE.putExtra(MainList.TITLENAME, edittitle);
        intentTE.putExtra(FROM_MAINLIST,  2);  // 2は編集時のアクセス
        startActivity(intentTE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    public void insertChoiceData() {
        // ContentValuesの用意
        ContentValues values = new ContentValues();
        // db open
        SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();
        try {
            if (inputtext != null) {
                if (inputtext.length() != 0) {
                    // タイトル文がインテントで受け取ったtitleと同じ文字列となっているレコードのタイトルIDをselect
                    Cursor c = db.rawQuery("select " + TestContract.Titles._ID + " from " +
                                    TestContract.Titles.TABLE_NAME + " where " + TestContract.Titles.COL_NAME + "=?",
                            new String[]{edittitle});


                    //Cursorの初期化
                    boolean next = c.moveToFirst();

                    // タイトルに対応するタイトルIDを変数に代入
                    while (next) {
                        titleID = c.getInt(0);
                        next = c.moveToNext();
                    }
                    c.close();

                    values.put(TestContract.Choices.COL_NAME, inputtext); // 要素をContentValueに追加
                    values.put(TestContract.Choices.COL_TITLEID, titleID); // タイトルIDをContentValueに追加
                    db.insert(TestContract.Choices.TABLE_NAME, null, values);
                }
            }
        } finally {
            db.close();
        }
    }


    public void makeAdapter() {
        // データベースから値を取得
        //choicelistデータを格納する変数を用意
        choiceList = new ArrayList<>();
        //データベース取得
        SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();
        try {
            //titleIDを取得
            Cursor cursorTitleID = db.rawQuery("select " + TestContract.Titles._ID + " from " +
                            TestContract.Titles.TABLE_NAME + " where " + TestContract.Titles.COL_NAME + "=?",
                    new String[]{edittitle});
            boolean nexttitle = cursorTitleID.moveToFirst();

            // タイトルに対応するタイトルIDを変数に代入
            while (nexttitle) {
                titleID = cursorTitleID.getInt(0);
                nexttitle = cursorTitleID.moveToNext();
            }
            cursorTitleID.close();

            //rawQueryというselect専用メソッドを使用してデータを取得する
            Cursor c = db.rawQuery(
                    "select " + TestContract.Choices.COL_NAME +
                            " from " + TestContract.Choices.TABLE_NAME + " WHERE " + TestContract.Choices.COL_TITLEID + "=?" + " order by " + TestContract.Choices._ID,
                    new String[]{String.valueOf(titleID)}
            );
            //Cursorの初期化
            boolean next = c.moveToFirst();

            //取得したすべての行をリストに入力
            while (next) {
                String choice = c.getString(0);
                choiceList.add(choice);
                next = c.moveToNext();
            }
            c.close();
        } finally {
            db.close();
        }
        // Adapter作成
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, choiceList);
    }
}
