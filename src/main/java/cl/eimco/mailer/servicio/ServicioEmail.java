package cl.eimco.mailer.servicio;

import cl.eimco.mailer.util.PropUtils;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastián Salazar Molina
 */
public class ServicioEmail implements Serializable {

    private static final long serialVersionUID = 3099759248103871488L;
    private static final String SALTO_LINEA = System.getProperty("line.separator");
    private static final Logger logger = LoggerFactory.getLogger(ServicioEmail.class);

    public boolean enviarCorreoCambioPassword(String nombre, String usuario, String correo) {
        boolean ok = false;
        try {

            String miNombre = PropUtils.obtenerPropiedad("mail.nombre");

            String miUsuario = PropUtils.obtenerPropiedad("mail.usuario");
            String miPass = PropUtils.obtenerPropiedad("mail.password");
            String servidor = PropUtils.obtenerPropiedad("mail.servidor");
            String puerto = PropUtils.obtenerPropiedad("mail.puerto");
            String auth = PropUtils.obtenerPropiedad("mail.auth");

            String miCorreo = StringUtils.lowerCase(String.format("%s@%s", miUsuario, servidor));

            String asunto = "Actualización de Plataforma EIMCO";
            String mensaje = String.format("Muy Buenas tardes. %s"
                    + "Espero que esté muy bien, junto con saludarle le escribo para comentarle "
                    + "que hemos actualizado nuestra plataforma virtual, con el fin de ofrecer una "
                    + "mejor experiencia de aprendizaje. %s"
                    + "Por su seguridad, se ha reseteado su contraseña y le pedimos que la cambie lo antes posible. %s"
                    + "1 - Ingrese a http://cursos.eimco.cl. %s"
                    + "2 - Utilice su usuario: '%s' y como contraseña: 'Eimco.2016' (Sin las comillas). %s"
                    + "3 - En la esquina superior derecha, verá un ícono con su nombre, al presionarlo se mostrará la opción de 'Preferencias', pinche esa opción. %s"
                    + "4 - Busque en la página que se le presentará la opción 'Cambiar contraseña', pinche ese link y cambie su contraseña. %s"
                    + "Espero que estos pequeños pasos le sean de utilidad, si encuentra algún problema o algo no funciona le invito a que me escriba "
                    + "al correo electrónico sebasalazar@gmail.com , para poder atender su inquietud. %s"
                    + "Le saluda atentamente, %s"
                    + "Sebastián Salazar Molina. %s"
                    + "PD: Este es un correo automático, si necesita soporte técnico, por favor escribame a sebasalazar@gmail.com",
                    SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, usuario, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA);

            Email email = new SimpleEmail();
            email.setHostName(servidor);
            email.setSmtpPort(Integer.parseInt(puerto));
            email.setAuthenticator(new DefaultAuthenticator(miUsuario, miPass));
            email.setStartTLSEnabled(true);
            email.setStartTLSRequired(true);
            email.setDebug(true);
            email.setFrom(miCorreo, miNombre);
            email.setSubject(asunto);
            email.setMsg(mensaje);
            email.addTo(correo, nombre);
            email.setSSLCheckServerIdentity(false);
            email.getMailSession().getProperties().put("mail.smtp.ssl.trust", "eimco.cl");

            String resultado = email.send();

            logger.debug("==== Correo ====");
            logger.debug("Nombre: '{}'", nombre);
            logger.debug("Correo: '{}'", correo);
            logger.debug("Asunto: '{}'", asunto);
            logger.debug(mensaje);
            logger.debug("Resultado: '{}'", resultado);
            logger.debug("==== Correo ====");

        } catch (Exception e) {
            ok = false;
            logger.error("No fue posible enviar el correo: {}", e.toString());
            logger.debug("No fue posible enviar el correo: {}", e.toString(), e);
        }
        return ok;
    }
}
