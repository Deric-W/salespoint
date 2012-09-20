package org.salespointframework.core.user;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.salespointframework.util.Iterables;
import org.salespointframework.util.Utility;

public class TransientUser implements User, Comparable<TransientUser> {

	private UserIdentifier userIdentifier;
	private String hashedPassword;
	private Set<Capability> capabilities = new TreeSet<Capability>();
	
	
	public TransientUser(UserIdentifier userIdentifier, String password, Capability... capabilities)
	{
		this.userIdentifier = Objects.requireNonNull(userIdentifier, "userIdentifier must not be null");
		
		Objects.requireNonNull(password, "password must not be null");
		this.hashedPassword = Utility.hashPassword(password);
		
		Objects.requireNonNull(capabilities, "capabilities must not be null");
		this.capabilities.addAll(Arrays.asList(capabilities));
	}
	
	@Override
	public UserIdentifier getIdentifier()
	{
		return userIdentifier;
	}
	
	@Override
	public boolean addCapability(Capability capability)
	{
		Objects.requireNonNull(capability, "capability must not be null");
		return capabilities.add(capability);
	}

	@Override
	public boolean removeCapability(Capability capability)
	{
		Objects.requireNonNull(capability, "capability must not be null");
		return capabilities.remove(capability);
	}

	@Override
	public boolean hasCapability(Capability capability)
	{
		Objects.requireNonNull(capability, "capability must not be null");
		return capabilities.contains(capability);
	}

	@Override
	public Iterable<Capability> getCapabilities()
	{
		return Iterables.of(capabilities);
	}

	@Override
	public boolean verifyPassword(String password)
	{
		Objects.requireNonNull(password, "password must not be null");
		
		return Utility.verifyPassword(password, this.hashedPassword);
	}

	@Override
	public void changePassword(String password)
	{
		Objects.requireNonNull(password, "password");
		this.hashedPassword = Utility.hashPassword(password);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
		{
			return false;
		}
		if (other == this)
		{
			return true;
		}
		if (other instanceof PersistentUser)
		{
			return this.userIdentifier.equals(((TransientUser)other).userIdentifier);
		}
		return false;
		
	}

	@Override
	public final int hashCode()
	{
		return userIdentifier.hashCode();
	}

	@Override
	public String toString()
	{
		return userIdentifier.toString();
	}

	@Override
	public int compareTo(TransientUser other)
	{
		return this.userIdentifier.compareTo(other.getIdentifier());
	}
}
