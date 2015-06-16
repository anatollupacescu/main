package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Promises;

import demo.command.Command;
import demo.command.gametable.GameTableAction;
import demo.command.gametable.GameTableCommand;
import demo.impl.BJGameTable;

@SpringBootApplication
public class DemoApplication {

		/*
	public @Bean Environment env() {
		return new Environment();
	}

	public @Bean EventBus eventBus(Environment env) {
		final EventBus eventBus = EventBus.config().env(env).dispatcher(Environment.WORK_QUEUE).get();
		eventBus.on(Selectors.type(Command.class), new LoggingConsumer());
		eventBus.on(Selectors.type(GameTableCommand.class), new GameTableConsumer());
		return eventBus;
	}
	
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = SpringApplication.run(DemoApplication.class);
		final EventBus eventBus = ctx.getBean(EventBus.class);
		GameTableCommand createGameTable = new GameTableCommand(GameTableAction.CREATE);
		createGameTable.setGameTable(new BJGameTable("id-1"));
		Promise<String> p = Promises. <String>prepare();
		Pair<Promise<String>, GameTableCommand> t = Tuples.pair(p, createGameTable);
		eventBus.notify(GameTableCommand.class, Event.wrap(t));
		p.await();
		System.out.println("Result of call: " + p.get());
		p = Promises. <String>prepare();
		eventBus.notify(GameTableCommand.class, Event.wrap(Tuples.pair(p, createGameTable)));
		p.await();
		System.out.println("Result of call: " + p.get());
	}

	private static void readMessages(EventBus eventBus) {
		System.out.println("Start sending messages:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String target = null;
		while (!"exit".equals(target)) {
			try {
				final String input = br.readLine();
				if (target == null) {
					target = input;
				} else {
					eventBus.notify(target, Event.wrap(input));
					target = null;
				}
			} catch (IOException ioe) {
				System.out.println("IO error trying to read console input");
				System.exit(1);
			}
		}
	}
	*/
}
