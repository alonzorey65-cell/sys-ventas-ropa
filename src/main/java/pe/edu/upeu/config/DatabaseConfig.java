package pe.edu.upeu.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * POO - CLASE, ENCAPSULAMIENTO, ABSTRACCIÓN, MÉTODOS y CONEXIÓN:
 * - CLASE: encapsula la configuración y gestión del pool/conexión a BD.
 * - ENCAPSULAMIENTO: atributos y detalles internos (pool, propiedades) son privados; acceso por métodos.
 * - ABSTRACCIÓN: ofrece métodos como getConnection()/init()/shutdown() ocultando detalles de HikariCP.
 * - MÉTODOS: init, getDataSource, getConnection, runScript, etc. implementan comportamientos.
 * CONEXIÓN: Conexion/Repositorios usan esta clase para conectarse a Neon PostgreSQL y ejecutar scripts.
 */
public final class DatabaseConfig { // CLASE de infraestructura para conexión BD

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final String PROPERTIES_FILE = "application.properties";

    private static final String DEFAULT_URL = "jdbc:h2:file:./data/sysventas;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;AUTO_SERVER=TRUE";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_DRIVER = "org.h2.Driver";
    private static final String DEFAULT_SCRIPT = "schema.sql";

    private static volatile HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    /** Inicializa el pool y ejecuta el DDL si está habilitado. */
    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
        Properties props = loadProperties();
        String url = props.getProperty("db.url", DEFAULT_URL);

        HikariDataSource ds = createDataSource(url, props);
        try {
            if (Boolean.parseBoolean(props.getProperty("db.ddl.auto", "true"))) {
                runScript(ds, props.getProperty("db.ddl.script", DEFAULT_SCRIPT));
            }
        } catch (RuntimeException e) {
            ds.close();
            throw e;
        }
        dataSource = ds;
        LOG.info("Base de datos conectada: {}", url);
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /** Cierra el pool de conexiones de HikariCP. */
    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            LOG.info("Pool de conexiones HikariCP cerrado correctamente.");
        }
    }

    private static HikariDataSource createDataSource(String url, Properties props) {
        String driver = props.getProperty("db.driver", DEFAULT_DRIVER);
        HikariConfig config = new HikariConfig();
        config.setPoolName("SysVentasPool");
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(props.getProperty("db.username", DEFAULT_USER));
        config.setPassword(props.getProperty("db.password", DEFAULT_PASSWORD));

        int maxPool = Integer.parseInt(props.getProperty("hikari.maximum-pool-size", "10"));
        int minIdle = Integer.parseInt(props.getProperty("hikari.minimum-idle", "2"));
        config.setMaximumPoolSize(maxPool);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        try {
            return new HikariDataSource(config);
        } catch (RuntimeException e) {
            throw new IllegalStateException("No fue posible abrir la conexión a la base de datos: " + url, e);
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + PROPERTIES_FILE)) {
            if (in != null) {
                props.load(in);
            } else {
                LOG.warn("No se encontró {} en resources. Usando valores por defecto.", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + PROPERTIES_FILE, e);
        }
        // Permite externalizar configuración por variables de entorno
        overrideFromEnv(props, "db.url", "DB_URL");
        overrideFromEnv(props, "db.username", "DB_USERNAME");
        overrideFromEnv(props, "db.password", "DB_PASSWORD");
        overrideFromEnv(props, "db.driver", "DB_DRIVER");
        overrideFromEnv(props, "db.ddl.auto", "DB_DDL_AUTO");
        overrideFromEnv(props, "db.ddl.script", "DB_DDL_SCRIPT");
        overrideFromEnv(props, "apisperu.token", "APISPERU_TOKEN");
        return props;
    }

    private static void overrideFromEnv(Properties props, String key, String envName) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            props.setProperty(key, value.trim());
        }
    }

    /** Ejecuta el script DDL sentencia por sentencia si db.ddl.auto está en true. */
    private static void runScript(DataSource ds, String script) {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + script)) {
            if (in == null) {
                LOG.warn("No se encontró el script DDL {}", script);
                return;
            }
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            StringBuilder cleaned = new StringBuilder();
            for (String line : content.split("\\R")) {
                String stripped = line.stripLeading();
                if (!stripped.startsWith("--")) {
                    cleaned.append(line).append('\n');
                }
            }

            try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
                for (String statement : cleaned.toString().split(";")) {
                    String s = statement.trim();
                    if (!s.isEmpty()) {
                        stmt.execute(s);
                    }
                }
            }
            LOG.info("DDL ejecutado: {}", script);
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Error ejecutando el script " + script, e);
        }
    }
}