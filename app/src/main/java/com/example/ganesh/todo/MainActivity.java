package com.example.ganesh.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ganesh.todo.Database.DatabaseHelper;
import com.example.ganesh.todo.Database.Model.Note;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Note> todolist=new ArrayList<>();
    public CoordinatorLayout coordinatorLayout;
    private TextView noNotesView;
    private DatabaseHelper db;
    private Myadaper myadaper;



    public static final String DatabaseName="Todo_database";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"Swipe left for delete Note & right for update",Toast.LENGTH_LONG).show();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        coordinatorLayout=findViewById(R.id.coordinator_layout);
        noNotesView=(TextView)findViewById(R.id.empty_notes_view) ;
        FloatingActionButton fab=findViewById(R.id.FAB);
        db= new DatabaseHelper(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialog(false, null, -1);
            }
        });
        todolist.addAll(db.getallNotes());
        myadaper=new Myadaper(this,todolist);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        ItemTouchHelper.SimpleCallback itemtouchhelper=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                deleteNote(viewHolder.getAdapterPosition());

            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if(viewHolder!=null){
                    final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback itemupdate=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                showNoteDialog(true, todolist.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if(viewHolder!=null){
                    final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }



            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView=((Myadaper.Holder) viewHolder).viewforeground;
                getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemtouchhelper).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(itemupdate).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(myadaper);
        toggleEmptyNotes();
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            todolist.add(0, n);

            // refreshing the list
            myadaper.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int position) {
        Note n = todolist.get(position);
        // updating note text
        n.setNotes(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        todolist.set(position, n);
        myadaper.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(todolist.get(position));

        // removing the note from the list
        todolist.remove(position);
        myadaper.notifyItemRemoved(position);

        toggleEmptyNotes();
    }





    private void showNoteDialog(final boolean shouldupdate,final Note note,final int position){
        LayoutInflater layoutInflater=LayoutInflater.from(getApplicationContext());
        View view=layoutInflater.inflate(R.layout.dialoglayout,null);

        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setView(view);

        final EditText inputnote=view.findViewById(R.id.note);
        TextView dialogtitle=view.findViewById(R.id.dialog_title);
        dialogtitle.setText(!shouldupdate ?"New Note":"Update Note");

        if(shouldupdate&&note!=null){
            inputnote.setText(note.getNotes());
        }
        alertdialogbuilder.setCancelable(false).
                setPositiveButton(shouldupdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog=alertdialogbuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(inputnote.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Note!!!",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    alertDialog.dismiss();

                }

                if (shouldupdate && note != null) {
                    // update note by it's id
                   updateNote(inputnote.getText().toString(),position);
                } else {
                    // create new note
                    createNote(inputnote.getText().toString());
                }
            }
        });

    }

    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.cursorcount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

}
