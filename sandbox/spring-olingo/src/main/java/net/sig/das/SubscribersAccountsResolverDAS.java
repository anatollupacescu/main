package net.sig.das;

import java.util.Map;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.GenericKey;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;

public class SubscribersAccountsResolverDAS extends SIGResolverService {

	public SubscribersAccountsResolverDAS(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	@SuppressWarnings("rawtypes")
	public Object load(Object subscriberKeyObject) {
		final GenericKey subscriberKey = (GenericKey)subscriberKeyObject;
		final String subscriberGuid = subscriberKey.get("guid");
		final Object accounts = getGateway().getService("Accounts").loadAll(null);
		ImmutableList.Builder<GenericKey> builder = ImmutableList.builder();
		for(Object account : ((Map)accounts).values()) {
			final GenericData accountEntity = (GenericData)account;
			final String parent = accountEntity.get("parent");
			if(parent.equals(subscriberGuid)) {
				builder.add(accountEntity.getKey());
			}
		}
		return builder.build();
	}
}
