/*  This is the client side program for the Iterative Socket Server assignment for CNT4504 -
 *  Computer Networks and Distributed Processes with Jim Littleton. The program is run on the
 *  command line with "java Client hostname port opNum numClients" where it will use multiple
 *  threads based on numClients to pass the opNum to the server program located at the designated
 *  hostname and port. It will then calculate the average turn-around time and print the returned
 *  data to a text file.
 *
 *  Created by: Gabriel Pridham and Samuel Schwartz
 *  Last Update: 6:44PM 7/12/2018
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class Client {
 
    public static void main(String[] args) throws Exception {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
	int opNum = Integer.parseInt(args[2]);
	int numClients = Integer.parseInt(args[3]);
	long sum = 0;
	float average;
	String operation = "";

	switch (opNum){
	    case 1: operation = "date";
		    break;
	    case 2: operation = "uptime";
		    break;
	    case 3: operation = "free";
	            break;
	    case 4: operation = "netstat";
		    break;
	    case 5: operation = "users";
		    break;
	    case 6: operation = "ps";
		    break;
	}

	System.out.println();

	ClientThread clientThreads[] = new ClientThread[numClients];

	for(int threadNum = 0; threadNum < numClients; threadNum++){
	    clientThreads[threadNum] = new ClientThread(hostname, port, operation);
            clientThreads[threadNum].start();
	}

	for(int threadNum = 0; threadNum < numClients; threadNum++){
            try{clientThreads[threadNum].join();}catch(Exception e){}
	    sum += clientThreads[threadNum].getTurnaround();
	}

	for(int threadNum = 0; threadNum < numClients; threadNum++){
            try{
		PrintWriter writer = new PrintWriter("output_" + operation + "_" + threadNum +".txt","UTF-8"); 
	        writer.println(clientThreads[threadNum].getResponse());
		writer.close();
	    }catch(Exception e){}
	}

	average = (float)sum / numClients;

	System.out.println();
	System.out.println("Total Turn-around Time: " + sum + "ms");
	System.out.println("Average Turn-around Time: " + average + "ms");

    }
}

class ClientThread extends Thread{

    private String hostname, operation, response = "";
    private int port;
    private long turnaround;

    public ClientThread (String hostname, int port, String operation){
        this.hostname = hostname;
        this.port = port;
        this.operation = operation;
    }

    public void run() {

        
	try (Socket socket = new Socket(hostname, port)) {

	OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
 
	Date start = new Date();
        writer.println(operation);

        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        for (String line = reader.readLine(); !line.equals(""); line = reader.readLine()) 
	    response += line + "\n";

	Date end = new Date();
	turnaround = end.getTime() - start.getTime();

        System.out.println(getName() + ":\t" + String.format("%7d", turnaround) + "ms");

        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        } catch (Exception e){}
    }

    public long getTurnaround(){
	return turnaround;
    }

    public String getResponse(){
        return response;
    }
}
