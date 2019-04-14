package com.ziemsky.springdata.jpa.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "entity_with_generated_id")
public class EntityWithGeneratedId {

    // Plugging custom generator which returns provided value if available or generates one if not:
    // https://stackoverflow.com/questions/3194721/bypass-generatedvalue-in-hibernate-merge-data-not-in-db

    @Id
    @Basic
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "UseIdOrGenerate")
    @GenericGenerator(name = "UseIdOrGenerate", strategy = "com.ziemsky.springdata.jpa.entities.UseIdOrGenerate")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "text_prop")
    private String textProp;

    public EntityWithGeneratedId() {
    }

    public EntityWithGeneratedId(final Integer id, final String textProp) {
        this.id = id;
        this.textProp = textProp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getTextProp() {
        return textProp;
    }

    public void setTextProp(final String textProp) {
        this.textProp = textProp;
    }

    @Override public String toString() {
        return "EntityWithGeneratedId{" +
            "id=" + id +
            ", textPropD='" + textProp + '\'' +
            '}';
    }


    @Override public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EntityWithGeneratedId that = (EntityWithGeneratedId) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(textProp, that.textProp);
    }

    @Override public int hashCode() {
        return Objects.hash(id, textProp);
    }
}
