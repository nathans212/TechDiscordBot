package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserInfoCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public UserInfoCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!userinfo";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"!uinfo"};
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.INFO;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        Member member1 = TechDiscordBot.getMemberFromString(message, String.join(" ", args));
        if(member1 == null) member1 = member;
        User user = member1.getUser();
        new TechEmbedBuilder(user.getName() + "#" + user.getDiscriminator())
                .addField("Status", member1.getOnlineStatus().getKey().substring(0, 1).toUpperCase() + member1.getOnlineStatus().getKey().substring(1), true)
                .addField("Created At", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Joined At", member1.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Flags", user.getFlags().clone().stream().map(User.UserFlag::getName).collect(Collectors.joining(", ")) + ".", false)
                .addField("Roles", member1.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", ")) + ".", false)
                .setThumbnail(user.getAvatarUrl())
        .send(channel);
    }
}
