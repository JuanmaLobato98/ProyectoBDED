package gestion;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *Clase donde se encuentran todos los metodos para gestionar cualquier base de datos
 *
 * @author Juanma Lobato
 * @version v1.6 28/05/2022
 */
public class DBManager {

	// Conexion a la base de datos
	/**
	 * Variable de conexion a una BD
	 * Gestiona la conexion a cualquier base de datos
	 */
	private static Connection conn = null;
	/**
	 * Variable de conexion a un servidor de BD
	 * Gestiona la conexion al servidor y la usamos para mostrar las bases de datos
	 */
	private static Connection connServer = null;

	// Configuracion de la conexion a la base de datos
	private static String db_host = getHost();
	private static String db_port = getPort();
	private static String db_name=null;
	private static String DB_URL = "jdbc:mysql://" + db_host + ":" + db_port;
	private static final String DB_USER = "root";
	private static final String DB_PASS = "1234";

	//////////////////////////////////////////////////
	// METODOS DE CONEXION A LA BASE DE DATOS
	//////////////////////////////////////////////////

	/**
	 * Metodo que pide la direccion del servidor de base de datos
	 * @return devuelve un string con la direccion
	 */
	public static String getHost() {
		Scanner in = new Scanner(System.in);
		try {
			System.out.println("Introduce la direccion de la Base de Datos");
			String host = in.nextLine();
			return host;
		} catch (Exception e) {
			e.printStackTrace();
			in.nextLine();
			return null;
		}

	}

	/**
	 * Metodo que pide el host del servidor de base de datos
	 * @return devuelve un string con el host
	 */
	public static String getPort() {
		Scanner in = new Scanner(System.in);
		try {
			
			System.out.println("Introduce el puerto de la Base de Datos");
			String port = in.nextLine();
			return port;
		} catch (Exception e) {
			e.printStackTrace();
			in.nextLine();
			return null;
		}
	}

	/**
	 * Metodo para obtener el nombre de la BD
	 * @return devuelve un string con el nombre de la BD
	 */
	public static String getDb_name() {
		return db_name;
	}
	
	/**
	 * Metodo para asignar el nombre de la BD
	 * @param db_name String del nombre de la BD
	 */
	public static void setDb_name(String db_name) {
		DBManager.db_name = db_name;
	}

	/**
	 * Metodo que pide el nombre de la base de datos y cierra la conexion con el servidor
	 * @return Devuelve un String con el nombre de la BD
	 */
	public static String getName() {
		Scanner in = new Scanner(System.in);
		try {		
			System.out.println("Introduce el nombre de la Base de Datos");
			String name = in.nextLine();
			setDb_name(name);
			connServer.close();
			
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			in.nextLine();
			return null;
		}
	}
	
