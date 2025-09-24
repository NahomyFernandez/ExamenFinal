package com.example.examenfinal;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.examenfinal.WebServices.Asynchtask;
import com.example.examenfinal.WebServices.WebService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback, Asynchtask {

    private GoogleMap mMap;
    private LatLng userLocation;
    private String userCity;
    private String userCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que el nombre del layout aquí coincida con tu archivo XML
        setContentView(R.layout.activity_detailactivity);

        // --- ENCONTRAR TODAS LAS VISTAS ---
        ImageView imgProfile = findViewById(R.id.imgDetailProfile);
        TextView txtName = findViewById(R.id.txtDetailName);
        // NUEVO: Encontrar los TextViews para los datos personales
        TextView txtEmail = findViewById(R.id.txtDetailEmail);
        TextView txtAddress = findViewById(R.id.txtDetailAddress);
        TextView txtAge = findViewById(R.id.txtDetailAge);
        TextView txtPhone = findViewById(R.id.txtDetailPhone);
        TextView txtCell = findViewById(R.id.txtDetailCell);
        TextView txtNationality = findViewById(R.id.txtDetailNationality);
        TextView txtId = findViewById(R.id.txtDetailId);

        // --- RECIBIR TODOS LOS DATOS DEL INTENT ---
        String userDataString = getIntent().getStringExtra("USER_JSON");
        int age = getIntent().getIntExtra("USER_AGE", 0);
        String cell = getIntent().getStringExtra("USER_CELL");
        String phone = getIntent().getStringExtra("USER_PHONE");
        String nationality = getIntent().getStringExtra("USER_NATIONALITY");
        String idValue = getIntent().getStringExtra("USER_ID");

        if (userDataString == null) {
            Toast.makeText(this, "No se recibieron datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // --- PROCESAR EL JSON PARA LOS DATOS RESTANTES ---
            JSONObject user = new JSONObject(userDataString);

            JSONObject nameObj = user.getJSONObject("name");
            String fullName = nameObj.getString("first") + " " + nameObj.getString("last");
            String email = user.getString("email");
            JSONObject locationObj = user.getJSONObject("location");
            JSONObject streetObj = locationObj.getJSONObject("street");
            String fullAddress = streetObj.getInt("number") + " " + streetObj.getString("name") + ", " +
                    locationObj.getString("city") + ", " + locationObj.getString("state") + ", " + locationObj.getString("country");
            String pictureUrl = user.getJSONObject("picture").getString("large");

            // --- DATOS PARA EL MAPA Y LA BANDERA ---
            userCity = locationObj.getString("city");
            userCountry = locationObj.getString("country");
            JSONObject coordinates = locationObj.getJSONObject("coordinates");
            userLocation = new LatLng(coordinates.getDouble("latitude"), coordinates.getDouble("longitude"));

            // --- ASIGNAR TODOS LOS DATOS A LAS VISTAS ---
            txtName.setText(fullName);
            Glide.with(this).load(pictureUrl).circleCrop().into(imgProfile);

            // NUEVO: Asignar el texto a los nuevos TextViews
            txtEmail.setText("Correo electrónico: " + email);
            txtAddress.setText("Dirección: " + fullAddress);
            txtAge.setText("Edad: " + age + " años");
            txtPhone.setText("Teléfono: " + phone);
            txtCell.setText("Celular: " + cell);
            txtNationality.setText("Nacionalidad: " + nationality);
            txtId.setText("Identificación: " + idValue);

            // Iniciar mapa y bandera
            initializeMap();
            fetchCountryFlag();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (userLocation != null) {
            mMap.addMarker(new MarkerOptions().position(userLocation).title(userCity));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
        }
    }

    private void fetchCountryFlag() {
        if (userCountry == null || userCountry.isEmpty()) return;
        String url = "https://restcountries.com/v3.1/name/" + userCountry;
        WebService ws = new WebService(url, new HashMap<>(), this, this);
        ws.execute("GET");
    }

    @Override
    public void processFinish(String result) {
        if (isFinishing() || isDestroyed()) return;
        try {
            ImageView imgFlag = findViewById(R.id.imgFlag);
            JSONArray jsonResponse = new JSONArray(result);
            if (jsonResponse.length() > 0) {
                String flagUrl = jsonResponse.getJSONObject(0).getJSONObject("flags").getString("png");
                Glide.with(this).load(flagUrl).into(imgFlag);
            }
        } catch (JSONException e) {
            Log.e("DetailActivity", "Error al procesar bandera", e);
        }
    }
}