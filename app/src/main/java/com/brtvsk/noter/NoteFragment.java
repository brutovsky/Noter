package com.brtvsk.noter;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;

import com.brtvsk.noter.utils.TextChangedListener;

import java.util.UUID;

public class NoteFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";
    public static final String DIALOG_DELETE = "delete";

    private EditText noteEditText;
    private TextView descriptionView;
    private ImageView noteMarkerView;
    private Note note;

    private String[] filter;

    public NoteFragment(String[] filter) {
        this.filter = filter;
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

        descriptionView = v.findViewById(R.id.note_fragment_description);
        descriptionView.setText(note.getDescription());

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

    public static NoteFragment newInstance(UUID id,String filter[]) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, id);
        NoteFragment fragment = new NoteFragment(filter);
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
                onDialogCreate(DIALOG_DELETE).show();
                return true;
            }
            case R.id.menu_item_edit: {
                Intent intent = NoteModificationActivity.newIntent(getActivity(), note.getId(),filter);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Dialog onDialogCreate(String tag) {
        switch (tag) {
            case DIALOG_DELETE: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.deleteConfirmation).setTitle(R.string.warning).setCancelable(true).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NotesStorage.getInstance(getActivity()).deleteNote(note);
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
