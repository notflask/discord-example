package net.flask.bot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class Bot {
    public static void main(String[] args) {
        // Инициалзиация бота
        DiscordClient client = DiscordClient.create(Config.TOKEN);

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // Когда бот запустился (ReadyEvent)
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                final User self = event.getSelf();
                                System.out.printf("Logged as %s#%s%n\n", self.getUsername(), self.getDiscriminator());
                            }))
                    .then();

            // Когда бот получил сообщение (MessageCreateEvent)
            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase(Config.EXAMPLE_COMMAND)) {
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("(\uD83C\uDDFA\uD83C\uDDF8) This is example command!\n(\uD83C\uDDF7\uD83C\uDDFA) Это пример команды!"));
                }
                return Mono.empty();
            }).then();

            return printOnLogin.and(handlePingCommand);
        });

        login.block();
    }
}
