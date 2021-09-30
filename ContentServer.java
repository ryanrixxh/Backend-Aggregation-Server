import java.io.*;
import java.net.*;
import java.util.*;

class ContentServer {
  int id = 001;
  
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

      out.println("PUT / HTTP/1.1");
      System.out.println(in.readLine());

      Scanner fromFile = new Scanner(new FileReader("input1.txt"));
      String toSend = fromFile.nextLine();
      out.println(toSend);
      System.out.println(in.readLine());

      while (true) {
        line = sc.nextLine();

        if (line.equalsIgnoreCase("exit")) {
          sc.close();
          socket.close();
          break;
        }
      }

      sc.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
