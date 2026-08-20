package server;

import common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer extends UnicastRemoteObject
        implements ChatService, ClockService {

    private final Set<String> users =
            ConcurrentHashMap.newKeySet();

    private final Map<String, List<Message>> messages =
            new ConcurrentHashMap<>();

    private final ExecutorService pool =
            Executors.newFixedThreadPool(5);

    private final LogicalClock clock;

    public ChatServer(long offset) throws RemoteException {
        super();
        clock = new LogicalClock(offset);
    }

    // ---------- CHAT METHODS ----------

    @Override
    public boolean registerUser(String username)
            throws RemoteException {

        try {
            return pool.submit(() -> {

                if (users.contains(username))
                    return false;

                users.add(username);

                messages.put(
                    username,
                    Collections.synchronizedList(
                        new ArrayList<>()
                    )
                );

                System.out.println(
                    Thread.currentThread().getName()
                    + " registered " + username
                );

                return true;

            }).get();

        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(
            String sender,
            String receiver,
            String content)
            throws RemoteException {

        pool.submit(() -> {

            if (!users.contains(receiver))
                return;

            messages.get(receiver).add(
                new Message(sender, receiver, content)
            );

            System.out.println(
                Thread.currentThread().getName()
                + " processed: "
                + sender + " -> " + receiver
            );
        });
    }

    @Override
    public List<Message> getMessages(String username)
            throws RemoteException {

        List<Message> list = messages.get(username);

        if (list == null)
            return new ArrayList<>();

        return new ArrayList<>(list);
    }

    @Override
    public String getServerStatus()
            throws RemoteException {

        return "SyncChat Server is running";
    }

    // ---------- CLOCK METHODS ----------

    @Override
    public long getTime()
            throws RemoteException {

        return clock.getTime();
    }

    @Override
    public void adjustClock(long adjustment)
            throws RemoteException {

        clock.adjust(adjustment);
    }
}