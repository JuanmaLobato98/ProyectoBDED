package gestion;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *
 * @author lionel
 */
public class DBManager {

	// Conexion a la base de datos
	private static Connection conn = null;

	// Configuracion de la conexion a la base de datos
	private static String db_host = getHost();
	private static String db_port = getPort();
	private static String db_name = getName();
	private static final String DB_URL = "jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name
			+ "?characterEncoding=latin1";
	private static final String DB_USER = "root";
	private static final String DB_PASS = "1234";
	private static final String DB_MSQ_CONN_OK = "CONEXION CORRECTA";
	private static final String DB_MSQ_CONN_NO = "ERROR EN LA CONEXION";

	//////////////////////////////////////////////////
	// METODOS DE CONEXION A LA BASE DE DATOS
	//////////////////////////////////////////////////

	public static String getHost() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Introduce la direccion de la Base de Datos");
			String host = in.nextLine();
			return host;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String getPort() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Introduce el puerto de la Base de Datos");
			String port = in.nextLine();
			return port;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getName() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Introduce el nombre de la Base de Datos");
			String name = in.nextLine();
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Intenta cargar el JDBC driver.
	 * 
	 * @return true si pudo cargar el driver, false en caso contrario
	 */
	public static boolean loadDriver() {
		try {
			System.out.print("Cargando Driver...");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("OK!");
			return true;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Intenta conectar con la base de datos.
	 *
	 * @return true si pudo conectarse, false en caso contrario
	 */
	public static boolean connect() {
		try {
			System.out.print("Conectando a la base de datos...");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			System.out.println("OK!");
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Comprueba la conexion y muestra su estado por pantalla
	 *
	 * @return true si la conexion existe y es valida, false en caso contrario
	 */
	public static boolean isConnected() {
		// Comprobamos estado de la conexion
		try {
			if (conn != null && conn.isValid(0)) {
				System.out.println(DB_MSQ_CONN_OK);
				return true;
			} else {
				return false;
			}
		} catch (SQLException ex) {
			System.out.println(DB_MSQ_CONN_NO);
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Cierra la conexion con la base de datos
	 */
	public static void close() {
		try {
			System.out.print("Cerrando la conexion...");
			conn.close();
			System.out.println("OK!");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	//////////////////////////////////////////////////
	// METODOS DE TABLA CLIENTES
	//////////////////////////////////////////////////
	;

	public static void printTablas() {
		try {
			DatabaseMetaData dbmt = conn.getMetaData();
			ResultSet rs = dbmt.getTables(conn.getCatalog(), null, "%", null);
			System.out.print("Tablas:\t");
			while (rs.next()) {
				String tabla = rs.getString("Table_NAME");
				System.out.print(tabla + "\t");
			}
			System.out.print("\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void printColumnas(String tabla) {
		try {
			DatabaseMetaData dbmt = conn.getMetaData();
			ResultSet rs = dbmt.getColumns(conn.getCatalog(), null, tabla, "%");
			System.out.print("Tablas:\t");
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				String columnType = rs.getString("TYPE_NAME");
				System.out.println("Columna: " + columnName + "\nTipo: " + columnType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet getColumnas(String tabla) {
		try {
			DatabaseMetaData dbmt = conn.getMetaData();
			ResultSet rs = dbmt.getColumns(conn.getCatalog(), null, tabla, "%");
			if (!rs.first() || rs == null) {
				return null;
			}
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ResultSet getTabla(String tabla) {
		try {
			String consulta = "SELECT * FROM " + tabla;
			PreparedStatement sentencia = conn.prepareStatement(consulta, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet result = sentencia.executeQuery();
			return result;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void printTabla(String tabla) {
		try {
			ResultSet rs = getTabla(tabla);
			for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++)
				System.out.print(rs.getMetaData().getColumnName(x) + "\t");

			System.out.println("");

			// Ahora volcamos los datos
			while (rs.next()) {
				for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++)
					System.out.print(rs.getString(x) + "\t");

				System.out.println("");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean insertColumn(String tabla) {
		try {
			Scanner ent = new Scanner(System.in);
			String valor;
			ResultSet rs = getTabla(tabla);

			if (rs == null) {
				System.out.println("Error. ResultSet null.");
				return false;
			}

			rs.moveToInsertRow();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				System.out.println("Introduce el valor de: " + rs.getMetaData().getColumnName(i));
				valor = ent.nextLine();
				rs.updateString(i, valor);
			}
			rs.insertRow();

			rs.close();
			System.out.println("OK!");
			return true;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Solicita a la BD modificar los datos de un cliente
	 *
	 * @param id        id del cliente a modificar
	 * @param nombre    nuevo nombre del cliente
	 * @param direccion nueva direccion del cliente
	 * @return verdadero si pudo modificarlo, false en caso contrario
	 */
	public static boolean updateColumn(String tabla) {
		try {
			Scanner ent = new Scanner(System.in);
			String pk = getPrimaryKey(tabla);
			System.out.println("Introduce el valor de la columna " + pk + " de la fila que desea modificar");
			String valor = ent.nextLine();

			String sql = "SELECT * FROM " + tabla + " WHERE " + pk + " = " + valor;
			PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet rs2 = stmt.executeQuery();

			rs2.first();
			for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
				System.out.println("Introduce el nuevo valor de: " + rs2.getMetaData().getColumnName(i));
				valor = ent.nextLine();
				rs2.updateString(i, valor);
			}
			rs2.updateRow();
			rs2.close();

			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static String getPrimaryKey(String tabla) {
		try {

			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getPrimaryKeys(conn.getCatalog(), null, tabla);
			ResultSetMetaData rsmd = rs.getMetaData();

			String pk = "";
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					rs.getString(i);
					if (i == 4) {
						pk = rs.getString(i);
					}
				}
			}
			return pk;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Solicita a la BD eliminar un cliente
	 *
	 * @param id id del cliente a eliminar
	 * @return verdadero si pudo eliminarlo, false en caso contrario
	 */
	public static boolean deleteColumn(String tabla) {
		try {
			Scanner ent = new Scanner(System.in);
			String pk = getPrimaryKey(tabla);
			System.out.println("Introduce el valor de la columna " + pk + " de la fila que desea borrar");
			String valor = ent.nextLine();

			String sql = "SELECT * FROM " + tabla + " WHERE " + pk + " = " +"'"+ valor+"'";
			PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery();

			 if (rs.first()) {
	                rs.deleteRow();
	                rs.close();
	                System.out.println("OK!");
	                return true;
	            } else {
	                System.out.println("ERROR. ResultSet vacio.");
	                return false;
	            }
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}

	}

	//obsoleto desde que se puede usar cualquier bd
	
	/*public static void getClientesDeDireccion(String direccion) {
		try {
			CallableStatement proc = conn.prepareCall("{call cliente_direccion(?)}");
			proc.setString(1, direccion);
			proc.execute();

			ResultSet rs = proc.getResultSet();
			while (rs.next()) {
				int id = rs.getInt(DB_CLI_ID);
				String n = rs.getString(DB_CLI_NOM);
				String d = rs.getString(DB_CLI_DIR);
				System.out.println(id + "\t" + n + "\t" + d);
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}*/

}
