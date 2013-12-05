/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sandbox.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTargetPath;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.AssociationSetEnd;
import org.apache.olingo.odata2.api.edm.provider.ComplexProperty;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.CustomizableFeedMappings;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Facets;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.Mapping;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.google.common.collect.ImmutableList;

/**
 * Provider for the entity data model used in the reference scenario
 * 
 */
public class ScenarioEdmProvider {

	public static final String SIG = "RefScenario";

	private static final FullQualifiedName FQ_NAME_EMPLOYEE = new FullQualifiedName(SIG, "Employee");
	private static final FullQualifiedName FQ_NAME_TEAM = new FullQualifiedName(SIG, "Team");

	private static final FullQualifiedName FQ_NAME_COMPLEX_CITY = new FullQualifiedName(SIG, "c_City");

	private static final FullQualifiedName FQ_NAME_ASSOC_TEAM_EMPLOYESS = new FullQualifiedName(SIG,
			"TeamEmployees");

	private static final String ROLE_EMPLOYEES = "r_Employees";
	private static final String ROLE_TEAM = "r_Team";

	private static final String ENTITY_CONTAINER = "Container";

	private static final String ENTITY_SET_EMPLOYEES = "Employees";
	private static final String ENTITY_SET_TEAMS = "Teams";

	final List<Schema> schemas;
	
	public ScenarioEdmProvider() throws ODataException {
		schemas = createSchemas();
	}
	
	public List<Schema> getSchemas() {
		return schemas;
	}
	
	private List<Schema> createSchemas() throws ODataException {

		Schema schema = new Schema();
		schema.setNamespace(SIG);

		/* entity types */
		EntityType employeeType = getEntityType(FQ_NAME_EMPLOYEE);
		EntityType teamType = getEntityType(FQ_NAME_TEAM);
		schema.setEntityTypes(ImmutableList.of(employeeType, teamType));

		/* complex type */
		ComplexType cityComplexType = getComplexType(FQ_NAME_COMPLEX_CITY);
		schema.setComplexTypes(ImmutableList.of(cityComplexType));

		/* associations */
		Association associationTeamEmployee = getAssociation(FQ_NAME_ASSOC_TEAM_EMPLOYESS);
		schema.setAssociations(ImmutableList.of(associationTeamEmployee));

		/* CONTAINER */
		EntityContainer entityContainer = new EntityContainer();
		entityContainer.setName(ENTITY_CONTAINER).setDefaultEntityContainer(true);

		/* entity sets */
		EntitySet employeeEntitySet = getEntitySet(ENTITY_CONTAINER, ENTITY_SET_EMPLOYEES);
		EntitySet teamsEntitySet = getEntitySet(ENTITY_CONTAINER, ENTITY_SET_TEAMS);
		entityContainer.setEntitySets(ImmutableList.of(teamsEntitySet, employeeEntitySet));

		AssociationSet associationSet = getAssociationSet(ENTITY_CONTAINER, FQ_NAME_ASSOC_TEAM_EMPLOYESS, ENTITY_SET_TEAMS);
		entityContainer.setAssociationSets(ImmutableList.of(associationSet));

		schema.setEntityContainers(Arrays.asList(entityContainer));

		return ImmutableList.of(schema);
	}

