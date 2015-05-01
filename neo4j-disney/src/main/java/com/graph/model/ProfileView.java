package com.graph.model;

import java.util.Date;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type="VISITED")
public class ProfileView {

    @GraphId
    Long id;

    @StartNode
    private Person profileOwner;

    @Fetch
    @EndNode
    private Person profileVisitor;

    private Date visitDate = new Date();

    public ProfileView() {
    }

    public ProfileView(Person profileOwner, Person visitor) {
        this.profileOwner = profileOwner;
        this.profileVisitor = visitor;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public Person getProfileVisitor() {
        return profileVisitor;
    }

    public Person getProfileOwner() {
        return profileOwner;
    }
    
    @Override
    public String toString() {
        return "ProfileView [id=" + id + ", profileVisitor=" + profileVisitor + ", visitDate=" + visitDate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((profileVisitor == null) ? 0 : profileVisitor.hashCode());
        result = prime * result + ((visitDate == null) ? 0 : visitDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileView other = (ProfileView) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (profileVisitor == null) {
            if (other.profileVisitor != null)
                return false;
        } else if (!profileVisitor.equals(other.profileVisitor))
            return false;
        if (visitDate == null) {
            if (other.visitDate != null)
                return false;
        } else if (!visitDate.equals(other.visitDate))
            return false;
        return true;
    }

}
