package com.example.oteloxtfgdam.activity.ui.home;

import static androidx.core.content.ContextCompat.getDrawable;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.oteloxtfgdam.databinding.FragmentHomeBinding;
import com.example.oteloxtfgdam.db.UsuariosDB;
import com.example.oteloxtfgdam.db.ZonasDB;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    MongoCollection<Document> mongoCollection2;
    UsuariosDB userActual;
    int nivel = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        List<ZonasDB> zonas = new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando zonas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                App app = MyApp.getAppInstance();
                User user = app.currentUser();

                mongoClient = user.getMongoClient("mongodb-atlas");
                mongoDatabase = mongoClient.getDatabase("bdoHelp");
                mongoCollection = mongoDatabase.getCollection("Zonas");
                CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
                MongoCollection<ZonasDB> mongoCollection =
                        mongoDatabase.getCollection(
                                "Zonas",
                                ZonasDB.class).withCodecRegistry(pojoCodecRegistry);

                RealmResultTask<MongoCursor<ZonasDB>> findTask = mongoCollection.find().iterator();
                findTask.getAsync(task -> {
                    if (task.isSuccess()) {
                        MongoCursor<ZonasDB> results = task.get();
                        while (results.hasNext()) {
                            ZonasDB z = results.next();
                            zonas.add(new ZonasDB(z.getId(),z.getNombre(),z.getTipoZona(),z.getItem1(),z.getItem2(),z.getItem3(),z.getItem4(),z.getItem5()));
                        }
                    } else {
                        Log.e("EXAMPLE", "Error al encontrar el documento: ", task.getError());
                    }
                    LinearLayout linearLayout = root.findViewById(R.id.linear_layout_home);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (ZonasDB zona: zonas) {
                                View zonaView = inflater.inflate(R.layout.zona_view, linearLayout, false);
                                ImageView zonaIcon = zonaView.findViewById(R.id.zona_icon);
                                ImageView item1Icon = zonaView.findViewById(R.id.item1_icon);
                                ImageView item2Icon = zonaView.findViewById(R.id.item2_icon);
                                ImageView item3Icon = zonaView.findViewById(R.id.item3_icon);
                                ImageView item4Icon = zonaView.findViewById(R.id.item4_icon);
                                if (zona.getTipoZona().equals("Tala")){
                                    zonaIcon.setImageDrawable(getDrawable(getContext(), R.drawable.tronco));
                                    item1Icon.setImageDrawable(getDrawable(getContext(),R.drawable.tronco));
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                    nivel = sharedPreferences.getInt("tala", 0)/50;
                                }

                                item2Icon.setImageDrawable(getDrawable(getContext(),R.drawable.polvo_espiritu));
                                item3Icon.setImageDrawable(getDrawable(getContext(),R.drawable.polvo_hadas));
                                item4Icon.setImageDrawable(getDrawable(getContext(),R.drawable.caphranita));

                                TextView zonaNombre = zonaView.findViewById(R.id.zona_nombre);
                                TextView item1Nombre = zonaView.findViewById(R.id.item1_nombre);
                                TextView item2Nombre = zonaView.findViewById(R.id.item2_nombre);
                                TextView item3Nombre = zonaView.findViewById(R.id.item3_nombre);
                                TextView item4Nombre = zonaView.findViewById(R.id.item4_nombre);
                                zonaNombre.setText(zona.getNombre());
                                item1Nombre.setText(zona.getItem1()*nivel+"");
                                item2Nombre.setText(zona.getItem2()*nivel+"");
                                item3Nombre.setText(zona.getItem3()*nivel+"");
                                item4Nombre.setText(zona.getItem4()*nivel+"");
                                linearLayout.addView(zonaView);
                            }
                        }
                    });

                });
                progressDialog.dismiss();
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