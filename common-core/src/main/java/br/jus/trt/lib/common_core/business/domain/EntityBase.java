package br.jus.trt.lib.common_core.business.domain;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This is the base {@link MappedSuperclass} for all the entities in the
 * application. It implements the Id and the created and modified time stamps.
 * It also includes the callback methods for populating the timestamp values,
 * and implements hashcode and equals based on the id.
 * <p/>
 * The abstract {@link EntityBase#getDisplayText()} method provides a nice way
 * to get a textual representation of who or what the entity is (course or
 * person name). Since it is baked into the superclass, it will be available for all entity classes.
 * 
 * @author Andy Gibson
 * 
 */
@SuppressWarnings({ "serial", "rawtypes" })
@MappedSuperclass
public abstract class EntityBase<PK> implements Entity<PK> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private PK id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedOn;

	public PK getId() {
		return id;
	}

	public void setId(PK id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	protected String getDisplayText() {
		return this.getClass().getSimpleName() + " [" + getId() + "]";
	}
	
	@Override
	public String toString() {
		return getDisplayText();
	}

	@PrePersist
	public void initTimeStamps() {
		// we do this for the purpose of the demo, this lets us create our own
		// creation dates. Typically we would just set the createdOn field.
		if (createdOn == null) {
			createdOn = new Date();
		}
		modifiedOn = createdOn;
	}

	@PreUpdate
	public void updateTimeStamp() {
		modifiedOn = new Date();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntityBase))
			return false;
		EntityBase other = (EntityBase) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}