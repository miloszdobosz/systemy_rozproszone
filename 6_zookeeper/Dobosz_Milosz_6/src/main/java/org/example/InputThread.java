package org.example;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.Scanner;

import static org.example.ZWatcher.Z;

public class InputThread implements Runnable {

    private final ZooKeeper zooKeeper;


    private boolean isRunning = true;

    public InputThread(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void run() {
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

    public void stop() {
        isRunning = false;
    }
}