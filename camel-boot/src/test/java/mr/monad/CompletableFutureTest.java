package mr.monad;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

public class CompletableFutureTest {

	@Test
	public void test0() {

		CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {

			@Override
			public String get() {
				return "Abc";
			}
		});

		future.thenAcceptAsync(new Consumer<String>() {

			@Override
			public void accept(String t) {
				System.out.println(t);
			}
		});
	}

	@Test
	public void test1() throws InterruptedException {
		final CompletableFuture<String> future = new CompletableFuture<String>();

		future.thenApply(new Function<String, Integer>() {

			@Override
			public Integer apply(String t) {
				return t.length();
			}
		})

		.thenComposeAsync(new Function<Integer, CompletionStage<Integer>>() {

			@Override
			public CompletionStage<Integer> apply(Integer t) {
				CompletableFuture<Integer> fu = new CompletableFuture<Integer>();
				fu.complete(t * 2);
				return fu;
			}
		})

		.thenAccept(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) {
				System.out.println(t);
			}
		});

		future.complete("jora");
	}
}
