package cl.eimco.mailer.servicio;

import cl.eimco.mailer.modelo.Estudiante;
import cl.eimco.mailer.util.PropUtils;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastián Salazar Molina <ssalazar@orangepeople.cl>
 */
public class ServicioDB implements Serializable {

    private boolean conectado = false;
    private Connection conexion = null;
    private static final Locale CHILE = new Locale("es", "CL");
    private static final Logger logger = LoggerFactory.getLogger(ServicioDB.class);

    @PostConstruct
    public void iniciar() {
        boolean ok = conectar();
        if (ok) {
            logger.info("Iniciando Servicio DB");
        } else {
            logger.info("ERROR: no fue posible conectarme a la base de datos");
        }

    }

    boolean conectar() {
        this.conectado = false;
        try {
            String url = PropUtils.obtenerPropiedad("jdbc.url");
            String usuario  = PropUtils.obtenerPropiedad("jdbc.usuario");
            String password  = PropUtils.obtenerPropiedad("jdbc.password");
            
            
            Properties props = new Properties();
            props.setProperty("user", usuario);
            props.setProperty("password", password);
            this.conexion = DriverManager.getConnection(url, props);

            if (conexion != null) {
                this.conectado = true;
            } else {
                this.conectado = false;
            }

            if (!conectado) {
                throw new RuntimeException("No se puede conectar al motor de base de datos.");
            }

        } catch (Exception e) {
            this.conectado = false;
            logger.error("Error al conectar con Base de datos: {}", e.toString());
            logger.debug("Error al conectar con Base de datos: {}", e.toString(), e);
        }
        return conectado;
    }

    boolean desconectar() {
        try {
            if (conexion != null) {
                conexion.close();
                conexion = null;
                conectado = false;
            } else {
                conectado = false;
            }
        } catch (Exception e) {
            conexion = null;
            conectado = false;
            logger.error("Error al desconectar con Base de datos: {}", e.toString());
            logger.debug("Error al desconectar con Base de datos: {}", e.toString(), e);
        }
        return conectado;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    @PreDestroy
    public void finalizar() {
        boolean desconectar = desconectar();
        if (desconectar) {
            logger.info("ERROR: conexión aún activa");
        }
    }

    public List<Estudiante> consultarUsuariosNoMigrados() {
        List<Estudiante> listado = new ArrayList<Estudiante>();
        try {
            boolean ok = conectar();
            if (ok) {
                String sql = "SELECT id, firstname || ' ' || lastname AS nombre, username, password, email FROM mdl_user WHERE password='$2y$10$kBSrnL2ZbonHYPX27Ew.VOpdgoMOBZDVD8JnewvJAj3XhP7rNTT56'";
                PreparedStatement pst = conexion.prepareStatement(sql);
                if (pst != null) {
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        Long id = rs.getLong("id");
                        String nombre = StringUtils.normalizeSpace(StringUtils.trimToEmpty(rs.getString("nombre")));
                        String username = StringUtils.trimToEmpty(rs.getString("username"));
                        String password = StringUtils.trimToEmpty(rs.getString("password"));
                        String email = StringUtils.lowerCase(StringUtils.trimToEmpty(rs.getString("email")), CHILE);

                        Estudiante estudiante = new Estudiante();
                        estudiante.setId(id);
                        estudiante.setNombre(nombre);
                        estudiante.setUsername(username);
                        estudiante.setPassword(password);
                        estudiante.setEmail(email);

                        listado.add(estudiante);
                    }
                }
                desconectar();
            }

        } catch (Exception e) {
            listado = new ArrayList<Estudiante>();
            logger.error("Error al obtener archivos: {}", e.toString());
            logger.debug("Error al obtener archivos: {}", e.toString(), e);
        }
        return listado;
    }
}
