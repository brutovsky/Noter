package com.brtvsk.noter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class NoteModificationActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE_ID =
            "NOTE_ID";
    private static final String EXTRA_FILTER =
            "FILTER";

    private static final String DIALOG_BACK = "back";
    private String filter[];
    private NoteModificationFragment fragment;
    private UUID noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_modification);

        noteID = (UUID) getIntent()
                .getSerializableExtra(EXTRA_NOTE_ID);
        filter = getIntent().getStringArrayExtra(EXTRA_FILTER);
        fragment = NoteModificationFragment.newInstance(noteID, filter);
        getSupportFragmentManager().beginTransaction().replace(R.id.notemodification_container, fragment).commit();
    }

    public static Intent newIntent(Context packageContext, String filter[]) {
        Intent intent = new Intent(packageContext, NoteModificationActivity.class);
        intent.putExtra(EXTRA_FILTER, filter);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    public static Intent newIntent(Context packageContext, UUID noteID, String[] filter) {
        Intent intent = new Intent(packageContext, NoteModificationActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteID);
        intent.putExtra(EXTRA_FILTER, filter);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        onDialogCreate(DIALOG_BACK).show();
    }

    private Dialog onDialogCreate(String tag) {
        switch (tag) {
            case DIALOG_BACK: {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteModificationActivity.this);
                builder.setMessage(R.string.areYouSure).setTitle(R.string.warning).setCancelable(true).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (fragment.isModification()) {
                            Intent intent = NotePagerActivity.newIntent(NoteModificationActivity.this, noteID, filter);
                            startActivity(intent);
                        } else
                            NoteModificationActivity.super.onBackPressed();
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
