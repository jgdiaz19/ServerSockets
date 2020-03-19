package Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * TaskServer
 */
 public class TaskServer extends Thread  {

    //Propiedades de la clase
    int id;
    Socket s = null;
    
    //Atributos para manejar salida y entrada de texto
     
    ObjectInputStream   myServerInputStream     = null;
    ObjectOutputStream  myServerOutputStream    = null;
    DataInputStream     myClientInputStream     = null;
    DataOutputStream    myClientOutputStream    = null;

   //Atributos para manejo de archivos y tiempo de procesamiento
    File myServerFile   = null;
    long startTime      = 0;
    long endTime        = 0;
    long totalTime      = 0;
    long initialTime    = 0;

    //Constructor de la clase, aqui se liga la tarea al socket
    public TaskServer (Socket socket, int id) {
        this.s = socket;
        this.id = id;
    }

    //Verifica que el archvio exista
    boolean validarArchivo(String nombre) {
        myServerFile = new File("F:\\Repo\\" + nombre);
        if (myServerFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    //Ejecucion de la tarea: despachar el archivo solicitado al servidor
    public void run() {
        try {
            myClientInputStream     = new DataInputStream(s.getInputStream());
            myClientOutputStream    = new DataOutputStream(s.getOutputStream());

            String nombreArchivo = myClientInputStream.readUTF();

            if (validarArchivo(nombreArchivo)) {
                startTime = (System.currentTimeMillis() - this.initialTime);

                System.out.println("El cliente: " + id + " comienza la transferencia EN EL TIEMPO: "
                                    + (System.currentTimeMillis() - this.initialTime)
                                    + " milisegundos");

                myClientOutputStream.writeBoolean(true);
                myClientOutputStream.writeUTF(" El archivo: " + nombreArchivo + " esta disponible en el servidor");
                
                myClientOutputStream.writeUTF(" Tamaño del archivo:" + (myServerFile.length() / 1024) + 
                                              " KB | Nombre: " + myServerFile.getName());

                //myClientOutputStream.writeUTF(" Comienza la descarga..........");

                myClientOutputStream.writeInt((int) myServerFile.length());
                myClientOutputStream.writeUTF(nombreArchivo);

                logServer("Enviando archivo: " + nombreArchivo + " a " + s.getInetAddress());
                
                FileInputStream entrada = new FileInputStream(myServerFile);

                BufferedInputStream leerArch = new BufferedInputStream(entrada);
                // Creamos el flujo de salida 
                BufferedOutputStream salida = new BufferedOutputStream(s.getOutputStream());

                // Creamos un array de tipo byte 
                byte[] arreglo = new byte[(int) myServerFile.length()];

                // Leemos el archivo y lo introducimos en el array de bytes 
                leerArch.read(arreglo);
                
                // Realizamos el envio de los bytes que conforman el archivo
                for (int i = 0; i < arreglo.length; i++) {
                    salida.write(arreglo[i]);
                }
                
                //Calulamos tiempos de procesamiento
                endTime=(System.currentTimeMillis() - this.initialTime);
                totalTime=endTime-startTime;
                
                logServer("Archivo Enviado a cliente:" + id);

                //Finalizamos ejecucion y limpiamos variables
                System.out.println("El servidor termino con cliente" + id + "  EN UN TIEMPO DE: "
                                    + totalTime + " milisegundos");
                System.out.println("Tiempo del cliente "+id +": ("+(System.currentTimeMillis() - this.initialTime)+") milisegundos");

                salida.flush();
                salida.flush();
                salida.close();
                entrada.close();
            }
            //Si el archivo no esta dispobible enviamos mensaje y terminamos conexion
            else {
                myClientOutputStream.writeBoolean(false);
                myClientOutputStream.writeUTF("NO existe el archivo:" + nombreArchivo + " en el servidor");
                logServer("se envio respuesta al cliente");
            }
        } catch (Exception ex) {
            logServer(ex.getMessage() + " id:" + id);
        } finally {
            try {
                if (myServerOutputStream != null) {
                    myServerOutputStream.close();
                }
                if (myServerInputStream != null) {
                    myServerInputStream.close();
                }
                if (s != null) {
                    s.close();
                }
                logServer("Termino proceso para cliente: " + id);

            } catch (Exception e) {
                logServer(e.getMessage() + " [servidor]");
            }
        }
    }

    public static void logServer(String txt) {
        System.out.println(txt);
    }
}
