package net.sandbox;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import net.sandbox.metadata.MetadataWrapper;
import net.sandbox.metadata.ScenarioEdmProvider;
import net.sandbox.segment.PathSegment;
import net.sandbox.segment.SegmentType;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.google.common.base.Optional;

public class Main {

	private static final Logger log = Logger.getAnonymousLogger();

	private static final String VALUE_LITERAL = "$value";
	
	private final MetadataWrapper wrapper;
	
	public Main() throws ODataException {
		wrapper = new MetadataWrapper(new ScenarioEdmProvider());
	}

	public PathSegment lookupSegments(final String path) throws MalformedURLException {
		final URI url = URI.create(path);
		final Optional<String> queryOptionsMap = Optional.fromNullable(url.getQuery());
		final String[] pathComponents = url.getPath().split("/");
		return parse(pathComponents, queryOptionsMap);
	}
	
	private PathSegment parse(String[] pathComponents, Optional<String> queryOptionsMap) {
		PathSegment segment = createSegment(pathComponents[0]);
		final AtomicBoolean isLinks = new AtomicBoolean();
		for (int i = 1; i < pathComponents.length; i++) {
			final String pathComponent = pathComponents[i];
			switch (segment.getType()) {
			case ENTITY_SET_W_ID:
			case NAV_PROP_W_ID:
				segment = createSegment(pathComponent, segment);
				break;
			case COMPLEX_PROP:
				segment = createSegment(pathComponent, segment, SegmentType.SIMPLE_PROP);
				break;
			case SIMPLE_PROP:
				if (!VALUE_LITERAL.equals(pathComponent)) {
					throw new IllegalArgumentException("Only $value expected after simple property");
				}
				segment = createSegment(pathComponent, segment, SegmentType.VALUE);
				break;
			case ENTITY_SET:
			case NAV_PROP:
			case VALUE:
				throw new IllegalArgumentException("Unexpected segment");
			default:
				break;
			}
		}
		//rewind
		while (segment.hasPrev()) {
			segment = segment.getPrev();
		}
		return segment;
	}

	private PathSegment createSegment(String pathComponent, PathSegment segment, SegmentType simpleProp) {
		// TODO Auto-generated method stub
		return null;
	}

	private PathSegment createSegment(String pathComponent, PathSegment segment) {
		switch(segment.getType()) {
		case COMPLEX_PROP:
			return createSegment(pathComponent, segment, SegmentType.SIMPLE_PROP);
			break;
		case 
		}
		String[] parts = separateKey(pathComponent);
		final String entitySetName = parts[0];
		final String keyString = parts[1];
		final SegmentType segmentType;
		final FullQualifiedName entitySet = wrapper.lookupEntitySet(entitySetName);
		if(entitySet == null) {
			wrapper.lookup
		}
		return null;
	}

	/**
	 * Root segment builder method
	 * @param pathComponent
	 * @return root segment object
	 */
	private PathSegment createSegment(final String pathComponent) {
		Map<String, String> keyMap = null;
		String entitySetName = pathComponent;
		SegmentType segmentType = SegmentType.ENTITY_SET;
		if (pathComponent.contains("(")) {
			int openingBracketIndex = pathComponent.indexOf("(");
			final String keyString = pathComponent.substring(openingBracketIndex);
			entitySetName = pathComponent.substring(0, openingBracketIndex);
			keyMap = Utils.extractKeyMap(keyString);
			final FullQualifiedName entityType = wrapper.lookupEntitySet(entitySetName);
			Key entityKey = wrapper.lookupEntityType(entityType); entityType.
			for (final PropertyRef key : entityKey.getKeys()) {
				if (keyMap.get(key.getName()) == null) {
					throw new IllegalArgumentException(String.format("Key not provided: '%s'", key.getName()));
				}
			}
			segmentType = SegmentType.ENTITY_SET_W_ID;
		}
		PathSegment segment = new PathSegment();
		segment.setType(segmentType);
		segment.setName(entitySetName);
		segment.setKeyMap(keyMap);
		return segment;
	}
	
	private String[] separateKey(String pathComponent) {
		String[] parts = new String[] { pathComponent, null };
		if (pathComponent.contains("(")) {
			int openingBracketIndex = pathComponent.indexOf("(");
			parts[1] = pathComponent.substring(openingBracketIndex);
			parts[0] = pathComponent.substring(0, openingBracketIndex);
		}
		return parts;
	}
}

/*
	static {

		stateMachine.put(SegmentType.ENTITY_SET, ImmutableSet.<SegmentType> of());

		stateMachine.put(SegmentType.ENTITY_SET_W_ID, ImmutableSet.<SegmentType> of(
			SegmentType.NAV_PROP,
			SegmentType.NAV_PROP_W_ID, 
			SegmentType.COMPLEX_PROP, 
			SegmentType.SIMPLE_PROP));

		stateMachine.put(SegmentType.NAV_PROP, ImmutableSet.<SegmentType> of());

		stateMachine.put(SegmentType.NAV_PROP_W_ID, ImmutableSet.<SegmentType> of(
			SegmentType.NAV_PROP,
			SegmentType.NAV_PROP_W_ID, 
			SegmentType.COMPLEX_PROP, 
			SegmentType.SIMPLE_PROP));

		stateMachine.put(SegmentType.COMPLEX_PROP, ImmutableSet.<SegmentType> of(SegmentType.SIMPLE_PROP));

		stateMachine.put(SegmentType.SIMPLE_PROP, ImmutableSet.<SegmentType> of(SegmentType.VALUE));
	}
*/
