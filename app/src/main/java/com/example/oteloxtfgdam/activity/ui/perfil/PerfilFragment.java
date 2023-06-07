package com.example.oteloxtfgdam.activity.ui.perfil;

import static androidx.core.content.ContextCompat.getDrawable;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.activity.InicioActivity;
import com.example.oteloxtfgdam.databinding.FragmentPerfilBinding;
import com.example.oteloxtfgdam.db.DbManager;
import com.example.oteloxtfgdam.db.UsuariosDB;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.mongo.result.UpdateResult;

public class PerfilFragment extends Fragment {

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private FragmentPerfilBinding binding;
    private UsuariosDB usuario;
    private Uri imagenActual;
    private Boolean nuevaFoto = false;
    ProgressDialog progressDialog;
    MongoCollection<UsuariosDB> mongoCollection;
    EditText talaEditText, sangreEditText, hozEditText, carneEditText;
    TextInputLayout talaEditTextInputLayout, sangreEditTextInputLayout, hozEditTextInputLayout, carneEditTextInputLayout;
    int valorTala, valorCarne, valorHierba, valorSangre;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando perfil...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        talaEditText = binding.talaEditText;
        sangreEditText = binding.sangreEditText;
        hozEditText = binding.hozEditText;
        carneEditText = binding.carneEditText;
        talaEditTextInputLayout = binding.talaInputLayout;
        sangreEditTextInputLayout = binding.sangreInputLayout;
        hozEditTextInputLayout = binding.hozInputLayout;
        carneEditTextInputLayout = binding.carneInputLayout;
        Button guardarButton = binding.guardarButton;

        DbManager db = new DbManager();
        mongoCollection = db.obtenerUsuariosCollection();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("user", "");
        String contraseña = sharedPreferences.getString("password", "");
        RealmResultTask<MongoCursor<UsuariosDB>> findTask = mongoCollection.find().iterator();
        findTask.getAsync(task -> {
            try {
                if (task.isSuccess()) {
                    MongoCursor<UsuariosDB> results = task.get();
                    Log.v("EXAMPLE", "successfully found documents:");
                    while (results.hasNext()) {
                        UsuariosDB u = results.next();
                        if (username.equals(u.getUsuario())) {
                            if (BCrypt.checkpw(contraseña, u.getContraseña())) {
                                usuario = u;
                                String tala = String.valueOf(usuario.getMaestriaTala());
                                String sangre = String.valueOf(usuario.getMaestriaSangre());
                                String hierbas = String.valueOf(usuario.getMaestriaHierbas());
                                String carne = String.valueOf(usuario.getMaestriaCarne());
                                talaEditText.setText(tala);
                                talaEditTextInputLayout.setError(null);
                                sangreEditText.setText(sangre);
                                sangreEditTextInputLayout.setError(null);
                                hozEditText.setText(hierbas);
                                hozEditTextInputLayout.setError(null);
                                carneEditText.setText(carne);
                                carneEditTextInputLayout.setError(null);

                                if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                                    byte[] imagenBytes = usuario.getImagen();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                                    binding.profileImage.setImageBitmap(bitmap);
                                } else {
                                    binding.profileImage.setImageDrawable(getDrawable(getContext(), R.drawable.logo));
                                }
                            }
                        }
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    showErrorMensaje("Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                    Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                showErrorMensaje("Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                e.printStackTrace();
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
                if (validarCampos()) {
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
                            InicioActivity activity = (InicioActivity) getActivity();
                            if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                                byte[] imagenBytes = usuario.getImagen();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                                activity.imageView.setImageBitmap(bitmap);
                            } else {
                                activity.imageView.setImageDrawable(getDrawable(getContext(), R.drawable.logo));
                            }

                            mongoCollection.updateOne(queryFilter, updateDocument).getAsync(task2 -> {
                                if (task2.isSuccess()) {
                                    UpdateResult result = task2.get();
                                    long count = result.getModifiedCount();
                                    if (count == 1) {
                                        Log.v("EXAMPLE", "Documento actualizado correctamente");
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("tala", usuario.getMaestriaTala());
                                        editor.putInt("hierbas", usuario.getMaestriaHierbas());
                                        editor.putInt("carne", usuario.getMaestriaCarne());
                                        editor.putInt("sangre", usuario.getMaestriaSangre());
                                        editor.apply();
                                        Toast.makeText(activity, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                                    } else if (count == 0) {
                                        Log.v("EXAMPLE", "No se encontró el documento para actualizar");
                                    } else {
                                        Log.v("EXAMPLE", "Se encontraron múltiples documentos para actualizar. Actualizados: " + count);
                                    }
                                } else {
                                    Log.e("EXAMPLE", "Error al actualizar el documento: ", task2.getError());
                                    showErrorMensaje("Error al actualizar el perfil. Por favor, inténtalo de nuevo más tarde.");
                                }
                            });
                        } catch (FileNotFoundException e) {
                            showErrorMensaje("Error al acceder a la imagen seleccionada. Por favor, selecciona otra imagen.");
                            e.printStackTrace();
                        } catch (IOException e) {
                            showErrorMensaje("Error al procesar la imagen seleccionada. Por favor, selecciona otra imagen.");
                            e.printStackTrace();
                        } catch (Exception e) {
                            showErrorMensaje("Error al actualizar el perfil. Por favor, inténtalo de nuevo más tarde.");
                            e.printStackTrace();
                        }
                    } else {
                        showErrorMensaje("No se pudo encontrar el usuario. Por favor, vuelve a iniciar sesión.");
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

    public Boolean validarCampos() {
        Boolean v = true;
        if (talaEditText.getText().toString().isEmpty()) {
            talaEditTextInputLayout.setError(getString(R.string.campo_vacio));
            v = false;

        } else {
            valorTala = Integer.parseInt(talaEditText.getText().toString());
            if (valorTala < 0 || valorTala > 2000) {
                talaEditTextInputLayout.setError((getString(R.string.maestria_fuera_rango)));
                v = false;
            } else {
                talaEditTextInputLayout.setError(null);
            }
        }

        if (carneEditText.getText().toString().isEmpty()) {
            carneEditTextInputLayout.setError(getString(R.string.campo_vacio));
            v = false;

        } else {
            valorCarne = Integer.parseInt(carneEditText.getText().toString());
            if (valorCarne < 0 || valorCarne > 2000) {
                carneEditTextInputLayout.setError((getString(R.string.maestria_fuera_rango)));
                v = false;
            } else {
                carneEditTextInputLayout.setError(null);
            }
        }

        if (hozEditText.getText().toString().isEmpty()) {
            hozEditTextInputLayout.setError(getString(R.string.campo_vacio));
            v = false;

        } else {
            valorHierba = Integer.parseInt(hozEditText.getText().toString());
            if (valorHierba < 0 || valorHierba > 2000) {
                hozEditTextInputLayout.setError((getString(R.string.maestria_fuera_rango)));
                v = false;
            } else {
                hozEditTextInputLayout.setError(null);
            }
        }

        if (sangreEditText.getText().toString().isEmpty()) {
            sangreEditTextInputLayout.setError(getString(R.string.campo_vacio));
            v = false;

        } else {
            valorSangre = Integer.parseInt(sangreEditText.getText().toString());
            if (valorSangre < 0 || valorSangre > 2000) {
                sangreEditTextInputLayout.setError((getString(R.string.maestria_fuera_rango)));
                v = false;
            } else {
                sangreEditTextInputLayout.setError(null);
            }
        }

        return v;
    }

    private void showErrorMensaje(String mensaje) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Error")
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
