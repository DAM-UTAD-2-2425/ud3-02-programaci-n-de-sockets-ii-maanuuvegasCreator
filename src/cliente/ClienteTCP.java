package cliente;

/**
 * @author manuel vegas
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TODO: Complementa esta clase para que genere la conexi�n TCP con el servidor
 * para enviar un boleto, recibir la respuesta y finalizar la sesion
 */
public class ClienteTCP {
	/**
	 * Declaro los Atributos de la clase, ip y puerto me los dabas tú para poder
	 * hacer la conexión, además yo he creado para la entrada, la salida y el socket
	 */
	private String ip;
	private int puerto;
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;

	/**
	 * Constructor
	 */
	public ClienteTCP(String ip, int puerto) {
		this.ip = ip;
		this.puerto = puerto;
		// Inicializo la conexión
		try {
			socket = new Socket(ip, puerto);
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Si falla, se imprime un mesnsaje de error
		} catch (Exception e) {
			System.out.println("Error al conectar con el servidor" + e.getMessage());
		}

	}

	/**
	 * @param combinacion que se desea enviar
	 * @return respuesta del servidor con la respuesta del boleto
	 */
	public String comprobarBoleto(int[] combinacion) {
		// Inicialiazo la respuesta vacía
		String respuesta = "";

		try {
			// Convierto la combinación a una cadena separada por espacios
			for (int numero : combinacion) {
				// Pongo un espacio entre números, que hará que el servidor pueda leer un número
				// por linea
				respuesta += numero + " ";
			}

			// Envio la respuesta al srvidor
			output.println(respuesta);

			// Almaceno la respuesta del servidor en la variable respuesta
			respuesta = input.readLine();
		} catch (IOException e) {
			// En caso de error de comunicación, asigno el mensaje de error a respuesta
			respuesta = "Error al comunicarse con el servidor: " + e.getMessage();
		}
		/**
		 * Devuelvo la respuesta, esto lo he incluido ya que nos lo dabas en el código
		 * pero lo que hago aqui es devolver o la espuesta del servidor o el mensaje del
		 * error
		 */
		return respuesta;
	}
	/**
	 * Sirve para finalizar la la conexi�n de Cliente y Servidor
	 */
	public void finSesion() {
		try {
			// He añadido este sysou para mostrar en pantalla cuando se cierra la conexión
			System.out.println("Conexión del Cliente cerrada");
			/**
			 * Si el cliente dice que no quiere jugar otra vez, se compara con el FIN del
			 * servidor y se acaba el programa
			 */
			output.println("FIN");
			// Cierro el socket
			socket.close();
		} catch (Exception e) {
			System.out.println("Error al cerrar la conexión: " + e.getMessage());
		}

	}

}
