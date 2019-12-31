package com.brtvsk.noter;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
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

public class NoteFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private EditText noteEditText;
    private EditText noteEditDescription;
    private ImageView noteMarkerView;
    private Note note;

    public NoteFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(NoteFragment.ARG_NOTE_ID);
        note = NotesStorage.getInstance(getActivity()).getNote(id);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        noteEditText = v.findViewById(R.id.note_fragment_text);
        noteEditText.setText(note.getNote());
        noteEditText.addTextChangedListener(new TextChangedListener<EditText>(noteEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                note.setNote(s.toString());
                NotesStorage.getInstance(getActivity()).updateNote(note);
            }
        });

        noteEditDescription = v.findViewById(R.id.note_fragment_description);
        noteEditDescription.setText(note.getDescription());
        noteEditDescription.addTextChangedListener(new TextChangedListener<EditText>(noteEditDescription) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                note.setDescription(s.toString());
                NotesStorage.getInstance(getActivity()).updateNote(note);
            }
        });

        noteMarkerView = v.findViewById(R.id.note_fragment_marker);

        switch (note.getMarker()) {
            case DEFAULT: {
                noteMarkerView.setBackgroundResource(R.drawable.default_24dp);
                break;
            }
            case IMPORTANT: {
                noteMarkerView.setBackgroundResource(R.drawable.star_24dp);
                break;
            }
            default: {

            }
        }

        return v;
    }

    public static NoteFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, id);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_note: {
                NotesStorage.getInstance(getActivity()).deleteNote(note);
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
