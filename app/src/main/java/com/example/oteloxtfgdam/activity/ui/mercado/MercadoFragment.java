package com.example.oteloxtfgdam.activity.ui.mercado;

import static androidx.core.content.ContextCompat.getDrawable;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.oteloxtfgdam.MyApp;
import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.FragmentMercadoBinding;
import com.example.oteloxtfgdam.db.Item;
import com.example.oteloxtfgdam.db.ItemsDB;
import com.squareup.picasso.Picasso;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MercadoFragment extends Fragment {

    private FragmentMercadoBinding binding;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MercadoViewModel mercadoViewModel =
                new ViewModelProvider(this).get(MercadoViewModel.class);

        binding = FragmentMercadoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Item> items = new ArrayList<>();
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = String.valueOf(jsonObject.getInt("id"));
                            String nombre = jsonObject.getString("name");
                            long precio = jsonObject.getLong("price");
                            long fecha = jsonObject.getLong("liveAt");
                            AtomicReference<String> grado = new AtomicReference<>("aa");
                            AtomicReference<String> imagenReference = new AtomicReference<>("");
                            CountDownLatch latch = new CountDownLatch(1);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    App app = MyApp.getAppInstance();
                                    User user = app.currentUser();

                                    mongoClient = user.getMongoClient("mongodb-atlas");
                                    mongoDatabase = mongoClient.getDatabase("bdoHelp");
                                    mongoCollection = mongoDatabase.getCollection("Items");
                                    CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                                            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
                                    MongoCollection<ItemsDB> mongoCollection =
                                            mongoDatabase.getCollection(
                                                    "Items",
                                                    ItemsDB.class).withCodecRegistry(pojoCodecRegistry);

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
                                        }
                                        latch.countDown();
                                    });
                                }
                            });
                            try {
                                latch.await(); // Espera hasta que findTask haya terminado
                            } catch (InterruptedException e) {

                            }
                            //Toast.makeText(getContext(), "ff"+grado.get(), Toast.LENGTH_SHORT).show();
                            items.add(new Item(new ObjectId(), nombre, fecha, precio, grado.get(), imagenReference.get()));
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
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