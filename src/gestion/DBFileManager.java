package gestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DBFileManager {

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
	
	public static boolean insertarDeFichero (String ruta) {
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
				String sql = "INSERT INTO "+tabla+" ("+columnas+") VALUES\n";
				String valores;
				do {
					 String linea = lec.nextLine();
					 valores = linea.replace(" ", "','");
					 sql+="('"+valores+"'),\n";
					
				}while (lec.hasNextLine());
				sql = sql.substring(0, sql.length()-2);
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
	
	public static boolean actualizarDeFichero (String ruta) {
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
				String valores = lec.nextLine();
				String listaValores[] = valores.split(",");
				do {
					for (int i=0;i<listaValores.length;i++) {
						String sql = "SELECT * FROM " + tabla + " WHERE " + DBManager.getPrimaryKey(tabla) + " = " +"'"+listaValores[i]+"'";
						PreparedStatement stmt = DBManager.getConn().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_UPDATABLE);
						ResultSet rs = stmt.executeQuery();
						

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
