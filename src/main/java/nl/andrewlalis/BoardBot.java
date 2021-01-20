package nl.andrewlalis;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.util.PropertiesManager;

import java.time.Duration;

/**
 * The main program entry point for the BoardBot application.
 */
@Slf4j
public class BoardBot {
	/**
	 * Main method which starts the bot's Discord client and begins listening
	 * for events.
	 * @param args The list of command-line arguments. We expect the first arg
	 *             to be the bot's token for authentication.
	 */
	public static void main(String[] args) {
		var client = DiscordClientBuilder.create(parseToken(args))
				.build()
				.login()
				.block(Duration.parse(PropertiesManager.getInstance().get("login_timeout")));
		if (client == null) {
			throw new RuntimeException("Could not obtain client and log in.");
		}

		client.getEventDispatcher().on(ReadyEvent.class)
				.subscribe(event -> {
					User self = event.getSelf();
					log.info("Logged in as {}#{}.", self.getUsername(), self.getDiscriminator());
				});

		client.getEventDispatcher().on(MessageCreateEvent.class)
				.subscribe(event -> {
					log.info("Message created: {}", event.getMessage().getContent());
					if (event.getMessage().getContent().equalsIgnoreCase("bb stop")) {
						client.logout().block();
					} else if (event.getMessage().getContent().equalsIgnoreCase("bb start")) {
						event.getMessage().getChannel()
								.subscribe(channel -> {
									channel.createEmbed(spec -> {
										spec.setColor(Color.BLUE);
									}).block();
								});
					}
				});

		client.onDisconnect().block();
	}

	/**
	 * Simple method to extract the auth token from the program arguments, or
	 * throw a runtime exception if that fails. Since the bot simply cannot
	 * start without a token, we use an unchecked exception and just let it stop
	 * the program.
	 * @param args The command-line arguments.
	 * @return The token that was parsed.
	 */
	private static String parseToken(String[] args) {
		if (args.length == 0 || args[0].isBlank()) {
			throw new IllegalArgumentException("Missing required token argument.");
		}
		return args[0];
	}
}
