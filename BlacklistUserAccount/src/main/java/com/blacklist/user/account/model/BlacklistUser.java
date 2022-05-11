package com.blacklist.user.account.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "blacklist_user")
@AttributeOverride(name = "id", column = @Column(name = "ID"))
@SequenceGenerator(name = "HIBERNATE_SEQUENCE", sequenceName = "seq_blacklist_user", initialValue = 1, allocationSize = 1)
public class BlacklistUser extends BaseDTO{
	
	@NotBlank(message = "First Name is mandatory")
	@Column(name = "first_name")
	private String firstName;
	
	@NotBlank(message = "Last Name is mandatory")
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "ACTIVE")
	@Pattern(regexp = "^(true|false)$", message = "Active field allowed input: true or false")
	private String active;
	
	public BlacklistUser() {
		
	}

	public BlacklistUser(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "BlacklistUser [firstName=" + firstName + ", lastName=" + lastName + ", active=" + active + ", error=" + super.getErrors() + "]";
	}
	
}
