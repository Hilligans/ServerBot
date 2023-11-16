package dev.hilligans;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Server {

    public Process process;
    public OutputStreamWriter writer;
    Thread active;

    public synchronized void start(MessageChannel logChannel, MessageDispatcher messageDispatcher) {
        stop();
        active = new Thread(() -> {
            try {
                File file = new File("/home/Modded5/ServerPack/");
                String[] commands = new String[]{"java", "-Xmx4096m", "-jar", "custom.jar"};
                ProcessBuilder processBuilder = new ProcessBuilder().directory(file).command(commands);
                process = processBuilder.start();
                writer = new OutputStreamWriter(process.getOutputStream());

                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        if(!line.contains("net.minecraft.server.management.PlayerList")) {
                            messageDispatcher.submit(line);
                        }
                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
                logChannel.sendMessage(err.getMessage()).queue();
            }
        });
        active.start();
    }

    public synchronized void stop() {
        if(active != null) {
            active.stop();
        }
        if(process != null) {
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }

    public synchronized void sendCommand(String command) {
        if(process.isAlive()) {
            try {
                writer.write(command);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
