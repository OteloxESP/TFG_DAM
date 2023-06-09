package com.example.oteloxtfgdam.activity.ui.home;

import static androidx.core.content.ContextCompat.getDrawable;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.FragmentHomeBinding;
import com.example.oteloxtfgdam.db.DbManager;
import com.example.oteloxtfgdam.db.ZonasDB;

import java.util.ArrayList;
import java.util.List;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    MongoCollection<ZonasDB> mongoCollection;
    LinearLayout linearLayout;
    int nivel = 0;
    boolean tituloTalaExiste;
    boolean tituloCarneExiste;
    boolean tituloSangreExiste;
    boolean tituloHierbasExiste;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        List<ZonasDB> zonas = new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando zonas...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        tituloTalaExiste = false;
        tituloCarneExiste = false;
        tituloSangreExiste = false;
        tituloHierbasExiste = false;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DbManager db = new DbManager();
                mongoCollection = db.obtenerZonasCollection();
                RealmResultTask<MongoCursor<ZonasDB>> findTask = mongoCollection.find().iterator();
                findTask.getAsync(task -> {
                    try {
                        if (task.isSuccess()) {
                            MongoCursor<ZonasDB> results = task.get();
                            while (results.hasNext()) {
                                ZonasDB z = results.next();
                                zonas.add(new ZonasDB(z.getId(), z.getNombre(), z.getTipoZona(), z.getItem1(), z.getItem2(), z.getItem3(), z.getItem4(), z.getItem5()));
                            }
                        } else {
                            Log.e("EXAMPLE", "Error al encontrar el documento: ", task.getError());
                            progressDialog.dismiss();
                            showMensaje("Error", "Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (ZonasDB zona : zonas) {
                                    if (zona.getTipoZona().equals("Tala")) {
                                        linearLayout = root.findViewById(R.id.linear_layout_home_tala);
                                    } else if (zona.getTipoZona().equals("Hierbas")) {
                                        linearLayout = root.findViewById(R.id.linear_layout_home_hierbas);
                                    } else if (zona.getTipoZona().equals("Sangre")) {
                                        linearLayout = root.findViewById(R.id.linear_layout_home_sangre);
                                    } else {
                                        linearLayout = root.findViewById(R.id.linear_layout_home_carne);
                                    }
                                    View zonaView = inflater.inflate(R.layout.zona_view, linearLayout, false);
                                    ImageView item1Icon = zonaView.findViewById(R.id.item1_icon);
                                    ImageView item2Icon = zonaView.findViewById(R.id.item2_icon);
                                    ImageView item3Icon = zonaView.findViewById(R.id.item3_icon);
                                    ImageView item4Icon = zonaView.findViewById(R.id.item4_icon);

                                    if (zona.getTipoZona().equals("Tala")) {
                                        if (!tituloTalaExiste) {
                                            TextView seccionTitulo = zonaView.findViewById(R.id.titulo_seccion);
                                            seccionTitulo.setVisibility(View.VISIBLE);
                                            seccionTitulo.setText(zona.getTipoZona());
                                            tituloTalaExiste = true;
                                        }
                                        item1Icon.setImageDrawable(getDrawable(getContext(), R.drawable.tronco));
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                        nivel = sharedPreferences.getInt("tala", 0) / 50;

                                    } else if (zona.getTipoZona().equals("Hierbas")) {
                                        if (!tituloHierbasExiste) {
                                            TextView seccionTitulo = zonaView.findViewById(R.id.titulo_seccion);
                                            seccionTitulo.setVisibility(View.VISIBLE);
                                            seccionTitulo.setText(zona.getTipoZona());
                                            tituloHierbasExiste = true;
                                        }
                                        item1Icon.setImageDrawable(getDrawable(getContext(), R.drawable.hierbas));
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                        nivel = sharedPreferences.getInt("hierbas", 0) / 50;

                                    } else if (zona.getTipoZona().equals("Sangre")) {
                                        if (!tituloSangreExiste) {
                                            TextView seccionTitulo = zonaView.findViewById(R.id.titulo_seccion);
                                            seccionTitulo.setVisibility(View.VISIBLE);
                                            seccionTitulo.setText(zona.getTipoZona());
                                            tituloSangreExiste = true;
                                        }
                                        item1Icon.setImageDrawable(getDrawable(getContext(), R.drawable.sangres));
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                        nivel = sharedPreferences.getInt("sangre", 0) / 50;

                                    } else {
                                        if (!tituloCarneExiste) {
                                            TextView seccionTitulo = zonaView.findViewById(R.id.titulo_seccion);
                                            seccionTitulo.setVisibility(View.VISIBLE);
                                            seccionTitulo.setText(zona.getTipoZona());
                                            tituloCarneExiste = true;
                                        }
                                        item1Icon.setImageDrawable(getDrawable(getContext(), R.drawable.carne));
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                        nivel = sharedPreferences.getInt("carne", 0) / 50;

                                    }

                                    item2Icon.setImageDrawable(getDrawable(getContext(), R.drawable.polvo_espiritu));
                                    item3Icon.setImageDrawable(getDrawable(getContext(), R.drawable.polvo_hadas));
                                    item4Icon.setImageDrawable(getDrawable(getContext(), R.drawable.caphranita));

                                    TextView zonaNombre = zonaView.findViewById(R.id.zona_nombre);
                                    TextView item1Nombre = zonaView.findViewById(R.id.item1_nombre);
                                    TextView item2Nombre = zonaView.findViewById(R.id.item2_nombre);
                                    TextView item3Nombre = zonaView.findViewById(R.id.item3_nombre);
                                    TextView item4Nombre = zonaView.findViewById(R.id.item4_nombre);
                                    zonaNombre.setText(zona.getNombre());
                                    item1Nombre.setText(String.valueOf(zona.getItem1() * nivel));
                                    item2Nombre.setText(String.valueOf(zona.getItem2() * nivel));
                                    item3Nombre.setText(String.valueOf(zona.getItem3() * nivel));
                                    item4Nombre.setText(String.valueOf(zona.getItem4() * nivel));
                                    linearLayout.addView(zonaView);
                                }
                            }
                        });

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        showMensaje("Error", "Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                });
            }
        });
        return root;
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