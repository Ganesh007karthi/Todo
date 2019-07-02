package com.example.ganesh.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ganesh.todo.Database.Model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Myadaper extends RecyclerView.Adapter<Myadaper.Holder> {
        private Context context;
        public MainActivity main;
        private List<Note> list;
    public class Holder extends RecyclerView.ViewHolder{
        public TextView note;
        public TextView dot;
        public TextView timestamp;
        public RelativeLayout viewbackground,viewforeground;
        public Holder(View view){
            super(view);
            note=view.findViewById(R.id.note);
            dot=view.findViewById(R.id.dot);
            timestamp=view.findViewById(R.id.timestamp);
            viewbackground=view.findViewById(R.id.view_background);
            viewforeground=view.findViewById(R.id.view_foreground);

        }
    }

    public Myadaper(Context context, List<Note> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list,viewGroup,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myadaper.Holder holder, int i) {
        Note note=list.get(i);
        holder.note.setText(note.getNotes());
        //displaying dot from html code
        holder.dot.setText(Html.fromHtml("&#8226;"));
        //displaying the formatted date
        holder.timestamp.setText(formatDate(note.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public void removeItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Note note, int position){
        list.add(position,note);
        notifyItemInserted(position);
    }

}
