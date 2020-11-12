/*  This is the server side program for the Iterative Socket Server assignment for CNT4504 -
 *  Computer Networks and Distributed Processes with Jim Littleton. The program is run on the
 *  command line with "java Server port" where it will start listening on the desginated port
 *  for any client requests. The server processes each request iteratively in a while loop so
 *  multiple requests will be placed in a queue to be processed with each successive iteration
 *  of the loop. It runs a Unix-based command line program passed by the client and prints the
 *  output to be received by the client program.
 *
 *  Created by: Gabriel Pridham and Samuel Schwartz
 *  Last Update: 6:51PM 7/12/2018
 */

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.*;

public class Server {
    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);
	Process p;
	String s;
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);
            System.out.println("Listening on:" + serverSocket.toString());
		
            while (true) {
                Socket socket = serverSocket.accept();
 
                System.out.println("\nNew client connected");
 
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

    	    }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } 
    }
} 
