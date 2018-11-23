package co.edu.icesi.modelo;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import co.edu.icesi.utilities.AES;
import co.edu.icesi.utilities.DiffieHellman;
import co.edu.icesi.vista.InterfazPrincipal;



public class Servidor implements Runnable {

	public static final int backlog = 100;

	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private ServerSocket servidor;
	private Socket conexion;
	private String nickname;

	private InterfazPrincipal principal;

	private PrivateKey privateKey;
	private PublicKey publicKey;
	private SecretKey secret;
	private KeyAgreement agreement;

	public Servidor(InterfazPrincipal principal, int puerto, String nickname) {
		this.principal = principal;
		try {
			servidor = new ServerSocket(puerto, backlog);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.nickname=nickname;
		
		KeyPair keys = DiffieHellman.generarKeys();
		privateKey = keys.getPrivate();
		publicKey = keys.getPublic();
		agreement = DiffieHellman.generateKeyAgreement(keys);
	}


	private void procesarConexion() throws IOException {
		byte[] mensaje = null;
		String mensajeDec = "";
		enviarClavePublica(publicKey);
		Object recibido = null;
		do {
			try {
				recibido = entrada.readObject();
				mensaje = (byte[]) recibido;
				mensajeDec = AES.desencriptar(mensaje, secret.getEncoded());
				mostrarMensaje(mensajeDec);
			} catch (ClassNotFoundException excepcionClaseNoEncontrada) {
				excepcionClaseNoEncontrada.printStackTrace();
			} catch (ClassCastException e) {
				secret = DiffieHellman.generarClaveSecretaComun(agreement, privateKey,
						((PublicKey) recibido).getEncoded());
			} catch (Exception e) {
			}
		} while (true);

	}

	private void cerrarConexion() {
		try {
			salida.close();
			entrada.close();
			conexion.close();
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

		String mensajeParaEnviar = nickname+": "+ mensaje + "\n";
		try {
			byte[] mensajeEncriptado = AES.encriptar(mensajeParaEnviar.getBytes(), secret.getEncoded());
			salida.writeObject(mensajeEncriptado);
			salida.flush();
			mostrarMensaje(mensajeParaEnviar);
		} catch (Exception e) {
			mostrarMensaje("Error al enviar mensaje, verifique que el cliente se encuentre conectado.\n");
		}
	}

	public void mostrarMensaje(String mensaje) {
		principal.mostrarMensaje(mensaje);
	}

	@Override
	public void run() {
		try {
			while (true) {
				try {
					conexion = servidor.accept();
					salida = new ObjectOutputStream(conexion.getOutputStream());
					salida.flush();
					entrada = new ObjectInputStream(conexion.getInputStream());
					procesarConexion();
				} catch (EOFException excepcionEOF) {
				} finally {
					cerrarConexion();
				}
			}
		} catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		}

	}

}
