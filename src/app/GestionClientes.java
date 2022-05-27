package app;

import gestion.DBManager;
import java.util.Scanner;


/**
 *
 * @author Juanma Lobato
 */
public class GestionClientes {

    public static void main(String[] args) {

    	System.out.println("Introduce los datos de la BD");
    	DBManager.loadDriver();
        DBManager.connect();
        
        boolean salir = false;
        do {
            salir = menuPrincipal();
        } while (!salir);

        DBManager.close();

    }

    public static boolean menuPrincipal() {
        System.out.println("");
        System.out.println("MENU PRINCIPAL");
        System.out.println("0. Salir");
        System.out.println("1. ");
        System.out.println("2. Imprimir tabla");
        System.out.println("3. Nueva tupla");
        System.out.println("4. Modificar tupla");
        System.out.println("5. Eliminar tupla");
        System.out.println("6. Exportar tabla"); 
        
        Scanner in = new Scanner(System.in);
            
        int opcion = pideInt("Elige una opcion: ");
        
        switch (opcion) {
        	case 0:
        		return true;
            case 1:
	            //no hace nada aun
                return false;
            case 2:
                opcionImprimirTabla();
                return false;
            case 3:
                opcionNuevoCliente();
                return false;
            case 4:
                opcionModificarCliente();
                return false;
            case 5:
                opcionEliminarCliente();
                return false;
            case 6:
            	opcionExportarTabla();
                return false;
            default:
                System.out.println("Opcion elegida incorrecta");
                return false;
        }
        
    }
    
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
    
    public static void opcionImprimirTabla () {
    	Scanner in = new Scanner (System.in);
    	DBManager.printTablas();
    	
    	String tabla = pideLinea("Introduzca la tabla que desee mostrar: ");
    	System.out.println( DBManager.printTabla(tabla));
    }


    public static void opcionNuevoCliente() {
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

    public static void opcionModificarCliente() {
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

    public static void opcionEliminarCliente() {
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
    
    public static void opcionExportarTabla () {
    	Scanner in = new Scanner(System.in);

        DBManager.printTablas();
        String tabla = pideLinea("Indica la tabla que exportar: ");
        
        if(DBManager.exportarTabla(tabla)) {
        	System.out.println("-----------------------");
			System.out.println("Fichero creado!");
			System.out.println("-----------------------");
        }else {
        	System.out.println("Error al crear el fichero");
        }
    }
    
    /*public static void opcionClientesDireccion() {
    	Scanner in = new Scanner(System.in);
    	
    	String direccion = pideLinea("Indica la direccion que desee buscar: ");
    	
    	System.out.println("Listado de clientes de "+direccion);
    	DBManager.getClientesDeDireccion(direccion);
    }*/
}
