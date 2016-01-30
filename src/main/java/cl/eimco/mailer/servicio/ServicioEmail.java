package cl.eimco.mailer.servicio;

import cl.eimco.mailer.util.PropUtils;
import java.io.Serializable;
import java.util.Locale;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastián Salazar Molina
 */
public class ServicioEmail implements Serializable {

    private static final long serialVersionUID = 3099759248103871488L;
    private String miNombre = null;
    private String miCorreo = null;
    private Session mailSession = null;
    private static final String SALTO_LINEA = System.getProperty("line.separator");
    private static final Logger logger = LoggerFactory.getLogger(ServicioEmail.class);

    public ServicioEmail() {
        try {
            logger.info("Iniciando Servicio Email");
            miNombre = PropUtils.obtenerPropiedad("mail.nombre");

            String miUsuario = PropUtils.obtenerPropiedad("mail.usuario");
            String miPass = PropUtils.obtenerPropiedad("mail.password");
            String servidor = PropUtils.obtenerPropiedad("mail.servidor");
            String puerto = PropUtils.obtenerPropiedad("mail.puerto");
            String auth = PropUtils.obtenerPropiedad("mail.auth");

            miCorreo = StringUtils.lowerCase(String.format("%s@%s", miUsuario, servidor));

            Properties props = new Properties();
            props.put("mail.smtp.host", servidor);
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.port", puerto);

            mailSession = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(miUsuario, miPass);
                }
            });
            // Debug
            mailSession.setDebug(true);
        } catch (Exception e) {
            logger.error("Error al iniciar servicio Email: {}", e.toString());
            logger.debug("Error al iniciar servicio Email: {}", e.toString(), e);
        }
    }

    public boolean enviarCorreoCambioPassword(String nombre, String usuario, String correo) {
        boolean ok = false;
        try {
            if (mailSession != null) {
                if (StringUtils.isNotBlank(usuario) && StringUtils.isNotBlank(correo)) {
                    InternetAddress desde = new InternetAddress(miCorreo, miNombre);
                    InternetAddress para;
                    if (StringUtils.isNotBlank(nombre)) {
                        para = new InternetAddress(correo, nombre);
                    } else {
                        para = new InternetAddress(correo);
                    }

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
                            + "PD: Este es un correo automático, si necesita soporte técnico, por favor escribama a sebasalazar@gmail.com",
                            SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, usuario, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA, SALTO_LINEA);

                    Message message = new MimeMessage(mailSession);
                    message.setFrom(desde);
                    message.setRecipient(Message.RecipientType.TO, para);
                    message.setSubject(asunto);
                    message.setText(mensaje);

                    Transport.send(message);

                    logger.debug("==== Correo ====");
                    logger.debug("Nombre: '{}'", nombre);
                    logger.debug("Correo: '{}'", correo);
                    logger.debug("Asunto: '{}'", asunto);
                    logger.debug(mensaje);
                    logger.debug("==== Correo ====");

                    ok = true;
                }
            } else {
                logger.error("No fue posible crear la sesión");
            }
        } catch (Exception e) {
            ok = false;
            logger.error("No fue posible enviar el correo: {}", e.toString());
            logger.debug("No fue posible enviar el correo: {}", e.toString(), e);
        }
        return ok;
    }
}
