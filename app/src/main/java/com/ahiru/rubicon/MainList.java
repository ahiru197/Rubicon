package com.ahiru.rubicon;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import junit.framework.Test;

import java.util.ArrayList;

public class MainList extends AppCompatActivity {

    ChoiceOpenHelper choiceOpenHelper = null;
    ArrayAdapter<String> titleadapter;
    ArrayList<String> titleList;
    public final static String FROM_MAINLIST = "com.example_koumei.rubicontest.MainList";
    public final static String TITLENAME = "com.example_koumei.rubicontest.TITLENAME";
    TextView emptyTextView;
    int titleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setLogo(R.drawable.logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.icon1);

        if (choiceOpenHelper == null){
            choiceOpenHelper = new ChoiceOpenHelper(MainList.this);
        }

        // アダプター作成
        makeAdapter();

        //ListviewにAdapterを関連付ける
        ListView mainListView = findViewById(R.id.mainListView);
        mainListView.setAdapter(titleadapter);

        // リスト項目をクリックしたときの処理
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             *
             * @param parent ListView
             * @param view 選択した項目
             * @param position 選択した項目の添字
             * @param id 選択した項目のID
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainList.this, com.ahiru.rubicon.MainActivity.class);

                // 選択されたビューを取得
                ListView listView = (ListView)parent;
                String item = (String)listView.getItemAtPosition(position);

                //Stringを送る
                intent.putExtra(TITLENAME,item);

                startActivity(intent);
            }
        });

        // 長押し時の処理
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                titleadapter.remove(dereteItem);
                // 削除を反映する
                titleadapter.notifyDataSetChanged();
                // Toastで○○を削除しましたと表示する
                String messsage = dereteItem + "を削除しました";
                Toast toast = Toast.makeText(MainList.this, messsage, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();

                // Titleテーブルから削除する & 対応するChoice要素をChoiceテーブルから削除

                SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();

                try {
                    // Choiceテーブルからデータを削除する
                    // 長押しされたタイトルのStringから_IDの値を取得
                    Cursor c = db.rawQuery("select " + TestContract.Titles._ID + " from " +
                                    TestContract.Titles.TABLE_NAME + " where " + TestContract.Titles.COL_NAME + "=?",
                            new String[]{dereteItem});
                    //Cursorの初期化
                    boolean next = c.moveToFirst();

                    // タイトルに対応するタイトルIDを変数に代入
                    while (next) {
                        titleID = c.getInt(0);
                        next = c.moveToNext();
                    }
                    c.close();
                    // 該当するChoiceレコードを削除
                    String deleteChoice = "delete from " + TestContract.Choices.TABLE_NAME +
                            " where " + TestContract.Choices.COL_TITLEID + "= '" + titleID + "'";

                    db.execSQL(deleteChoice);
                    // Titleテーブルからデータを削除する
                    String deleteRecordSQL = "delete from " + TestContract.Titles.TABLE_NAME +
                            " where " + TestContract.Titles.COL_NAME + " = '" +  dereteItem + "'";
                    db.execSQL(deleteRecordSQL);
                }finally {
                    db.close();
                }

                return false;
            }
        });

        // 要素がない場合に別のViewを表示
        emptyTextView = (TextView)findViewById(R.id.emptyTextView);
        mainListView.setEmptyView(emptyTextView);

    }

    public void toCreate(View v){
        Intent intent = new Intent(this, com.ahiru.rubicon.Editer.class);
        intent.putExtra(FROM_MAINLIST,  1);
        startActivity(intent);
    }

    public void makeAdapter(){
        // データベースから値を取得
        //choicelistデータを格納する変数を用意
        titleList = new ArrayList<>();
        //データベース取得
        SQLiteDatabase db = choiceOpenHelper.getWritableDatabase();
        if(choiceOpenHelper !=null) {
            try {
                //rawQueryというselect専用メソッドを使用してデータを取得する
                Cursor c = db.rawQuery(
                        "select " + TestContract.Titles.COL_NAME +
                                " from " + TestContract.Titles.TABLE_NAME + " order by " + TestContract.Titles._ID,
                        null
                );

                //Cursorの初期化
                boolean next = c.moveToFirst();

                //取得したすべての行をリストに入力
                while (next) {
                    String title = c.getString(0);
                    titleList.add(title);
                    next = c.moveToNext();
                }
            } finally {
                db.close();
            }
        }

        // Adapter作成
        titleadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList);
    }

    // 以下オプションメニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
