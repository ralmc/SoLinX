/**
 * SoLinX Web — utils.js
 */

// ─── Toast ────────────────────────────────────────────────────
let toastContainer = null;

export function toast(msg, type = 'info', duration = 3500) {
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.className = 'toast-container';
    document.body.appendChild(toastContainer);
  }

  const icons = {
    success: `<svg width="18" height="18" fill="none" stroke="#38a169" stroke-width="2" viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"/></svg>`,
    error:   `<svg width="18" height="18" fill="none" stroke="#e53e3e" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>`,
    info:    `<svg width="18" height="18" fill="none" stroke="#1497B9" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`,
  };

  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = `${icons[type] ?? icons.info}<span>${msg}</span>`;
  toastContainer.appendChild(el);
  setTimeout(() => el.remove(), duration);
}

// ─── Loader ───────────────────────────────────────────────────
export function showLoader(container) {
  container.innerHTML = `<div class="loader-wrap"><div class="spinner"></div></div>`;
}

// ─── Modal ────────────────────────────────────────────────────
export function confirmDialog(title, msg) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay active';
    overlay.innerHTML = `
      <div class="modal">
        <div class="modal-header"><h3>${title}</h3></div>
        <div class="modal-body"><p>${msg}</p></div>
        <div class="modal-footer">
          <button class="btn btn-outline" id="dlg-no">Cancelar</button>
          <button class="btn btn-primary" id="dlg-yes">Confirmar</button>
        </div>
      </div>`;
    document.body.appendChild(overlay);

    overlay.querySelector('#dlg-yes').onclick = () => { overlay.remove(); resolve(true);  };
    overlay.querySelector('#dlg-no').onclick  = () => { overlay.remove(); resolve(false); };
    overlay.onclick = e => { if (e.target === overlay) { overlay.remove(); resolve(false); } };
  });
}

// ─── Theme toggle ─────────────────────────────────────────────
export function initTheme() {
  const saved = localStorage.getItem('sl_theme') ?? 'light';
  document.documentElement.setAttribute('data-theme', saved);
  return saved;
}

export function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme');
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('sl_theme', next);
  return next;
}

// ─── Tabs ─────────────────────────────────────────────────────
export function initTabs(tabsEl, panelsEl) {
  const btns   = tabsEl.querySelectorAll('.tab-btn');
  const panels = panelsEl.querySelectorAll('.tab-panel');

  btns.forEach(btn => {
    btn.onclick = () => {
      if (btn.disabled) return;
      btns.forEach(b   => b.classList.remove('active'));
      panels.forEach(p => p.classList.remove('active'));
      btn.classList.add('active');
      const target = panelsEl.querySelector(`#${btn.dataset.tab}`);
      if (target) target.classList.add('active');
    };
  });
}

// ─── Avatar con foto o iniciales ─────────────────────────────
export function avatarHtml(nombre, foto, size = 'md') {
  const initials = nombre?.split(' ').slice(0,2).map(p => p[0]).join('').toUpperCase() ?? '?';
  if (foto) {
    return `<div class="avatar avatar-${size}"><img src="data:image/jpeg;base64,${foto}" alt="foto"></div>`;
  }
  return `<div class="avatar avatar-${size}" style="background:var(--brand)">${initials}</div>`;
}

// ─── Badge de estado solicitud ───────────────────────────────
export function badgeEstado(estado) {
  const map = {
    pendiente:  'badge-yellow',
    aceptada:   'badge-green',
    rechazada:  'badge-red',
    aprobada:   'badge-blue',
    enviada:    'badge-gray',
  };
  const cls = map[estado?.toLowerCase()] ?? 'badge-gray';
  return `<span class="badge ${cls}">${estado ?? 'N/A'}</span>`;
}

// ─── Formatear fecha ISO → dd/MM/yyyy ────────────────────────
export function fmtFecha(iso) {
  if (!iso) return 'N/A';
  const d = iso.substring(0, 10).split('-');
  return `${d[2]}/${d[1]}/${d[0]}`;
}

// ─── Sidebar hamburguesa (mobile) ────────────────────────────
export function initMobileSidebar(btnId, sidebarId) {
  const btn     = document.getElementById(btnId);
  const sidebar = document.getElementById(sidebarId);
  if (!btn || !sidebar) return;
  btn.onclick = () => sidebar.classList.toggle('open');
  document.addEventListener('click', e => {
    if (!sidebar.contains(e.target) && !btn.contains(e.target)) {
      sidebar.classList.remove('open');
    }
  });
}

// ─── Leer archivo como Base64 ────────────────────────────────
export function fileToBase64(file) {
  return new Promise((resolve, reject) => {
    const r = new FileReader();
    r.onload  = () => resolve(r.result.split(',')[1]);
    r.onerror = reject;
    r.readAsDataURL(file);
  });
}