package mr.f.spring_boot_camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.rx.ObservableBody;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class RxRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Access us using http://localhost:8080/camel/hello
		// from("servlet:///hello").transform().constant("Hello from Camel!");

		// ReactiveCamel rx = new ReactiveCamel(getContext());
		// rx.toObservable("servlet:///rx")
		// .filter(m -> true)
		// .map(m -> "Hello " + m.getHeader("nam")).toBlocking();
		// .subscribe(s -> { System.out.print(s); });

		from("servlet:///rx").process(new MyObservableBody()).process(exch -> {
			exch.getIn().setBody("Jora");
		});
	}

	public class MyObservableBody extends ObservableBody<String> {

		public MyObservableBody() {
			super(String.class);
		}

		protected void configure(Observable<String> observable) {
			// lets process the messages using the RX API
			observable.map(new Func1<String, String>() {
				public String call(String body) {
					return "Hello " + body;
				}
			}).subscribe(new Action1<String>() {
				public void call(String body) {
				}
			});
		}
	}
}
