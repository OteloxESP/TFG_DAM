package com.example.oteloxtfgdam.activity;

import static androidx.core.content.ContextCompat.getDrawable;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.oteloxtfgdam.MyApp;
import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.ActivityNavBinding;
import com.example.oteloxtfgdam.db.UsuariosDB;
import com.google.android.material.navigation.NavigationView;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.mindrot.jbcrypt.BCrypt;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class InicioActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavBinding binding;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    public ImageView imageView;
    public TextView titleTextView;
    public TextView subtitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarNav.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View header = navigationView.getHeaderView(0);
        imageView = header.findViewById(R.id.imageView);
        titleTextView = header.findViewById(R.id.username_text_header);
        subtitleTextView = header.findViewById(R.id.email_text_header);

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
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
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
                                titleTextView.setText(u.getUsuario());
                                subtitleTextView.setText(u.getEmail());

                                if (u.getImagen()!=null){
                                    byte[] imagenBytes = u.getImagen();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                                    imageView.setImageBitmap(bitmap);

                                }else{
                                    imageView.setImageDrawable(getDrawable(R.drawable.logo));
                                }
                            }
                        }
                    }
                } else {
                    Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });

        MenuItem salirItem = navigationView.getMenu().findItem(R.id.nav_salir);
        salirItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_mercado, R.id.nav_perfil)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}