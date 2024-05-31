package com.example.handlerthreadapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    HandlerThread handlerThread;
    Handler handler;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> pokemonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(pokemonList);
        recyclerView.setAdapter(recyclerViewAdapter);


        // Create a new handlerThread
        handlerThread = new HandlerThread("MyHandler");
        handlerThread.start();

        //Attached a handler
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);

                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                pokemonList.add(message);
                recyclerViewAdapter.notifyDataSetChanged();
                Log.d("message","" + message);

            }
        };

        final Button button = findViewById(R.id.pokemonButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewMessage();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //This method simulate that a sender send a new pokemon name to the app
    //then we send a message to the HandlerThread
    public void sendNewMessage() {

        Message message = handler.obtainMessage();
        ArrayList<String> listNewPokemons = new ArrayList<>();
        listNewPokemons.add("Pikachu");
        listNewPokemons.add("Bulbasaur");
        listNewPokemons.add("Charmander");

        int index = new Random().nextInt(listNewPokemons.size());
        String pokemonName = listNewPokemons.get(index);

        Bundle b = new Bundle();
        b.putString("message", pokemonName);

        message.setData(b);
        handler.sendMessage(message);

    }

    @Override
    protected void onStop() {
        super.onStop();
        handlerThread.quit();
    }
}