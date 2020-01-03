package com.brtvsk.noter;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.UUID;

public class NoteModificationFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_NOTE_ID = "note_id";

    private static final String DIALOG_TOSAVE = "yes_or_no";

    private EditText noteEditText;
    private EditText noteEditDescription;
    private ImageView noteMarkerView;
    private ExtendedFloatingActionButton saveButton;
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
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note_modification, container, false);
        noteEditText = v.findViewById(R.id.mod_note_fragment_text);
        noteEditText.setText(note.getNote());

        noteEditText.addTextChangedListener(new TextChangedListener<EditText>(noteEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                note.setNote(s.toString());
            }
        });

        noteEditDescription = v.findViewById(R.id.mod_note_fragment_description);
        noteEditDescription.setText(note.getDescription());

        noteEditDescription.addTextChangedListener(new TextChangedListener<EditText>(noteEditDescription) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                note.setDescription(s.toString());
            }
        });

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

        saveButton = v.findViewById(R.id.mode_save_button);
        saveButton.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        Log.println(Log.ERROR, "AD", "Aa");
        switch (view.getId()) {
            case R.id.mode_save_button: {
                onDialogCreate(DIALOG_TOSAVE).show();
            }
        }
    }

    private Dialog onDialogCreate(String tag) {
        switch (tag) {
            case DIALOG_TOSAVE: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.toSave).setCancelable(true).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (NotesStorage.getInstance(getActivity()).getNote(note.getId()) == null)
                            NotesStorage.getInstance(getActivity()).addNote(note);
                        else
                            NotesStorage.getInstance(getActivity()).updateNote(note);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                return builder.create();
            }
            default: {
                return null;
            }
        }
    }
}
