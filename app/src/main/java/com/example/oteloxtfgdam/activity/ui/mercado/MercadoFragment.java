package com.example.oteloxtfgdam.activity.ui.mercado;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.FragmentMercadoBinding;
import com.example.oteloxtfgdam.db.ItemDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MercadoFragment extends Fragment {

private FragmentMercadoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        MercadoViewModel mercadoViewModel =
                new ViewModelProvider(this).get(MercadoViewModel.class);

    binding = FragmentMercadoBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    List<ItemDB> items = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.arsha.io/v2/eu/GetWorldMarketWaitList?lang=es";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String nombre = jsonObject.getString("name");
                            long precio = jsonObject.getLong("price");
                            long fecha = jsonObject.getLong("liveAt");
                            items.add(new ItemDB(id, nombre, fecha , precio));
                        }


                        // Aquí puedes utilizar la lista de personas como quieras
                        LinearLayout linearLayout = root.findViewById(R.id.linear_layout);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (ItemDB itemDB : items) {
                                    View itemView = inflater.inflate(R.layout.item_view, linearLayout, false);
                                    ImageView itemIcon = itemView.findViewById(R.id.item_icon);
                                    TextView itemName = itemView.findViewById(R.id.item_name);
                                    TextView itemDate = itemView.findViewById(R.id.item_date);
                                    TextView itemAmount = itemView.findViewById(R.id.item_amount);

                                    itemIcon.setImageResource(R.drawable.outline_home_24);
                                    itemName.setText(itemDB.getNombre());
                                    long millis = itemDB.getFecha() * 1000; // convertir segundos a milisegundos
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm"); // crear objeto SimpleDateFormat con el formato deseado
                                    sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
                                    String fechaFormateada = sdf.format(millis);
                                    itemDate.setText(fechaFormateada);
                                    DecimalFormat formatter = new DecimalFormat("#,###");
                                    itemAmount.setText(formatter.format(itemDB.getPrecio()));

                                    linearLayout.addView(itemView);
                                }
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    throw new IOException("Error al realizar la solicitud: " + response);
                }
            }
        });


        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}