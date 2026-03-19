<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX — Iniciar Sesión</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .login-page {
            min-height: 100vh; display: flex; align-items: center; justify-content: center;
            padding: 40px 24px;
        }
        .login-card {
            width: 100%; max-width: 440px;
            animation: fadeInUp 0.5s ease;
        }
        .login-title {
            font-family: 'Quicksand', sans-serif;
            font-size: 2.2rem; font-weight: 800;
            color: var(--color-on-background);
            margin-bottom: 36px; text-align: center;
        }
        .login-bottom {
            display: flex; align-items: center; gap: 16px;
            margin-top: 28px;
        }
        .login-bottom .login-logo {
            width: 64px; height: 64px; object-fit: contain; flex-shrink: 0;
        }
        .login-bottom .btn { flex: 1; }
        .login-link {
            text-align: center; margin-top: 24px;
            font-size: 0.95rem;
        }
        @keyframes fadeInUp {
            from { opacity:0; transform:translateY(20px); }
            to { opacity:1; transform:translateY(0); }
        }
    </style>
</head>
<body>
    <div class="login-page">
        <div class="login-card">
            <h1 class="login-title">Iniciar Sesión</h1>

            <div class="form-group">
                <div class="input-icon-wrapper">
                    <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M22 4l-10 8L2 4"/></svg>
                    <input type="email" id="etUsuario" class="form-control" placeholder="Correo electrónico">
                </div>
            </div>

            <div class="form-group">
                <div class="input-icon-wrapper">
                    <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
                    <input type="password" id="etContrasena" class="form-control" placeholder="Contraseña" style="padding-right:48px;">
                    <button class="password-toggle" onclick="togglePassword('etContrasena', this)">👁</button>
                </div>
            </div>

            <div class="login-bottom">
                <img src="assets/img/solinx_logo.png" alt="SoLinX" class="login-logo">
                <button class="btn btn-primary btn-lg" onclick="handleLogin()">Enviar</button>
            </div>

            <div class="login-link">
                <a href="index.jsp">¿No tienes cuenta? Regístrate aquí.</a>
            </div>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        function handleLogin() {
            var email = document.getElementById('etUsuario').value.trim();
            var pass = document.getElementById('etContrasena').value;
            if (!email || !pass) {
                showToast('Por favor llena todos los campos.', 'warning');
                return;
            }
            // TODO: API call to Spring Boot backend
            // fetch('/api/auth/login', { method:'POST', ... })
            showToast('Conectando con el servidor...', 'info');

            // Placeholder — simulate login based on URL param role
            var params = new URLSearchParams(window.location.search);
            var role = params.get('role') || 'alumno';
            setSession({ email: email, role: role, nombre: 'Usuario Demo', boleta: '2023630000' });
            setTimeout(function() {
                switch(role) {
                    case 'alumno': window.location.href = 'alumno_menu_empresas.jsp'; break;
                    case 'empresa': window.location.href = 'empresa_vista_menu.jsp'; break;
                    case 'supervisor': window.location.href = 'supervisor_menu.jsp'; break;
                }
            }, 800);
        }

        // Enter key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') handleLogin();
        });
    </script>
</body>
</html>
