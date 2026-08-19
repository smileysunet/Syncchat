package server;

import common.ChatService;
import common.Message;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer
        extends UnicastRemoteObject
        implements ChatService {

    /*
     * Stores messages for each user.
     *
     * Example:
     *
     * Vedansh → [message1, message2]
     * Rahul   → [message3, message4]
     */
    private final Map<String, List<Message>> userMessages;

    /*
     * Stores registered users.
     */
    private final Map<String, Boolean> users;

    public ChatServer() throws RemoteException {

        super();

        userMessages =
                new ConcurrentHashMap<>();

        users =
                new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean registerUser(
            String username
    ) throws RemoteException {

        if (users.containsKey(username)) {

            System.out.println(
                    "[SERVER] User already exists: "
                            + username
            );

            return false;
        }

        users.put(username, true);

        userMessages.put(
                username,
                new ArrayList<>()
        );

        System.out.println(
                "[SERVER] User registered: "
                        + username
        );

        return true;
    }

    @Override
    public synchronized void sendMessage(
            String sender,
            String receiver,
            String content
    ) throws RemoteException {

        /*
         * Check whether receiver exists.
         */
        if (!users.containsKey(receiver)) {

            System.out.println(
                    "[SERVER] Receiver not found: "
                            + receiver
            );

            return;
        }

        /*
         * Create message.
         */
        Message message =
                new Message(
                        sender,
                        receiver,
                        content
                );

        /*
         * Store message in receiver's inbox.
         */
        userMessages
                .get(receiver)
                .add(message);

        System.out.println(
                "[SERVER] "
                        + sender
                        + " → "
                        + receiver
                        + ": "
                        + content
        );
    }

    @Override
    public synchronized List<Message> getMessages(
            String username
    ) throws RemoteException {

        if (!userMessages.containsKey(username)) {

            return new ArrayList<>();
        }

        /*
         * Return a copy so client cannot
         * directly modify server data.
         */
        return new ArrayList<>(
                userMessages.get(username)
        );
    }

    @Override
    public String getServerStatus()
            throws RemoteException {

        return "SyncChat Server is running.";
    }

    public static void main(String[] args) {

        try {

            /*
             * Create RMI registry.
             */
            Registry registry =
                    LocateRegistry.createRegistry(
                            1099
                    );

            /*
             * Create server.
             */
            ChatServer server =
                    new ChatServer();

            /*
             * Register service.
             */
            registry.rebind(
                    "ChatService",
                    server
            );

            System.out.println();
            System.out.println(
                    "===================================="
            );

            System.out.println(
                    "       SYNCCHAT CHAT SERVER"
            );

            System.out.println(
                    "===================================="
            );

            System.out.println(
                    "RMI Registry : 1099"
            );

            System.out.println(
                    "Service      : ChatService"
            );

            System.out.println(
                    "Status       : RUNNING"
            );

            System.out.println(
                    "===================================="
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}