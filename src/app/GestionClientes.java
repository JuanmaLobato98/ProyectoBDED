package app;

import gestion.DBFileManager;
import gestion.DBManager;
import java.util.Scanner;


/**
 *Clase se encuentra el menu dentro del main donde funcionan todas las utilidades
 *del programa
 *
 * @author Juanma Lobato
 * @version 1.6 28/05/2022
 */
public class GestionClientes {

    public static void main(String[] args) {

    	System.out.println("Introduce los datos de la BD");
    	DBManager.loadDriver();
    	DBManager.connectServer();
    	DBManager.getBDs();
    	DBManager.getName();
        DBManager.connect();
        
        boolean salir = false;
        do {
            salir = menuPrincipal();
        } while (!salir);

        DBManager.close();

    }
    /**
     * Metodo que ejecuita el menu principal donde se alojan todas 
     * las opciones del programa
     * @return devuelve false mientras siga funcionando, true cuando para
     */
    public static boolean menuPrincipal() {
        System.out.println("");
        System.out.println("MENU PRINCIPAL");
        System.out.println("0. Salir");
        System.out.println("1. Cambiar base de datos");
        System.out.println("2. Imprimir tabla");
        System.out.println("3. Nueva tupla");
        System.out.println("4. Modificar tupla");
        System.out.println("5. Eliminar tupla");
        System.out.println("6. Exportar tabla");
        System.out.println("7. Crear tabla");
        System.out.println("8. Filtrar filas");
        System.out.println("9. Insertar desde fichero");
        System.out.println("10. Actualizar desde fichero");
        System.out.println("11. Borrar desde fichero");
       try {
    	   Scanner in = new Scanner(System.in);
           
           int opcion = pideInt("Elige una opcion: ");
           
           switch (opcion) {
           	case 0:
           		return true;
               case 1:
   	            	opcionCambiarBD();
                   return false;
               case 2:
                   opcionImprimirTabla();
                   return false;
               case 3:
                   opcionNuevoRegistro();
                   return false;
               case 4:
                   opcionModificarRegistro();
                   return false;
               case 5:
                   opcionEliminarRegistro();
                   return false;
               case 6:
               	opcionExportarTabla();
                   return false;
               case 7:
               	opcionCrearTabla();
                   return false;
               case 8:
               	opcionFiltrarFilas();
                   return false;
               case 9:
            	   opcionInsertarFichero();
                      return false;
               case 10:
            	   opcionActualizarFichero();
                     return false;
               case 11:
            	   opcionBorrarFichero();
                     return false;
               default:
                   System.out.println("Opcion elegida incorrecta");
                   return false;
           }
       }catch (Exception e) {
    	   e.printStackTrace();
    	   return false;
       }
        
        
    }
    
    /**
     * Metodo para pedir un entero al usuario
     * 
     * @param mensaje Mensaje que visualiza el usuario
     * @return devuelve el entero introducido por el usuario
     */
    public static int pideInt(String mensaje){
        
        while(true) {
            try {
                System.out.print(mensaje);
                Scanner in = new Scanner(System.in);
                int valor = in.nextInt();
                //in.nextLine();
                return valor;
            } catch (Exception e) {
                System.out.println("No has introducido un numero entero. Vuelve a intentarlo.");
            }
        }
    }
    
    /**
     * Metodo para pedir un String al usuario
     * 
     * @param mensaje Mensaje que visualiza el usuario
     * @return devuelve el String introducido por el usuario
     */
    public static String pideLinea(String mensaje){
        
        while(true) {
            try {
                System.out.print(mensaje);
                Scanner in = new Scanner(System.in);
                String linea = in.nextLine();
                return linea;
            } catch (Exception e) {
                System.out.println("No has introducido una cadena de texto. Vuelve a intentarlo.");
            }
        }
    }
    
    /**
     * Funcion que llama al Metodo para cambiar de base de datos
     */
    public static void opcionCambiarBD () {
    	DBManager.close();
    	DBManager.connectServer();
    	DBManager.getBDs();
    	DBManager.getName();
        DBManager.connect();
    }
    
