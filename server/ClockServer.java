package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClockServer {

    public static void main(String[] args) {

        try {
            int port = Integer.parseInt(args[0]);
            long offset = Long.parseLong(args[1]);

            Registry registry =
                    LocateRegistry.createRegistry(port);

            ChatServer server =
                    new ChatServer(offset);

            registry.rebind("ClockService", server);

            System.out.println(
                    "Server running on port " + port);

            System.out.println(
                    "Clock: " + server.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}