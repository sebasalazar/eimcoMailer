package cl.eimco.mailer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastián Salazar Molina
 */
public class PropUtils implements Serializable {

    private static final long serialVersionUID = 7715272510451256320L;
    private static final Properties VARIABLES = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(PropUtils.class);

    private PropUtils() {
        throw new AssertionError();
    }

    public static String obtenerPropiedad(String llave) {
        String propiedad = StringUtils.EMPTY;
        try {
            if (VARIABLES.isEmpty()) {
                InputStream is = null;
                String archivo = System.getProperty("user.home") + System.getProperty("file.separator") + ".mailer" + System.getProperty("file.separator") + "mailer.properties";
                File externo = new File(archivo);
                if (externo.isFile()) {
                    is = new FileInputStream(externo);
                } else {
                    is = PropUtils.class.getClassLoader().getResourceAsStream("mailer.properties");
                }

                VARIABLES.load(is);
            }

            propiedad = StringUtils.trimToEmpty(VARIABLES.getProperty(llave, StringUtils.EMPTY));
        } catch (Exception e) {
            propiedad = StringUtils.EMPTY;
            logger.error("Error al obtener propiedad: {}", e.toString());
            logger.debug("Error al obtener propiedad: {}", e.toString(), e);
        }
        return propiedad;
    }
}
