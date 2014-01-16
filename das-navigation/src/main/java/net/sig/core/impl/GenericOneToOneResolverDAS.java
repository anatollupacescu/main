package net.sig.core.impl;

import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.SIGResolverService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class GenericOneToOneResolverDAS extends SIGResolverService {

	private final SIGAbstractCacheStore childDAS;
	
	public GenericOneToOneResolverDAS(SIGEntityGateway gateway, SIGAbstractCacheStore childDAS) {
		super(gateway, null);
		this.childDAS = childDAS;
	}

	public GenericOneToOneResolverDAS(SIGEntityGateway gateway, SIGAbstractCacheStore childDAS, Map<String,String> keyMapping) {
		super(gateway, keyMapping);
		this.childDAS = childDAS;
	}
	
	public Object load(final Object parentKey) {
		GenericKey childKey = new GenericKey(childDAS.getKeyNames());
		if(keyMapping == null) {
    		childKey.inferValues((GenericKey)parentKey);
		} else {
    		Builder<String, String> builder = ImmutableMap.builder();
    		for (String key : keyMapping.keySet()) {
    			String value = ((GenericKey)parentKey).get(key);
    			String newKey = keyMapping.get(key);
    			builder.put(newKey, value);
    		}
    		childKey.inferValues(builder.build());
		}
		return ImmutableList.of(childKey);
	}
}
