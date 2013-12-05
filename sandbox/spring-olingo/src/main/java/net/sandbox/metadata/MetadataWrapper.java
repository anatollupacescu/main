package net.sandbox.metadata;

import net.sandbox.segment.PathSegment;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;

public class MetadataWrapper {

	private final ScenarioEdmProvider metadata;

	public MetadataWrapper(final ScenarioEdmProvider metadata) {
		this.metadata = metadata;
	}

	public boolean hasNavigation(PathSegment parent, String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for(Association assoc : schema.getAssociations()) {
				if(assoc.getName().equals(parent.getName())) {
					return assoc.getEnd1().getType().getName().equals(pathComponent);
				}
			}
		}
		return false;
	}
	
	public String lookupNavigationProperty(String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for(Association assoc : schema.getAssociations()) {
				if(assoc.getEnd1().getType().getName().equals(pathComponent)) {
					return assoc.getEnd2().getType().getName();
				}
			}
		}
		return null;
	}

	public EntityType lookupEntityTypeForFullQualifiedName(final FullQualifiedName entityType) {
		for (Schema schema : metadata.getSchemas()) {
			for (EntityType type : schema.getEntityTypes()) {
				if (type.getName().equals(entityType.getName())) {
					return type;
				}
			}
		}
		throw new IllegalStateException(String.format("Entity type not found: '%s'", entityType.getName()));
	}

	public EntitySet lookupEntitySet(final String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for (EntityContainer container : schema.getEntityContainers()) {
				for (EntitySet entitySet : container.getEntitySets()) {
					if (pathComponent.equals(entitySet.getName())) {
						return entitySet;
					}
				}
			}
		}
		throw new IllegalStateException(String.format("Entity set not found: '%s'", pathComponent));
	}
	
	public EntityType lookupEntityTypeForEntitySetName(final String pathComponent) {
		final EntitySet entitySet = lookupEntitySet(pathComponent);
		final FullQualifiedName entityTypeFullQualifiedName = entitySet.getEntityType();
		return lookupEntityTypeForFullQualifiedName(entityTypeFullQualifiedName);
	}

	public boolean hasProperty(String parent, String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for (EntityType type : schema.getEntityTypes()) {
				if (type.getName().equals(parent)) {
					return type.getProperties().contains(pathComponent);
				}
			}
		}
		throw new IllegalStateException(String.format("Entity type not found: '%s'", parent));
	}

	public boolean hasComplexProperty(String name, String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for (ComplexType type : schema.getComplexTypes()) {
				if (type.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasSimpleProperty(String name, String pathComponent) {
		// TODO Auto-generated method stub
		return false;
	}

}
