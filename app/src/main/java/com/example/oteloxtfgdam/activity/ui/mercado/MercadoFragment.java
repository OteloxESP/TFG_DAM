package com.example.oteloxtfgdam.activity.ui.mercado;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    User user;
    App app;
    String AppId = "bdoinfo-wwrmh";
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
                            final String[] imagen = {"a"};
                            AtomicReference<String> grado = new AtomicReference<>("");
                            AtomicReference<String> imagenReference = new AtomicReference<>("bb");
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
                                                // Accede a los campos del documento recuperado
                                                imagenReference.set(result.getImagen());

                                                // Realiza las acciones deseadas con los campos recuperado
                                                //Toast.makeText(getContext(), "Imagen: " + imagenReference.get(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "No se encontró el documento", Toast.LENGTH_SHORT).show();
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
                                // Manejo de la interrupción
                            }

                            items.add(new Item(new ObjectId(), nombre, fecha , precio, grado.get(), imagenReference.get()));
                        }


                        // Aquí puedes utilizar la lista de personas como quieras
                        LinearLayout linearLayout = root.findViewById(R.id.linear_layout);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (Item item : items) {
                                    View itemView = inflater.inflate(R.layout.item_view, linearLayout, false);
                                    ImageView itemIcon = itemView.findViewById(R.id.item_icon);
                                    TextView itemName = itemView.findViewById(R.id.item_name);
                                    TextView itemDate = itemView.findViewById(R.id.item_date);
                                    TextView itemAmount = itemView.findViewById(R.id.item_amount);
                                    //Toast.makeText(getContext(), "imagen2"+item.getImagen(), Toast.LENGTH_SHORT).show();
                                    // Cargar imagen utilizando Picasso
                                    //Picasso.get().load("https://"+item.getImagen()).into(itemIcon);
                                    Picasso.with( getContext() )
                                            .load( "https://"+item.getImagen() )
                                            .error( R.drawable.outline_question_mark_24 )
                                            .placeholder( R.drawable.outline_downloading_24 )
                                            .into( itemIcon );
                                    itemName.setText(item.getNombre());
                                    long millis = item.getFecha() * 1000; // convertir segundos a milisegundos
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm"); // crear objeto SimpleDateFormat con el formato deseado
                                    sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
                                    String fechaFormateada = sdf.format(millis);
                                    itemDate.setText(fechaFormateada);
                                    DecimalFormat formatter = new DecimalFormat("#,###");
                                    itemAmount.setText(formatter.format(item.getPrecio()));

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