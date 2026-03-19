<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .main-hero {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 40px 24px;
        }
        .brand-row {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 80px;
            animation: fadeInDown 0.7s ease;
        }
        .brand-title {
            font-family: 'Quicksand', sans-serif;
            font-size: 4.5rem;
            font-weight: 800;
            color: var(--color-primary);
            letter-spacing: -1px;
        }
        .brand-logo { width: 90px; height: 90px; object-fit: contain; }
        .main-actions {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 8px;
            animation: fadeInUp 0.7s ease 0.2s both;
        }
        .main-btn-login {
            font-family: 'Quicksand', sans-serif;
            font-size: 2.8rem;
            font-weight: 800;
            color: var(--color-on-background);
            background: none;
            border: none;
            cursor: pointer;
            padding: 4px 16px;
            transition: color 0.18s ease;
        }
        .main-btn-login:hover { color: var(--color-primary); }
        .main-btn-underline {
            width: 260px; height: 4px;
            background: var(--color-on-background);
            border-radius: 2px; margin-bottom: 16px;
        }
        .main-btn-register {
            font-family: 'Quicksand', sans-serif;
            font-size: 2.2rem; font-weight: 600;
            color: var(--color-text-secondary);
            background: none; border: none;
            cursor: pointer; padding: 4px 16px;
            transition: color 0.18s ease;
        }
        .main-btn-register:hover { color: var(--color-primary); }
        .role-selector {
            display: flex; align-items: center; gap: 8px;
            margin-top: auto; padding-bottom: 32px;
            font-family: 'Quicksand', sans-serif;
            font-weight: 700; font-size: 0.95rem;
            animation: fadeIn 0.7s ease 0.5s both;
        }
        .role-selector a {
            color: var(--color-on-background);
            padding: 6px 12px; border-radius: 6px;
            transition: all 0.18s ease;
        }
        .role-selector a:hover, .role-selector a.active-role {
            color: var(--color-primary);
        }
        .role-selector a.active-role { font-weight: 800; }
        .role-selector .sep { color: var(--color-text-secondary); }
        @keyframes fadeInDown { from { opacity:0; transform:translateY(-30px); } to { opacity:1; transform:translateY(0); } }
        @keyframes fadeInUp { from { opacity:0; transform:translateY(30px); } to { opacity:1; transform:translateY(0); } }
        @keyframes fadeIn { from { opacity:0; } to { opacity:1; } }
        @media (max-width: 600px) {
            .brand-title { font-size: 3rem; }
            .brand-logo { width: 64px; height: 64px; }
            .main-btn-login { font-size: 2rem; }
            .main-btn-register { font-size: 1.6rem; }
            .main-btn-underline { width: 200px; }
        }
    </style>
</head>
<body>
    <div class="main-hero">
        <div class="brand-row">
            <span class="brand-title">SoLinX</span>
            <img src="assets/img/solinx_logo.png" alt="SoLinX" class="brand-logo">
        </div>
        <div class="main-actions">
            <button class="main-btn-login" onclick="goLogin()">Iniciar Sesión</button>
            <div class="main-btn-underline"></div>
            <button class="main-btn-register" onclick="goRegister()">Crear Cuenta</button>
        </div>
        <div class="role-selector" id="roleSelector">
            <a href="#" class="active-role" data-role="alumno" onclick="selectRole(event,'alumno')">Alumno</a>
            <span class="sep">|</span>
            <a href="#" data-role="empresa" onclick="selectRole(event,'empresa')">Empresa</a>
            <span class="sep">|</span>
            <a href="#" data-role="supervisor" onclick="selectRole(event,'supervisor')">Supervisor</a>
        </div>
    </div>
    <script src="assets/js/app.js"></script>
    <script>
        var selectedRole = 'alumno';
        function selectRole(e, role) {
            e.preventDefault(); selectedRole = role;
            document.querySelectorAll('#roleSelector a').forEach(function(a) {
                a.classList.toggle('active-role', a.dataset.role === role);
            });
        }
        function goLogin() { window.location.href = 'iniciar_sesion.jsp?role=' + selectedRole; }
        function goRegister() {
            if (selectedRole === 'alumno') window.location.href = 'alumno_crear_cuenta.jsp';
            else if (selectedRole === 'empresa') window.location.href = 'empresa_crear.jsp';
            else showToast('El registro de supervisores es por administración.', 'info');
        }
    </script>
</body>
</html>
