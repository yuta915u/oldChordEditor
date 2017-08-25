package com.example.yuta.chordeditor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Created by yuta on 2017/08/21.
 */

public class LyricsEdit extends AppCompatActivity {
    MyOpenHelper helper = new MyOpenHelper(this);
    int id =1;

    // ツールバー
    public void toolbar(String name){
        // ツールバーをアクションバーとして使う
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // UPナビゲーションを有効化する
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(getApplication(),EditPage.class);
                dbIntent.putExtra("id", id);
                startActivity(dbIntent);
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyrics_edit);
        Intent intent = getIntent();
        id = intent.getIntExtra("id",1);


        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select name,data from note where id ="+ id +";";
        Cursor c = db.rawQuery(sql,null);
        boolean mov1 = c.moveToFirst();

        String name = c.getString(0);
        String data = c.getString(1);
        toolbar(name);


        EditText editText = (EditText) findViewById(R.id.edit1);
        editText.setText(data);

        c.close();
        db.close();

    }





    @Override
    protected void onRestart() {
        super.onRestart();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select name,data from note where id ="+ id +";";
        Cursor c = db.rawQuery(sql,null);
        boolean mov1 = c.moveToFirst();

        String name = c.getString(0);
        String data = c.getString(1);
        toolbar(name);

        EditText editText = (EditText) findViewById(R.id.edit1);
        editText.setText(data);

        c.close();
        db.close();
    }



    @Override
    protected void onPause() {
        super.onPause();
        // ここでファイル保存処理を行う
        EditText editText = (EditText) findViewById(R.id.edit1);
        String text = editText.getText().toString();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "update note set data = '"+ text +"' where id = "+ id +";";
        db.execSQL(sql);
        Log.i("テスト  ", "ファイル保存！！");
        db.close();
    }







}
