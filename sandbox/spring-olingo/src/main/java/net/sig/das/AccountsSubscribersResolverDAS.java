package net.sig.das;

import java.util.Map;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;

public class AccountsSubscribersResolverDAS extends SIGResolverService {

	public AccountsSubscribersResolverDAS(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	public Object load(Object arg0) {
		Object account = getGateway().getService("Accounts").load(arg0);
		return ImmutableList.of(((Map)account).get("parent"));
	}

}
