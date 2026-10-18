package com.example.Shopping_Cart.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class UserDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Name cannot be blank")
	private String name;

	@NotBlank(message = "Mobile number cannot be blank")
	@Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
	private String mobileNumber;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Address cannot be blank")
	private String address;

	@NotBlank(message = "City cannot be blank")
	private String city;

	@NotBlank(message = "State cannot be blank")
	private String state;

	@NotBlank(message = "Pincode cannot be blank")
	private String pincode;

	@NotBlank(message = "Password cannot be blank")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	private String profileImage;
	private String role;
	private Boolean isEnable;

	private Boolean accountNonLocked = true;
	private Integer failedAttempt = 0;
	private Date lockTime;
	private Integer lockDuration = 0;

	private String resetToken;
	private Date tokenExpiration;

	public UserDtls() {
	}

	public UserDtls(Integer id, String name, String mobileNumber, String email, String address, String city,
			String state, String pincode, String password, String profileImage, String role, Boolean isEnable,
			Boolean accountNonLocked, Integer failedAttempt, Date lockTime, Integer lockDuration, String resetToken,
			Date tokenExpiration) {
		this.id = id;
		this.name = name;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.address = address;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.password = password;
		this.profileImage = profileImage;
		this.role = role;
		this.isEnable = isEnable;
		this.accountNonLocked = accountNonLocked != null ? accountNonLocked : true;
		this.failedAttempt = failedAttempt != null ? failedAttempt : 0;
		this.lockTime = lockTime;
		this.lockDuration = lockDuration != null ? lockDuration : 0;
		this.resetToken = resetToken;
		this.tokenExpiration = tokenExpiration;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Integer getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(Integer failedAttempt) {
		this.failedAttempt = failedAttempt;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public Integer getLockDuration() {
		return lockDuration;
	}

	public void setLockDuration(Integer lockDuration) {
		this.lockDuration = lockDuration;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Date getTokenExpiration() {
		return tokenExpiration;
	}

	public void setTokenExpiration(Date tokenExpiration) {
		this.tokenExpiration = tokenExpiration;
	}
}