import java.io.*;
import java.net.Socket;
import java.sql.Struct;

public class ConnectionHandler implements Runnable {
    private User user;

    public ConnectionHandler(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        try {
            handleMessage();
        } catch (IOException e) {

        }
    }

    public void handleMessage() throws IOException {
        InputStream inputStream = this.user.socket.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader buffer = new BufferedReader(reader);

        OutputStream outputStream = this.user.socket.getOutputStream();
        DataOutputStream backToClient = new DataOutputStream(outputStream);

        boolean isRunning = true;
        while (isRunning) {
            String line = buffer.readLine();
            String response = line.toUpperCase() + "\n";

            if (line.startsWith("@quit")) {
                isRunning = false;
                this.user.socket.close();
            } else if (line.startsWith("@list")) {
//                response = listUsers(line);
            } else if (line.startsWith("@nickname")){
                this.setUsername(line);

            } else if (line.startsWith("@dm")){
                directMessage(line);
            }
            // TODO: implement other command methods

            TCPServer.broadcast(this.user.toString() + ": " + response);
        }
    }

//    public String listUsers (String line) {
//        // TODO: implement list users
//        String userList = "";
//        for (User user : TCPServer.connections) {
//            userList += "@" + user.nickname + "\n";
//        }
//        TCPServer.message(this.user.nickname, userList);
//    }

    public void setUsername(String line){
        String oldUsername = this.user.nickname;
        String[] cells = line.split(" ");

        if (cells.length > 1) {
            this.user.nickname = cells[1];
            TCPServer.message(this.user.nickname, "successfully set username to @" + line);
        } else {
            TCPServer.message(oldUsername, "Error changing name");
        }
    }

    private void directMessage(String line){
        String[] cells = line.split(" ");
        String user = cells[1];



        int secondSpaceIndex = line.indexOf(" ", 4);
        String message = line.substring(secondSpaceIndex);
        message =  this.user.nickname + "Says: " + message;


    }
}
