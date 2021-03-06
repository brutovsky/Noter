package com.brtvsk.noter;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import com.brtvsk.noter.database.NoteDBSchema;
import com.brtvsk.noter.utils.ItemTouchHelperAdapter;
import com.brtvsk.noter.utils.SimpleItemTouchHelperCallback;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListFragment extends Fragment {

    private RecyclerView recView;
    private NotesAdapter notesAdapter;
    private static final String DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss";

    private Set<Markers> filter = new HashSet<>();

    {
        filter.add(Markers.IMPORTANT);
        filter.add(Markers.DEFAULT);
    }

    public NotesListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes_list, container, false);
        recView = v.findViewById(R.id.notes_recycler_view);
        recView.setLayoutManager(new LinearLayoutManager(
                (getActivity())));
        updateUI();
        //
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(notesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recView);
        //
        return v;
    }

    private void updateUI() {
        NotesStorage noteStorage = NotesStorage.getInstance(getActivity());
        List<Note> notes = noteStorage.getNotes(filter);
        if (notesAdapter == null) {
            notesAdapter = new NotesAdapter(notes);
            recView.setAdapter(notesAdapter);
        } else {
            notesAdapter.setCrimes(notes);
            notesAdapter.notifyDataSetChanged();
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
        private Button popup;
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
            String dateString = DateFormat.format(DATE_FORMAT,
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
                    Intent intent = NotePagerActivity.newIntent(getActivity(),
                            note.getId());
                    startActivity(intent);
                }
            }
        }

        private void setMarkerImage(Markers marker) {
            switch (marker) {
                case DEFAULT: {
                    popup.setBackgroundResource(R.drawable.default_24dp);
                    break;
                }
                case IMPORTANT: {
                    popup.setBackgroundResource(R.drawable.star_24dp);
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
        int order = NotesStorage.getInstance(getActivity()).getOrder() + 1;
        Note note = new Note(order);
        NotesStorage.getInstance(getActivity()).addNote(note);
        Intent intent = NotePagerActivity
                .newIntent(getActivity(), note.getId());
        startActivity(intent);
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

}
