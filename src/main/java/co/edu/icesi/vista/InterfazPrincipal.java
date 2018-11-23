package co.edu.icesi.vista;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import co.edu.icesi.modelo.Cliente;
import co.edu.icesi.modelo.Servidor;

public class InterfazPrincipal  extends JFrame {

	private static final long serialVersionUID = -5070643191804052892L;
	
	// Cliente y servidor
	private Cliente cliente;
	private Servidor servidor;
	
	// Conexion
	private JTextField direccionIP;
	private JTextField puerto;
	private JTextField nickname;
	private JButton btnConectar;
	private JButton iniciarServidor;

	// Mensajes
	private JTextArea textArea;
	private JTextField mensaje;
	private JButton btnEnviar;

	public InterfazPrincipal() {
		
		cliente =null;
		servidor=null;
		
		this.setTitle("ChatCrypt");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setResizable(false);
		this.setPreferredSize(new Dimension(500, 300));

		JPanel content= new JPanel();
		content.setBounds(0, 0, 500, 300);
		content.setLayout(null);
		content.setVisible(false);
		this.add(content);
		
		// Agregar campo para direccion IP
		JLabel dirIP = new JLabel("Direccion IP:");
		dirIP.setBounds(10, 10, 150, 30);
		this.add(dirIP);
		direccionIP = new JTextField();
		direccionIP.setBounds(190, 10, 200, 25);
		this.add(direccionIP);

		// Agregar campo para puerto
		JLabel port = new JLabel("Puerto:");
		port.setBounds(10, 40, 200, 25);
		this.add(port);
		puerto = new JTextField();
		puerto.setBounds(190, 40, 200, 25);
		this.add(puerto);
		
		JLabel name = new JLabel("Nickname:");
		name.setBounds(10, 70, 200, 25);
		this.add(name);
		nickname = new JTextField();
		nickname.setBounds(190, 70, 200, 25);
		this.add(nickname);


		// agregar boton conectar
		btnConectar = new JButton("Conectar");
		btnConectar.setBounds(10, 100, 150, 25);
		btnConectar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				content.setVisible(true);
				btnConectar.setEnabled(false);
				puerto.setEnabled(false);
				iniciarServidor.setEnabled(false);
				btnEnviar.setEnabled(true);
				int puertoValue=Integer.parseInt(puerto.getText());
				cliente = new Cliente(direccionIP.getText(), getChat(),puertoValue,nickname.getText());
				Thread hilo = new Thread(cliente);
				direccionIP.setVisible(false);
				btnConectar.setVisible(false);
				iniciarServidor.setVisible(false);
				hilo.start();
			}
		});
		this.add(btnConectar);
		
		// agregar boton iniciar servidor
		iniciarServidor = new JButton("Iniciar Servidor");
		iniciarServidor.setBounds(170, 100, 150, 25);
		iniciarServidor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				content.setVisible(true);
				btnConectar.setEnabled(false);
				try {
					direccionIP.setText((InetAddress.getLocalHost()+"").split("/")[1]);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				direccionIP.setEnabled(false);
				puerto.setEnabled(false);
				int puertoValue=Integer.parseInt(puerto.getText());
				servidor = new Servidor(getChat(),puertoValue,nickname.getText());
				Thread hilo = new Thread(servidor);
				btnEnviar.setEnabled(true);
				iniciarServidor.setEnabled(false);
				direccionIP.setVisible(false);
				btnConectar.setVisible(false);
				iniciarServidor.setVisible(false);
				hilo.start();
			}
		});
		this.add(iniciarServidor);

		
		
		
		// Agregar campo mensaje
		mensaje = new JTextField();
		mensaje.setBounds(10, 260, 360, 25);
		content.add(mensaje);

		// Agregar boton enviar
		btnEnviar = new JButton("Enviar");
		btnEnviar.setBounds(390, 260, 100, 25);
		btnEnviar.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if(servidor!=null){
					servidor.enviarDatos(mensaje.getText());
					mensaje.setText("");
				}
				else if (cliente!=null) {
					cliente.enviarDatos(mensaje.getText());
					mensaje.setText("");
				}
			}
		});
		btnEnviar.setEnabled(false);
		content.add(btnEnviar);
		this.pack();
		
		//Agregar text area
		textArea = new JTextArea();
		JScrollPane pane = new JScrollPane(textArea);
		pane.setBounds(10,10,480,250);
		content.add(pane);
	}
	
	public void refresh() {
		direccionIP.setEnabled(true);
		direccionIP.setText("");
		puerto.setText("");
		puerto.setEnabled(true);
		btnEnviar.setEnabled(false);
		iniciarServidor.setEnabled(true);
		btnConectar.setEnabled(true);
		servidor=null;
		cliente=null;
	}
	
	private InterfazPrincipal getChat() {
		return this;
	}
	
	public void mostrarMensaje(String mensaje) {
		textArea.append(mensaje);
	}

	public static void main(String[] args) {
		InterfazPrincipal interfaz = new InterfazPrincipal();
		interfaz.setVisible(true);
	}
}
