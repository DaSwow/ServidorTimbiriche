package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//////////////////
//////////////////
//////////////////
public class ServerSideConnection implements Serializable {

    private Socket socket;
    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private int cantidadMax;
    private int turno;
    private String nombreJugador;

    public ServerSideConnection(Socket s, ObjectOutputStream dos, ObjectInputStream dis, int cant, String nombreJugador) {
        socket = s;
        cantidadMax = cant;
        this.dos = dos;
        this.dis = dis;
        this.nombreJugador = nombreJugador;
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

    public Object recibirMensaje() {
        try {
            Object object = dis.readObject();
            
            return object;

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getNombre() {
        return nombreJugador;
    }

    public void setTurno(int i) {
        turno = i;
    }

    public int getTurno() {
        return turno;
    }

}
