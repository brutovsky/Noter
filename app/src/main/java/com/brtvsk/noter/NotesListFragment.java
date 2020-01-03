package com.brtvsk.noter;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtvsk.noter.database.NoteDBSchema;
import com.brtvsk.noter.utils.ItemTouchHelperAdapter;
import com.brtvsk.noter.utils.SimpleItemTouchHelperCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListFragment extends Fragment {

    public static final String EXTRA_FILTER = "FILTER";

    private RecyclerView recView;
    private NotesAdapter notesAdapter;
    private static final String DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss";

    private Set<String> filter;

    public NotesListFragment() {
    }

    public NotesListFragment(String[] filter) {
        this.filter = new HashSet(Arrays.asList(filter));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (filter == null)
            if (savedInstanceState != null) {
                filter = new HashSet<>(Arrays.asList(savedInstanceState.getStringArray(EXTRA_FILTER)));
            } else {
                {
                    filter = new HashSet<>();
                    filter.add(Markers.IMPORTANT.toString());
                    filter.add(Markers.DEFAULT.toString());
                }
            }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] filtr = new String[filter.size()];
        outState.putStringArray(EXTRA_FILTER, filter.toArray(filtr));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes_list, container, false);
        recView = v.findViewById(R.id.notes_recycler_view);
        recView.setLayoutManager(new LinearLayoutManager(
                (getActivity())));
        updateUI();
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(notesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recView);
        return v;
    }

    public void updateUI() {
        NotesStorage noteStorage = NotesStorage.getInstance(getActivity());
        String[] filtr = new String[filter.size()];
        List<Note> notes = noteStorage.getNotes(filter.toArray(filtr));
        if (notesAdapter == null) {
            notesAdapter = new NotesAdapter(notes);
            recView.setAdapter(notesAdapter);
        } else {
            notesAdapter.setCrimes(notes);
            notesAdapter.notifyDataSetChanged();
        }
        ImageView image = getActivity().findViewById(R.id.sad_picture);
        if(notes.size() <= 0){
            image.setVisibility(View.VISIBLE);
        }else{
            image.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView description;
        private TextView date;
        private ImageView popup;
        private Note note;

        public NoteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            description =
                    itemView.findViewById(R.id.note_decription);
            description.setOnClickListener(this);
            date = itemView.findViewById(R.id.note_date);
            date.setOnClickListener(this);

            popup = itemView.findViewById(R.id.note_popup);
            popup.setOnClickListener(this);

        }

        public void bindNote(Note note) {
            this.note = note;
            description.setText(note.getDescription());

            String dateFormat = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingsFragment.KEY_PREF_DATEFORMAT, DATE_FORMAT);

            String dateString = DateFormat.format(dateFormat,
                    note.getDate()).toString();
            date.setText(dateString);
            setMarkerImage(note.getMarker());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.note_popup: {
                    PopupMenu popup = new PopupMenu(getActivity(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.fragment_list_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.default_item:
                                    note.mark(Markers.DEFAULT);
                                    NotesStorage.getInstance(getActivity()).updateNote(note);
                                    setMarkerImage(Markers.DEFAULT);
                                    return true;
                                case R.id.important_item:
                                    note.mark(Markers.IMPORTANT);
                                    NotesStorage.getInstance(getActivity()).updateNote(note);
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
                default: {
                    String[] filtr = new String[filter.size()];
                    Intent intent = NotePagerActivity.newIntent(getActivity(),
                            note.getId(), filter.toArray(filtr));
                    startActivity(intent);
                }
            }
        }

        private void setMarkerImage(Markers marker) {
            switch (marker) {
                case DEFAULT: {
                    popup.setImageResource(R.drawable.default_24dp);
                    //popup.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorDefault));
                    break;
                }
                case IMPORTANT: {
                    popup.setImageResource(R.drawable.star_24dp);
                    //popup.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorStar));
                    break;
                }
            }

        }
    }

    private class NotesAdapter extends RecyclerView.Adapter<NoteHolder> implements ItemTouchHelperAdapter {
        private List<Note> notes;

        public NotesAdapter(List<Note> notes) {
            this.notes = notes;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_note, parent, false);
            return new NoteHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
            Note note = notes.get(position);
            holder.bindNote(note);
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        public void setCrimes(List<Note> crimes) {
            notes = crimes;
        }

        @Override
        public void onItemDismiss(int position) {
            NotesStorage.getInstance(getActivity()).deleteNote(notes.get(position));
            for (int i = position; i < notes.size() - 1; i++) {
                NotesStorage.getInstance(getActivity()).swapNotes(notes.get(i), notes.get(i + 1));
            }
            NotesStorage.getInstance(getActivity()).deleteNote(notes.get(position));
            notes.remove(position);
            notifyItemRemoved(position);
            updateUI();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    NotesStorage.getInstance(getActivity()).swapNotes(notes.get(i), notes.get(i + 1));
                    Collections.swap(notes, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    NotesStorage.getInstance(getActivity()).swapNotes(notes.get(i), notes.get(i - 1));
                    Collections.swap(notes, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_note: {
                return createNote();
            }
            case R.id.menu_item_sortby: {
                sortByPopup();
                return true;
            }
            case R.id.menu_item_filter: {
                itemFilterPopup();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean createNote() {
        String[] filtr = new String[filter.size()];
        startActivity(NoteModificationActivity
                .newIntent(getActivity(), filter.toArray(filtr)));
        return true;
    }

    private void sortByPopup() {
        View vItem = getActivity().findViewById(R.id.menu_item_sortby);
        PopupMenu popup = new PopupMenu(getActivity(), vItem);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.fragment_list_sort_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sortby_description:
                        NotesStorage.getInstance(getActivity()).sortBy(NoteDBSchema.NoteTable.Cols.DESCRIPTION);
                        break;
                    case R.id.sortby_date:
                        NotesStorage.getInstance(getActivity()).sortBy(NoteDBSchema.NoteTable.Cols.DATE);
                        break;
                    case R.id.sortby_reset:
                        NotesStorage.getInstance(getActivity()).sortBy(NoteDBSchema.NoteTable.Cols.MARKER);
                        break;
                    default:
                        return false;
                }
                updateUI();
                return true;
            }
        });
        popup.show();
    }

    private void itemFilterPopup() {
        View vItem = getActivity().findViewById(R.id.menu_item_filter);
        PopupMenu popup = new PopupMenu(getActivity(), vItem);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.fragment_list_filter_popup_menu, popup.getMenu());

        if (filter.contains(Markers.DEFAULT.toString()))
            ((MenuItem) popup.getMenu().findItem(R.id.filter_default)).setChecked(true);
        if (filter.contains(Markers.IMPORTANT.toString()))
            ((MenuItem) popup.getMenu().findItem(R.id.filter_important)).setChecked(true);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.filter_default: {
                        if (menuItem.isChecked()) {
                            menuItem.setChecked(false);
                            filter.remove(Markers.DEFAULT.toString());
                        } else {
                            menuItem.setChecked(true);
                            filter.add(Markers.DEFAULT.toString());
                        }
                        break;
                    }
                    case R.id.filter_important: {
                        if (menuItem.isChecked()) {
                            menuItem.setChecked(false);
                            filter.remove(Markers.IMPORTANT.toString());
                        } else {
                            menuItem.setChecked(true);
                            filter.add(Markers.IMPORTANT.toString());
                        }
                        break;
                    }
                }
                updateUI();
                return true;
            }
        });
        popup.show();
    }

}
