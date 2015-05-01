package hello.server.consumer.trickle;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.spotify.trickle.Trickle.call;

import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.trickle.Func1;
import com.spotify.trickle.Func2;
import com.spotify.trickle.Graph;
import com.spotify.trickle.Input;

public class TrickleGraphBuilder {

	public static final Input<String> NAME = Input.named("person name");
	public static final Input<String> GREETING = Input.named("greeting");

	public static Graph<String> getGraph() throws Exception {
		Func1<String, String> pre = new Func1<String, String>() {
			@Override
			public ListenableFuture<String> run(String name) {
				return immediateFuture("Mr. " + name);
			}
		};
		Func1<String, String> post = new Func1<String, String>() {
			@Override
			public ListenableFuture<String> run(String greeting) {
				return immediateFuture(greeting);
			}
		};
		Func2<String, String, String> combine = new Func2<String, String, String>() {
			@Override
			public ListenableFuture<String> run(String greet, String name) {
				String result = String.format("%s %s!", greet, name);
				return immediateFuture(result);
			}
		};
		Graph<String> name = call(pre).with(NAME);
		Graph<String> greeting = call(post).with(GREETING);

		return call(combine).with(greeting, name);
	}
}
