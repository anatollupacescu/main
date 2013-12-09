package net.sig.das;

import java.util.Map;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;

public class SubscribersAccountsResolverDAS extends SIGResolverService {

	public SubscribersAccountsResolverDAS(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	public Object load(Object arg0) {
		Map accounts = (Map)getGateway().getService("Accounts").loadAll(null);
		/*computations*/
		ImmutableList.Builder<Object> builder = ImmutableList.builder();
		for(Object account : accounts.values()) {
			if(((Map)account).get("parent").equals(((Map)arg0).get(SubscriberDAS.KEYS.guid))) {
				builder.add(((Map)account).get(AccountDAS.KEYS.accId.toString()));
			}
		}
		return builder.build();
	}

	private Object compute(Object res1, Object res2) {
		return null;
	}
}
