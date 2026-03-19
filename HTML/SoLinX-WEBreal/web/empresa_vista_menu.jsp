<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Menú Empresa</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .empresa-project { display:flex; gap:12px; padding:14px; align-items:flex-start; }
        .empresa-project .proj-img { width:80px; height:80px; border-radius:var(--radius-sm); object-fit:cover; background:var(--color-surface-variant); flex-shrink:0; }
        .empresa-project .proj-body { flex:1; display:flex; flex-direction:column; gap:2px; }
        .empresa-project .proj-header { display:flex; justify-content:space-between; align-items:flex-start; }
        .empresa-project .proj-meta { font-size:0.85rem; color:var(--color-text-secondary); }
        .empresa-project .proj-actions { display:flex; gap:12px; align-items:center; }
        .add-project-link { text-align:center; padding:16px; font-weight:700; font-size:1.05rem; color:var(--color-primary); cursor:pointer; }
        .add-project-link:hover { color:var(--color-secondary); }
    </style>
</head>
<body>
    <div class="page-wrapper">
        <!-- Top Bar -->
        <div class="top-bar">
            <img src="assets/img/solinx_logo.png" alt="SoLinX" class="logo">
            <div class="top-bar-nav">
                <span class="active">Menú</span>
                <span class="sep">|</span>
                <a href="empresa_notificaciones.jsp">Notificaciones</a>
            </div>
            <span class="spacer"></span>
            <img src="assets/img/icono_salida.png" alt="Cerrar Sesión" style="width:28px;height:28px;cursor:pointer;opacity:0.7;" onclick="clearSession()" title="Cerrar Sesión">
        </div>
        <div class="bar-divider"></div>

        <!-- Content -->
        <div class="page-content">
            <div class="container">
                <h2 class="page-title font-display">Proyectos Publicados</h2>
                <div id="projectList"></div>
                <div id="emptyState" class="empty-state" style="display:none;">
                    <div class="empty-icon">📁</div>
                    <p>No hay proyectos publicados</p>
                </div>
                <div class="add-project-link" onclick="window.location.href='gestion_proyecto.jsp'">
                    + Añadir Proyecto
                </div>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        requireSession('empresa');

        // Demo data — TODO: GET /api/empresas/{id}/proyectos
        var proyectos = [
            { id:1, nombre:'Sistema IoT Industrial', carrera:'Sistemas', empresa:'TechNova', fechaInicio:'2026-02-01', fechaFin:'2026-08-01', vacantes:5, ubicacion:'CDMX', objetivo:'Monitoreo industrial con IoT' },
            { id:2, nombre:'App de Inventarios', carrera:'Software', empresa:'TechNova', fechaInicio:'2026-03-15', fechaFin:'2026-09-15', vacantes:3, ubicacion:'Guadalajara', objetivo:'Control de inventarios' },
        ];

        function renderProjects() {
            var container = document.getElementById('projectList');
            var empty = document.getElementById('emptyState');
            if (proyectos.length === 0) { empty.style.display = 'block'; return; }

            var html = '';
            proyectos.forEach(function(p) {
                html += '<div class="card" style="margin-bottom:16px;">' +
                    '<div class="empresa-project">' +
                    '<img src="assets/img/img_default_proyecto.png" class="proj-img">' +
                    '<div class="proj-body">' +
                    '<div class="proj-header">' +
                    '<div><button class="btn-text btn-text-danger" style="font-size:0.85rem;" onclick="eliminarProyecto(' + p.id + ')">Eliminar</button>' +
                    '<span class="proj-meta"> # ' + String(p.id).padStart(3,'0') + '</span></div>' +
                    '<img src="assets/img/edit.png" style="width:24px;height:24px;cursor:pointer;opacity:0.6;" onclick="editarProyecto(' + p.id + ')" title="Editar">' +
                    '</div>' +
                    '<span class="proj-meta" style="font-weight:700;">Carrera: ' + p.carrera + '</span>' +
                    '<span class="proj-meta">Empresa: ' + p.empresa + '</span>' +
                    '<span class="proj-meta">Inicio: ' + p.fechaInicio + '</span>' +
                    '<span class="proj-meta">Fin: ' + p.fechaFin + '</span>' +
                    '<span class="proj-meta">Vacantes: ' + p.vacantes + '</span>' +
                    '<span class="proj-meta">Ubicación: ' + p.ubicacion + '</span>' +
                    '<span class="proj-meta">Objetivo: ' + p.objetivo + '</span>' +
                    '<span class="proj-meta" style="font-style:italic;">Proyecto: ' + p.nombre + '</span>' +
                    '</div></div></div>';
            });
            container.innerHTML = html;
        }

        function eliminarProyecto(id) {
            if (confirm('¿Eliminar este proyecto?')) {
                // TODO: DELETE /api/proyectos/{id}
                proyectos = proyectos.filter(function(p) { return p.id !== id; });
                renderProjects();
                showToast('Proyecto eliminado.', 'info');
            }
        }

        function editarProyecto(id) {
            window.location.href = 'gestion_proyecto.jsp?id=' + id;
        }

        renderProjects();
    </script>
</body>
</html>
