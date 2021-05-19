/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carls
 */
public class Servidor {

    //Cantidad maxima de clientes conectados
    private int cantMaxCon = 0;
    //Cantidad actual de clientes conectados
    private ArrayList<String> clientesConectados = new ArrayList();
    //Servidor
    private ServerSocket serverSocket;
    //Puerto en el que corre el juego
    private final int port = 9876;

    //Los posibles jugadores
    private ArrayList<ServerSideConnection> clientes = new ArrayList<>();

    private ServerSideConnection jugador1;
    private ServerSideConnection jugador2;
    private ServerSideConnection jugador3;
    private ServerSideConnection jugador4;

    public Servidor() {
        System.out.println("Servidor encendido.");

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error al construir el servidor.");
        }
    }

    public void aceptarConexiones() {
        try {
            System.out.println("Esperando conexiones");

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());

                if (cantMaxCon == 0 && clientesConectados.isEmpty()) {
                    cantMaxCon = (Integer) dis.readObject();

                    System.out.println("Cantidad maxima de jugadores: " + cantMaxCon);
                }
                String nuevoJugador = (String) dis.readObject();
                clientesConectados.add(nuevoJugador);
                System.out.println("Jugador " + nuevoJugador + " agregado");

                ServerSideConnection ssc = new ServerSideConnection(socket, dos, dis, clientesConectados.size(), nuevoJugador);

                //Agreagr los disintos clientes a una lista para poder enviarles mensajes en cualquier momento 
                //y que no se borren al entrar una nueva conexion
                if (jugador1 == null) {
                    jugador1 = ssc;
                    clientes.add(jugador1);
                } else if (jugador2 == null) {
                    jugador2 = ssc;
                    clientes.add(jugador2);
                } else if (jugador3 == null) {
                    jugador3 = ssc;
                    clientes.add(jugador3);
                } else if (jugador4 == null) {
                    jugador4 = ssc;
                    clientes.add(jugador4);
                }

                //Enviar a los servidores la cantidad maxima de conexiones permitidas y la cantidas de clientes conectados
                for (ServerSideConnection cliente : clientes) {
                    cliente.enviarMensaje(cantMaxCon);
                    cliente.enviarMensaje(clientesConectados.size());
                }

                Thread hilo = new Thread(ssc);
                hilo.start();
                if (clientesConectados.size() == cantMaxCon) {

                    break;
                }

            }
            //Enviar a los servidores la cantidad maxima de conexiones permitidas y la cantidas de clientes conectados
            for (ServerSideConnection cliente : clientes) {
                cliente.enviarMensaje(cantMaxCon);
                cliente.enviarMensaje(clientesConectados.size());
            }
            
            System.out.println("La cantidad maxima de clientes conectados se ha alcanzado.");
         
            comienzoJuego();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void comienzoJuego() {
        //Se establecen los turnos
        Collections.shuffle(clientes);
        for (ServerSideConnection cliente : clientes) {
            
        }

    }

    public static void main(String args[]) throws IOException {
        Servidor s = new Servidor();
        s.aceptarConexiones();
    }

    //////////////////
    //////////////////
    //////////////////
    private class ServerSideConnection implements Runnable {

        private Socket socket;
        private ObjectOutputStream dos;
        private ObjectInputStream dis;
        private int playerID;
        private String nombreJugador;

        public ServerSideConnection(Socket s, ObjectOutputStream dos, ObjectInputStream dis, int playerID, String nombreJugador) {
            socket = s;
            this.playerID = playerID;
            this.dos = dos;
            this.dis = dis;
            this.nombreJugador = nombreJugador;
        }

        @Override
        public void run() {
            try {
                dos.writeObject(playerID);
                dos.flush();
                while (true) {

                }
            } catch (IOException e) {
                System.out.println("Excepcion IO de parte de run() SSC");
            }
        }

        public Socket getSocket() {
            return socket;
        }

        public void enviarMensaje(Object object) {
            try {
                dos.writeObject(object);
                dos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void limpiarSalida(){
            try {
                dos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
