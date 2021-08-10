package com.example.mynoteapp;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    public ArrayList<Note> list = new ArrayList<>();
    Context context;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    public void updateData(List<Note> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvNote = itemView.findViewById(R.id.tvNote);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("case", 2);
                    int index = list.indexOf((Note) itemView.getTag());
                    bundle.putInt("id", list.get(index).getId());
                    bundle.putString("heading", list.get(index).getHeading());
                    bundle.putString("content", list.get(index).getContent());

                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.slide_out
                            )
                            .replace(R.id.container, AddFrag.class, bundle)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        SpannableString spannableString;

        holder.itemView.setTag(list.get(position));

        if (list.get(position).getHeading().isEmpty())
            holder.tvNote.setText(list.get(position).getContent());
        else if (list.get(position).getContent().isEmpty()) {
            spannableString = new SpannableString(list.get(position).getHeading());
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                    0,list.get(position).getHeading().length(), 0);
            spannableString.setSpan(new RelativeSizeSpan(1.25f),
                    0,list.get(position).getHeading().length(), 0);
            holder.tvNote.setText(spannableString);
        }
        else {
            spannableString = new SpannableString(list.get(position).getHeading() + "\n\n" + list.get(position).getContent());
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    0,list.get(position).getHeading().length(), 0);
            spannableString.setSpan(new RelativeSizeSpan(1.25f),
                    0,list.get(position).getHeading().length(), 0);
            holder.tvNote.setText(spannableString);
        }
    }

    @Override
    public int getItemCount() {
        if (list == null)
        return 0;
        return list.size();
    }
}
