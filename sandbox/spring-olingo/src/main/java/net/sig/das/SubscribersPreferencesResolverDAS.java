package net.sig.das;

import net.sig.core.SIGResolverService;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;

public class SubscribersPreferencesResolverDAS extends SIGResolverService {

	public SubscribersPreferencesResolverDAS(SIGEntityGateway gateway) {
		super(gateway);
	}

	public Object load(Object subscriberKeyObject) {
		return ImmutableList.of(subscriberKeyObject);
	}
}