	/**
	 * Metodo que muestra el nombre de las bases de datos alojadas en el servidor
	 * La usamos para mostrar al usuario las bases e datos disponibles en el servidor y poder elegir una
	 */
	public static void getBDs () {
		try {
			PreparedStatement sentencia = connServer.prepareStatement("SHOW DATABASES");
			
			ResultSet resultado = sentencia.executeQuery();
			
			while(resultado.next()) {
				System.out.println(resultado.getString(1));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Metodo getter de la conexion a la base de datos
	 * La usamos para comprobar la conexion a la hora de usar un fichero
	 * @return devuelve un objeto conecction con la conexcion a la BD
	 */
	public static Connection getConn() {
		return conn;
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
	 * Intenta conectarse al servidor que aloja las bases de datos
	 * 
	 * @return devuelve true si se conecta, false si no
	 */
	public static boolean connectServer () {
		try {
			System.out.print("Conectando al servidor...");
			connServer = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			System.out.println("OK!");
			return true;
		} catch (SQLException ex) {
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
			conn = DriverManager.getConnection(DB_URL+"/"+getDb_name(), DB_USER, DB_PASS);
			System.out.println("OK!");
			return true;
		} catch (SQLException ex) {
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

	/**
	 * Imprime las tablas que contiene una base de datos
	 */
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

	/**
	 * Imprime las columnas de una tabla
	 * 
	 * @param tabla nombre de la tabla
	 * @return devuelve un String con el nombre de las columnas
	 */
	public static String printColumnas(String tabla) {
		try {
			String columnas="";
			DatabaseMetaData dbmt = conn.getMetaData();
			ResultSet rs = dbmt.getColumns(conn.getCatalog(), null, tabla, "%");
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				columnas += columnName+"\t";
			}
			return columnas;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Obtiene las columnas de una tabla
	 * 
	 * @param tabla nombre de la tabla
	 * @return devuelve un resultset con las columnas de la tabla
	 */
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

	/**
	 * Obtiene una tabla de una base de datos
	 * 
	 * @param tabla nombre de la tabla
	 * @return devuelve un resultset con las tablas de la BD
	 */
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

	/**
	 * Imprime el contenido de una tabla de la base de datos
	 * 
	 * @param tabla nombre de la tabla
	 * @return devuelve un string con el contenido de la tabla
	 */
	public static String printTabla(String tabla) {
		try {
			ResultSet rs = getTabla(tabla);

			System.out.println("");
			String datosTabla=printColumnas(tabla)+"\n";
			// Ahora volcamos los datos
			while (rs.next()) {
				for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
					datosTabla += (rs.getString(x) + "\t");
				}
				datosTabla +="\n";
			}
			return datosTabla;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Obtiene las tuplas filtradas de una tabla mediante una consulta
	 * 
	 * @param tabla nombre de la tabla
	 * @param campo nombre del campo a filtrar
	 * @param valor valor por el que filtrar
	 * @return devuelve un resultset con la seleccion de la consulta
	 */
	public static ResultSet getTuplas(String tabla, String campo, String valor) {
		try {
			// Realizamos la consulta SQL
			String sql = "SELECT * FROM " + tabla + " WHERE " + campo + " =?";
			PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setString(1, valor);
			ResultSet rs = stmt.executeQuery();

			// Si no hay primer registro entonces no existe el cliente
			if (!rs.first()) {
				return null;
			}

			// Todo bien, devolvemos el cliente
			return rs;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Obtiene las tuplas filtradas de una tabla mediante una consulta
	 * 
	 * @param tabla nombre de la tabla
	 * @param campo nombre del campo a filtrar
	 * @param valor valor por el que filtrar
	 * @return devuelve un String con la seleccion de la consulta
	 */
	 public static boolean printTuplas(String tabla, String campo, String valor) {
    	 try {
             // Obtenemos el cliente
             ResultSet rs = getTuplas(tabla,campo,valor);
             if (rs == null || !rs.first()) {
                 System.out.println("No existe el resultado ");
                 return false;
             }
             while (rs.next()) {
 				for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
 					System.out.print( (rs.getString(x) + "\t"));
 				}
 				System.out.print("\n");
 			}
             
             return true;
         } catch (SQLException ex) {
             ex.printStackTrace();
             return false;
         }
	 }
	
	 /**
	  * Crea una tabla en una base de datos con el numero de columnas que se desee
	  * 
	  * @param nombre nombre de la tabla 
	  * @param columnas numero de columnas que tendra la tabla
	  */
	public static void crearTabla (String nombre, int columnas) {
		Scanner in = new Scanner (System.in);
		try {
			System.out.println("Introduce el nombre de la columna 1");
			String col1 = in.next(); //pedimo el nombre
			System.out.println("Introduce el tipo de la columna 1 (1 VARCHAR o 2 INT)"); //pedimos el tipo, habra que introducir 1 o 2
			int tipo1 = in.nextInt();
			if(tipo1==1) {
				PreparedStatement stmt = conn.prepareStatement("CREATE TABLE "+nombre+" ("+col1+" VARCHAR(50)) ");//creamos la tabla con la primera columna
				stmt.execute();
			}else {
				PreparedStatement stmt = conn.prepareStatement("CREATE TABLE "+nombre+" ("+col1+" INT)");
				stmt.execute();
			}
			
			
			
			for (int i=2; i<=columnas;i++) { //añadimos el resto de las columnas
				System.out.println("Introduce el nombre de la columna "+i);
				String col = in.next();
				System.out.println("Introduce el tipo de la columna "+i+" (1 VARCHAR o 2 INT");
				int tipo = in.nextInt();
				if(tipo==1) {
					PreparedStatement stmtcols = conn.prepareStatement("ALTER TABLE "+nombre+" ADD COLUMN "+col+" VARCHAR(50);");
					stmtcols.execute();
				}else {
					PreparedStatement stmtcols = conn.prepareStatement("ALTER TABLE "+nombre+" ADD COLUMN "+col+" INT;");
					stmtcols.execute();
				}
			}
			
		}catch (InputMismatchException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}

	/**
	 * Inserta un registro en una tabla de la base de datos
	 * 
	 * @param tabla nombre de la tabla
	 * @return true si se ha creado el registro, false si no
	 */
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
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {//obtenemos el numero de columnas y las recorremos en un bucle
				System.out.println("Introduce el valor de: " + rs.getMetaData().getColumnName(i));//obtenemos el nombre y lo vamos mostrando
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
	 * Solicita a la BD modificar los datos de un registro
	 *
	 * @param tabla nombre de la tabla
	 * @return verdadero si pudo modificarlo, false en caso contrario
	 */
	public static boolean updateColumn(String tabla) {
		try {
			Scanner ent = new Scanner(System.in);
			String pk = getPrimaryKey(tabla);//obtenemos el campo calve de la tabla
			System.out.println("Introduce el valor de la columna " + pk + " de la fila que desea modificar");
			String valor = ent.nextLine(); //se lo pedimos al usuario

			String sql = "SELECT * FROM " + tabla + " WHERE " + pk + " = " + valor; //hacemos una consulta que obtenga ese registro
			PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet rs2 = stmt.executeQuery();

			rs2.first();
			for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {//obtenemos su columna y lo modificamos
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

	/**
	 * Obtiene la primary key de una tabla de la BD
	 * 
	 * @param tabla nombre de la tabla
	 * @return devuelve un string con el nombre del campo clave
	 */
	public static String getPrimaryKey(String tabla) {
		try {

			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getPrimaryKeys(conn.getCatalog(), null, tabla);//este metodo devuelve un resutl set con iformacion de la tabla
			ResultSetMetaData rsmd = rs.getMetaData();

			String pk = "";
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					rs.getString(i);
					if (i == 4) { //donde la columna 4 es el nombre del campo clave
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
	 * Solicita a la BD eliminar un registro
	 *
	 * @param tabla nombre de la tabla
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

}
