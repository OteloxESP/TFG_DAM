package com.example.oteloxtfgdam.activity.ui.perfil;

import static androidx.core.content.ContextCompat.getDrawable;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.oteloxtfgdam.MyApp;
import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.FragmentPerfilBinding;
import com.example.oteloxtfgdam.db.UsuariosDB;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.mongo.result.UpdateResult;

public class PerfilFragment extends Fragment {

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private FragmentPerfilBinding binding;
    private UsuariosDB usuario;
    private Uri imagenActual;
    private Boolean nuevaFoto = false;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando perfil...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EditText usernameEditText = binding.usernameEdittext;
        EditText talaEditText = binding.talaEditText;
        EditText sangreEditText = binding.sangreEditText;
        EditText hozEditText = binding.hozEditText;
        EditText carneEditText = binding.carneEditText;
        Button guardarButton = binding.guardarButton;

        App app = MyApp.getAppInstance();
        User user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("bdoHelp");
        mongoCollection = mongoDatabase.getCollection("Usuarios");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<UsuariosDB> mongoCollection =
                mongoDatabase.getCollection(
                        "Usuarios",
                        UsuariosDB.class).withCodecRegistry(pojoCodecRegistry);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("user", "");
        String contraseña = sharedPreferences.getString("password", "");
        RealmResultTask<MongoCursor<UsuariosDB>> findTask = mongoCollection.find().iterator();
        findTask.getAsync(task -> {
            try {
                Boolean v = false;
                Boolean v2 = false;
                if (task.isSuccess()) {
                    MongoCursor<UsuariosDB> results = task.get();
                    Log.v("EXAMPLE", "successfully found documents:");
                    while (results.hasNext()) {
                        UsuariosDB u = results.next();
                        if (username.equals(u.getUsuario())) {
                            if (BCrypt.checkpw(contraseña, u.getContraseña())) {
                                usuario = u;
                                usernameEditText.setText(usuario.getUsuario());
                                String tala = String.valueOf(usuario.getMaestriaTala());
                                String sangre = String.valueOf(usuario.getMaestriaSangre());
                                String hierbas = String.valueOf(usuario.getMaestriaHierbas());
                                String carne = String.valueOf(usuario.getMaestriaCarne());
                                talaEditText.setText(tala);
                                sangreEditText.setText(sangre);
                                hozEditText.setText(hierbas);
                                carneEditText.setText(carne);

                                if (usuario.getImagen()!=null){
                                    byte[] imagenBytes = usuario.getImagen();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                                    // Asigna el Bitmap a la ImageView
                                    binding.profileImage.setImageBitmap(bitmap);
                                }else{
                                    binding.profileImage.setImageDrawable(getDrawable(getContext(), R.drawable.logo));
                                }
                            }
                        }
                    }
                    progressDialog.dismiss();
                } else {
                    Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (usuario != null) {
                    try {
                        if (nuevaFoto) {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(imagenActual);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            // Leer los datos de la imagen y escribirlos en el ByteArrayOutputStream
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            // Obtener el array de bytes resultante de la conversión
                            byte[] imagenBytes = outputStream.toByteArray();

                            // Asignar los bytes de la imagen al campo blob
                            usuario.setImagen(imagenBytes);
                            inputStream.close();
                            outputStream.close();
                        }

                        usuario.setUsuario(usernameEditText.getText().toString());
                        usuario.setMaestriaTala(Integer.parseInt(talaEditText.getText().toString()));
                        usuario.setMaestriaSangre(Integer.parseInt(sangreEditText.getText().toString()));
                        usuario.setMaestriaHierbas(Integer.parseInt(hozEditText.getText().toString()));
                        usuario.setMaestriaCarne(Integer.parseInt(carneEditText.getText().toString()));

                        Document queryFilter = new Document("_id", usuario.getId());
                        Document updateDocument = new Document("$set", new Document()
                                .append("usuario", usuario.getUsuario())
                                .append("maestriaTala", usuario.getMaestriaTala())
                                .append("maestriaSangre", usuario.getMaestriaSangre())
                                .append("maestriaHierbas", usuario.getMaestriaHierbas())
                                .append("maestriaCarne", usuario.getMaestriaCarne()));
                        if (nuevaFoto) {
                            updateDocument = new Document("$set", new Document()
                                    .append("imagen", usuario.getImagen())
                                    .append("usuario", usuario.getUsuario())
                                    .append("maestriaTala", usuario.getMaestriaTala())
                                    .append("maestriaSangre", usuario.getMaestriaSangre())
                                    .append("maestriaHierbas", usuario.getMaestriaHierbas())
                                    .append("maestriaCarne", usuario.getMaestriaCarne()));
                        }

                        mongoCollection.updateOne(queryFilter, updateDocument).getAsync(task2 -> {
                            if (task2.isSuccess()) {
                                UpdateResult result = task2.get();
                                long count = result.getModifiedCount();
                                if (count == 1) {
                                    Log.v("EXAMPLE", "Documento actualizado correctamente");
                                } else if (count == 0) {
                                    Log.v("EXAMPLE", "No se encontró el documento para actualizar");
                                } else {
                                    Log.v("EXAMPLE", "Se encontraron múltiples documentos para actualizar. Actualizados: " + count);
                                }
                            } else {
                                Log.e("EXAMPLE", "Error al actualizar el documento: ", task2.getError());
                            }
                        });
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }


            }
        });

        return root;
    }

    public void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imagenActual = data.getData();
            nuevaFoto = true;
            ImageView imagenPreview = binding.profileImage;
            imagenPreview.setImageURI(imagenActual);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
