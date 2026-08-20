package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClockService extends Remote {
    long getTime() throws RemoteException;
    void adjustClock(long adjustment) throws RemoteException;
}