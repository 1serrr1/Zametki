package com.example.zametki;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;

    private NoteDatabase database;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);

        database = NoteDatabase.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        loadNotes();

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });

        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
            intent.putExtra("note_id", note.getId());
            intent.putExtra("note_title", note.getTitle());
            intent.putExtra("note_content", note.getContent());
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        });
    }

    private void loadNotes() {
        new Thread(() -> {
            List<Note> notes = database.noteDao().getAllNotes();
            runOnUiThread(() -> adapter.setNotes(notes));
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            loadNotes();
        }
    }
}
