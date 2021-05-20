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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
            System.out.println("Error al iniciar el servidor.");
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

                ServerSideConnection ssc = new ServerSideConnection(socket, dos, dis, cantMaxCon, nuevoJugador);

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

                ssc.enviarMensaje(cantMaxCon);
                //Enviar a los servidores la cantidad maxima de conexiones permitidas y la cantidas de clientes conectados
                for (ServerSideConnection cliente : clientes) {
                    cliente.enviarMensaje(clientesConectados.size());
                }
                if (clientesConectados.size() == cantMaxCon) {
                    break;
                }
            }
            //Enviar a los servidores la cantidad maxima de conexiones permitidas y la cantidas de clientes conectados
            for (ServerSideConnection cliente : clientes) {
                //cliente.enviarMensaje(cantMaxCon);
                cliente.enviarMensaje(clientesConectados.size());
            }

            System.out.println("La cantidad maxima de clientes conectados se ha alcanzado.");
            comenzarJuego();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void comenzarJuego() {
        //   Collections.shuffle(clientes);
        for (int i = 0; i < clientes.size(); i++) {
            clientes.get(i).setTurno(i + 1);
        }
        
        for (ServerSideConnection cliente : clientes) {
            cliente.enviarMensaje(clientes.size());
            cliente.enviarMensaje(cliente.getTurno());
        }

        while (true) {

            for (int i = 0; i < clientes.size(); i++) {
                //Enviar el numero activo a todos los clientes
                for (ServerSideConnection cliente : clientes) {
                    cliente.enviarMensaje(i+1);
                }
                //Muestra nombre del jugador en turno
                System.out.println("Turno del jugador en turno:" + clientes.get(i).getNombre());
                //Espera recibir una jugada del jugador en turno para enviarla a todos los demas jugadores
                 Object object=null ;

                      object = clientes.get(i).recibirMensaje();
                   
                for (ServerSideConnection cliente1 : clientes) {
                    cliente1.enviarMensaje(object);
                }
            }
        }
    }

    public static void main(String args[]) throws IOException {
        Servidor s = new Servidor();
        s.aceptarConexiones();
    }

}
