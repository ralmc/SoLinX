<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Crear Cuenta Empresa</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .register-page { min-height:100vh; display:flex; align-items:center; justify-content:center; padding:40px 24px; }
        .register-card { width:100%; max-width:500px; animation:fadeInUp 0.5s ease; }
        .register-title { font-family:'Quicksand',sans-serif; font-size:2rem; font-weight:800; color:var(--color-on-background); margin-bottom:30px; text-align:center; }
        .register-bottom { display:flex; align-items:center; gap:16px; margin-top:28px; }
        .register-bottom .reg-logo { width:64px; height:64px; object-fit:contain; flex-shrink:0; }
        .register-bottom .btn { flex:1; }
        .register-link { text-align:center; margin-top:20px; font-size:0.95rem; }
        @keyframes fadeInUp { from{opacity:0;transform:translateY(20px);} to{opacity:1;transform:translateY(0);} }
    </style>
</head>
<body>
    <div class="register-page">
        <div class="register-card">
            <h1 class="register-title">Crear cuenta empresa</h1>

            <div class="form-group">
                <input type="text" id="etNombreEmpresa" class="form-control" placeholder="Nombre de la Empresa">
            </div>
            <div class="form-group">
                <input type="email" id="etEmailEmpresa" class="form-control" placeholder="Correo Electrónico">
            </div>
            <div class="form-group">
                <input type="email" id="etConfirmEmail" class="form-control" placeholder="Confirmar Correo Electrónico">
            </div>
            <div class="form-group">
                <input type="tel" id="etTelefono" class="form-control" placeholder="Teléfono" maxlength="10">
            </div>
            <div class="form-group">
                <div style="position:relative">
                    <input type="password" id="etPassword" class="form-control" placeholder="Contraseña" style="padding-right:48px;">
                    <button class="password-toggle" onclick="togglePassword('etPassword', this)">👁</button>
                </div>
            </div>
            <div class="form-group">
                <div style="position:relative">
                    <input type="password" id="etConfirmPassword" class="form-control" placeholder="Confirmar Contraseña" style="padding-right:48px;">
                    <button class="password-toggle" onclick="togglePassword('etConfirmPassword', this)">👁</button>
                </div>
            </div>

            <div class="register-bottom">
                <img src="assets/img/solinx_logo.png" alt="SoLinX" class="reg-logo">
                <button class="btn btn-primary btn-lg" onclick="handleRegister()">Enviar</button>
            </div>

            <div class="register-link">
                <a href="iniciar_sesion.jsp?role=empresa">¿Ya tienes cuenta? Inicia sesión aquí.</a>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        function handleRegister() {
            var nombre = document.getElementById('etNombreEmpresa').value.trim();
            var email = document.getElementById('etEmailEmpresa').value.trim();
            var confirmEmail = document.getElementById('etConfirmEmail').value.trim();
            var pass = document.getElementById('etPassword').value;
            var confirmPass = document.getElementById('etConfirmPassword').value;

            if (!nombre || !email || !pass) { showToast('Por favor llena todos los campos.', 'warning'); return; }
            if (email !== confirmEmail) { showToast('Los correos no coinciden.', 'error'); return; }
            if (pass !== confirmPass) { showToast('Las contraseñas no coinciden.', 'error'); return; }

            // TODO: API call POST /api/empresas/registro
            showToast('Cuenta de empresa creada exitosamente.', 'success');
            setTimeout(function() { window.location.href = 'iniciar_sesion.jsp?role=empresa'; }, 1500);
        }
    </script>
</body>
</html>
