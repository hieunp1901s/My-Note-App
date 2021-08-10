package com.example.mynoteapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.room.Room;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFrag extends Fragment {
    ImageButton btnBack, btnDelete;
    EditText etHeading;
    EditText etContent;
    AppDatabase db;
    NoteDao noteDao;
    ExecutorService myExcutor;
    ActivityResultLauncher<Intent> getText = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getData() != null) {
                ArrayList<String> Result = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                etContent.setText(Result.get(0));
            }

        }
    });

    ImageButton btnVoice;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFrag newInstance(String param1, String param2) {
        AddFrag fragment = new AddFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = getView().findViewById(R.id.btnBack);
        btnDelete = getView().findViewById(R.id.btnDelete);
        etHeading = getView().findViewById(R.id.etHeading);
        etContent = getView().findViewById(R.id.etContent);
        btnVoice = getView().findViewById(R.id.btnVoice);

        myExcutor = Executors.newFixedThreadPool(1);
        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "note").build();

        noteDao = db.noteDao();

        final int Case = getArguments().getInt("case");
        if (Case == 2) {
            etHeading.setText(getArguments().getString("heading"));
            etContent.setText(getArguments().getString("content"));
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myExcutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (Case == 1) {
                            if (!etHeading.getText().toString().trim().isEmpty() || !etContent.getText().toString().trim().isEmpty())
                                noteDao.addNewNote(new Note(getArguments().getInt("index") + 1, etHeading.getText().toString().trim(), etContent.getText().toString().trim()));
                        }
                        else {
                            if (!etHeading.getText().toString().trim().isEmpty())
                                noteDao.updateNote(getArguments().getInt("id"), etHeading.getText().toString().trim(), etContent.getText().toString().trim());
                        }
                            getParentFragmentManager().popBackStack();
                    }
                });
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                myExcutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (Case == 1) {
                            if (!etHeading.getText().toString().trim().isEmpty() || !etContent.getText().toString().trim().isEmpty())
                                noteDao.addNewNote(new Note(getArguments().getInt("index") + 1, etHeading.getText().toString().trim(), etContent.getText().toString().trim()));
                        }
                        else {
                            if (!etHeading.getText().toString().trim().isEmpty())
                                noteDao.updateNote(getArguments().getInt("id"), etHeading.getText().toString().trim(), etContent.getText().toString().trim());
                        }
                        getParentFragmentManager().popBackStack();
                    }
                });
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myExcutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        noteDao.deleteNote(getArguments().getInt("id"));
                        getParentFragmentManager().popBackStack();
                    }
                });
            }
        });

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                getText.launch(intent);
            }
        });
    }



}