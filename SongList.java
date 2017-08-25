package com.example.yuta.chordeditor;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import android.database.DatabaseUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuta on 2017/08/15.
 */

public class SongList extends AppCompatActivity {
    MyOpenHelper helper = new MyOpenHelper(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //新規作成時タイトルを聞く
    public void TitleAsk(final int flag){
        //テキスト入力を受け付けるビューを作成します。
        final EditText editView = new EditText(SongList.this);
        editView.setInputType(InputType.TYPE_CLASS_TEXT );
        AlertDialog.Builder b = new AlertDialog.Builder(SongList.this);
        b.setView(editView);
        b.setTitle("タイトルを入力してください");
        b.setPositiveButton(android.R.string.ok, null);
        b.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = b.show();
        Button buttonP = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button buttonN = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        final SQLiteDatabase db = helper.getReadableDatabase();
        if(flag>=0){
            String sql = "select name from note where id ="+ flag +";";
            Cursor c = db.rawQuery(sql,null);
            c.moveToFirst();
            String title = c.getString(0);
            editView.setText(title);
        }


        // 通常のViewのように実装します。
        buttonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editView.getText().toString().trim();

                if (name.equals("")) {
                    Toast.makeText(SongList.this, "タイトルを正しく入力してください。", Toast.LENGTH_SHORT).show();
                }else{

                    String sql = "select * from note where name ='"+ name +"';";
                    Cursor c = db.rawQuery(sql,null);
                    boolean torf = c.moveToFirst();//cの中をすべて見終わるまでまでtrue
                    if(torf){
                        if(flag<0) {
                            Toast.makeText(SongList.this, "すでに存在するタイトルです。", Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(getIntent());
                            dialog.dismiss();
                        }
                    }else {
                        if(flag<0) {
                            int recode = (int) DatabaseUtils.queryNumEntries(db, "note");
                            String sql2 = "insert into note(name) values('" + name + "');";
                            db.execSQL(sql2);
                            dialog.dismiss();
                            Intent dbIntent = new Intent(getApplication(), LyricsEdit.class);
                            dbIntent.putExtra("id", recode + 1);
                            startActivity(dbIntent);
                        }else{

                            String sql3 = "update note set name = '"+ name +"' where id = "+flag+";";
                            db.execSQL(sql3);
                            startActivity(getIntent());
                            dialog.dismiss();
                        }
                    }
                    c.close();
                }

                db.close();
            }
        });

        // 通常のViewのように実装します。
        buttonN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    // ツールバー
    public void toolbar(){

        // ツールバーをアクションバーとして使う
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // UPナビゲーションを有効化する
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(getApplication(),MainActivity.class);
                startActivity(dbIntent);
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // アイテムクリック時の処理
                switch (item.getItemId()) {
                    case R.id.action_new:
                        TitleAsk(-1);
                        Log.i("テスト  ", "新規作成");
                        return true;
                    case R.id.action_edit:
                        Log.i("テスト  ", "編集");
                        return true;
                    default:
                        return false;
                }
            }
        });

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songlist);
        toolbar();


        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("note", new String[] { "id", "name", "data"}, null,null, null, null, null,null);
        boolean mov = c.moveToFirst();


        //int recode=(int)DatabaseUtils.queryNumEntries(db, "note");


        // ListView を取得
        ListView listView = (ListView) findViewById(R.id.nameList);
        List<Map<String, String>> retDataList = new ArrayList<Map<String, String>>();
        Map<String, String> data = new HashMap<String, String>();

        int i=1;
        while (mov) {
            int id = c.getInt(0);
            if(id !=i){
                String sql2 = "update note set id = "+i+" where id = "+id+";";
                db.execSQL(sql2);
                Log.i("テスト  ", "更新あり"+i);
            }else{
                Log.i("テスト  ", "更新なし"+id+"  "+i);
            }
            String str = c.getString(1);
            String str2 = c.getString(2);
            String sid = String.valueOf(id);
            data = new HashMap<String, String>();
            data.put("title",str);
            data.put("data", str2);
            data.put("id", sid);
            retDataList.add(data);
            mov = c.moveToNext();
            i++;
        }


        // リストビューに渡すアダプタを生成します。
        SimpleAdapter adapter2 = new SimpleAdapter(this, retDataList,
                R.layout.list_layout, new String[] { "title", "data","id" },
                new int[] {R.id.item1, R.id.item2,R.id.item3 });

        listView.setAdapter(adapter2);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent dbIntent = new Intent(getApplication(),EditPage.class);
                dbIntent.putExtra("id", position+1);
                startActivity(dbIntent);
            }
        });



        // リストビューのアイテムが長押しされた時に呼び出す。
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, View view,
                                           final int position, long id) {


                final CharSequence[] shareItems = {"開く", "タイトル変更", "削除","キャンセル"};

                AlertDialog.Builder builder = new AlertDialog.Builder(SongList.this);
                builder.setItems(shareItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Log.i("テスト  ", "開く");
                                Intent dbIntent = new Intent(getApplication(),EditPage.class);
                                dbIntent.putExtra("id", position+1);
                                startActivity(dbIntent);
                                break;
                            case 1:
                                Log.i("テスト  ", "タイトル変更");
                                TitleAsk(position+1);
                                break;
                            case 2:
                                Log.i("テスト  ", "削除");
                                SQLiteDatabase db2 = helper.getReadableDatabase();
                                db2.delete("note", "id="+position+1,null);
                                db2.close();
                                finish();
                                startActivity(getIntent());

                                break;
                            case 3:
                                Log.i("テスト  ", "キャンセル");
                                break;
                        }
                    }
                });
                AlertDialog shareAlert = builder.create();
                shareAlert.show();



                return true;
            }
        });



        c.close();
        db.close();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // ここでファイル保存処理を行う
    }
}
