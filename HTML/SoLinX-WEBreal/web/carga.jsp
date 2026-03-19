<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SoLinX</title>
    <link rel="stylesheet" href="assets/css/styles.css">
</head>
<body>
    <div class="loading-screen" id="splashScreen">
        <img src="assets/img/solinx_logo_sf.png" alt="SoLinX Logo" class="loading-logo">
        <div class="loading-credit">
            Powered by<br>
            <span style="letter-spacing: 6px;">R A L M</span>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        // Redirect after 2.5s splash
        setTimeout(function() {
            var session = getSession();
            if (session) {
                switch(session.role) {
                    case 'alumno': window.location.href = 'alumno_menu_empresas.jsp'; break;
                    case 'empresa': window.location.href = 'empresa_vista_menu.jsp'; break;
                    case 'supervisor': window.location.href = 'supervisor_menu.jsp'; break;
                    default: window.location.href = 'index.jsp';
                }
            } else {
                window.location.href = 'index.jsp';
            }
        }, 2500);
    </script>
</body>
</html>
