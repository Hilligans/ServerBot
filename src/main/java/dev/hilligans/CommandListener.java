package dev.hilligans;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    public long time;
    public int throttle = 1000;
    public Server server;
    public TextChannel channel;
    public MessageDispatcher messageDispatcher;

    public CommandListener(Server server) {
        this.server = server;
    }

    public void setChannel(TextChannel textChannel) {
        this.channel = textChannel;
        messageDispatcher = new MessageDispatcher(textChannel);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {

            if(event.getName().equals("link")) {

                return;
            }

            out:
            {
                for (Role role : event.getMember().getRoles()) {
                    if (role.getName().equals("MCServerAdmin")) {
                        break out;
                    }
                }
                event.reply("Insufficient Perms").setEphemeral(true).queue();
                return;
            }
            long newTime = System.currentTimeMillis();
            if(newTime - time < throttle) {
                event.reply("Wait before sending another command").setEphemeral(true).queue();
                return;
            }
            time = newTime;
            if(channel == null) {
                event.reply("Wait a few seconds for bot to restart").setEphemeral(true).queue();
                return;
            }
            if (event.getName().equals("restart")) {
                server.start(channel,messageDispatcher);
                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " Has restarted the server").queue();
                event.reply("Restarting").setEphemeral(true).queue();
            } else if (event.getName().equals("stop")) {
                server.stop();
                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " Has stopped the server").queue();
                event.reply("Stopping").setEphemeral(true).queue();
            } else if(event.getName().equals("disable_logs")) {
                messageDispatcher.setLog(false);
                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " Has disabled console").queue();
                event.reply("Disabled Logs").setEphemeral(true).queue();
            } else if(event.getName().equals("enable_logs")) {
                messageDispatcher.setLog(true);
                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " Has enabled console").queue();
                event.reply("Enabled Logs").setEphemeral(true).queue();
            } else if(event.getName().equals("chat_off")) {
                messageDispatcher.setChatOnly(false);
                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " Has set console to all").queue();
                event.reply("Console All").setEphemeral(true).queue();
            } else if(event.getName().equals("chat_on")) {
                messageDispatcher.setChatOnly(true);
                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " Has set console to chat only").queue();
                event.reply("Chat Only").setEphemeral(true).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getAuthor().isBot()) {
            if (event.getTextChannel().getIdLong() == channel.getIdLong()) {
                String out = getFormattedTellraw(event.getMember().getEffectiveName(), event.getMessage().getContentRaw());
                server.sendCommand(out);
            }
        }
    }

    public static String getFormattedTellraw(String player, String message) {
        return "tellraw @a \"[" + player + "] " + message + "\"\n";
    }
}
