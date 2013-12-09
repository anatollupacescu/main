package net.sig.das;

import java.util.Map;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AccountsSubscribersResolverDAS extends SIGResolverService {

	public AccountsSubscribersResolverDAS(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	public Object load(Object guidMap) {
		Object account = getGateway().getService("Accounts").load(guidMap);
		return ImmutableList.of(ImmutableMap.of(SubscriberDAS.KEYS.guid.toString(), ((Map)account).get("parent")));
	}

}
