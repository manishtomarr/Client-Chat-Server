//Created by Manish Tomar (UCID: mt427)

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Hashtable;

public class Server
{
	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	private static final int maxclient = 10;
	private static final ServerThread[] serverThread = new ServerThread[maxclient];
	private static HashSet<String> clients = new HashSet<>();   //data structure to maintain the connected clients
    private static Hashtable<String,Socket> connections = new Hashtable<>();    //data structure to maintain sockets with respect to the client ID
    public static void main(String args[])
    {
        try
        {
            	serverSocket = new ServerSocket(60010); //Starting server at a port
           	 	System.out.println("*****Server started*****");
                System.out.println("Running on port: " + 60010);
                int i;
                while(true) //to be always into accepting state
                {
                	socket=serverSocket.accept();   // accepting the client socket
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                	i=0;
                	for(i=0;i<maxclient;i++)
                	{
                		if(serverThread[i]==null)
                		{
                			System.out.println ("Accepted from " + socket.getInetAddress());    //keep a log of IP addresses from where the client is connected
                            dataOutputStream.writeUTF("*****Server is successfully running*****");
                            dataOutputStream.writeUTF("Enter Client ID : ");
                            String clientid = null;
                            while(true)
                            {
                                clientid=dataInputStream.readUTF().trim();
                                if(clientid.indexOf('@') == 0)
                                {
                                    dataOutputStream.writeUTF("The name should not contain '@' character");
                                    dataOutputStream.writeUTF("Please Try Again!!!");
                                }
                                if (clients.contains(clientid))
                                {
                                    dataOutputStream.writeUTF("Client with ID '" + clientid + "' already exists!!!");
                                    dataOutputStream.writeUTF("Please Try Again!!!");
                                }
                                else
                                {
                                    clients.add(clientid);
                                    connections.put(clientid,socket);
                                    break;
                                }
                            }
                        	serverThread[i]= new ServerThread(socket,serverThread,clients,clientid,connections,i);
                        	serverThread[i].setDaemon(true);
                        	serverThread[i].start();    //thread to handle each client separately
                        	break;
                		}
                	}
                    if (i == maxclient)     //condition to check if the server reached its maximum limit
                    {
                        dataOutputStream.writeUTF("Server busy!!! Try again later!!!");
                        dataOutputStream.close();
                        socket.close();
                    }
                }
        }
        catch(BindException be)
        {
            System.out.println("Cannot connect again to the same port!!!");
        }
        catch (SocketException se)
        {
            System.out.println("Client closed unexpectedly!!!");
        }
        catch (IOException ioe)
        {
			ioe.printStackTrace();
		}
    }

    /**
     * clientid to be removed from both the data structures after the close of session
     * @param clientid
     */
    public void updatelist(String clientid)
    {
        clients.remove(clientid);
        connections.remove(clientid);
    }
}