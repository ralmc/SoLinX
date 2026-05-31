/**
 * SoLinX Web — chatbot.js
 * Widget de chatbot SoliBot. Se monta automáticamente al importarse.
 *
 * Uso:
 *   import { mountChatbot } from '../../js/chatbot.js';
 *   mountChatbot({ nombre: 'Juan' });  // (opcional) inicial del usuario para avatar
 */

import { Api } from './api.js';

// ─── Estado del módulo ─────────────────────────────────────
let historial = [];          // [{rol, contenido}] que se envía a la API
let esperando = false;
let isOpen = false;
let userInitial = '?';
let badgeCount = 0;

// Persistencia ligera durante la sesión
const STORAGE_KEY = 'solinx_chatbot_history';

// Preguntas sugeridas iniciales
const QUICK_PROMPTS = [
  { icon: '⏱️',  text: '¿Cuántas horas necesito de servicio social?' },
  { icon: '📄',  text: '¿Qué documentos debo entregar?' },
  { icon: '🆚',  text: 'Servicio social vs prácticas profesionales' },
  { icon: '🚀',  text: '¿Cómo me postulo a un proyecto?' }
];

// ─── Markdown muy básico (negritas, italics, code, listas, saltos de línea) ──
function renderMarkdown(text) {
  // Escape HTML primero
  let html = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');

  // Code inline `code`
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>');
  // Bold **texto**
  html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  // Italics *texto*
  html = html.replace(/(?<![*])\*([^*\n]+)\*(?![*])/g, '<em>$1</em>');

  // Listas
  const lines = html.split('\n');
  const out = [];
  let inUl = false;
  for (const line of lines) {
    const m = line.match(/^[\s]*[-•*]\s+(.*)$/);
    if (m) {
      if (!inUl) { out.push('<ul>'); inUl = true; }
      out.push(`<li>${m[1]}</li>`);
    } else {
      if (inUl) { out.push('</ul>'); inUl = false; }
      out.push(line);
    }
  }
  if (inUl) out.push('</ul>');

  return out.join('\n');
}

// ─── Render del HTML del widget ────────────────────────────
function renderWidget() {
  if (document.getElementById('solibot-fab')) return;  // ya montado

  const container = document.createElement('div');
  container.innerHTML = `
    <!-- Botón flotante -->
    <button class="chatbot-fab" id="solibot-fab" aria-label="Abrir asistente SoliBot">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
      </svg>
      <span class="chatbot-fab-badge" id="solibot-badge">1</span>
    </button>

    <!-- Ventana del chat -->
    <div class="chatbot-window" id="solibot-window" role="dialog" aria-labelledby="solibot-title">
      <div class="chatbot-header">
        <div class="chatbot-avatar">🤖</div>
        <div class="chatbot-header-info">
          <div class="chatbot-header-title" id="solibot-title">SoliBot</div>
          <div class="chatbot-header-status">
            <span class="chatbot-status-dot"></span>
            <span>Asistente de SoLinX · En línea</span>
          </div>
        </div>
        <div class="chatbot-header-actions">
          <button class="chatbot-icon-btn" id="solibot-clear" title="Limpiar conversación" aria-label="Limpiar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6l-2 14a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L5 6"/>
            </svg>
          </button>
          <button class="chatbot-icon-btn" id="solibot-close" title="Cerrar" aria-label="Cerrar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
      </div>

      <div class="chatbot-messages" id="solibot-messages"></div>

      <div class="chatbot-input-area">
        <div class="chatbot-input-wrapper">
          <textarea
            id="solibot-input"
            class="chatbot-input"
            placeholder="Escribe tu duda..."
            rows="1"
            maxlength="500"
            aria-label="Mensaje"></textarea>
        </div>
        <button class="chatbot-send-btn" id="solibot-send" disabled aria-label="Enviar">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <line x1="22" y1="2" x2="11" y2="13"/>
            <polygon points="22 2 15 22 11 13 2 9 22 2"/>
          </svg>
        </button>
      </div>

      <div class="chatbot-footer">
        Powered by <strong>Claude AI</strong> · Tus dudas se procesan con IA
      </div>
    </div>
  `;

  document.body.appendChild(container);
}

// ─── Helpers de UI ─────────────────────────────────────────
function el(id) { return document.getElementById(id); }

function scrollAlFinal() {
  const cont = el('solibot-messages');
  requestAnimationFrame(() => { cont.scrollTop = cont.scrollHeight; });
}

function renderWelcome() {
  const cont = el('solibot-messages');
  cont.innerHTML = `
    <div class="chatbot-welcome">
      <div class="chatbot-welcome-icon">👋</div>
      <div class="chatbot-welcome-title">¡Hola! Soy SoliBot</div>
      <div class="chatbot-welcome-subtitle">
        Tu asistente para resolver dudas sobre el servicio social en SoLinX.
        Pregúntame lo que necesites.
      </div>
      <div class="chatbot-chips">
        ${QUICK_PROMPTS.map((q, i) => `
          <button class="chatbot-chip" data-chip-index="${i}">
            <span class="chatbot-chip-icon">${q.icon}</span>
            <span>${q.text}</span>
          </button>
        `).join('')}
      </div>
    </div>
  `;

  // Listeners de chips
  cont.querySelectorAll('.chatbot-chip').forEach(btn => {
    btn.addEventListener('click', () => {
      const idx = parseInt(btn.dataset.chipIndex, 10);
      const prompt = QUICK_PROMPTS[idx].text;
      enviarMensaje(prompt);
    });
  });
}

