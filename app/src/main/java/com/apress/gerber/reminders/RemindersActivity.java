package com.apress.gerber.reminders;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RemindersActivity extends AppCompatActivity
{
    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private SimpleCursorAdapter mCursorAdapter;

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState)
    {
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int masterListPosition, long l)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeListView = new ListView(RemindersActivity.this);
                String[] modes = {"Edytuj", "Usuń"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(RemindersActivity.this,
                        android.R.layout.simple_expandable_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        if (i == 0)
                        {
                            Toast.makeText(RemindersActivity.this,
                                    "Edycja pozycji " + masterListPosition, Toast.LENGTH_SHORT ).show();
                        }
                        else
                        {
                            Toast.makeText(RemindersActivity.this,
                                       "Usuwanie pozycji " + masterListPosition, Toast.LENGTH_SHORT ).show();

                        }

                        dialog.dismiss();

                    }
                });
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener(){
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b)
                {

                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
                {
                    MenuInflater inflater = actionMode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
                {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
                {
                   switch (menuItem.getItemId())
                   {
                       case R.id.menu_item_delete_reminder:
                           for (int nC = mCursorAdapter.getCount()-1; nC >=0; --nC)
                           {
                               if (mListView.isItemChecked(nC))
                                   mDbAdapter.deleteReminderById(getIdFromPosition(nC));
                           }
                           actionMode.finish();
                           mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                           return true;
                   }
                   return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode)
                {

                }
            });
        }

    }

    private int getIdFromPosition(int nC)
    {
        return (int)mCursorAdapter.getItemId(nC);
    }

    private void insertDummyData()
    {
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
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
