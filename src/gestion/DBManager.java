package gestion;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;

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
    private static final String DB_URL = "jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name + "?characterEncoding=latin1";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "1234";
    private static final String DB_MSQ_CONN_OK = "CONEXION CORRECTA";
    private static final String DB_MSQ_CONN_NO = "ERROR EN LA CONEXION";

    // Configuracion de la tabla Clientes
    private static final String DB_CLI = "clientes";
    private static final String DB_CLI_SELECT = "SELECT * FROM " + DB_CLI;
    private static final String DB_CLI_ID = "id";
    private static final String DB_CLI_NOM = "nombre";
    private static final String DB_CLI_DIR = "direccion";

    //////////////////////////////////////////////////
    // METODOS DE CONEXION A LA BASE DE DATOS
    //////////////////////////////////////////////////
    
    public static String getHost () {
    	try {
    		Scanner in = new Scanner (System.in);
        	System.out.println("Introduce la direccion de la Base de Datos");
        	String host=in.nextLine();
        	return host;
    	}catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    	
    }
    
    public static String getPort () {
       	try {
       		Scanner in = new Scanner (System.in);
        	System.out.println("Introduce el puerto de la Base de Datos");
        	String port=in.nextLine();
        	return port;
    	}catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public static String getName () {
       	try {
       		Scanner in = new Scanner (System.in);
        	System.out.println("Introduce el nombre de la Base de Datos");
        	String name=in.nextLine();
        	return name;
    	}catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * Intenta cargar el JDBC driver.
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
    			System.out.print(tabla+"\t");
    		}
    	}catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public static void printColumnas (String tabla) {
    	try {
    		DatabaseMetaData dbmt = conn.getMetaData();
    		ResultSet rs = dbmt.getColumns(conn.getCatalog(), null, tabla, "%");
    		System.out.print("Tablas:\t");
    		while (rs.next()) {
    			String columnName = rs.getString("COLUMN_NAME");
    		    String columnType = rs.getString("TYPE_NAME");
    			System.out.println("Columna: "+columnName+"\nTipo: "+columnType);
    		}
    	}catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public static ResultSet getColumnas (String tabla) {
    	try {
    		DatabaseMetaData dbmt = conn.getMetaData();
    		ResultSet rs = dbmt.getColumns(conn.getCatalog(), null, tabla, "%");
    		if(!rs.first() || rs==null) {
    			return null;
    		}
    		return rs;
    	}catch (SQLException e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public static ResultSet getTabla(String tabla) {
        try {
        	String consulta = "SELECT * FROM "+tabla;
            PreparedStatement sentencia = conn.prepareStatement(consulta,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
            for (int x=1;x<=rs.getMetaData().getColumnCount();x++)
            	  System.out.print(rs.getMetaData().getColumnName(x)+ "\t");
            		      
            	System.out.println("");
            		      
            	// Ahora volcamos los datos
            	while(rs.next()) {
            	  for (int x=1;x<=rs.getMetaData().getColumnCount();x++)
            	    System.out.print(rs.getString(x)+ "\t");
            		      
            	  System.out.println("");
            	}
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    // Devuelve 
    // Los argumentos indican el tipo de ResultSet deseado
    /**
     * Obtiene toda la tabla clientes de la base de datos
     * @param resultSetType Tipo de ResultSet
     * @param resultSetConcurrency Concurrencia del ResultSet
     * @return ResultSet (del tipo indicado) con la tabla, null en caso de error
     */
    public static ResultSet getTablaClientes(int resultSetType, int resultSetConcurrency) {
        try {
            PreparedStatement stmt = conn.prepareStatement(DB_CLI_SELECT, resultSetType, resultSetConcurrency);
            ResultSet rs = stmt.executeQuery();
            //stmt.close();
            return rs;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * Obtiene toda la tabla clientes de la base de datos
     *
     * @return ResultSet (por defecto) con la tabla, null en caso de error
     */
    public static ResultSet getTablaClientes() {
        return getTablaClientes(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * Imprime por pantalla el contenido de la tabla clientes
     */
    public static void printTablaClientes() {
        try {
            ResultSet rs = getTablaClientes(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                int id = rs.getInt(DB_CLI_ID);
                String n = rs.getString(DB_CLI_NOM);
                String d = rs.getString(DB_CLI_DIR);
                System.out.println(id + "\t" + n + "\t" + d);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //////////////////////////////////////////////////
    // METODOS DE UN SOLO CLIENTE
    //////////////////////////////////////////////////
    ;
    
    /**
     * Solicita a la BD el cliente con id indicado
     * @param id id del cliente
     * @return ResultSet con el resultado de la consulta, null en caso de error
     */
    public static ResultSet getCliente(int id) {
        try {
            // Realizamos la consulta SQL
        	String sql = DB_CLI_SELECT + " WHERE " + DB_CLI_ID + " = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, String.valueOf(id));
            //System.out.println(sql);
            ResultSet rs = stmt.executeQuery();
            //stmt.close();
            
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
     * Comprueba si en la BD existe el cliente con id indicado
     *
     * @param id id del cliente
     * @return verdadero si existe, false en caso contrario
     */
    public static boolean existsCliente(int id) {
        try {
            // Obtenemos el cliente
            ResultSet rs = getCliente(id);

            // Si rs es null, se ha producido un error
            if (rs == null) {
                return false;
            }

            // Si no existe primer registro
            if (!rs.first()) {
                rs.close();
                return false;
            }

            // Todo bien, existe el cliente
            rs.close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Imprime los datos del cliente con id indicado
     *
     * @param id id del cliente
     */
    public static void printCliente(int id) {
        try {
            // Obtenemos el cliente
            ResultSet rs = getCliente(id);
            if (rs == null || !rs.first()) {
                System.out.println("Cliente " + id + " NO EXISTE");
                return;
            }
            
            // Imprimimos su informacion por pantalla
            int cid = rs.getInt(DB_CLI_ID);
            String nombre = rs.getString(DB_CLI_NOM);
            String direccion = rs.getString(DB_CLI_DIR);
            System.out.println("Cliente " + cid + "\t" + nombre + "\t" + direccion);

        } catch (SQLException ex) {
            System.out.println("Error al solicitar cliente " + id);
            ex.printStackTrace();
        }
    }

    /**
     * Solicita a la BD insertar un nuevo registro cliente
     *
     * @param nombre nombre del cliente
     * @param direccion direccion del cliente
     * @return verdadero si pudo insertarlo, false en caso contrario
     */
    public static boolean insertCliente(String nombre, String direccion) {
        try {
            // Obtenemos la tabla clientes
            System.out.print("Insertando cliente " + nombre + "...");
            ResultSet rs = getTablaClientes(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // Insertamos el nuevo registro
            rs.moveToInsertRow();
            rs.updateString(DB_CLI_NOM, nombre);
            rs.updateString(DB_CLI_DIR, direccion);
            rs.insertRow();

            // Todo bien, cerramos ResultSet y devolvemos true
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
     * @param id id del cliente a modificar
     * @param nombre nuevo nombre del cliente
     * @param direccion nueva direccion del cliente
     * @return verdadero si pudo modificarlo, false en caso contrario
     */
    public static boolean updateCliente(int id, String nuevoNombre, String nuevaDireccion) {
        try {
            // Obtenemos el cliente
            System.out.print("Actualizando cliente " + id + "... ");
            ResultSet rs = getCliente(id);

            // Si no existe el Resultset
            if (rs == null) {
                System.out.println("Error. ResultSet null.");
                return false;
            }

            // Si tiene un primer registro, lo eliminamos
            if (rs.first()) {
                rs.updateString(DB_CLI_NOM, nuevoNombre);
                rs.updateString(DB_CLI_DIR, nuevaDireccion);
                rs.updateRow();
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

    /**
     * Solicita a la BD eliminar un cliente
     *
     * @param id id del cliente a eliminar
     * @return verdadero si pudo eliminarlo, false en caso contrario
     */
    public static boolean deleteCliente(int id) {
        try {
            System.out.print("Eliminando cliente " + id + "... ");

            // Obtenemos el cliente
            ResultSet rs = getCliente(id);

            // Si no existe el Resultset
            if (rs == null) {
                System.out.println("ERROR. ResultSet null.");
                return false;
            }

            // Si existe y tiene primer registro, lo eliminamos
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
    
    public static void getClientesDeDireccion (String direccion) {
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
    		
    	}catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    }

}
