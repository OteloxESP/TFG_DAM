package com.example.oteloxtfgdam.activity.ui.mercado;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.FragmentMercadoBinding;
import com.example.oteloxtfgdam.db.DbManager;
import com.example.oteloxtfgdam.db.Item;
import com.example.oteloxtfgdam.db.ItemsDB;
import com.squareup.picasso.Picasso;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MercadoFragment extends Fragment {

    private FragmentMercadoBinding binding;
    MongoCollection<ItemsDB> mongoCollection;
    List<Item> items = new ArrayList<>();
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMercadoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.arsha.io/v2/eu/GetWorldMarketWaitList?lang=es";
        Request request = new Request.Builder()
                .url(url)
                .build();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando items...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                showMensaje("Error", "Error de red. Por favor, comprueba tu conexión a internet.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Object json = new JSONTokener(responseBody).nextValue();
                        if (json instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) json;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                procesarJSON(jsonObject);
                            }
                        } else if (json instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) json;
                            procesarJSON(jsonObject);
                        } else {
                            progressDialog.dismiss();
                            showMensaje("Error", "La respuesta del servidor no es válida.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        showMensaje("Error", "Error al procesar la respuesta del servidor.");
                    }

                    LinearLayout linearLayout = root.findViewById(R.id.linear_layout);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Item item : items) {
                                View itemView = inflater.inflate(R.layout.item_view, linearLayout, false);
                                ImageView itemIcon = itemView.findViewById(R.id.item_icon);
                                TextView itemName = itemView.findViewById(R.id.item_name);
                                TextView itemDate = itemView.findViewById(R.id.item_date);
                                TextView itemDate2 = itemView.findViewById(R.id.item_date2);
                                TextView itemAmount = itemView.findViewById(R.id.item_amount);
                                final int version = android.os.Build.VERSION.SDK_INT;
                                if (item.getGrado().equals("4")) {
                                    if (version < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                        itemIcon.setBackgroundDrawable(getDrawable(getContext(), R.drawable.image_border_red));
                                        itemName.setTextColor(getResources().getColor(R.color.red));
                                    } else {
                                        itemIcon.setBackground(getDrawable(getContext(), R.drawable.image_border_red));
                                        itemName.setTextColor(getResources().getColor(R.color.red));
                                    }
                                } else if (item.getGrado().equals("3")) {
                                    if (version < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                        itemIcon.setBackgroundDrawable(getDrawable(getContext(), R.drawable.image_border_yellow));
                                        itemName.setTextColor(getResources().getColor(R.color.yellow));
                                    } else {
                                        itemIcon.setBackground(getDrawable(getContext(), R.drawable.image_border_yellow));
                                        itemName.setTextColor(getResources().getColor(R.color.yellow));
                                    }
                                }
                                Picasso.with(getContext())
                                        .load("https://" + item.getImagen())
                                        .error(R.drawable.baseline_question_mark_24)
                                        .placeholder(R.drawable.outline_downloading_24)
                                        .into(itemIcon);
                                itemName.setText(item.getNombre());
                                long millis = item.getFecha() * 1000; // convertir segundos a milisegundos
                                SimpleDateFormat diaF = new SimpleDateFormat("dd-MM");
                                diaF.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
                                String fechaFormateada = diaF.format(millis);
                                itemDate.setText(fechaFormateada);
                                SimpleDateFormat horaF = new SimpleDateFormat("HH:mm");
                                horaF.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
                                String fechaFormateada2 = horaF.format(millis);
                                itemDate2.setText(fechaFormateada2);
                                DecimalFormat formatter = new DecimalFormat("#,###");
                                itemAmount.setText(formatter.format(item.getPrecio()));

                                linearLayout.addView(itemView);
                            }
                        }
                    });
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Log.e("Error llamada", response.body() + "");
                    if (response.code() == 515) {
                        showMensaje("Atención", "No hay items en la lista de espera en estos momentos");
                    } else {
                        showMensaje("Error", "Error al realizar la solicitud. Por favor, inténtalo de nuevo más tarde.");
                    }
                }
            }
        });
        return root;
    }

    public void procesarJSON(JSONObject jsonObject) {
        try {
            String id = String.valueOf(jsonObject.getInt("id"));
            String nombre = jsonObject.getString("name");
            long precio = jsonObject.getLong("price");
            long fecha = jsonObject.getLong("liveAt");
            AtomicReference<String> grado = new AtomicReference<>("");
            AtomicReference<String> imagenReference = new AtomicReference<>("");
            CountDownLatch latch = new CountDownLatch(1);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DbManager db = new DbManager();
                    mongoCollection = db.obtenerItemsCollection();

                    Document queryFilter = null;
                    queryFilter = new Document("itemId", id);

                    RealmResultTask<ItemsDB> findTask = mongoCollection.findOne(queryFilter);
                    findTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            ItemsDB result = task.get();
                            if (result != null) {
                                // Accede a los campos
                                imagenReference.set(result.getImagen());
                                grado.set(result.getGrado());
                            } else {
                                //Toast.makeText(getContext(), "No se encontró el documento", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("EXAMPLE", "Error al encontrar el documento: ", task.getError());
                            progressDialog.dismiss();
                            showMensaje("Error", "Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                        }
                        latch.countDown();
                    });
                }
            });
            try {
                latch.await(); // Espera hasta que findTask haya terminado
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            items.add(new Item(new ObjectId(), nombre, fecha, precio, grado.get(), imagenReference.get()));
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            showMensaje("Error", "Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
        }
    }

    private void showMensaje(String titulo, String mensaje) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(titulo)
                        .setMessage(mensaje)
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}