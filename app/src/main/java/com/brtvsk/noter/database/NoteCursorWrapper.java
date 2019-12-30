package com.brtvsk.noter.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.brtvsk.noter.Markers;
import com.brtvsk.noter.Note;
import com.brtvsk.noter.database.NoteDBSchema.NoteTable;

import java.util.Date;
import java.util.UUID;

public class NoteCursorWrapper extends CursorWrapper {
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote() {
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String description = getString(getColumnIndex(NoteTable.Cols.DESCRIPTION));
        long date = getLong(getColumnIndex(NoteTable.Cols.DATE));
        String marker = getString(getColumnIndex(NoteTable.Cols.MARKER));
        String text = getString(getColumnIndex(NoteTable.Cols.TEXT));

        Note note = new Note(UUID.fromString(uuidString), getInt(getColumnIndex(NoteTable.Cols.POSITION)));
        note.setDescription(description);
        note.setDate(new Date(date));
        note.mark(Markers.valueOf(marker));
        note.setNote(text);

        return note;
    }
}