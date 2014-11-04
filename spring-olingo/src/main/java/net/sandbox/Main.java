package net.sandbox;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import net.sandbox.metadata.MetadataWrapper;
import net.sandbox.metadata.ScenarioEdmProvider;
import net.sandbox.segment.PathSegment;
import net.sandbox.segment.SegmentType;

import org.apache.olingo.odata2.api.edm.provider.ComplexProperty;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.apache.olingo.odata2.api.exception.ODataException;

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
		PathSegment parent = createRootSegment(pathComponents[0]);
		for (int i = 1; i < pathComponents.length; i++) {
			final String pathComponent = pathComponents[i];
			PathSegment segment = createSegment(pathComponent, parent);
			parent.setNext(segment);
			segment.setPrev(parent);
			parent = segment;
		}
		/*rewind*/
		while (parent.hasPrev()) {
			parent = parent.getPrev();
		}
		return parent;
	}
	
	private PathSegment createSegment(String pathComponent, PathSegment parent) {
		switch (parent.getType()) {
		case ENTITY_SET_W_ID:
		case NAV_PROP_W_ID:
			if(hasSimpleProperty(parent, pathComponent)) {
				return createSimplePropertySegment(parent, pathComponent);
			} else if (hasComplexProperty(parent, pathComponent)) {
				return createComplexSegment(parent, pathComponent);
			} else if (hasNavigationProperty(parent, pathComponent)) {
				return createNavigationSegment(parent, pathComponent);
			}
			throw new IllegalStateException(String.format("Unrecognized segment after '%s/'", parent.getName()));
		case SIMPLE_PROP:
			if (VALUE_LITERAL.equals(pathComponent)) {
				return createValueSegment(parent, pathComponent);
			}
			throw new IllegalStateException(String.format("Content not expected after simple property '%s/'", parent.getName()));
		case COMPLEX_PROP:
			if(hasSimpleProperty(parent, pathComponent)) {
				return createSimplePropertySegment(parent, pathComponent);
			}
			throw new IllegalStateException(String.format("Only simple property expected after '%s/'", parent.getName()));
		case $LINKS:
			parent.linksToNext(true);
			return parent;
		case $VALUE:
		case NAV_PROP:
		case ENTITY_SET:
			throw new IllegalStateException(String.format("Content not expected after segment '%s/'", parent.getName()));
		default:
			throw new IllegalStateException("Unexpected segment type");
		}
	}

	private PathSegment createNavigationSegment(PathSegment parent, String pathComponent) {
		// TODO Auto-generated method stub
		return null;
	}

	private PathSegment createComplexSegment(PathSegment parent, String pathComponent) {
		// TODO Auto-generated method stub
		return null;
	}

	private PathSegment createSimplePropertySegment(PathSegment parent, String pathComponent) {
		PathSegment segment = new PathSegment();
		segment.setType(SegmentType.SIMPLE_PROP);
		segment.setName(pathComponent);
		return segment;
	}
	
	private PathSegment createValueSegment(PathSegment parent, String pathComponent) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean hasNavigationProperty(PathSegment parent, String pathComponent) {
		EntityType type = (EntityType)parent.getEdmType();
		for(NavigationProperty prop : type.getNavigationProperties()) {
			if(prop.getName().equals(pathComponent)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasComplexProperty(PathSegment parent, String pathComponent) {
		ComplexType type = parent.getEdmType();
		for(Property prop : type.getProperties()) {
			if(prop.getName().equals(pathComponent) && prop instanceof ComplexProperty) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasSimpleProperty(PathSegment parent, String pathComponent) {
		ComplexType type = parent.getEdmType();
		for(Property prop : type.getProperties()) {
			if(prop.getName().equals(pathComponent) && prop instanceof SimpleProperty) {
				return true;
			}
		}
		return false;
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
		}
		final EntityType entityType = wrapper.lookupEntityTypeForEntitySetName(entitySetName);
		if(keyMap != null) {
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
		segment.setEdmType(entityType);
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
