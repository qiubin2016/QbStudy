package com.qb.toolbox.demo01.objectBox;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.qb.toolbox.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class NoteActivity extends Activity {

    public static final String TAG = NoteActivity.class.getSimpleName();
    private EditText editText;
    private EditText editTextId;
    private View addNoteButton;
    private Button queryButton;

    private Box<Note> notesBox;
    private Query<Note> notesQuery;
    private NotesAdapter notesAdapter;
    private NotesAdapter queryAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setUpViews();

        notesBox = ObjectBox.get().boxFor(Note.class);

        //query all notes, sorted a-z by their text (https://docs.objectbox.io/queries)
        notesQuery = notesBox.query().order(Note_.text).build();
        updateNotes();
    }

    private void updateNotes() {
        List<Note> notes = notesQuery.find();  //查询
        notesAdapter.setNotes(notes);
    }

    protected void setUpViews() {
        ListView listView = findViewById(R.id.listViewNotes);
        listView.setOnItemClickListener(noteClickListener);

        notesAdapter = new NotesAdapter();
        listView.setAdapter(notesAdapter);

        ListView listViewQuery = findViewById(R.id.listViewQuery);
        listViewQuery.setOnItemClickListener(queryClickListener);

        queryAdapter = new NotesAdapter();
        listViewQuery.setAdapter(queryAdapter);

        addNoteButton = findViewById(R.id.buttonAdd);
        addNoteButton.setEnabled(false);

        queryButton = findViewById(R.id.buttonQuery);

        editText = findViewById(R.id.editTextNote);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNote();
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                addNoteButton.setEnabled(enable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextId = findViewById(R.id.id);
    }

    public void onAddButtonClick(View view) {
        addNote();
    }

    public void onQueryButtonClick(View view) {
        long id = 0;
        String idText = editTextId.getText().toString();
        try {
            id = Integer.parseInt(idText);
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
        }
        List<Note> notes = notesBox.query().equal(Note_.id, id).order(Note_.text).build().find();  //查询
        queryAdapter.setNotes(notes);
    }

    private void addNote() {
        String noteText = editText.getText().toString();
        editText.setText("");

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        Note note = new Note();
        note.setText(noteText);
        note.setComment(comment);
        note.setDate(new Date());
        notesBox.put(note);
        Log.d(ObjectBoxApp.TAG, "Inserted new note, ID: " + note.getId());

        updateNotes();
    }

    AdapterView.OnItemClickListener noteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Note note = notesAdapter.getItem(position);
            notesBox.remove(note);
            Log.d(ObjectBoxApp.TAG, "Deleted note, ID: " + note.getId());
            updateNotes();
        }
    };

    AdapterView.OnItemClickListener queryClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Note note = notesAdapter.getItem(position);
            notesBox.remove(note);
            Log.d(ObjectBoxApp.TAG, "Deleted note, ID: " + note.getId());
            updateNotes();
        }
    };
}
