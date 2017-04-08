package com.apress.gerber.reminders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class RemindersActivity extends AppCompatActivity {
    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private SimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);
        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();
        insertDummyData();
        Cursor cursor = mDbAdapter.fetchAllReminders();

        String[] from = new String[]{RemindersDbAdapter.COL_CONTENT};
        int[] to = {R.id.row_text};
        mCursorAdapter = new RemindersSimpleCursorAdapter(this, R.layout.reminders_row, cursor, from, to, 0);
        mListView.setAdapter(mCursorAdapter);


    }

    private void insertDummyData() {
        mDbAdapter.deleteAllReminders();
        mDbAdapter.createReminder("Spotkanie z Tomkiem", true);
        mDbAdapter.createReminder("Siłownia", false);
        mDbAdapter.createReminder("Iść pobiegać", true);
        mDbAdapter.createReminder("Powtórzyć materiał", false);
        mDbAdapter.createReminder("Ugotować obiad", false);
        mDbAdapter.createReminder("Zrobić zakupy", false);
        mDbAdapter.createReminder("Naprawić pralkę", true);
        mDbAdapter.createReminder("Nie wiem co jeszcze", true);
        mDbAdapter.createReminder("Spotkanie z Tomkiem", true);
        mDbAdapter.createReminder("Siłownia", false);
        mDbAdapter.createReminder("Iść pobiegać", true);
        mDbAdapter.createReminder("Powtórzyć materiał", false);
        mDbAdapter.createReminder("Ugotować obiad", false);
        mDbAdapter.createReminder("Zrobić zakupy", false);
        mDbAdapter.createReminder("Naprawić pralkę", true);
        mDbAdapter.createReminder("Nie wiem co jeszcze", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Log.d(getLocalClassName(), "New reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;

        }
    }
}
