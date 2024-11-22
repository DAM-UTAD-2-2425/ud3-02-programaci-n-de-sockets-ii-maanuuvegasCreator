package servidor;

/**
 * @author manuel vegas
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Complementa esta clase para que acepte conexiones TCP con clientes para
 * recibir un boleto, generar la respuesta y finalizar la sesion
 */
public class ServidorTCP {
	/**
	 * Atributos de la clase de los cuales he añadido, la entrada, la salida, el
	 * socket del servidor y del cliente
	 */
	private String[] respuesta;
	private int[] combinacion;
	private int reintegro;
	private int complementario;
	private ServerSocket serverSocket;
	private Socket cliente;
	private BufferedReader in;
	private PrintWriter out;
	// Almaceno la última combinación recibida
	private int[] clienteCombinada;

	/**
	 * Constructor
	 */
	public ServidorTCP(int puerto) {
		this.respuesta = new String[9];
		this.respuesta[0] = "Boleto inválido - Números repetidos";
		this.respuesta[1] = "Boleto inválido - Números incorrectos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";

		try {
			// Creo el socket del servidor y espero la conexión del cliente
			this.serverSocket = new ServerSocket(puerto);
			System.out.println("Esperando conexión del cliente...");
			cliente = this.serverSocket.accept();
			System.out.println("Cliente conectado");

			// Inicializo flujo de entrada y salida
			in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			out = new PrintWriter(cliente.getOutputStream(), true);

			// Genero y muestro la combinación
			generarCombinacion();
			imprimirCombinacion();
		} catch (IOException e) {
			System.err.println("Error al iniciar el servidor: " + e.getMessage());
		}
	}

	/**
	 * @return Debe leer la combinacion de numeros que le envia el cliente
	 */
	public String leerCombinacion() {
		// Creo la entrada para poder luego usarlo en el método
		String entrada;
		try {
			// Leo la linea de entrada
			entrada = in.readLine();
			/**
			 * Ahora he creado un condicional donde voy a verificar que la combinada que ha
			 * puesto el Cliente,no debe ser ni nula, es decir que este vacía, y tampoco que
			 * la entrada sea igual a FIN, ya que eso nos lleva a la clase de Primitiva
			 * servidor donde se verifica en el do-while si coinciden
			 */
			if (entrada != null && !"FIN".equals(entrada)) {
				/**
				 * Añado un espacio entre los elementos que en este caso son cada número de la
				 * combinada, para asi verificarlo cada uno en una línea
				 */
				String[] elementos = entrada.split(" ");
				clienteCombinada = new int[elementos.length];
				int indice = 0;
				for (String elemento : elementos) {
					clienteCombinada[indice++] = Integer.parseInt(elemento);
				}
			}
			return entrada;
		} catch (Exception excepcion) {
			return null;
		}
	}

	/**
	 * @return Debe devolver una de las posibles respuestas configuradas
	 */
	public String comprobarBoleto() {
		// Verifico si la combinación del cliente es válida
		if (clienteCombinada == null || clienteCombinada.length != 6) {
			// Respuesta por error en la combinación
			return respuesta[8];
		}

		// Compruebo si hay números repetidos en la combinación
		Set<Integer> numerosUnicos = new TreeSet<>();
		for (int numero : clienteCombinada) {
			if (!numerosUnicos.add(numero)) {
				// Boleto inválido - Números repetidos
				return respuesta[0];
			}
		}
		/**
		 * Creo estas variables para contar aciertos y verificar el complementario y
		 * reintegro
		 */
		int aciertos = 0;
		/**
		 * Inicializo estas varable booleanas en false para que cuando se recorran los
		 * número si coincide que es reintegro y complementaria, se pondrán en true
		 */
		boolean complementarioAcertado = false;
		boolean reintegroAcertado = false;

		// Compruebo los números introducidos por el cliente
		for (int i = 0; i < clienteCombinada.length; i++) {
			int numero = clienteCombinada[i];

			// Valido si el número está dentro del rango y no se repite
			if (numero < 1 || numero > 49) {
				// Respuesta Números incorrectos
				return respuesta[1];
			}

			// Compruebo si el número está en la combinación ganadora
			for (int j = 0; j < combinacion.length; j++) {
				if (combinacion[j] == numero) {
					// Si coincide, incremento los aciertos
					aciertos++;
				}
			}
			// si el número es el complementario o el reintegro
			if (numero == complementario) {
				complementarioAcertado = true;
			}
			if (numero == reintegro) {
				reintegroAcertado = true;
			}
		}

		// Determino la respuesta en base a los aciertos usando un switch
		switch (aciertos) {
		case 6:
			// Todos acertados
			return respuesta[2];
		case 5:
			if (complementarioAcertado) {
				// 5 acertados + complementario
				return respuesta[3];
			} else {
				// 5 acertados
				return respuesta[4];
			}
		case 4:
			// 4 acertados
			return respuesta[5];
		case 3:
			// 3 acertados
			return respuesta[6];
		default:
			if (reintegroAcertado) {
				// Reintegro acertado
				return respuesta[7];
			} else {
				// Ningún acierto
				return respuesta[8];
			}
		}
	}

	/**
	 * @param respuesta se debe enviar al ciente
	 */
	public void enviarRespuesta(String respuesta) {
		// Devuelvo al cliente la respuesta cogida del método anterior
		out.println(respuesta);
	}

	/**
	 * Cierra el servidor
	 */
	public void finSesion() {
		// Finalizo todo lo que estaba lanzado
		try {
			// He añadido este sysou para mostrar en pantalla cuando se cierra la conexión
			System.out.println("Conexión del servidor cerrrada");
			out.close();
			in.close();
			cliente.close();
			serverSocket.close();
		} catch (IOException e) {
			// Si no es así muestro un mensaje de error
			System.out.println("Error al cerrar el servidor: " + e.getMessage());
		}
	}

	/**
	 * Metodo que genera una combinacion. NO MODIFICAR
	 */
	private void generarCombinacion() {
		Set<Integer> numeros = new TreeSet<Integer>();
		Random aleatorio = new Random();
		while (numeros.size() < 6) {
			numeros.add(aleatorio.nextInt(49) + 1);
		}
		int i = 0;
		this.combinacion = new int[6];
		for (Integer elto : numeros) {
			this.combinacion[i++] = elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}

	/**
	 * Metodo que saca por consola del servidor la combinacion
	 */
	private void imprimirCombinacion() {
		System.out.print("Combinaci�n ganadora: ");
		for (Integer elto : this.combinacion)
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}

}
