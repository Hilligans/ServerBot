package dev.hilligans;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.ArrayList;
import java.util.concurrent.*;

public class MessageDispatcher {

    public static final int maxLength = 1999;
    public static final int dispatchWait = 1000;

    public TextChannel channel;
    public StringBuilder stringBuilder = new StringBuilder();

    public long lastDispatch;
    public final MessageDispatcher messageDispatcher;
    public boolean log = true;
    public boolean chatOnly = true;

    public MessageDispatcher(TextChannel textChannel) {
        messageDispatcher = this;
        this.channel = textChannel;
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleAtFixedRate(() -> {
            synchronized (messageDispatcher) {
                if(System.currentTimeMillis() - lastDispatch > dispatchWait) {
                    dispatch();
                }
            }
        }, 200,200, TimeUnit.MILLISECONDS);
    }

    public synchronized void submit(String string) {
        if(!log) {
            return;
        }
        if(chatOnly) {
            if(!string.contains("[Server thread/INFO] [minecraft/DedicatedServer]: <"))  {
                return;
            } else {
                string = string.substring("[23:08:52] [Server thread/INFO] [minecraft/DedicatedServer]: ".length());
            }
        }
        if(stringBuilder.length() + string.length() > maxLength) {
            dispatch();
        }
        if(stringBuilder.length() == 0) {
            stringBuilder.append(string);
            return;
        }
        stringBuilder.append('\n').append(string);
    }

    public synchronized void dispatch() {
        if(stringBuilder.length() == 0) {
            return;
        }
        System.out.println(stringBuilder);
        channel.sendMessage(stringBuilder.toString()).queue();
        stringBuilder = new StringBuilder();
        lastDispatch = System.currentTimeMillis();
    }

    public synchronized void setLog(boolean value) {
        log = value;
    }

    public synchronized void setChatOnly(boolean value) {
        chatOnly = value;
    }
}
