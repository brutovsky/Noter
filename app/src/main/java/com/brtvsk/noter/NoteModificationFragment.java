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
import android.widget.PopupMenu;

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

    private String filter[];

    private boolean MODIFICATION;

    private ImageView popup;

    public NoteModificationFragment(String filter[]) {
        this.filter = filter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(NoteModificationFragment.ARG_NOTE_ID);
        if (id == null) {
            note = newNote();
            MODIFICATION = false;
        }
        else {
            MODIFICATION = true;
            note = NotesStorage.getInstance(getActivity()).getNote(id);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note_modification, container, false);

        popup = v.findViewById(R.id.mod_note_fragment_marker);
        popup.setOnClickListener(this);

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
                noteMarkerView.setImageResource(R.drawable.default_24dp);
                break;
            }
            case IMPORTANT: {
                noteMarkerView.setImageResource(R.drawable.star_24dp);
                break;
            }
        }

        saveButton = v.findViewById(R.id.mode_save_button);
        saveButton.setOnClickListener(this);
        return v;
    }

    public static NoteModificationFragment newInstance(UUID noteId, String filter[]) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);
        NoteModificationFragment fragment = new NoteModificationFragment(filter);
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
                break;
            }
            case R.id.mod_note_fragment_marker: {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.fragment_list_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.default_item:
                                note.mark(Markers.DEFAULT);
                                setMarkerImage(Markers.DEFAULT);
                                return true;
                            case R.id.important_item:
                                note.mark(Markers.IMPORTANT);
                                setMarkerImage(Markers.IMPORTANT);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                break;
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

                        if (MODIFICATION) {
                            Intent intent = NotePagerActivity.newIntent(getActivity(), note.getId(), filter);
                            startActivity(intent);
                        } else {
                            Intent intent = MainActivity.newIntent(getActivity(),filter);
                            startActivity(intent);
                        }
                    }
                })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (MODIFICATION) {
                                    Intent intent = NotePagerActivity.newIntent(getActivity(), note.getId(), filter);
                                    startActivity(intent);
                                } else {
                                    Intent intent = MainActivity.newIntent(getActivity(),filter);
                                    startActivity(intent);
                                }
                            }
                        });
                return builder.create();
            }
            default: {
                return null;
            }
        }
    }

    public boolean isModification(){
        return MODIFICATION;
    }

    private void setMarkerImage(Markers marker) {
        switch (marker) {
            case DEFAULT: {
                popup.setImageResource(R.drawable.default_24dp);
                break;
            }
            case IMPORTANT: {
                popup.setImageResource(R.drawable.star_24dp);
                break;
            }
        }

    }
}
