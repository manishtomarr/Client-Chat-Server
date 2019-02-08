
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;

public class ServerThread extends Thread {
	private DataInputStream[] dataInputStream = new DataInputStream[10];	//input stream to read from the client
	private DataOutputStream[] dataOutputStream = new DataOutputStream[10];	//output stream to write to the client
	private DataOutputStream[] dataOutputStream1 = new DataOutputStream[10];	//output stream to write to the requested client
	private Socket socket = null;	//client's socket
	private Socket socket1 = null;	//requested client's socket
	private final ServerThread[] serverThread;
	private int maxclient;
	private int id;
	private String[] clientid = new String[10];
	private HashSet<String> clients;
	private Hashtable<String, Socket> connections;
	private String[] buddy = new String[10];

	/**
	 * @param socket as an incoming client socket
	 * @param serverThread respective client thread
	 * @param clients updated connected clients' list
	 * @param clientid unique clientid
	 * @param connections updated data structure for client and its respective socket
	 * @param i unique id for every client thread
	 */
	public ServerThread(Socket socket, ServerThread[] serverThread, HashSet<String> clients, String clientid,
						Hashtable<String, Socket> connections, int i)
	{
		this.socket = socket;
		this.serverThread = serverThread;
		this.maxclient = serverThread.length;
		this.clients = clients;
		this.clientid[i] = clientid;
		this.connections = connections;
		this.id=i;
	}

	public void run()
	{
		int maxclient = this.maxclient;
		try
		{
			dataInputStream[id] = new DataInputStream(socket.getInputStream());
			dataOutputStream[id] = new DataOutputStream(socket.getOutputStream());
			dataOutputStream[id].writeUTF("Welcome " + clientid[id]);
			dataOutputStream[id].writeUTF("Enter '.bye' to exit");
			String ch="yes";
			do
			{
				dataOutputStream[id].writeUTF("*****Choose one of the following*****");
				dataOutputStream[id].writeUTF("1. Open a session with a client");
				dataOutputStream[id].writeUTF("2. Display clients connected to the server");
				String option;
				int choice;
				option = dataInputStream[id].readUTF();
				choice = Integer.parseInt(option);
				switch (choice)
				{
					case 1: synchronized (this)
							{
								dataOutputStream[id].writeUTF("*****Enter the Client ID you want to chat: ");
								buddy[id] = dataInputStream[id].readUTF();
								socket1 = connections.get(buddy[id]);
								try
								{
									dataOutputStream1[id] = new DataOutputStream(socket1.getOutputStream());
								}
								catch (Exception e)
								{
									dataOutputStream[id].writeUTF("Client does not exists!!!");
								}
								dataOutputStream[id].writeUTF("*****Your buddy: " + buddy[id]+"*****");
								if (clients.contains(buddy[id]))	//check if the requested client exists in the data structure
								{
									dataOutputStream[id].writeUTF("*****Happy Chatting*****");
									while (true)
									{
										String input = dataInputStream[id].readUTF();	//read from the input stream
										if (input.equalsIgnoreCase(".bye"))
										{
											break;
										}
										else
										{
											dataOutputStream1[id].writeUTF(clientid[id] + " says: " + input);	//write to requested client
										}
									}
									dataOutputStream[id].writeUTF("*****Bye '" + clientid[id]+"' *****");
									dataOutputStream1[id].writeUTF("'"+clientid[id]+"' ended the conversation");
									dataOutputStream[id].close();
									dataInputStream[id].close();
									dataOutputStream1[id].close();
								}
								else
								{
									dataOutputStream[id].writeUTF("Client does not exists!!!");
								}
							}
								break;
					case 2:	dataOutputStream[id].writeUTF(String.valueOf(clients));	//displaying the updated clients' list
							break;
					default:dataOutputStream[id].writeUTF("Wrong Choice");
				}
				dataOutputStream[id].writeUTF("Want to choose again from menu (yes/no)?");
				ch=dataInputStream[id].readUTF();
			}while (ch.equalsIgnoreCase("yes"));

		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

	}
}