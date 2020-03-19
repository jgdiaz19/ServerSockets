package ClientSocket;

import java.net.*;
import java.io.*;
import java.awt.Desktop;

public class ClientSocket {

    //Atributos para envio y recepcion de datos
    BufferedReader myClientInput;
    DataOutputStream myServerDataOutput;
    DataInputStream myServerDataInput;
    
    //atributos para manejar entrada de archivos
    ObjectInputStream myFileInputStream = null;
    ObjectOutputStream myFileOutputStream = null;
    boolean myFlagExchange = false;
    int fileLength = 0;
    String myFileRequest = null;

    //Metodo iniciar el servidor cliente
    public void iniciar() {
        try {
            //Establecemos conexion con servidor mediante socket
            Socket myClientSocket = new Socket("192.168.0.9", 5433); //<- Cambie ip y puerto a su servidor

            //Habilitamos canales de comunicacion de salida al servidor (del cliente --> al servidor)
            myClientInput = new BufferedReader(new InputStreamReader(System.in)); // Del teclad a la consola
            myServerDataOutput = new DataOutputStream(myClientSocket.getOutputStream()); //Del cliente al servidor

            //Habilitamos canales de comunicacion de entrada (del servidor --> al cliente)
            myServerDataInput = new DataInputStream(myClientSocket.getInputStream());

            //Obtenemos los datos del archivo para descarga
            logClient("Teclee el nombre del archivo:");
            String fileRequest = myClientInput.readLine();

            //Enviamos peticion a servidor con el nombre del archivo y leemos respuesta
            myServerDataOutput.writeUTF(fileRequest);
            myFlagExchange = myServerDataInput.readBoolean(); //previamente se establecio en protocolo respuesta boleana

            //Si archivo esta disponible, establecemos una segunda comunicacion con servidor para comenzar descarga
            if (myFlagExchange) {
                //Desplegamos informacion segun protocolo (si archivo existe, nombre y tamaño)
                logClient("[Servidor]: " + myServerDataInput.readUTF());
                logClient("[Servidor]: " + myServerDataInput.readUTF());

                //obtenemos informacion para descarga de archivo
                fileLength = myServerDataInput.readInt(); //tamaño
                myFileRequest = myServerDataInput.readUTF(); //nombre

                //Habilitamos los mecanismos de deserializacion y alamacenamiento del archivo
                FileOutputStream myFileOutputStream = new FileOutputStream("C:\\Users\\Jorge\\Downloads\\Recibidos\\" + myFileRequest);
                BufferedOutputStream myBufferOutput = new BufferedOutputStream(myFileOutputStream);
                BufferedInputStream myBufferInput = new BufferedInputStream(myClientSocket.getInputStream());
                
                // Creamos el array de bytes para leer los datos del archivo
                byte[] buffer = new byte[fileLength];

                // Obtenemos el archivo mediante la lectura de bytes enviados
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = (byte) myBufferInput.read();
                }
                // Escribimos el archivo 
                myBufferOutput.write(buffer);
                logClient("El archivo se recibio correctamente");

                //Mostramos archivo descargado
                openFileDownloaded();
                //Cerramos flujos
                myBufferOutput.flush();
                myBufferOutput.flush();
                myBufferInput.close();
                myBufferOutput.close();
                myClientSocket.close();

            }
            //Si el archivo no esta en el repo lo indicamos 
            else {
                logClient("[Servidor]: " + myServerDataInput.readUTF());
                myClientSocket.close();
            }
        } catch (Exception e) {
            System.out.println("error " + e.getMessage() + " cliente");
        }
    }

    public static void main(String args[]) {
        //Creamos cliente y lo iniciamos
        ClientSocket myClient = new ClientSocket();
        myClient.iniciar();
    }

    public static void logClient(String txt) {
        System.out.println(txt);
    }

    public void openFileDownloaded() {
        try {
            //cambiar la direccion para cada computadora
            File myFile = new File("C:\\Users\\Jorge\\Downloads\\Recibidos\\" + myFileRequest);
            Desktop.getDesktop().open(myFile);

        } catch (IOException ex) {

            System.out.println(ex);
        }
    }

}
