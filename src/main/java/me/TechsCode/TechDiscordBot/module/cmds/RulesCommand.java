package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand extends CommandModule {

    private final DefinedQuery<net.dv8tion.jda.api.entities.Role> STAFF_ROLE = new DefinedQuery<net.dv8tion.jda.api.entities.Role>() {
        @Override
        protected Query<net.dv8tion.jda.api.entities.Role> newQuery() { return bot.getRoles("Staff"); }
    };
    private final DefinedQuery<TextChannel> RULES_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("overview"); }
    };

    public RulesCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "rules";
    }

    @Override
    public String getDescription() {
        return "Resend the #rules messages.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, SlashCommandEvent e) {
        RULES_CHANNEL.query().first().getIterableHistory()
                .takeAsync(200)
                .thenAccept(channel::purgeMessages);

        e.deferReply().queue();
        showAll();
    }

    public void showAll() {
        showTOS();
        showRules();
        showNote();
        showRoles();
    }

    public void showTOS() {
        new TechEmbedBuilder("Discord TOS & Guidelines")
                .text("This server is compliant with the Discord Terms of Service and Guidelines. " +
                        "We will ban if the content is not a complaint. Be sure to familiarize yourself with them here:\n" +
                        "- [Terms of Service](https://dis.gd/tos)\n- [Content Guidelines](https://dis.gd/guidelines)" +
                        "\n\nOur Staff reserve the right to request a member to confirm their age if they are perceived to be potentially under 13. Noncompliance will result in the assumption of being under 13.")
                .queue(RULES_CHANNEL.query().first());
    }

    public void showNote() {
        new TechEmbedBuilder()
                .text("**PLEASE NOTE**: Staff do reserve the right to punish for things not listed on this list under the staff's discretion. Please use common sense, and if you are unsure about anything, please ask.")
                .queue(RULES_CHANNEL.query().first());
    }

    public void showRules() {
        StringBuilder sBuilder = new StringBuilder();

        List<String> embeds = new ArrayList<>();

        int i = 0;
        for(Rule rule : Rule.values()) {
            if((sBuilder.length() + (i == 0? "" : "\n\n") + "- " + rule.getDescription()).length() > 2048) {
                i = 0;
                embeds.add(sBuilder.toString());
                sBuilder = new StringBuilder();
            }

            sBuilder.append(i == 0 ? "" : "\n\n").append("- ").append(rule.getDescription());

            i++;
        }

        if(embeds.size() == 0) embeds.add(sBuilder.toString());

        i = 0;
        for(String s : embeds) {
            new TechEmbedBuilder(i == 0 ? "Rules" : null, false)
                    .text(s)
                    .queue(RULES_CHANNEL.query().first());
            i++;
        }
    }

    public void showRoles() {
        StringBuilder sBuilder = new StringBuilder();

        int i = 0;
        for(Role role : Role.values()) {
            if(i != 0)
                sBuilder.append("\n\n");

            sBuilder.append(role.getAsMention()).append(": ").append(role.getDescription());

            i++;
        }

        new TechEmbedBuilder("Roles")
                .text(sBuilder + "\n\nPlease don't ask to be Staff, it's annoying.")
                .queue(RULES_CHANNEL.query().first());
    }

    public enum Rule {

        NSFW("**NSFW content is not allowed** on this server. Keep all content SFW, including profile pictures and names."),
        SPAMMING("**Do not spam** or else you will be muted/banned. Spam includes posting the same thing across many channels at the same time or spamming characters (letters, numbers, emojis, and symbols)."),
        INVITES("Server invites **are not allowed** unless it's done privately and with the user's consent."),
        MASS_MENTION("Do do mass mention a user. If the user has blocked or is ignoring you, it doesn’t count as a proper cause."),
        GHOST_PING("Ghost pinging a user is prohibited and can result in **mute**."),
        PINGING_STAFF("Pinging us won't make us answer faster, please be patient and we will get to you once we are available."),
        SUPPORT_DM("Please do not DM staff for support, instead use the appropriate support channels or create a ticket."),        
        DOXXING("We have a **zero tolerance policy** for doxxing of any kind. If you're found doxxing **anyone** from the server or others, you will be **banned**."),
        LAG("Do not purposefully include characters that may crash or lag other devices. Includes massive animated emotes."),
        MINI_MODDING("Mini-modding, or telling people what to do, is prohibited. If there's something that's not okay, ask a staff member to look into it."),
        ASKING_FOR_STUFF("Please don’t ask for stuff, including nitro and money. If someone offers to give it to you, that’s fine, but don’t ask for it."),
        LOGGING("This server logs all deleted and edited messages for in the case of an infraction.");

        private final String description;

        Rule(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Role {

        CODING_WIZARD("Coding Wizard", "311178859171282944", "It was Tech & now it's MATRIX!, The former head of the operations."),
        ASSISTANT("Assistant", "608113993038561325", "This is the Developers Assistant! If the Developers are not online, he is in charge. Occasionally also helps with coding."),
        STAFF("Staff", "608114002387533844", "They are here to help! Don't argue with Staff. If you think there is an issue, please contact <@&608113993038561325> or <@&311178859171282944>."),
        PATREON("Patreon", "795101981051977788", "These are our incredible Patreon supporters who getting rewarded by us for their monthly support with various rewards, additions and exclusive stuff."),
        DONATOR("Donator", "311179148691505152", "These are amazing people who have donated to Tech!"),
        VERIFIED_CREATOR("Knows how to Code", "435183665719541761", "A role given to people who have well known coding projects."),
        NITRO_BOOSTERS("Nitro Booster", "585559418008109075", "These people get are also **AMAZING** who are helping us to unlock great features for our Discord!"),
        REVIEW_SQUAD("Review Squad", "457934035549683713", "These people are the **AMAZING** people in the community who have review all of their plugin's owned by Tech."),
        VERIFIED("Verified", "416174015141642240", "This role is given to members who have successfully verified that they've purchased *as least* one of Tech's resources.");

        private final String name;
        private final String id;
        private final String description;

        Role(String name, String id, String description) {
            this.name = name;
            this.id = id;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getAsMention() {
            net.dv8tion.jda.api.entities.Role role = TechDiscordBot.getJDA().getRoleById(id);

            if(role == null) return getName();
            return role.getAsMention();
        }

        public String getDescription() {
            return description;
        }
    }
}
