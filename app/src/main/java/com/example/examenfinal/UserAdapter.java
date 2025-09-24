package com.example.examenfinal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final JSONArray usersArray;
    private final Context context;

    public UserAdapter(JSONArray usersArray, Context context) {
        this.usersArray = usersArray;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lv_usuarios, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        try {
            JSONObject user = usersArray.getJSONObject(position);

            // --- Extraer datos básicos para la lista principal ---
            String fullName = user.getJSONObject("name").getString("first") + " " + user.getJSONObject("name").getString("last");
            String email = user.getString("email");
            String country = user.getJSONObject("location").getString("country");
            String imageUrl = user.getJSONObject("picture").getString("thumbnail");

            holder.txtName.setText(fullName);
            holder.txtEmail.setText(email);
            holder.txtCountry.setText(country);

            Glide.with(context)
                    .load(imageUrl)
                    .circleCrop()
                    .into(holder.imgProfile);

            // --- Configurar el OnClickListener para ir al detalle ---
            holder.itemView.setOnClickListener(v -> {
                try {
                    // Extraer TODOS los datos necesarios para la DetailActivity
                    JSONObject dob = user.getJSONObject("dob");
                    int age = dob.getInt("age");

                    String cell = user.getString("cell");
                    String phone = user.getString("phone");
                    String nationality = user.getString("nat");

                    JSONObject idObj = user.getJSONObject("id");
                    String idName = idObj.optString("name", "N/A"); // optString para evitar error si no existe
                    String idValue = idObj.optString("value", "N/A");
                    String fullId = idName + ": " + idValue;

                    // Crea el Intent y añade cada dato como un extra
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("USER_JSON", user.toString()); // Enviamos el JSON completo por si acaso
                    intent.putExtra("USER_AGE", age);
                    intent.putExtra("USER_CELL", cell);
                    intent.putExtra("USER_PHONE", phone);
                    intent.putExtra("USER_NATIONALITY", nationality);
                    intent.putExtra("USER_ID", fullId);

                    context.startActivity(intent);

                } catch (JSONException e) {
                    Log.e("UserAdapter", "Error al parsear datos para el Intent", e);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return usersArray != null ? usersArray.length() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView txtName, txtEmail, txtCountry;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgUserProfile);
            txtName = itemView.findViewById(R.id.txtUserName);
            txtEmail = itemView.findViewById(R.id.txtUserEmail);
            txtCountry = itemView.findViewById(R.id.txtUserCountry);
        }
    }
}