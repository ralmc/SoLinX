/**
 * SoLinX Web — api.js (v2)
 * Basado en ApiService.java de esta versión del proyecto
 * Cambia BASE_URL por la IP de tu servidor Spring Boot
 */

const BASE_URL = 'http://192.168.1.163:8080/SoLinX/api';

async function request(method, endpoint, body = null, isMultipart = false) {
  const headers = {};
  if (!isMultipart) headers['Content-Type'] = 'application/json';
  const options = { method, headers };
  if (body) options.body = isMultipart ? body : JSON.stringify(body);
  try {
    return await fetch(`${BASE_URL}/${endpoint}`, options);
  } catch (e) {
    throw new Error('Sin conexión: ' + e.message);
  }
}

const get  = (ep)       => request('GET',    ep);
const post = (ep, b)    => request('POST',   ep, b);
const put  = (ep, b)    => request('PUT',    ep, b);
const del  = (ep)       => request('DELETE', ep);
const postForm = (ep,f) => request('POST',   ep, f, true);

export const Api = {
  // ─── Auth ────────────────────────────────────────────────
  login: (correo, password) => post('login', { correo, password }),
  reenviarVerificacion: (correo) => post(`auth/verificar/reenviar?correo=${encodeURIComponent(correo)}`),

  // ─── Registro ────────────────────────────────────────────
  registrarAlumno:  (dto) => post('registro',         dto),
  registrarEmpresa: (dto) => post('registro/empresa', dto),

  // ─── Empresa ─────────────────────────────────────────────
  obtenerEmpresaPorId:  (id)      => get(`empresa/${id}`),
  actualizarEmpresa:    (id, dto) => put(`empresa/${id}`, dto),

  // ─── Proyectos ───────────────────────────────────────────
  getProyectos:              ()        => get('proyecto'),
  getProyectosParaAlumno:    (boleta)  => get(`proyecto/alumno/${boleta}`),
  getProyectosPorEmpresa:    (id)      => get(`proyecto/empresa/${id}`),
  crearProyecto:             (dto)     => post('proyecto', dto),
  actualizarProyecto:        (id, dto) => put(`proyecto/${id}`, dto),
  eliminarProyecto:          (id)      => del(`proyecto/${id}`),
  actualizarImagenProyecto:  (id, b64) => put(`proyecto/${id}/imagen`, { imagen: b64 }),

  // ─── Solicitudes Alumno ──────────────────────────────────
  getSolicitudesEstudiante: (boleta) => get(`solicitudes/estudiante/${boleta}`),
  enviarSolicitud:          (dto)    => post('solicitud', dto),
  aceptarSolicitud:         (dto)    => post('solicitud/accept', dto),

  // ─── Solicitudes Empresa ─────────────────────────────────
  getSolicitudesPorEmpresa:    (id)                => get(`solicitud/empresa/${id}`),
  actualizarEstadoSolicitud:   (id, nuevoEstado)   => put(`solicitud/${id}/estado?nuevoEstado=${encodeURIComponent(nuevoEstado)}`),

  // ─── Supervisor ──────────────────────────────────────────
  getSupervisorData:        (idUsuario)   => get(`supervisor/datos?idUsuario=${idUsuario}`),
  getSolicitudesEnviadas:   (idSupervisor)=> get(`supervisor/solicitudes-enviadas?idSupervisor=${idSupervisor}`),
  getSolicitudesAceptadas:  (idEmpresa)   => get(`supervisor/solicitudes-aceptadas?idEmpresa=${idEmpresa}`),
  actualizarSolicitudSupervisor: (idSolicitud, nuevoEstado) => {
    const form = new URLSearchParams();
    form.append('idSolicitud', idSolicitud);
    form.append('nuevoEstado', nuevoEstado);
    return postForm('supervisor/actualizar-solicitud', form);
  },

  // ─── Horario ─────────────────────────────────────────────
  getHorario:    (boleta) => get(`horario/${boleta}`),
  crearHorario:  (boleta, dto) => post(`horario/${boleta}`, dto),

  // ─── Documentos ──────────────────────────────────────────
  getDocumentos:    (boleta) => get(`documento/${boleta}`),
  subirDocumento:   (boleta, periodo, file) => {
    const form = new FormData();
    form.append('archivo', file);
    return postForm(`documento/${boleta}/${periodo}`, form);
  },

  // ─── Perfil ──────────────────────────────────────────────
  getPerfil:       (idUsuario) => get(`perfil/usuario/${idUsuario}`),
  actualizarFoto:  (idUsuario, base64) => put(`perfil/usuario/${idUsuario}/foto`, { foto: base64 }),

  // ─── Notificaciones ──────────────────────────────────────
  getNotificaciones:    (id)    => get(`notificacion/usuario/${id}`),
  contarNoLeidas:       (id)    => get(`notificacion/usuario/${id}/no-leidas`),
  marcarLeida:          (id)    => put(`notificacion/${id}/leida`),
  marcarTodasLeidas:    (id)    => put(`notificacion/usuario/${id}/leidas`),
};
