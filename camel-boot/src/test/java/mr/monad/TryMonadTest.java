package mr.monad;

import java.util.function.Consumer;

import org.junit.Test;

public class TryMonadTest {
	
    @Test
    public void testM() {
        MyMap<String, String> map = new MyMap<String, String>();
        map.put("name", "jora");

        Consumer<String> c = new Consumer<String>() {
            @Override
            public void accept(String o) {
                System.out.println("Received " + o);
            }
        };
        RuntimeException v = map.find("nam1e").ifPresentOrFail(c).successValue();

        System.out.println(v);
    }
}
