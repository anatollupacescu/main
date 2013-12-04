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
import org.apache.olingo.odata2.api.edm.provider.EntityType;
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
//		final Optional<String> queryOptionsMap = Optional.fromNullable(url.getQuery());
		final String[] pathComponents = url.getPath().split("/");
		PathSegment segment = createRootSegment(pathComponents[0]);
		for (int i = 1; i < pathComponents.length; i++) {
			final String pathComponent = pathComponents[i];
			segment = createSegment(pathComponent, segment);
		}
		/*rewind*/
		while (segment.hasPrev()) {
			segment = segment.getPrev();
		}
		return segment;
	}
	
	private PathSegment createSegment(String pathComponent, PathSegment parent) {
		final SegmentType segmentType = lookupSegmentType(pathComponent, parent);
		PathSegment current = null;
		switch (segmentType) {
		case NAV_PROP_W_ID:
		case NAV_PROP:
			current = createNavigationSegment(pathComponent);
			break;
		case SIMPLE_PROP:
		case COMPLEX_PROP:
			current =  createPropertySegment(pathComponent);
			break;
		case $VALUE:
			current = createValueSegment(pathComponent);
			break;
		case $LINKS:
			current = parent;
			current.linksToNext(true);
			break;
		case ENTITY_SET:
			throw new IllegalArgumentException("Content not expected after entity set without id specified");
			break;
			default:
				throw new IllegalArgumentException("Unexpected segment type");
		}
		return current;
	}

	/**
	 * Root segment builder method
	 * @param pathComponent
	 * @return root segment object
	 */
	private PathSegment createRootSegment(final String pathComponent) {
		Map<String, String> keyMap = null;
		String entitySetName = pathComponent;
		SegmentType segmentType = SegmentType.ENTITY_SET;
		if (Utils.hasKey(pathComponent)) {
			String[] pathSegmentAndKey = Utils.separateKey(pathComponent);
			entitySetName = pathSegmentAndKey[0];
			final String keyString = pathSegmentAndKey[1];
			keyMap = Utils.extractKeyMap(keyString);
			final EntityType entityType = wrapper.lookupEntityTypeForEntitySetName(entitySetName);
			Key entityKey = entityType.getKey();
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
