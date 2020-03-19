package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSockets {

    public static void main(String[] args) throws IOException, InterruptedException {
        //Clase para implementar un servidor de sockets
        ServerSocket mySocketServer = null;
        //Clase para implementar canal de comunicacion "socket"
        Socket mySocket = null;
        //Tarera que se debe ejecutar en servidor
        TaskServer myTaskinServer;
        //Identificador de conexiones
        int id = 0;

        try {
            //iniciamos servidor de sockets
            mySocketServer = new ServerSocket(5433);
            do {
                TaskServer.logServer("\n*** SERVIDOR INICIADO ****\n");
                TaskServer.logServer("Socket escuchando en puerto 5433");
                mySocket = mySocketServer.accept();
                id++;
                TaskServer.logServer("\nSe conecto el cliente No." + id + " desde la IP: " + mySocket.getInetAddress());
                TaskServer.logServer("**************************************************");

                myTaskinServer = new TaskServer(mySocket, id);

                myTaskinServer.start();
                if (myTaskinServer.isAlive() == false) {
                    mySocketServer.close();
                }

            } while (myTaskinServer.isAlive());

        } catch (IOException e) {
            TaskServer.logServer(e.getMessage() + " [servidor]");
            System.exit(3);
        } finally {
            try {
                mySocketServer.close();
            } catch (IOException e) {
                TaskServer.logServer(e.getMessage() + " [servidor]");
                System.exit(4);
            }
        }


    }
}


