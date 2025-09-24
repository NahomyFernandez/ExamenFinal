package com.example.examenfinal;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ya no es necesario el código de EdgeToEdge para este ejemplo.
        setContentView(R.layout.activity_main);

        // 1. Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Inicializar la cola de peticiones de Volley
        requestQueue = Volley.newRequestQueue(this);

        // 3. Llamar al método para obtener los datos de los usuarios
        fetchUsers();
    }

    private void fetchUsers() {
        // La URL de la API especificada en el examen
        String url = "https://randomuser.me/api/?results=20";

        // Crear la petición JSON usando Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Este bloque se ejecuta si la petición es exitosa
                    try {
                        // Extraer el array de "results" del objeto JSON principal
                        JSONArray usersArray = response.getJSONArray("results");

                        // Crear e inicializar el adaptador con los datos y el contexto
                        userAdapter = new UserAdapter(usersArray, this);

                        // Asignar el adaptador al RecyclerView para mostrar los datos
                        recyclerView.setAdapter(userAdapter);

                    } catch (JSONException e) {
                        Log.e("MainActivity", "Error de parseo JSON", e);
                        Toast.makeText(MainActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Este bloque se ejecuta si hay un error en la petición
                    Log.e("MainActivity", "Error de Volley", error);
                    Toast.makeText(MainActivity.this, "Error al obtener los usuarios", Toast.LENGTH_SHORT).show();
                }
        );

        // Añadir la petición a la cola para que se ejecute
        requestQueue.add(jsonObjectRequest);
    }
}


