package dev.hilligans;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static Server server = new Server();
    public static long guildID = 868146989517381632L;
    public static long logChannel = 927786993650393100L;
    public static TextChannel messageChannel;

    public static void main(String[] args) throws LoginException, InterruptedException {
        CommandListener commandListener = new CommandListener(server);
        JDA jda = JDABuilder.createDefault(readString("/secret.txt").get(0)).addEventListeners(commandListener).build();
        jda.awaitReady();
        Guild guild = jda.getGuildById(guildID);
        jda.upsertCommand("restart","Restarts the server").queue();
        jda.upsertCommand("stop","Stops the server").queue();
        jda.upsertCommand("enable_logs", "Enables logging to log channel").queue();
        jda.upsertCommand("disable_logs", "Disables logs to the log channel").queue();
        jda.upsertCommand("chat_on", "Enables chat only mode").queue();
        jda.upsertCommand("chat_off", "Disables chat only mode").queue();

        messageChannel = guild.getTextChannelById(logChannel);
        commandListener.setChannel(messageChannel);
    }

    public static ArrayList<String> readString(String source) {
        InputStream stream = Main.class.getResourceAsStream(source);
        if(stream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ArrayList<String> strings = new ArrayList<>();
        reader.lines().forEach(strings::add);
        return strings;
    }
}
