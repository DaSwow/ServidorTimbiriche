package servidor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidor.Servidor;

    //////////////////
    //////////////////
    //////////////////
    public class ServerSideConnection implements Runnable {

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

        public Object recibirMensaje() {
            try {
                return dis.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }