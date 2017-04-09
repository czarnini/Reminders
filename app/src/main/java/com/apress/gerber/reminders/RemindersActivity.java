package com.apress.gerber.reminders;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);
        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();
        //insertDummyData();
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
                            int nId = getIdFromPosition(masterListPosition);
                            Reminder reminder = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);
                        }
                        else
                        {
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
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
                fireCustomDialog(null);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;

        }
    }

    private void fireCustomDialog(final Reminder reminder)
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView            titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText      editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button              commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox      checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout        rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);

        final boolean isEditOperation = (reminder != null);

        if(isEditOperation)
        {
            titleView.setText("Edycja Przypomnienia");
            checkBox.setChecked(reminder.getmImportant() == 1);
            editCustom.setText(reminder.getmContent());
        }

        commitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                String reminderText = editCustom.getText().toString();
                if(isEditOperation)
                {
                    Reminder newReminder = new Reminder(reminder.getmID(), reminderText, checkBox.isChecked()? 1:0);
                    mDbAdapter.updateReminder(newReminder);
                }
                else
                {
                    mDbAdapter.createReminder(reminderText, checkBox.isChecked());
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.custom_button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
