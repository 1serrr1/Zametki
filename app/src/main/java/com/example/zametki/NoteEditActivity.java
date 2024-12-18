package com.example.zametki;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Toast;
import android.content.Intent;



public class NoteEditActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSave;
    private Button buttonBack;
    private Button buttonDelete;

    private NoteDatabase database;
    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        // Инициализация элементов интерфейса
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.edit_text_content);
        buttonSave = findViewById(R.id.button_save);
        buttonBack = findViewById(R.id.button_back);
        buttonDelete = findViewById(R.id.button_delete);

        database = NoteDatabase.getInstance(this);

        // Получаем данные из интента
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note_id")) {
            noteId = intent.getIntExtra("note_id", -1);
            if (noteId != -1) {
                editTextTitle.setText(intent.getStringExtra("note_title"));
                editTextContent.setText(intent.getStringExtra("note_content"));
            } else {
                Toast.makeText(this, "Ошибка загрузки заметки", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Обработчик кнопки "Сохранить"
        buttonSave.setOnClickListener(v -> saveNote());

        // Обработчик кнопки "Назад"
        buttonBack.setOnClickListener(v -> finish());

        // Обработчик кнопки "Удалить"
        buttonDelete.setOnClickListener(v -> deleteNote());
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (noteId == -1) {
                // Создание новой заметки
                Note note = new Note(title, content);
                database.noteDao().insert(note);
            } else {
                // Обновление существующей заметки
                Note note = new Note(title, content);
                note.setId(noteId);
                database.noteDao().update(note);
            }

            runOnUiThread(() -> {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }).start();
    }

    private void deleteNote() {
        if (noteId != -1) {
            new Thread(() -> {
                Note noteToDelete = new Note("", "");
                noteToDelete.setId(noteId);
                database.noteDao().delete(noteToDelete);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
            }).start();
        } else {
            Toast.makeText(this, "Невозможно удалить", Toast.LENGTH_SHORT).show();
        }
    }
}


