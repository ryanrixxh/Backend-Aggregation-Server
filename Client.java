import java.io.*;
import java.net.*;
import java.util.*;


class Client {
  int id = 001;
  public static void main(String[] args) {

    //Client input handling
    Scanner input = new Scanner(System.in);
    String str = input.nextLine();
    String[] split = str.split(":");
    String servername = split[1];
    String cutName = servername.replace("/", "");
    int port = Integer.parseInt(split[2]);

    //Socket Connection using input
    try (Socket socket = new Socket(cutName, port)) {

      //write to AtomServer
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      Scanner sc = new Scanner(System.in);
      String line = null;

      out.println("GET / HTTP/1.1");
      System.out.println(in.readLine());

      ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());

      @SuppressWarnings("unchecked")
      Queue<String> currentFeed = (Queue<String>) inObj.readObject();

      if (currentFeed instanceof Queue) {
        System.out.println(currentFeed);
      } else {
        System.out.println("Error: Object is not a feed");
      }

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
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
