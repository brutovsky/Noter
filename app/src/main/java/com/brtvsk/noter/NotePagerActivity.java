package com.brtvsk.noter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class NotePagerActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Note> notes;
    private static final String EXTRA_NOTE_ID =
            "NOTE_ID";
    private static final String EXTRA_FILTER =
            "FILTER";
    private String[] filter;

    public static Intent newIntent(Context packageContext, UUID noteID, String[] filter) {
        Intent intent = new Intent(packageContext, NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteID);
        intent.putExtra(EXTRA_FILTER,filter);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);

        UUID noteId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_NOTE_ID);

        filter = getIntent().getStringArrayExtra(EXTRA_FILTER);

        viewPager = findViewById(R.id.activity_note_pager_view_pager);

        notes = NotesStorage.getInstance(this).getNotes(filter);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Note note = notes.get(position);
                return NoteFragment.newInstance(note.getId(),filter);
            }

            @Override
            public int getCount() {
                return notes.size();
            }
        });
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(noteId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
