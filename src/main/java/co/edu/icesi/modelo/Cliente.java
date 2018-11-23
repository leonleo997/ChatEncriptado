package co.edu.icesi.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import co.edu.icesi.utilities.AES;
import co.edu.icesi.utilities.DiffieHellman;
import co.edu.icesi.vista.InterfazPrincipal;

public class Cliente implements Runnable {

	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private String IPservidor;
	private int puerto;
	private Socket cliente;
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private SecretKey secret;
	private KeyAgreement agreement; 
	
	private String nickname;
	
	private InterfazPrincipal principal;

	public Cliente(String direccionIp, InterfazPrincipal principal, int puerto, String nickname) {
		this.principal=principal;
		IPservidor = direccionIp;
		this.puerto=puerto;
		this.nickname=nickname;
		
		KeyPair keys = DiffieHellman.generarKeys();
		privateKey = keys.getPrivate();
		publicKey = keys.getPublic();
	}


	public void procesarConexion() throws IOException {	
		byte[] mensaje=null;
		Object recibido=null;
		String mensajeDec ="";

			do {
				try {
					recibido = entrada.readObject();
					mensaje = (byte[]) recibido;
					mensajeDec = AES.desencriptar(mensaje, secret.getEncoded());
					mostrarMensaje(mensajeDec);
				} catch (ClassNotFoundException excepcionClaseNoEncontrada) {
					excepcionClaseNoEncontrada.printStackTrace();
				} 
				catch(ClassCastException e) {
					KeyPair keys = DiffieHellman.generarKeys(((PublicKey)recibido).getEncoded());
					privateKey = keys.getPrivate();
					publicKey = keys.getPublic();
					agreement = DiffieHellman.generateKeyAgreement(keys);
					secret= DiffieHellman.generarClaveSecretaComun(agreement,privateKey, ((PublicKey)recibido).getEncoded());
					enviarClavePublica(publicKey);
				}catch(Exception e) {
				}
				
			} while (true);
		
	}

	public void cerrarConexion() {
		try {
			salida.close();
			entrada.close();
			cliente.close();
		} catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		}
	}

	public void enviarClavePublica(PublicKey key) {
		try {
			salida.writeObject(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarDatos(String mensaje) {
		String mensajeParaEnviar =nickname+": " + mensaje+ "\n";
		try {
			byte[] mensajeEncriptado = AES.encriptar(mensajeParaEnviar.getBytes(), secret.getEncoded());
			salida.writeObject(mensajeEncriptado);
			salida.flush();
			mostrarMensaje(mensajeParaEnviar);
		} catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mostrarMensaje(String mensaje) {
		principal.mostrarMensaje(mensaje);
	}

	@Override
	public void run() {
		try {
			cliente = new Socket(IPservidor, puerto);
			
			salida = new ObjectOutputStream(cliente.getOutputStream());
			salida.flush();
			entrada = new ObjectInputStream(cliente.getInputStream());
			
			procesarConexion();
			principal.refresh();
		}catch(Exception e) {
		}
		finally {
			cerrarConexion();
		}
	}

}
