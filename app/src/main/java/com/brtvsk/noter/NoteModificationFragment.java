package com.brtvsk.noter;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.brtvsk.noter.utils.TextChangedListener;

import java.util.UUID;

public class NoteModificationFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private EditText noteEditText;
    private EditText noteEditDescription;
    private ImageView noteMarkerView;
    private Note note;

    public NoteModificationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(NoteModificationFragment.ARG_NOTE_ID);
        if (id == null) note = newNote();
        else {
            note = NotesStorage.getInstance(getActivity()).getNote(id);
            Log.println(Log.ERROR,"ERROr",note.getId().toString());
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note_modification, container, false);
        noteEditText = v.findViewById(R.id.mod_note_fragment_text);
        noteEditText.setText(note.getNote());
        noteEditDescription = v.findViewById(R.id.mod_note_fragment_description);
        noteEditDescription.setText(note.getDescription());
        noteMarkerView = v.findViewById(R.id.mod_note_fragment_marker);

        switch (note.getMarker()) {
            case DEFAULT: {
                noteMarkerView.setBackgroundResource(R.drawable.default_24dp);
                break;
            }
            case IMPORTANT: {
                noteMarkerView.setBackgroundResource(R.drawable.star_24dp);
                break;
            }
        }

        return v;
    }

    public static NoteModificationFragment newInstance(UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);
        NoteModificationFragment fragment = new NoteModificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Note newNote() {
        int order = NotesStorage.getInstance(getActivity()).getOrder() + 1;
        return new Note(order);
    }

}
