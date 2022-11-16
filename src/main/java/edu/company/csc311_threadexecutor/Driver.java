package edu.company.csc311_threadexecutor;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class Driver {

    static public int total = 0;
    static public Queue<String> work = new LinkedList<>();
    static final CountDownLatch finishedSignal = new CountDownLatch(5);
    static Object LockQueue = new Object();
    static Object LockTotal = new Object();

    public static void main(String[] args) {
        work.add("Data1.txt");
        work.add("Data2.txt");
        work.add("Data3.txt");
        work.add("Data4.txt");
        work.add("Data5.txt");
        ExecutorService exec = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 5; i++) {
            final int loopNum = i;
            exec.submit(() -> {
                sumFile();
                //String tName = Thread.currentThread().getName();
                //System.out.println("Message from " + tName + " " + loopNum);
            });
        }
        try {
            finishedSignal.await();
        } catch (InterruptedException ex) {
        }
        System.out.println(total);
    }

    static public void sumFile() {
        String filename;
        int fileTotal = 0;
        synchronized (LockQueue) {
            filename = work.remove();
        }
        try {
            FileReader fr = new FileReader(filename);
            Scanner infile = new Scanner(fr);
            while (infile.hasNext()) {
                int num = infile.nextInt();
                fileTotal += num;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }
        synchronized (LockTotal) {
            total += fileTotal;
        }
        finishedSignal.countDown();
    }
}