	private EntityType getEntityType(final FullQualifiedName edmFQName) throws ODataException {
		if (SIG.equals(edmFQName.getNamespace())) {
			if (FQ_NAME_EMPLOYEE.getName().equals(edmFQName.getName())) {
				List<Property> properties = new ArrayList<Property>();
				properties.add(new SimpleProperty().setName("EmployeeId").setType(EdmSimpleTypeKind.String)
						.setFacets(new Facets().setNullable(false))
						.setMapping(new Mapping().setInternalName("getId")));
				properties.add(new SimpleProperty()
						.setName("EmployeeName")
						.setType(EdmSimpleTypeKind.String)
						.setCustomizableFeedMappings(
								new CustomizableFeedMappings()
										.setFcTargetPath(EdmTargetPath.SYNDICATION_TITLE)));
				properties.add(new SimpleProperty().setName("ManagerId").setType(EdmSimpleTypeKind.String)
						.setMapping(new Mapping().setInternalName("getManager.getId")));
				properties.add(new SimpleProperty().setName("RoomId").setType(EdmSimpleTypeKind.String)
						.setMapping(new Mapping().setInternalName("getRoom.getId")));
				properties.add(new SimpleProperty().setName("TeamId").setType(EdmSimpleTypeKind.String)
						.setFacets(new Facets().setMaxLength(2))
						.setMapping(new Mapping().setInternalName("getTeam.getId")));
				properties.add(new SimpleProperty().setName("Age").setType(EdmSimpleTypeKind.Int16));
				properties.add(new SimpleProperty()
						.setName("EntryDate")
						.setType(EdmSimpleTypeKind.DateTime)
						.setFacets(new Facets().setNullable(true))
						.setCustomizableFeedMappings(
								new CustomizableFeedMappings()
										.setFcTargetPath(EdmTargetPath.SYNDICATION_UPDATED)));
				properties.add(new SimpleProperty().setName("ImageUrl").setType(EdmSimpleTypeKind.String)
						.setMapping(new Mapping().setInternalName("getImageUri")));
				List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();
				navigationProperties.add(new NavigationProperty().setName("ne_Team")
						.setRelationship(FQ_NAME_ASSOC_TEAM_EMPLOYESS).setFromRole(ROLE_EMPLOYEES)
						.setToRole(ROLE_TEAM));
				return new EntityType().setName(FQ_NAME_EMPLOYEE.getName()).setProperties(properties)
						.setHasStream(true).setKey(getKey("guid"))
						.setNavigationProperties(navigationProperties);

			} else if (FQ_NAME_TEAM.getName().equals(edmFQName.getName())) {
				List<Property> properties = new ArrayList<Property>();
				properties.add(new SimpleProperty().setName("isScrumTeam").setType(EdmSimpleTypeKind.Boolean)
						.setFacets(new Facets().setNullable(true))
						.setMapping(new Mapping().setInternalName("isScrumTeam")));
		        properties.add(new ComplexProperty().setName("City").setType(FQ_NAME_COMPLEX_CITY).setFacets(
		                new Facets().setNullable(false)));
				List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();
				navigationProperties.add(new NavigationProperty().setName("nt_Employees")
						.setRelationship(FQ_NAME_ASSOC_TEAM_EMPLOYESS).setFromRole(ROLE_TEAM)
						.setToRole(ROLE_EMPLOYEES));
				return new EntityType().setName(FQ_NAME_TEAM.getName()).setProperties(properties)
						.setNavigationProperties(navigationProperties);
			}
		}

		return null;
	}

	private ComplexType getComplexType(final FullQualifiedName edmFQName) throws ODataException {
		if (SIG.equals(edmFQName.getNamespace())) {
			if (FQ_NAME_COMPLEX_CITY.getName().equals(edmFQName.getName())) {
				List<Property> properties = new ArrayList<Property>();
				properties.add(new SimpleProperty().setName("PostalCode").setType(EdmSimpleTypeKind.String));
				properties.add(new SimpleProperty().setName("CityName").setType(EdmSimpleTypeKind.String));
				return new ComplexType().setName(FQ_NAME_COMPLEX_CITY.getName()).setProperties(properties);
			}
		}

		return null;
	}

	private Association getAssociation(final FullQualifiedName edmFQName) throws ODataException {
		if (SIG.equals(edmFQName.getNamespace())) {
			if (FQ_NAME_ASSOC_TEAM_EMPLOYESS.getName().equals(edmFQName.getName())) {
				return new Association()
						.setName(FQ_NAME_ASSOC_TEAM_EMPLOYESS.getName())
						.setEnd1(
								new AssociationEnd().setType(FQ_NAME_EMPLOYEE).setRole(ROLE_EMPLOYEES)
										.setMultiplicity(EdmMultiplicity.MANY))
						.setEnd2(
								new AssociationEnd().setType(FQ_NAME_TEAM).setRole(ROLE_TEAM)
										.setMultiplicity(EdmMultiplicity.ONE));
			}
		}

		return null;
	}

	private EntitySet getEntitySet(final String entityContainer, final String name) throws ODataException {
		if (ENTITY_CONTAINER.equals(entityContainer)) {
			if (ENTITY_SET_EMPLOYEES.equals(name)) {
				return new EntitySet().setName(name).setEntityType(FQ_NAME_EMPLOYEE);
			} else if (ENTITY_SET_TEAMS.equals(name)) {
				return new EntitySet().setName(name).setEntityType(FQ_NAME_TEAM);
			}
		}
		return null;
	}

	private AssociationSet getAssociationSet(final String entityContainer,
			final FullQualifiedName association,
			final String sourceEntitySetRole) throws ODataException {
		if (ENTITY_CONTAINER.equals(entityContainer)) {
			if (FQ_NAME_ASSOC_TEAM_EMPLOYESS.equals(association)) {
				return new AssociationSet()
						.setName(FQ_NAME_ASSOC_TEAM_EMPLOYESS.getName())
						.setAssociation(FQ_NAME_ASSOC_TEAM_EMPLOYESS)
						.setEnd1(new AssociationSetEnd().setRole(ROLE_TEAM).setEntitySet(ENTITY_SET_TEAMS))
						.setEnd2(
								new AssociationSetEnd().setRole(ROLE_EMPLOYEES).setEntitySet(
										ENTITY_SET_EMPLOYEES));
			}
		}

		return null;
	}

	private Key getKey(final String... keyNames) {
		List<PropertyRef> keyProperties = new ArrayList<PropertyRef>();
		for (final String keyName : keyNames) {
			keyProperties.add(new PropertyRef().setName(keyName));
		}
		return new Key().setKeys(keyProperties);
	}
}
