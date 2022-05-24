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
        System.out.println("1. Imprimir tabla");
        System.out.println("2. Imprimir tabla");
        System.out.println("3. Nuevo cliente");
        System.out.println("4. Modificar cliente");
        System.out.println("5. Eliminar cliente");
        System.out.println("6. Buscar clientes por direccion"); //procedimiento almacenado
        
        Scanner in = new Scanner(System.in);
            
        int opcion = pideInt("Elige una opcion: ");
        
        switch (opcion) {
        	case 0:
        		return true;
            case 1:
                
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
            	opcionClientesDireccion();
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
    	DBManager.printTabla(tabla);
    }

    public static void opcionMostrarClientes() {
        System.out.println("Listado de Clientes:");
        DBManager.printTablaClientes();
    }

    public static void opcionNuevoCliente() {
        Scanner in = new Scanner(System.in);

        System.out.println("Introduce los datos del nuevo cliente:");
        String nombre = pideLinea("Nombre: ");
        String direccion = pideLinea("Direccion: ");

        boolean res = DBManager.insertCliente(nombre, direccion);

        if (res) {
            System.out.println("Cliente registrado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    public static void opcionModificarCliente() {
        Scanner in = new Scanner(System.in);

        int id = pideInt("Indica el id del cliente a modificar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Mostramos datos del cliente a modificar
        DBManager.printCliente(id);

        // Solicitamos los nuevos datos
        String nombre = pideLinea("Nuevo nombre: ");
        String direccion = pideLinea("Nueva direccion: ");

        // Registramos los cambios
        boolean res = DBManager.updateCliente(id, nombre, direccion);

        if (res) {
            System.out.println("Cliente modificado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }

    public static void opcionEliminarCliente() {
        Scanner in = new Scanner(System.in);

        int id = pideInt("Indica el id del cliente a eliminar: ");

        // Comprobamos si existe el cliente
        if (!DBManager.existsCliente(id)) {
            System.out.println("El cliente " + id + " no existe.");
            return;
        }

        // Eliminamos el cliente
        boolean res = DBManager.deleteCliente(id);

        if (res) {
            System.out.println("Cliente eliminado correctamente");
        } else {
            System.out.println("Error :(");
        }
    }
    
    public static void opcionClientesDireccion() {
    	Scanner in = new Scanner(System.in);
    	
    	String direccion = pideLinea("Indica la direccion que desee buscar: ");
    	
    	System.out.println("Listado de clientes de "+direccion);
    	DBManager.getClientesDeDireccion(direccion);
    }
}
