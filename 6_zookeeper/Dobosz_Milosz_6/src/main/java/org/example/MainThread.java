package org.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.zookeeper.ClientCnxn;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainThread implements Runnable, Watcher {
    private static final String HOST_URL = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT = 3000;

    private final ZWatcher zWatcher;
    private final ZooKeeper zooKeeper;
    private final InputThread inputThread;

    private boolean isRunning = true;

    public MainThread() throws IOException {
        this.zooKeeper = new ZooKeeper(HOST_URL, SESSION_TIMEOUT, this);
        this.zWatcher = new ZWatcher(zooKeeper);

        zWatcher.addWatch();
        inputThread = new InputThread(zooKeeper);

        // Disable logging
        ((Logger) LoggerFactory.getLogger(ClientCnxn.class)).setLevel(Level.OFF);
        commandListenerThread();
    }

    @Override
    public void run() {
        synchronized (this) {
            while (this.isRunning) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        inputThread.stop();

        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getState()) {
            case Disconnected, AuthFailed, Closed -> {
                synchronized (this) {
                    isRunning = false;
                    notifyAll();
                }
            }
        }

        zWatcher.process(event);
    }

    private void commandListenerThread() {
        new Thread(inputThread).start();
    }
}