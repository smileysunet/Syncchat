package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {

    /*
     * Send a message to another user.
     */
    void sendMessage(
            String sender,
            String receiver,
            String content
    ) throws RemoteException;

    /*
     * Get messages received by a particular user.
     */
    List<Message> getMessages(
            String username
    ) throws RemoteException;

    /*
     * Register a user with the server.
     */
    boolean registerUser(
            String username
    ) throws RemoteException;

    /*
     * Check server status.
     */
    String getServerStatus()
            throws RemoteException;
}