package com.example.yuta.chordeditor;

/**
 * Created by yuta on 2017/08/14.
 */
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowDataBase extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_database);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // queryメソッドの実行例
        Cursor c = db.query("note", new String[] { "id", "name"}, null,
                null, null, null, null,null);
        boolean mov = c.moveToFirst();//cの中をすべて見終わるまでまでtrue
        while (mov) {
            TextView textView = new TextView(this);
            textView.setText(String.format("%d : %s", c.getInt(0),c.getString(1)));
            layout.addView(textView);
            mov = c.moveToNext();
        }
        c.close();
        db.close();
    }
}