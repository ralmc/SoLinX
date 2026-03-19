<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Crear Cuenta Alumno</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .register-page {
            min-height: 100vh; display: flex; align-items: center; justify-content: center;
            padding: 40px 24px;
        }
        .register-card { width: 100%; max-width: 500px; animation: fadeInUp 0.5s ease; }
        .register-title {
            font-family: 'Quicksand', sans-serif; font-size: 2rem; font-weight: 800;
            color: var(--color-on-background); margin-bottom: 30px; text-align: center;
        }
        .register-bottom { display: flex; align-items: center; gap: 16px; margin-top: 28px; }
        .register-bottom .reg-logo { width: 64px; height: 64px; object-fit: contain; flex-shrink: 0; }
        .register-bottom .btn { flex: 1; }
        .register-link { text-align: center; margin-top: 20px; font-size: 0.95rem; }
        @keyframes fadeInUp { from { opacity:0; transform:translateY(20px); } to { opacity:1; transform:translateY(0); } }
    </style>
</head>
<body>
    <div class="register-page">
        <div class="register-card">
            <h1 class="register-title">Crear Cuenta</h1>

            <div class="form-group">
                <input type="text" id="etNombreUsuario" class="form-control" placeholder="Nombre de Usuario">
            </div>
            <div class="form-group">
                <input type="number" id="etBoleta" class="form-control" placeholder="Boleta">
            </div>
            <div class="form-group">
                <select id="spEscuela" class="form-control">
                    <option value="" disabled selected>Escuela</option>
                    <option>ESCOM</option>
                    <option>ESIME Zacatenco</option>
                    <option>ESIME Culhuacán</option>
                    <option>UPIICSA</option>
                    <option>UPIITA</option>
                    <option>ESIA</option>
                </select>
            </div>
            <div class="form-group">
                <select id="spCarrera" class="form-control">
                    <option value="" disabled selected>Carrera</option>
                    <option>Ingeniería en Sistemas Computacionales</option>
                    <option>Ingeniería en Inteligencia Artificial</option>
                    <option>Ingeniería en Software</option>
                    <option>Ingeniería en Comunicaciones y Electrónica</option>
                    <option>Ingeniería Mecánica</option>
                    <option>Licenciatura en Ciencia de Datos</option>
                </select>
            </div>
            <div class="form-group">
                <input type="email" id="etCorreo" class="form-control" placeholder="Correo Electrónico">
            </div>
            <div class="form-group">
                <input type="email" id="etConfirmarCorreo" class="form-control" placeholder="Confirmar Correo">
            </div>
            <div class="form-group">
                <div class="input-icon-wrapper" style="position:relative">
                    <input type="password" id="etContrasena" class="form-control" placeholder="Contraseña" style="padding-right:48px; padding-left:16px;">
                    <button class="password-toggle" onclick="togglePassword('etContrasena', this)">👁</button>
                </div>
            </div>
            <div class="form-group">
                <div class="input-icon-wrapper" style="position:relative">
                    <input type="password" id="etConfirmarContrasena" class="form-control" placeholder="Confirmar Contraseña" style="padding-right:48px; padding-left:16px;">
                    <button class="password-toggle" onclick="togglePassword('etConfirmarContrasena', this)">👁</button>
                </div>
            </div>

            <div class="register-bottom">
                <img src="assets/img/solinx_logo.png" alt="SoLinX" class="reg-logo">
                <button class="btn btn-primary btn-lg" onclick="handleRegister()">Crear Cuenta</button>
            </div>

            <div class="register-link">
                <a href="iniciar_sesion.jsp?role=alumno">¿Ya tienes cuenta? Inicia sesión aquí.</a>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        function handleRegister() {
            var nombre = document.getElementById('etNombreUsuario').value.trim();
            var boleta = document.getElementById('etBoleta').value.trim();
            var correo = document.getElementById('etCorreo').value.trim();
            var confirmarCorreo = document.getElementById('etConfirmarCorreo').value.trim();
            var pass = document.getElementById('etContrasena').value;
            var confirmPass = document.getElementById('etConfirmarContrasena').value;

            if (!nombre || !boleta || !correo || !pass) {
                showToast('Por favor llena todos los campos.', 'warning'); return;
            }
            if (correo !== confirmarCorreo) {
                showToast('Los correos no coinciden.', 'error'); return;
            }
            if (pass !== confirmPass) {
                showToast('Las contraseñas no coinciden.', 'error'); return;
            }
            // TODO: API call POST /api/alumnos/registro
            showToast('Cuenta creada exitosamente.', 'success');
            setTimeout(function() {
                window.location.href = 'iniciar_sesion.jsp?role=alumno';
            }, 1500);
        }
    </script>
</body>
</html>
