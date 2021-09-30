import java.io.*;
import java.net.*;
import java.util.*;

class ContentServer {
  public static void main(String[] args) {

    //ContentServer input handling
    Scanner input = new Scanner(System.in);
    String str = input.nextLine();
    String[] split = str.split(":");
    String servername = split[1];
    String cutName = servername.replace("/", "");
    int port = Integer.parseInt(split[2]);


    try (Socket socket = new Socket(cutName, port)) {

      //write to AtomServer
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      Scanner sc = new Scanner(System.in);
      String line = null;

      out.println("ContentServer");
      System.out.println(in.readLine());

      Scanner fromFile = new Scanner(new FileReader("input1.txt"));
      String toSend = fromFile.nextLine();
      out.println(toSend);

      while (!"exit".equalsIgnoreCase(line)) {
        //Read from user
        line = sc.nextLine();

        //Send input to AtomServer
        out.println(line);
        out.flush();

        //Server reply
        System.out.println("Server replied: " + in.readLine());
      }

      sc.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
