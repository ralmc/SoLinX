/* ============================================
   SoLinX WEB — Global JavaScript
   ============================================ */

// ---- Theme Toggle ----
function initTheme() {
    const saved = localStorage.getItem('solinx-theme') || 'light';
    document.documentElement.setAttribute('data-theme', saved);
    updateThemeDots(saved);
}

function setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('solinx-theme', theme);
    updateThemeDots(theme);
}

function updateThemeDots(theme) {
    document.querySelectorAll('.theme-dot').forEach(dot => {
        dot.classList.toggle('active', dot.dataset.theme === theme);
    });
}

// ---- Session Management (localStorage placeholder) ----
function getSession() {
    const data = localStorage.getItem('solinx-session');
    return data ? JSON.parse(data) : null;
}

function setSession(userData) {
    localStorage.setItem('solinx-session', JSON.stringify(userData));
}

function clearSession() {
    localStorage.removeItem('solinx-session');
    window.location.href = 'index.html';
}

function requireSession(role) {
    const session = getSession();
    if (!session) {
        window.location.href = 'iniciar_sesion.html';
        return null;
    }
    if (role && session.role !== role) {
        window.location.href = 'index.html';
        return null;
    }
    return session;
}

// ---- Expandable Sections ----
function toggleExpandable(triggerId, contentId) {
    const content = document.getElementById(contentId);
    const trigger = document.getElementById(triggerId);
    if (content.classList.contains('open')) {
        content.classList.remove('open');
        if (trigger) trigger.textContent = '▼';
    } else {
        content.classList.add('open');
        if (trigger) trigger.textContent = '▲';
    }
}

// ---- Toast / Alert ----
function showToast(message, type) {
    type = type || 'info';
    const colors = {
        success: 'var(--color-aprobar)',
        error: 'var(--color-rechazar)',
        info: 'var(--color-primary)',
        warning: 'var(--color-estado-pendiente)'
    };

    const toast = document.createElement('div');
    toast.style.cssText =
        'position:fixed;top:24px;right:24px;padding:14px 24px;border-radius:8px;color:#fff;font-family:Poppins,sans-serif;font-size:0.95rem;font-weight:500;z-index:9999;opacity:0;transform:translateY(-10px);transition:all 0.3s ease;max-width:400px;box-shadow:0 4px 20px rgba(0,0,0,0.2);background:' + (colors[type] || colors.info);
    toast.textContent = message;
    document.body.appendChild(toast);

    requestAnimationFrame(function () {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    setTimeout(function () {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(-10px)';
        setTimeout(function () { toast.remove(); }, 300);
    }, 3000);
}

// ---- Password Toggle ----
function togglePassword(inputId, btn) {
    const input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
        btn.textContent = '🙈';
    } else {
        input.type = 'password';
        btn.textContent = '👁';
    }
}

// ---- Init on load ----
document.addEventListener('DOMContentLoaded', function () {
    initTheme();
});