function agregarMensajeUI(tipo, contenido) {
  // Si está la welcome screen, quítala
  const welcome = document.querySelector('.chatbot-welcome');
  if (welcome) welcome.remove();

  const cont = el('solibot-messages');
  const wrap = document.createElement('div');
  wrap.className = `chatbot-msg ${tipo}`;

  const avatar = tipo === 'bot' ? '🤖' : (userInitial || '?');
  const html = tipo === 'bot' ? renderMarkdown(contenido) : escapeHtml(contenido);

  wrap.innerHTML = `
    <div class="chatbot-msg-avatar">${avatar}</div>
    <div class="chatbot-msg-bubble">${html}</div>
  `;
  cont.appendChild(wrap);
  scrollAlFinal();
}

function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

function mostrarTyping(mostrar) {
  const existing = document.getElementById('solibot-typing');
  if (mostrar) {
    if (existing) return;
    const cont = el('solibot-messages');
    const div = document.createElement('div');
    div.id = 'solibot-typing';
    div.className = 'chatbot-typing';
    div.innerHTML = `
      <div class="chatbot-msg-avatar" style="background: linear-gradient(135deg, var(--brand), var(--brand-dark)); color:#fff; font-weight:700;">🤖</div>
      <div class="chatbot-typing-bubble">
        <div class="chatbot-typing-dot"></div>
        <div class="chatbot-typing-dot"></div>
        <div class="chatbot-typing-dot"></div>
      </div>
    `;
    cont.appendChild(div);
    scrollAlFinal();
  } else {
    if (existing) existing.remove();
  }
}

function updateBadge(count) {
  badgeCount = count;
  const b = el('solibot-badge');
  if (!b) return;
  if (count > 0 && !isOpen) {
    b.textContent = count > 9 ? '9+' : count;
    b.classList.add('show');
  } else {
    b.classList.remove('show');
  }
}

// ─── Persistencia ──────────────────────────────────────────
function guardarHistorial() {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(historial));
  } catch { /* sessionStorage podría no estar disponible */ }
}

function cargarHistorial() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return false;
    const arr = JSON.parse(raw);
    if (!Array.isArray(arr) || arr.length === 0) return false;
    historial = arr;
    return true;
  } catch {
    return false;
  }
}

function repintarHistorial() {
  const cont = el('solibot-messages');
  cont.innerHTML = '';
  for (const m of historial) {
    agregarMensajeUI(m.rol === 'user' ? 'user' : 'bot', m.contenido);
  }
}

// ─── Enviar mensaje ────────────────────────────────────────
async function enviarMensaje(texto) {
  if (esperando) return;
  texto = (texto || '').trim();
  if (!texto) return;

  agregarMensajeUI('user', texto);

  const input = el('solibot-input');
  input.value = '';
  input.style.height = 'auto';
  el('solibot-send').disabled = true;

  mostrarTyping(true);
  esperando = true;

  try {
    const res = await Api.chatbotMensaje(historial, texto);

    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();

    mostrarTyping(false);

    if (data.exito && data.respuesta) {
      historial.push({ rol: 'user', contenido: texto });
      historial.push({ rol: 'assistant', contenido: data.respuesta });
      guardarHistorial();

      agregarMensajeUI('bot', data.respuesta);

      if (!isOpen) updateBadge(badgeCount + 1);
    } else {
      agregarMensajeUI('error', data.error || 'No se pudo procesar tu mensaje. Intenta de nuevo.');
    }
  } catch (e) {
    mostrarTyping(false);
    agregarMensajeUI('error', '⚠️ No pude conectarme con el asistente. Verifica tu conexión e intenta de nuevo.');
    console.error('Chatbot error:', e);
  } finally {
    esperando = false;
    actualizarBotonEnviar();
    input.focus();
  }
}

// ─── Listeners ─────────────────────────────────────────────
function actualizarBotonEnviar() {
  const input = el('solibot-input');
  const btn = el('solibot-send');
  if (!input || !btn) return;
  btn.disabled = !input.value.trim() || esperando;
}

function autoResize(textarea) {
  textarea.style.height = 'auto';
  textarea.style.height = Math.min(textarea.scrollHeight, 100) + 'px';
}

function abrir() {
  isOpen = true;
  el('solibot-window').classList.add('open');
  el('solibot-fab').classList.add('open');
  updateBadge(0);
  setTimeout(() => el('solibot-input')?.focus(), 300);
}

function cerrar() {
  isOpen = false;
  el('solibot-window').classList.remove('open');
  el('solibot-fab').classList.remove('open');
}

function limpiarConversacion() {
  if (historial.length === 0) return;
  if (!confirm('¿Borrar toda la conversación?')) return;
  historial = [];
  guardarHistorial();
  renderWelcome();
}

function bindEvents() {
  el('solibot-fab').addEventListener('click', () => isOpen ? cerrar() : abrir());
  el('solibot-close').addEventListener('click', cerrar);
  el('solibot-clear').addEventListener('click', limpiarConversacion);

  const input = el('solibot-input');
  input.addEventListener('input', () => {
    autoResize(input);
    actualizarBotonEnviar();
  });
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      enviarMensaje(input.value);
    }
  });

  el('solibot-send').addEventListener('click', () => enviarMensaje(input.value));

  // Cerrar con Escape
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && isOpen) cerrar();
  });
}

// ─── API pública ───────────────────────────────────────────
export function mountChatbot(opts = {}) {
  // Evitar montaje doble
  if (document.getElementById('solibot-fab')) return;

  // Inicial del usuario para el avatar
  if (opts.nombre) {
    userInitial = opts.nombre.trim().charAt(0).toUpperCase() || '?';
  }

  renderWidget();
  bindEvents();

  // Restaurar historial si existe
  const restaurado = cargarHistorial();
  if (restaurado) {
    repintarHistorial();
  } else {
    renderWelcome();
  }
}
