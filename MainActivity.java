package com.example.yuta.chordeditor;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;







public class MainActivity extends AppCompatActivity {


    public void songlist(View v) {
        Intent dbIntent = new Intent(getApplication(),SongList.class);
        startActivity(dbIntent);
    }

    public void dataBase(View v) {
        Intent dbIntent = new Intent(MainActivity.this,ShowDataBase.class);
        startActivity(dbIntent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();
        final EditText nameText = (EditText) findViewById(R.id.editName);
        final EditText idText = (EditText) findViewById(R.id.editID);

        Button entryButton = (Button) findViewById(R.id.insert);
        entryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                //String id = idText.getText().toString();

                if (name.trim().equals("")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("エラー")
                            .setMessage("データ追加に失敗しました。")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    String sql = "insert into note(name) values('" + name + "');";
                    db.execSQL(sql);
                }
            }
        });

        Button updateButton = (Button) findViewById(R.id.update);
        updateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String id = idText.getText().toString();

                if (name.equals("")) {
                    Toast.makeText(MainActivity.this, "タイトルを入力してください。",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues updateValues = new ContentValues();
                    updateValues.put("name", name);
                    db.update("note", updateValues, "id=?", new String[]{id});
                }
            }
        });

        Button deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String id = idText.getText().toString();

                if (name.equals("")) {
                    Toast.makeText(MainActivity.this, "タイトルを入力してください。",
                            Toast.LENGTH_SHORT).show();
                } else {
                    db.delete("note", "id=?", new String[]{id});
                }
            }
        });

        Button deleteAllButton = (Button) findViewById(R.id.deleteAll);
        deleteAllButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String id = idText.getText().toString();
                db.delete("note", null, null);
            }
        });

}


    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.v("LifeCycle", "onDestroy");
    }

}