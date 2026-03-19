<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Notificaciones Empresa</title>
    <link rel="stylesheet" href="assets/css/styles.css">
</head>
<body>
    <div class="page-wrapper">
        <!-- Top Bar -->
        <div class="top-bar">
            <img src="assets/img/solinx_logo.png" alt="SoLinX" class="logo">
            <div class="top-bar-nav">
                <a href="empresa_vista_menu.jsp">Menú</a>
                <span class="sep">|</span>
                <span class="active" style="font-weight:700;">Notificaciones</span>
            </div>
            <span class="spacer"></span>
            <img src="assets/img/icono_salida.png" alt="Cerrar Sesión" style="width:28px;height:28px;cursor:pointer;opacity:0.7;" onclick="clearSession()" title="Cerrar Sesión">
        </div>
        <div class="bar-divider"></div>

        <!-- Content -->
        <div class="page-content">
            <div class="container">
                <h2 class="page-title font-display">Solicitudes Recibidas</h2>
                <div id="notiList"></div>
                <div id="emptyState" class="empty-state" style="display:none;">
                    <div class="empty-icon">🔔</div>
                    <p>No hay solicitudes nuevas.</p>
                </div>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        requireSession('empresa');

        // Demo — TODO: GET /api/empresas/{id}/solicitudes
        var solicitudes = [
            { id:1, numero:'#001', alumno:'Carlos Ramírez', boleta:'2023630001', carrera:'Sistemas', proyecto:'Sistema IoT', estado:'Enviada', foto:'assets/img/imagen_prederterminada.png' },
            { id:2, numero:'#002', alumno:'Ana Torres', boleta:'2023630045', carrera:'Software', proyecto:'App Inventarios', estado:'Enviada', foto:'assets/img/imagen_prederterminada.png' },
        ];

        function render() {
            var container = document.getElementById('notiList');
            var empty = document.getElementById('emptyState');
            if (solicitudes.length === 0) { empty.style.display = 'block'; container.innerHTML = ''; return; }

            var html = '';
            solicitudes.forEach(function(s) {
                html += '<div class="card" style="margin-bottom:14px;">' +
                    '<div class="solicitud-card">' +
                    '<img src="' + s.foto + '" alt="Foto" class="student-photo">' +
                    '<div class="solicitud-info">' +
                    '<span class="solicitud-title">Solicitud ' + s.numero + '</span>' +
                    '<span class="solicitud-project">' + s.proyecto + '</span>' +
                    '<span class="solicitud-meta">Boleta: ' + s.boleta + '</span>' +
                    '<span class="solicitud-meta">Carrera: ' + s.carrera + '</span>' +
                    '<span class="estado-badge estado-pendiente">' + s.estado + '</span>' +
                    '<div class="solicitud-actions">' +
                    '<button class="btn-text btn-text-success" onclick="admitir(' + s.id + ')">Admitir</button>' +
                    '<span style="color:var(--color-text-secondary);">|</span>' +
                    '<button class="btn-text btn-text-danger" onclick="rechazar(' + s.id + ')">Rechazar</button>' +
                    '</div></div></div></div>';
            });
            container.innerHTML = html;
        }

        function admitir(id) {
            // TODO: PUT /api/solicitudes/{id}/admitir
            solicitudes = solicitudes.filter(function(s) { return s.id !== id; });
            render();
            showToast('Solicitud admitida.', 'success');
        }

        function rechazar(id) {
            // TODO: PUT /api/solicitudes/{id}/rechazar
            solicitudes = solicitudes.filter(function(s) { return s.id !== id; });
            render();
            showToast('Solicitud rechazada.', 'info');
        }

        render();
    </script>
</body>
</html>
