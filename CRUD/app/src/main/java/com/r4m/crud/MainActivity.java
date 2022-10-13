package com.r4m.crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r4m.crud.databasefirebase.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText etNombre, etApellido, etCorreo, etContrasena;
    ListView listaPersonas;

    private List<Persona> listaP = new ArrayList<>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    Persona personaSeleccionada;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etLAddLNombre);
        etApellido = findViewById(R.id.etLAddLApellido);
        etCorreo = findViewById(R.id.etLAddLCorreoE);
        etContrasena = findViewById(R.id.etLAddLContraseña);
        listaPersonas = findViewById(R.id.lwAddLLista);

        //Inicia firebse
        iniciarFirebase();
        //Listar datos en firebase
        listarDatos();
        listaPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSeleccionada = (Persona) adapterView.getItemAtPosition(i);
                etNombre.setText(personaSeleccionada.getNombre());
                etApellido.setText(personaSeleccionada.getApellido());
                etCorreo.setText(personaSeleccionada.getCorreo_e());
                etContrasena.setText(personaSeleccionada.getContrasena());
            }
        });
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        /*firebaseDatabase.setPersistenceEnabled(true);*/ //using just without fragments
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaP.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Persona persona = data.getValue(Persona.class);
                    listaP.add(persona);
                    //arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listaP);
                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listaP);
                    listaPersonas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menu) {
        //Extraemos la informacion de las cajas
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String correo_e = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        switch (menu.getItemId()) {
            case R.id.menuopcAgregar:
                if (nombre.equals("") || apellido.equals("") || correo_e.equals("") || contrasena.equals("")) {
                    validarDatos();
                } else {
                    Persona persona = new Persona();
                    persona.setUid(UUID.randomUUID().toString());
                    persona.setNombre(nombre);
                    persona.setApellido(apellido);
                    persona.setCorreo_e(correo_e);
                    persona.setContrasena(contrasena);
                    databaseReference.child("Persona").child(persona.getUid()).setValue(persona);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    listarDatos();
                }
                break;
            case R.id.menuopcEditar:
                Persona personaEditada = new Persona();
                personaEditada.setUid(personaSeleccionada.getUid());
                personaEditada.setNombre(etNombre.getText().toString().trim());
                personaEditada.setApellido(etApellido.getText().toString().trim());
                personaEditada.setCorreo_e(etCorreo.getText().toString().trim());
                personaEditada.setContrasena(etContrasena.getText().toString().trim());
                databaseReference.child("Persona").child(personaEditada.getUid()).setValue(personaEditada);
                Toast.makeText(this, "Editado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                break;
            case R.id.menuopcEliminar:
                Persona personaEliiminada = new Persona();
                personaEliiminada.setUid(personaSeleccionada.getUid());
                databaseReference.child("Persona").child(personaEliiminada.getUid()).removeValue();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_LONG).show();
                limpiarCampos();
                break;
            case R.id.menuopcListar:
                listarDatos();
                Toast.makeText(this, "Listado", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void validarDatos() {
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String correo_e = etCorreo.getText().toString();
        String contrasena = etContrasena.getText().toString();

        if (nombre.equals("")) {
            etNombre.setError("Requerido");
        } else if (apellido.equals("")) {
            etApellido.setError("Requerido");
        } else if (correo_e.equals("")) {
            etCorreo.setError("Requerido");
        } else if (contrasena.equals("")) {
            etContrasena.setError("Requerido");
        }
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etApellido.setText("");
        etCorreo.setText("");
        etContrasena.setText("");
    }
}