<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Aprobar Aceptaciones</title>
    <link rel="stylesheet" href="assets/css/styles.css">
</head>
<body>
    <div class="page-wrapper">
        <div class="top-bar">
            <a href="supervisor_menu.jsp"><img src="assets/img/solinx_logo.png" alt="SoLinX" class="logo"></a>
            <span class="spacer"></span>
            <span class="page-title font-display" style="margin:0;font-size:1.4rem;">Aceptaciones Empresas</span>
        </div>
        <div class="bar-divider-thin"></div>

        <div class="page-content">
            <div class="container">
                <div id="listContainer"></div>
                <div id="emptyState" class="empty-state" style="display:none;">
                    <div class="empty-icon">✅</div>
                    <p>No hay aceptaciones pendientes</p>
                </div>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        requireSession('supervisor');

        // Demo — TODO: GET /api/supervisor/aceptaciones-empresa
        var aceptaciones = [
            { id:1, idAcept:'#001', alumno:'Carlos Ramírez López', empresa:'TechNova Solutions', fecha:'14/11/2025' },
            { id:2, idAcept:'#002', alumno:'Ana Torres García', empresa:'GreenEnergy MX', fecha:'20/11/2025' },
        ];

        function render() {
            var container = document.getElementById('listContainer');
            var empty = document.getElementById('emptyState');
            if (aceptaciones.length === 0) { empty.style.display = 'block'; container.innerHTML = ''; return; }
            empty.style.display = 'none';

            var html = '';
            aceptaciones.forEach(function(a) {
                html += '<div class="card" style="margin-bottom:14px;">' +
                    '<div class="card-body">' +
                    '<div style="font-weight:700;font-size:1.1rem;color:var(--color-on-surface);margin-bottom:8px;">Aceptación ' + a.idAcept + '</div>' +
                    '<div style="font-size:0.95rem;color:var(--color-on-surface);margin-bottom:4px;">Alumno: ' + a.alumno + '</div>' +
                    '<div style="font-size:0.95rem;color:var(--color-on-surface);margin-bottom:4px;">Empresa: ' + a.empresa + '</div>' +
                    '<div style="font-size:0.9rem;color:var(--color-text-secondary);margin-bottom:12px;">Fecha Aceptación: ' + a.fecha + '</div>' +
                    '<div style="display:flex;justify-content:flex-end;gap:12px;">' +
                    '<button class="btn-text btn-text-danger" style="font-size:1rem;font-weight:700;" onclick="rechazar(' + a.id + ')">Rechazar</button>' +
                    '<button class="btn-text btn-text-success" style="font-size:1rem;font-weight:700;" onclick="aprobar(' + a.id + ')">Aprobar</button>' +
                    '</div></div></div>';
            });
            container.innerHTML = html;
        }

        function aprobar(id) {
            aceptaciones = aceptaciones.filter(function(a) { return a.id !== id; });
            render();
            showToast('Aceptación aprobada.', 'success');
        }
        function rechazar(id) {
            aceptaciones = aceptaciones.filter(function(a) { return a.id !== id; });
            render();
            showToast('Aceptación rechazada.', 'info');
        }

        render();
    </script>
</body>
</html>
