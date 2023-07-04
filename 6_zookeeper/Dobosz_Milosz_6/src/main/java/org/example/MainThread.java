package org.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.zookeeper.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class MainThread implements Runnable, Watcher {
    public static final String HOST_URL = "127.0.0.1:2181";
    public static final int SESSION_TIMEOUT = 5000;

    public static final String Z = "/z";
    public static final String APP = "gnome-system-monitor";

    public static void main(String[] args) throws IOException {
        new MainThread(HOST_URL, SESSION_TIMEOUT).run();
    }

    private Process appProcess;
    private final ZooKeeper zooKeeper;
    private boolean isRunning;

    public MainThread(String host_url, int session_timeout) throws IOException {
        this.zooKeeper = new ZooKeeper(host_url, session_timeout, this);
        addZWatch();

        // Disable logging
        ((Logger) LoggerFactory.getLogger(ClientCnxn.class)).setLevel(Level.OFF);
    }

    @Override
    public void run() {
        isRunning = true;
        final Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            if (scanner.nextLine().equals("show")) {
                try {
                    zooKeeper.getAllChildrenNumber(Z);
                    showTree(Z, Z, "");
                } catch (KeeperException e) {
                    System.err.println("/z does not exist...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("???");
            }
        }
    }

    public void stop() {
        isRunning = false;

        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();
        switch (state) {
            case Disconnected, AuthFailed, Closed, Expired -> stop();
        }

        String path = event.getPath();
        if (path == null) {
            return;
        }
        

        if (path.startsWith(Z)) {
            Event.EventType type = event.getType();
            if (path.equals(Z)) {
                switch (type) {
                    case NodeCreated -> startApp();
                    case NodeDeleted -> stopApp();
                }
            }
            
            addZWatch();

            switch (type) {
                case NodeCreated, NodeDeleted -> showChildren();
            }
        }
    }
    
    private void showChildren() {
        try {
            System.out.println("Children count: " + zooKeeper.getAllChildrenNumber(Z));
        } catch (KeeperException | InterruptedException ignored) {
        }
    }

    private void showTree(String path, String name, String tab) {
        System.out.println(tab + "└── " + name);

        try {
            zooKeeper
                    .getChildren(path, true)
                    .forEach(childName -> showTree(path + "/" + childName, childName, tab + "\t"));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addZWatch() {
        try {
            // Persistent recursive to track children aas well
            zooKeeper.addWatch(Z, AddWatchMode.PERSISTENT_RECURSIVE);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startApp() {
        try {
            System.out.println("Starting external application");
            appProcess = new ProcessBuilder(APP).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopApp() {
        System.out.println("Stopping external application");
        appProcess.destroy();
    }
}