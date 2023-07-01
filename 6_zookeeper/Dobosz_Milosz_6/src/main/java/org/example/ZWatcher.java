package org.example;

import org.apache.zookeeper.*;

import java.io.IOException;

public class ZWatcher implements Watcher {
    public static final String Z = "/z";
    public static final String APP = "gnome-system-monitor";

    private final ZooKeeper zooKeeper;
    private Process appProcess;

    public ZWatcher(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        final var path = event.getPath();

        if (event.getState() == Event.KeeperState.Expired || path == null) {
            return;
        }

        if (path.equals(Z)) {
            switch (event.getType()) {
                case NodeCreated -> startApp();
                case NodeDeleted -> stopApp();
            }
        }

        if (path.startsWith(Z)) {
            addWatch();

            switch (event.getType()) {
                case NodeCreated, NodeDeleted -> {
                    try {
                        System.out.println("Children count: " + zooKeeper.getAllChildrenNumber(Z));
                    } catch (KeeperException | InterruptedException ignored) {
                    }
                }
            }
        }
    }

    public void addWatch() {
        try {
            // Persistent recursive to track children aas well
            zooKeeper.addWatch(Z, AddWatchMode.PERSISTENT_RECURSIVE);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startApp() {
        try {
            System.out.println("Starting application");
            appProcess = new ProcessBuilder(APP).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopApp() {
        System.out.println("Stopping application");
        appProcess.destroy();
    }
}