package com.brtvsk.noter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class NoteModificationActivity extends AppCompatActivity{

    private static final String EXTRA_NOTE_ID =
            "NOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_modification);

        UUID noteID = (UUID) getIntent()
                .getSerializableExtra(EXTRA_NOTE_ID);

        Log.println(Log.ERROR,"AHAHAH","AHAHAH");

        getSupportFragmentManager().beginTransaction().replace(R.id.notemodification_container, NoteModificationFragment.newInstance(noteID)).commit();
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, NoteModificationActivity.class);
        return intent;
    }

    public static Intent newIntent(Context packageContext, UUID noteID) {
        Intent intent = new Intent(packageContext, NoteModificationActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteID);
        return intent;
    }
}
