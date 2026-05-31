package com.example.solinx;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.MensajeChatbotAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.ChatbotRequest;
import com.example.solinx.DTO.ChatbotResponse;
import com.example.solinx.DTO.MensajeChatbot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerMensajes;
    private MensajeChatbotAdapter adapter;
    private final List<MensajeChatbot> historial  = new ArrayList<>();   // se envía a la API
    private final List<MensajeChatbot> mensajesUI = new ArrayList<>();   // lo que se muestra

    private EditText editMensaje;
    private ImageButton btnEnviar;
    private LinearLayout layoutTyping;
    private View layoutWelcome;

    private ApiService apiService;
    private boolean esperandoRespuesta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // ─── Toolbar ──────────────────────────────────
        Toolbar toolbar = findViewById(R.id.toolbarChatbot);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // ─── Vistas ───────────────────────────────────
        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        editMensaje      = findViewById(R.id.editMensaje);
        btnEnviar        = findViewById(R.id.btnEnviar);
        layoutTyping     = findViewById(R.id.layoutTyping);
        layoutWelcome    = findViewById(R.id.layoutWelcome);

        // ─── RecyclerView ─────────────────────────────
        adapter = new MensajeChatbotAdapter(mensajesUI);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        recyclerMensajes.setLayoutManager(lm);
        recyclerMensajes.setAdapter(adapter);

        // ─── API ──────────────────────────────────────
        apiService = ApiClient.getClient().create(ApiService.class);

        // ─── Input ────────────────────────────────────
        btnEnviar.setEnabled(false);
        editMensaje.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                btnEnviar.setEnabled(s.toString().trim().length() > 0 && !esperandoRespuesta);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        editMensaje.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                enviarMensajeDelInput();
                return true;
            }
            return false;
        });

        btnEnviar.setOnClickListener(v -> enviarMensajeDelInput());

        // ─── Quick prompts (chips) ────────────────────
        configurarChips();

        // ─── Mostrar welcome al inicio ────────────────
        layoutWelcome.setVisibility(View.VISIBLE);
        recyclerMensajes.setVisibility(View.GONE);
    }

    private void configurarChips() {
        findViewById(R.id.chip1).setOnClickListener(v ->
                enviarMensaje("¿Cuántas horas necesito de servicio social?"));
        findViewById(R.id.chip2).setOnClickListener(v ->
                enviarMensaje("¿Qué documentos debo entregar?"));
        findViewById(R.id.chip3).setOnClickListener(v ->
                enviarMensaje("¿Cuál es la diferencia entre servicio social y prácticas profesionales?"));
        findViewById(R.id.chip4).setOnClickListener(v ->
                enviarMensaje("¿Cómo me postulo a un proyecto?"));
    }

    private void enviarMensajeDelInput() {
        if (editMensaje.getText() == null) return;
        String texto = editMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;
        editMensaje.setText("");
        enviarMensaje(texto);
    }

    private void enviarMensaje(String texto) {
        if (esperandoRespuesta) return;

        // Esconder welcome la primera vez
        if (layoutWelcome.getVisibility() == View.VISIBLE) {
            layoutWelcome.setVisibility(View.GONE);
            recyclerMensajes.setVisibility(View.VISIBLE);
        }

        MensajeChatbot msgUsuario = new MensajeChatbot("user", texto);
        adapter.agregarMensaje(msgUsuario);
        recyclerMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);

        esperandoRespuesta = true;
        btnEnviar.setEnabled(false);
        mostrarTyping(true);

        ChatbotRequest request = new ChatbotRequest(new ArrayList<>(historial), texto);

        apiService.enviarMensajeChatbot(request).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatbotResponse> call,
                                   @NonNull Response<ChatbotResponse> response) {
                mostrarTyping(false);
                esperandoRespuesta = false;
                btnEnviar.setEnabled(editMensaje.getText() != null
                        && editMensaje.getText().toString().trim().length() > 0);

                if (response.isSuccessful() && response.body() != null) {
                    ChatbotResponse body = response.body();
                    if (body.isExito() && body.getRespuesta() != null) {
                        historial.add(msgUsuario);
                        MensajeChatbot msgBot = new MensajeChatbot("assistant", body.getRespuesta());
                        historial.add(msgBot);
                        adapter.agregarMensaje(msgBot);
                        recyclerMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);
                    } else {
                        String err = body.getError() != null ? body.getError()
                                : "No pude procesar tu mensaje. Intenta de nuevo.";
                        adapter.agregarMensajeError("⚠️ " + err);
                        recyclerMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                } else {
                    adapter.agregarMensajeError("⚠️ Error del servidor (" + response.code() + ")");
                    recyclerMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatbotResponse> call, @NonNull Throwable t) {
                mostrarTyping(false);
                esperandoRespuesta = false;
                btnEnviar.setEnabled(editMensaje.getText() != null
                        && editMensaje.getText().toString().trim().length() > 0);
                adapter.agregarMensajeError("⚠️ Sin conexión. Verifica tu red e intenta de nuevo.");
                recyclerMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void mostrarTyping(boolean mostrar) {
        layoutTyping.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (adapter.getItemCount() > 0) {
                    recyclerMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }, 50);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatbot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_limpiar) {
            limpiarConversacion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarConversacion() {
        if (mensajesUI.isEmpty()) {
            Toast.makeText(this, "No hay mensajes para borrar", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Limpiar conversación")
                .setMessage("¿Borrar todos los mensajes de esta conversación?")
                .setPositiveButton("Borrar", (d, w) -> {
                    historial.clear();
                    adapter.limpiar();
                    layoutWelcome.setVisibility(View.VISIBLE);
                    recyclerMensajes.setVisibility(View.GONE);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
