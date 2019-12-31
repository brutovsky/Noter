package com.brtvsk.noter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brtvsk.noter.database.NoteBaseHelper;
import com.brtvsk.noter.database.NoteCursorWrapper;
import com.brtvsk.noter.database.NoteDBSchema.NoteTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NotesStorage {
    private static NotesStorage NOTES = null;
    private Context context;
    private SQLiteDatabase db;

    private NotesStorage(Context context) {
        context = context.getApplicationContext();
        db = new NoteBaseHelper(context)
                .getWritableDatabase();
    }

    public static NotesStorage getInstance(Context context) {
        if (NOTES == null) {
            NOTES = new NotesStorage(context);
        }
        return
                NOTES;
    }

    public List<Note> getNotes() {
        NoteCursorWrapper cursor = queryNotes(null, null);
        List<Note> notes = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    public List<Note> getNotes(String[] filter) {
        List<Note> notes = new ArrayList<>();

        NoteCursorWrapper cursor = queryNotes(NoteTable.Cols.MARKER + "=? OR " + NoteTable.Cols.MARKER + "=?", filter);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    public Note getNote(UUID id) {
        NoteCursorWrapper cursor = queryNotes(
                NoteTable.Cols.UUID + " = ?", new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    public void updateNote(Note note) {
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);
        db.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteTable.Cols.DESCRIPTION, note.getDescription());
        values.put(NoteTable.Cols.DATE, note.getDate().getTime());
        values.put(NoteTable.Cols.MARKER, note.getMarker().toString());
        values.put(NoteTable.Cols.TEXT, note.getNote());
        values.put(NoteTable.Cols.POSITION, note.getPosition());
        return values;
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        return querySortedNotes(whereClause, whereArgs, NoteTable.Cols.POSITION);
    }

    public void addNote(Note note) {
        ContentValues values = getContentValues(note);
        db.insert(NoteTable.NAME, null, values);
    }

    public void deleteNote(Note note) {
        ContentValues values = getContentValues(note);
        db.delete(NoteTable.NAME, NoteTable.Cols.UUID + " = ?", new String[]{note.getId().toString()});
    }

    public void swapNotes(Note note1, Note note2) {
        int pos = note2.getPosition();
        note2.setPosition(note1.getPosition());
        note1.setPosition(pos);
        updateNote(note1);
        updateNote(note2);
    }

    public int getOrder() {
        Cursor cursor = db.rawQuery("Select MAX(" + NoteTable.Cols.POSITION + ") as " + NoteTable.Cols.POSITION + " from " + NoteTable.NAME, null);
        if (!cursor.moveToFirst()) return -1;
        return cursor.getInt(cursor.getColumnIndex(NoteTable.Cols.POSITION));
    }

    public void sortBy(String order) {
        NoteCursorWrapper cursor = querySortedNotes(null, null, order);
        List<Note> notes = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        for (int i = 0; i < notes.size(); ++i) {
            notes.get(i).setPosition(i + 1);
            updateNote(notes.get(i));
        }
    }

    private NoteCursorWrapper querySortedNotes(String whereClause, String[] whereArgs, String order) {
        return new NoteCursorWrapper(db.query(
                NoteTable.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                order // orderBy
        ));
    }
}
