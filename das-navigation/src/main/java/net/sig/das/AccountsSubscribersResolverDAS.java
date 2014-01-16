package net.sig.das;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.GenericKey;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AccountsSubscribersResolverDAS extends SIGResolverService {

	public AccountsSubscribersResolverDAS(SIGEntityGateway gateway2) {
		super(gateway2, ImmutableMap.<String,String> of("guid", "parent"));
	}

	public Object load(Object accountKey) {
		GenericData account = (GenericData)getGateway().getService("Accounts").load(accountKey);
		GenericKey subscriberKey = new GenericKey(SubscriberDAS.entityKeys);
		subscriberKey.inferValues(account, keyMapping);
		return ImmutableList.of(subscriberKey);
	}
}
