package com.siemens.smartsales.cli_process_manager.manager_webapp;

import java.io.File;
import java.io.IOException;

public class RunMe {

    public static void main(String[] args) throws IOException, InterruptedException {

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        System.out.println("OS is Windows: " + isWindows);

        String command = "java -cp ./* com.siemens.smartsales.cli_process_manager.sample_process.RunMe";

        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", "ls");
        }
        builder.directory(new File("D:\\Repositories\\cli-process-manager\\sample-process\\target"));
        Process process = builder.start();

        long pid = process.pid();
        System.out.println("PID: " + pid);

        // StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(),
        // System.out::println);
        // Executors.newSingleThreadExecutor().submit(streamGobbler);

        // int exitCode = process.waitFor();
        // assert exitCode == 0;

    }

}
