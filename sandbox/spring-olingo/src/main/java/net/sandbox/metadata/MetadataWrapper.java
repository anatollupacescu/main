package net.sandbox.metadata;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.Schema;

public class MetadataWrapper {

	private final ScenarioEdmProvider metadata;

	public MetadataWrapper(final ScenarioEdmProvider metadata) {
		this.metadata = metadata;
	}

	public String lookupNavigationProperty(String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for(Association assoc : schema.getAssociations()) {
				if(assoc.getEnd1().get)
			}
		}
		return null;
	}

	public Key lookupEntityType(final FullQualifiedName entityType) {
		for (Schema schema : metadata.getSchemas()) {
			for (EntityType type : schema.getEntityTypes()) {
				if (type.getName().equals(entityType.getName())) {
					return type.getKey();
				}
			}
		}
		throw new IllegalStateException(String.format("Entity type not found: '%s'", entityType.getName()));
	}

	public FullQualifiedName lookupEntitySet(final String pathComponent) {
		for (Schema schema : metadata.getSchemas()) {
			for (EntityContainer container : schema.getEntityContainers()) {
				for (EntitySet entitySet : container.getEntitySets()) {
					if (pathComponent.equals(entitySet.getName())) {
						return entitySet.getEntityType();
					}
				}
			}
		}
		throw new IllegalStateException(String.format("Entity set not found: '%s'", pathComponent));
	}
}
