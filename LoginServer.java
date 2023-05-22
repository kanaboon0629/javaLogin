import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

import java.util.stream.Stream;


public class LoginServer {
  private static final int SERVER_PORT = 8080;
  private static Set<String> loggedInUsers = new HashSet<>();

  //ユーザー情報
  private static final String USERS_FILE = "users.txt";

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
      System.out.println("Server started on port " + SERVER_PORT);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket);

        // Handle client connection in a new thread
        new Thread(() -> handleClient(clientSocket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleClient(Socket clientSocket) {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

      String message;
      while ((message = in.readLine()) != null) {

        if (message.startsWith("LOGIN_c")) {
          String[] parts = message.split(" ");

          //ユーザー名とパスワードがきちんと入力されているとき
          if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            //ユーザーリストに名前があるとき
            if (verifyPassword(username, password)) {

              //ログイン済みのときはログインできない
              if (loggedInUsers.contains(username)) {
                out.println("FAILURE_s");

              //ログインしたユーザーを記録
              } else {
                loggedInUsers.add(username);
                out.println("SUCCESS_s");
              }

            //ユーザーリストに名前がないとき
            } else {
              out.println("FAILURE_s");
            }
          //されていないとき
          } else {
            out.println("FAILURE_s");
          }
        }
      }

      clientSocket.close();
      System.out.println("Client disconnected: " + clientSocket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //パスワード照合
  private static boolean verifyPassword(String loginusername, String loginpassword) {
    
    System.out.println("verifyPassword");
      
    try (Stream<String> lines = Files.lines(Paths.get(USERS_FILE))) {
          return lines.anyMatch(line -> line.equals(loginusername + "," + loginpassword));
    } catch (IOException e) {
      return false;
    }
  }
}


