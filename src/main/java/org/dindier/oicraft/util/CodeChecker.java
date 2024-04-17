package org.dindier.oicraft.util;


import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class CodeChecker {
    static {
        URL url = CodeChecker.class.getClassLoader().getResource("lib/CodeChecker.dll");
        if (url != null) {
            System.load(url.getPath());
        }
    }

    // This is just for testing
    public static void run(String program) throws  IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(program);
        Process p = pb.start();
        // Watch memory usage
        long max = 0;
        long pid = p.pid();
        AtomicBoolean isRunning = new AtomicBoolean(true);
        p.onExit().thenAccept((exitCode) -> isRunning.set(false));
        while (isRunning.get()) {
            long mem = getProcessMemoryUsage(pid);
            max = Math.max(max, mem);
            System.out.println("Memory usage: " + mem/1024 + "KB");
            Thread.sleep(500);
        }
    }

    public static native long getProcessMemoryUsage(long pid);
}
