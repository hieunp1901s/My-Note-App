package com.example.mynoteapp;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM note")
    List<Note> getAll();

    @Query("SELECT * FROM note WHERE ID = (:id)")
    Note getNote(int id);

    @Insert
    void addNewNote(Note ... note);

    @Query("UPDATE note SET heading = (:heading), content = (:content) WHERE id = (:id)")
    void updateNote(int id, String heading, String content);

    @Query("DELETE FROM note Where id = :id")
    void deleteNote(int id);

    @Query("SELECT * from note WHERE heading LIKE :s or content LIKE :s")
    List<Note> findNotes(String s);
}
