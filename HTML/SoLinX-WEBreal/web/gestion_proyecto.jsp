<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Gestión de Proyecto</title>
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
                <a href="empresa_notificaciones.jsp">Notificaciones</a>
            </div>
            <span class="spacer"></span>
            <img src="assets/img/icono_salida.png" alt="Cerrar Sesión" style="width:28px;height:28px;cursor:pointer;opacity:0.7;" onclick="clearSession()">
        </div>
        <div class="bar-divider"></div>

        <!-- Content -->
        <div class="page-content">
            <div class="container" style="max-width:600px;">
                <h2 class="page-title font-display">Gestión de Proyecto</h2>

                <div class="form-group">
                    <label>Carrera Enfocada</label>
                    <input type="text" id="etCarrera" class="form-control" placeholder="Ej: Sistemas Computacionales">
                </div>
                <div class="form-group">
                    <label>Nombre del Proyecto</label>
                    <input type="text" id="etNombreProyecto" class="form-control" placeholder="Nombre del Proyecto">
                </div>
                <div class="form-group">
                    <label>Objetivo del proyecto</label>
                    <textarea id="etObjetivo" class="form-control" placeholder="Describe el objetivo del proyecto..." rows="3"></textarea>
                </div>
                <div class="form-group">
                    <label>Número de Vacantes</label>
                    <input type="number" id="etVacantes" class="form-control" placeholder="Ej: 3">
                </div>
                <div class="form-group">
                    <label>Ubicación</label>
                    <input type="text" id="etUbicacion" class="form-control" placeholder="Ciudad de México">
                </div>
                <div class="form-group">
                    <label>Nombre imagen (referencia)</label>
                    <input type="text" id="etImagenRef" class="form-control" placeholder="Ej: img_dron">
                </div>

                <button class="btn btn-primary btn-block btn-lg" onclick="guardarProyecto()" style="margin-top:8px;">
                    GUARDAR PROYECTO
                </button>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        requireSession('empresa');

        function guardarProyecto() {
            var carrera = document.getElementById('etCarrera').value.trim();
            var nombre = document.getElementById('etNombreProyecto').value.trim();
            var objetivo = document.getElementById('etObjetivo').value.trim();
            var vacantes = document.getElementById('etVacantes').value;

            if (!carrera || !nombre || !objetivo || !vacantes) {
                showToast('Por favor llena los campos obligatorios.', 'warning');
                return;
            }
            // TODO: POST /api/proyectos or PUT /api/proyectos/{id}
            showToast('Proyecto guardado exitosamente.', 'success');
            setTimeout(function() { window.location.href = 'empresa_vista_menu.jsp'; }, 1500);
        }
    </script>
</body>
</html>
