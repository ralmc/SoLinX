/**
 * SoLinX Web — auth.js
 */

// ─── Sesión ───────────────────────────────────────────────────
export const Session = {
  save(loginResponse) {
    localStorage.setItem('sl_idUsuario',    loginResponse.idUsuario);
    localStorage.setItem('sl_rol',          loginResponse.rol);
    localStorage.setItem('sl_nombre',       loginResponse.nombre ?? '');
    localStorage.setItem('sl_correo',       loginResponse.correo ?? '');

    if (loginResponse.rol?.toLowerCase() === 'estudiante') {
      localStorage.setItem('sl_boleta',   loginResponse.boleta ?? '');
      localStorage.setItem('sl_carrera',  loginResponse.carrera ?? '');
      localStorage.setItem('sl_escuela',  loginResponse.escuela ?? '');
      localStorage.setItem('sl_telefono', loginResponse.telefono ?? '');
    }

    if (loginResponse.rol?.toLowerCase() === 'empresa') {
      localStorage.setItem('sl_idEmpresa', loginResponse.idEmpresa ?? '');
    }

    if (loginResponse.rol?.toLowerCase() === 'supervisor') {
      localStorage.setItem('sl_idSupervisor', loginResponse.idSupervisor ?? '');
      localStorage.setItem('sl_idEmpresa',    loginResponse.idEmpresa ?? '');
      localStorage.setItem('sl_area',         loginResponse.area ?? '');
    }
  },

  get(key)    { return localStorage.getItem(`sl_${key}`); },
  getInt(key) { return parseInt(localStorage.getItem(`sl_${key}`) ?? '0'); },

  get idUsuario()    { return this.getInt('idUsuario'); },
  get nombre()       { return this.get('nombre') ?? ''; },
  get correo()       { return this.get('correo') ?? ''; },
  get rol()          { return this.get('rol') ?? ''; },
  get boleta()       { return this.getInt('boleta'); },
  get carrera()      { return this.get('carrera') ?? ''; },
  get escuela()      { return this.get('escuela') ?? ''; },
  get telefono()     { return this.get('telefono') ?? ''; },
  get idEmpresa()    { return this.getInt('idEmpresa'); },
  get idSupervisor() { return this.getInt('idSupervisor'); },
  get area()         { return this.get('area') ?? ''; },

  isLoggedIn() { return !!localStorage.getItem('sl_rol'); },

  clear() {
    const keys = Object.keys(localStorage).filter(k => k.startsWith('sl_'));
    keys.forEach(k => localStorage.removeItem(k));
  },

  initials() {
    const n = this.nombre.trim();
    if (!n) return '?';
    const parts = n.split(' ');
    return (parts[0][0] + (parts[1]?.[0] ?? '')).toUpperCase();
  }
};

// ─── Calcular path raíz del proyecto (funciona en Tomcat y directo) ──
function getRootPath() {
  // Si estamos en pages/rol/pagina.html, necesitamos subir 2 niveles
  // Si estamos en index.html, ya estamos en la raíz
  const path = window.location.pathname;
  const depth = (path.match(/\//g) || []).length;
  // En Tomcat: /SoLinX-Web/pages/alumno/dashboard.html → depth=4 → subir 2
  // En file://: /C:/...SoLinX/web/pages/alumno/dashboard.html → similar
  if (path.includes('/pages/')) {
    return '../../index.html';
  }
  return 'index.html';
}

// ─── Guard: redirigir si no está logueado ────────────────────
export function requireAuth(expectedRol = null) {
  if (!Session.isLoggedIn()) {
    window.location.href = getRootPath();
    return false;
  }
  if (expectedRol && Session.rol.toLowerCase() !== expectedRol.toLowerCase()) {
    window.location.href = getRootPath();
    return false;
  }
  return true;
}

// ─── Logout ───────────────────────────────────────────────────
export function logout() {
  Session.clear();
  window.location.href = getRootPath();
}