package cl.eimco.mailer.run;

import cl.eimco.mailer.modelo.Estudiante;
import cl.eimco.mailer.servicio.ServicioDB;
import cl.eimco.mailer.servicio.ServicioEmail;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastián Salazar Molina <ssalazar@experti.cl>
 */
public class App implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            logger.info("Iniciando envio de correo");
            ServicioDB servicioDB = new ServicioDB();
            List<Estudiante> listado = servicioDB.consultarUsuariosNoMigrados();
            if (!listado.isEmpty()) {
                ServicioEmail servicioEmail = new ServicioEmail();
                long buenas = 0;
                long malas = 0;
                for (Estudiante estudiante : listado) {
                    boolean ok = servicioEmail.enviarCorreoCambioPassword(estudiante.getNombre(), estudiante.getUsername(), estudiante.getEmail());
                    if (ok) {
                        buenas += 1;
                    } else {
                        malas += 1;
                    }
                }

                long total = buenas + malas;
                logger.info(String.format("Se procesaron %d Correos # Buenos: %d # Malos: %d", total, buenas, malas));
            }
            logger.info("Finalizando envio de correo");
        } catch (Exception e) {
            String mensaje = String.format("Error al procesar: %s", e.toString());
            logger.error(mensaje);
            logger.debug(mensaje, e);
            System.out.println(mensaje);
        }
    }
}
