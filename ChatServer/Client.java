//Created by Manish Tomar (UCID: mt427)

import java.net.*;
import java.io.*;
public class Client implements Runnable
{
	private static Socket socket= null;
	private static DataInputStream dataInputStream = null;
	private static DataOutputStream dataOutputStream = null;
	private static BufferedReader bufferedReader = null;
	private static boolean flag = false;
    public static void main(String args[])
    {
        try
        {
        		socket = new Socket("localhost", 60010);	//connect to the server
                dataInputStream = new DataInputStream(socket.getInputStream());	//server's input stream
                dataOutputStream = new DataOutputStream(socket.getOutputStream());	//server's output stream
                bufferedReader = new BufferedReader(new InputStreamReader(System.in));	//read from command line
                if(socket!=null && dataInputStream!=null && dataOutputStream!=null)
                {
                	new Thread(new Client()).start();	//thread to read from server
                	while(!flag && dataOutputStream!=null)
                	{
                		dataOutputStream.writeUTF(bufferedReader.readLine());	//writing to the server's stream
                	}
                	dataOutputStream.close();
                	dataInputStream.close();
                	socket.close();
                }
        }
		catch(ConnectException ce)
		{
			System.out.println("Server is down");
		}
        catch(UnknownHostException uhe)
        {
			System.out.println("Server is down");
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    // Thread to read from server
	public void run()
	{
		try
		{
			String line;
			while((line=dataInputStream.readUTF())!=null && dataInputStream!=null)
			{
				System.out.println(line);
				if(line.equals(".bye"))
				{
					dataInputStream=null;
					dataOutputStream=null;
					break;
				}
			}
			flag=true;
		}
		catch(IOException ioe)
		{
			System.out.println("Disconnected from server!!!");
		}
	}
}