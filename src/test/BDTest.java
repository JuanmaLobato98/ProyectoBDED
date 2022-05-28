package test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gestion.DBManager;

class BDTest {

	@Test
	public void testConnect () {
		boolean resultado =DBManager.connect();
		boolean esperado = false; //va a dar false porque po si solo el nombre de la bd es null y no se deberia conectar
		assertEquals(esperado, resultado);
	}
	
	@Test
	public void testConnectServer () {
		boolean resultado =DBManager.connectServer();
		boolean esperado = true; //si introduces la direccion correcta deberia dar true
		assertEquals(esperado, resultado);
	}
	
	@Test
	public void testPrimaryKey () {
    	DBManager.loadDriver();
    	DBManager.connectServer();
    	DBManager.getBDs();
    	DBManager.getName();
        DBManager.connect();//dando por hecho que nos conectamos correctamente
        String resultado = DBManager.getPrimaryKey("clientes");
        String esperado = "dni";//el resultado esperado 
        assertEquals(esperado, resultado);
	}

}
