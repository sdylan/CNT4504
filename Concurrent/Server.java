import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.*;

/**
 * This program demonstrates a simple TCP/IP socket server.
 */
public class Server {
    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);
            System.out.println("Listening on:" + serverSocket.toString());
		
            while (true) {
                Socket socket = serverSocket.accept();
 
                System.out.println("\nNew client connected");
		new ServerThread(socket).start(); 
    	    }
	}   catch (IOException ex) {
		System.out.println("Server exception: " + ex.getMessage());
		ex.printStackTrace();
	}
    }
}

class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
		Process p;
		String s;
		OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                
                String command = reader.readLine();
                String response = "";
                System.out.println("Running command: " + command);
                
		try {
     		       p = Runtime.getRuntime().exec(command);
           	 	BufferedReader br = new BufferedReader(
                	new InputStreamReader(p.getInputStream()));
            		while ((s = br.readLine()) != null){
                	    System.out.println("line: " + s);
			    response += s + "\n";
			}
            		p.waitFor();
            		System.out.println ("exit: " + p.exitValue());
            		p.destroy();
	        }catch (Exception e) {}
	
		writer.println(response);

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } 
    }
}