    /**
     * Funcion que llama al Metodo que imprime una tabla 
     */
    public static void opcionImprimirTabla () {
    	Scanner in = new Scanner (System.in);
    	DBManager.printTablas();
    	
    	String tabla = pideLinea("Introduzca la tabla que desee mostrar: ");
    	System.out.println( DBManager.printTabla(tabla));
    }

    /**
     * Funcion que llama al Metodo para insertar un nuevo registro
     */
    public static void opcionNuevoRegistro() {
        Scanner in = new Scanner(System.in);
        DBManager.printTablas();
        System.out.println("Introduce en que tabla insertar:");
        
        String tabla = pideLinea("");

        boolean res = DBManager.insertColumn(tabla);

        if (res) {
            System.out.println("Cliente registrado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    /**
     * Funcion que llama al Metodo para modificar un registro
     */
    public static void opcionModificarRegistro() {
        Scanner in = new Scanner(System.in);

        DBManager.printTablas();
        String tabla = pideLinea("Indica la tabla donde modificar: ");
        
        

        // Registramos los cambios
        boolean res = DBManager.updateColumn(tabla);

        if (res) {
            System.out.println("Cliente modificado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    /**
     * Funcion que llama al Metodo para eliminar un registro de la base de datos
     */
    public static void opcionEliminarRegistro() {
        Scanner in = new Scanner(System.in);

        DBManager.printTablas();
        String tabla = pideLinea("Indica la tabla donde borrar: ");

        // Eliminamos el cliente
        boolean res = DBManager.deleteColumn(tabla);

        if (res) {
            System.out.println("Cliente eliminado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }
    
    /**
     * Funcion que llama al Metodo para exportar una tabla de la base de datos a un fichero
     */
    public static void opcionExportarTabla () {
    	Scanner in = new Scanner(System.in);

        DBManager.printTablas();
        String tabla = pideLinea("Indica la tabla que exportar: ");
        
        if(DBFileManager.exportarTabla(tabla)) {
        	System.out.println("-----------------------");
			System.out.println("Fichero creado!");
			System.out.println("-----------------------");
        }else {
        	System.out.println("Error al crear el fichero");
        }
    }
    
    /**
     * Funcion que llama al Metodo para crear una tabla en ua base de datos
     */
    public static void opcionCrearTabla () {
    	Scanner in = new Scanner (System.in);
    	
    	System.out.println("Introduce el nombre de la nueva tabla");
    	String nombre = in.next();
    	System.out.println("Introduce el numero de columnas tabla");
    	int columnas = in.nextInt();
    	
    	DBManager.crearTabla(nombre, columnas);

    }
    
    /**
     * Funcion que llama al Metodo para filtrar registros de una tabla de la BD
     */
    public static void opcionFiltrarFilas () {
    	Scanner in = new Scanner (System.in);
    	
    	System.out.println("Introduce el nombre de la tabla: ");
    	String tabla=in.next();
    	
    	System.out.println(DBManager.printColumnas(tabla));
    	
    	System.out.println("Introduce el nombre del campo a filtrar: ");
    	String campo=in.next();
    	System.out.println("Introduce el valor del campo: ");
    	String valor=in.next();
    	
    	DBManager.printTuplas(tabla, campo, valor);
    }
    
    /**
     * Funcion que llama al metodo para insertar registros desde un fichero
     */
    public static void opcionInsertarFichero () {
    	Scanner in = new Scanner (System.in);
    	
    	System.out.println("Introduce el nombre de la ruta del fichero: ");
    	String ruta=in.next();
    	
    	DBFileManager.insertarDeFichero(ruta);
    }
    
    /**
     * Funcion que llama al metodo para actualizar registros desde ujn fichero
     */
    public static void opcionActualizarFichero () {
    	Scanner in = new Scanner (System.in);
    	
    	System.out.println("Introduce el nombre de la ruta del fichero: ");
    	String ruta=in.next();
    	
    	DBFileManager.actualizarDeFichero(ruta);
    }
    
    /**
     * Funcion que llama al metodo para borrar registros desde un fichero
     */
    public static void opcionBorrarFichero () {
    	Scanner in = new Scanner (System.in);
    	
    	System.out.println("Introduce el nombre de la ruta del fichero: ");
    	String ruta=in.next();
    	
    	DBFileManager.borrarDeFichero(ruta);
    }
}
