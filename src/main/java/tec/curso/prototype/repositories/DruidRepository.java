package tec.curso.prototype.repositories;

/**
 * Interfaz que define las operaciones de acceso a datos para la ingesta en Druid.
 */
public interface DruidRepository {

    /**
     * Envía una consulta SQL de ingesta de datos a Druid.
     *
     * @param sql la sentencia SQL (INSERT) a ejecutar.
     */
    void ingerirDatos(String sql);
}