package com.user.account.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
@AttributeOverride(name = "id", column = @Column(name = "ID"))
@SequenceGenerator(name = "HIBERNATE_SEQUENCE", sequenceName = "seq_user", initialValue = 1, allocationSize = 1)
public class User extends BaseDTO{
	
	@NotBlank(message = "First Name is mandatory")
	@Column(name = "first_name")
	private String firstName;
	
	@NotBlank(message = "Last Name is mandatory")
	@Column(name = "last_name")
	private String lastName;
	
	@NotBlank(message = "Email is mandatory")
	@Column(name = "email")
	private String email;
	
	public User() {
		
	}

	public User(String firstName, String lastName, String email) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
}
