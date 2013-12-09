package net.sig.das;

import java.util.Map;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SubscribersAccountsResolverDAS extends SIGResolverService {

	public SubscribersAccountsResolverDAS(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	public Object load(Object arg0) {
		Map accounts = (Map)getGateway().getService("Accounts").loadAll(null);
		/*computations*/
		ImmutableList.Builder<Map<String, String>> builder = ImmutableList.builder();
		for(Object account : accounts.values()) {
			if(((Map)account).get("parent").equals(((Map)arg0).get(SubscriberDAS.KEYS.guid.toString()))) {
				final String keyName = AccountDAS.KEYS.accId.toString();
				builder.add(ImmutableMap.<String, String>of(keyName, (String)((Map)account).get(keyName)));
			}
		}
		return builder.build();
	}
}
