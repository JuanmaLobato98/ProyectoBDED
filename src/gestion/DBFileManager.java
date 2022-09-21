package gestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Clase que gestiona los metodos que trabajan con ficheros en la base de datos
 * 
 * @author Juanma
 *
 */
public class DBFileManager {

	/**
	 * Crea un fichero con el contenido de una tabla
	 * @param tabla nombre de la tabla
	 * @return true si se ha creado el fichero, false si no
	 */
	public static boolean exportarTabla (String tabla) {
		try {
			File fichero = new File (tabla+".txt");//declaramos el fichero, con esta ruta se crea directamente en la raiz del proyecto
			FileWriter escribir = new FileWriter(fichero);//declaramos el objeto para escribir
			
			fichero.createNewFile();//creamos el fichero
			
			escribir.write(DBManager.getDb_name() +"\n");
			escribir.write(tabla + "\n");
			escribir.write(DBManager.printTabla(tabla));
			escribir.close();

			return true;
		}catch (IOException e) {//controlamos las excepciones
			System.err.println("Error con el fichero");
			return false;
		}
	}
	
	/**
	 * Inserta registros en una tabla provenientes de un fichero
	 * 
	 * @param ruta ruta donde se encuentra el fichero
	 * @return true si se han insertado, false si no
	 */
	public static boolean insertarDeFichero (String ruta) {
		try {
			File fichero = new File(ruta);
			Scanner lec = new Scanner (fichero);
			do{
				String dbName=lec.nextLine();//la primera linea es el nombre de la BD
				if(!dbName.equals(DBManager.getDb_name())) { //si el nombre no coincide con el de la conexion actual 
					DBManager.close();//cierra la conexion
					DBManager.setDb_name(dbName);
					DBManager.connect(); //y crea una nueva conexion a esa base de datos
					System.out.println("Base de datos cambiada a "+dbName);
				}
				String tabla=lec.nextLine(); //la segunda linea es la tabla
				String columnas = lec.nextLine(); //la tercera las columnas
				String sql = "INSERT INTO "+tabla+" ("+columnas+") VALUES\n"; //creamos la sentencia
				String valores;
				do {
					 String linea = lec.nextLine(); //a aprtir de la cuarta los valores
					 valores = linea.replace(" ", "','"); //los vamos añadiendo a la consulta con el formato adecuado
					 sql+="('"+valores+"'),\n";
					
				}while (lec.hasNextLine());
				sql = sql.substring(0, sql.length()-2); //borramos la ultima coma que se le queda a la sntencia
				//System.out.println(sql);
				PreparedStatement stmt = DBManager.getConn().prepareStatement(sql);
				stmt.execute();
			}while (lec.hasNext());
			lec.close();
			System.out.println("Registros insertados!");
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Actualiza registros en una tabla provenientes de un fichero
	 * 
	 * @param ruta ruta donde se encuentra el fichero
	 * @return true si se han actualizado, false si no
	 */
	public static boolean actualizarDeFichero (String ruta) { //practicamente mismo procedimiento que el metodo de insertar
		try {
			File fichero = new File(ruta);
			Scanner lec = new Scanner (fichero);
			do{
				String dbName=lec.nextLine();
				if(!dbName.equals(DBManager.getDb_name())) {
					DBManager.close();
					DBManager.setDb_name(dbName);
					DBManager.connect();
					System.out.println("Base de datos cambiada a "+dbName);
				}
				String tabla=lec.nextLine();
				String columnas = lec.nextLine();
				String columnasNombre[] = columnas.split(",");
				do {
					String lineavalores = lec.nextLine();
					String valores[]= lineavalores.split(" ");
					String sql = "SELECT * FROM " + tabla + " WHERE " + columnasNombre[0] + " = " + "'"+valores[0]+"'";
					PreparedStatement stmt = DBManager.getConn().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					ResultSet rs2 = stmt.executeQuery();
					//System.out.println(sql);

					rs2.first();
					for (int i = 1; i < columnasNombre.length; i++) {
						
						rs2.updateString(columnasNombre[i], valores[i]);
					}
					rs2.updateRow();
					rs2.close();
				}while (lec.hasNextLine());
			}while(lec.hasNextLine());
			
			lec.close();
			System.out.println("Registros actualizados!");
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Borra registros en una tabla provenientes de un fichero
	 * 
	 * @param ruta ruta donde se encuentra el fichero
	 * @return true si se han borrado, false si no
	 */
	public static boolean borrarDeFichero (String ruta ) {
		try {
			File fichero = new File(ruta);
			Scanner lec = new Scanner (fichero);
			do{
				String dbName=lec.nextLine();
				if(!dbName.equals(DBManager.getDb_name())) {
					DBManager.close();
					DBManager.setDb_name(dbName);
					DBManager.connect();
					System.out.println("Base de datos cambiada a "+dbName);
				}
				String tabla=lec.nextLine();
				String valores = lec.nextLine();//en este caso la tercera linea en adelante son la pk
				String listaValores[] = valores.split(","); //separamos estos valores
				do {
					for (int i=0;i<listaValores.length;i++) {               //obtenemos el pk de la tabla				
						String sql = "SELECT * FROM " + tabla + " WHERE " + DBManager.getPrimaryKey(tabla) + " = " +"'"+listaValores[i]+"'";
						PreparedStatement stmt = DBManager.getConn().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_UPDATABLE);
						ResultSet rs = stmt.executeQuery();//hacemos la seleccion de cada uno de ellos
						
						//y lo borramos
						 if (rs.first()) {
				                rs.deleteRow();
				                rs.close();
				               
				            } else {
				                System.out.println("ERROR. ResultSet vacio.");
				                return false;
				            }
					}
					
				}while (lec.hasNextLine());
			}while (lec.hasNext());
			lec.close();
			System.out.println("Registros borrados!");
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
}
