package org.salespointframework.core.product;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.salespointframework.core.money.Money;
import org.salespointframework.util.Iterables;
import java.util.Objects;

/**
 * A persistent implementation of the {@link Product} interface.
 * @author Paul Henke
 * 
 */
@Entity
public class PersistentProduct implements Product, Comparable<PersistentProduct>
{
	@EmbeddedId
	@AttributeOverride(name = "id", column = @Column(name = "PRODUCT_ID"))
	private ProductIdentifier productIdentifier = new ProductIdentifier();

	private String name;
	private Money price;

	@ElementCollection
	private Set<ProductFeature> productFeatures = new HashSet<ProductFeature>();

	@ElementCollection
	private Set<String> categories = new HashSet<String>();

	/**
	 * Parameterless constructor required for JPA. Do not use.
	 */
	@Deprecated
	protected PersistentProduct()
	{
	}

	/**
	 * Creates a new PersistentProductType
	 * @param name the name of the PersistentProduct
	 * @param price the price of the PersistentProduct
	 */
	public PersistentProduct(String name, Money price)
	{
		this.name = Objects.requireNonNull(name, "name must not be null");
		this.price = Objects.requireNonNull(price, "price must not be null");
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
		if (other instanceof PersistentProduct)
		{
			return this.productIdentifier.equals(((PersistentProduct)other).productIdentifier);
		}
		return false;
	}

	@Override
	public final int hashCode()
	{
		return productIdentifier.hashCode();
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public final String getName()
	{
		return name;
	}

	@Override
	public Money getPrice()
	{
		return price;
	}

	@Override
	public final Iterable<ProductFeature> getProductFeatures()
	{
		return Iterables.of(productFeatures);
	}

	@Override
	public final ProductIdentifier getIdentifier()
	{
		return productIdentifier;
	}

	@Override
	public final boolean addProductFeature(ProductFeature productFeature)
	{
		Objects.requireNonNull(productFeature, "productFeature must not be null");
		return productFeatures.add(productFeature);
	}

	@Override
	public final boolean removeProductFeature(ProductFeatureIdentifier productFeatureIdentifier)
	{
		Objects.requireNonNull(productFeatureIdentifier, "productFeatureIdentifier must not be null");
		return productFeatures.remove(this.getProductFeature(productFeatureIdentifier));
	}
	
	@Override
	public ProductFeature getProductFeature(ProductFeatureIdentifier productFeatureIdentifier) {
		Objects.requireNonNull(productFeatureIdentifier, "productFeatureIdentifier must not be null");
		
		ProductFeature productFeature = null;
		for (ProductFeature pf : productFeatures) {
			if (pf.getIdentifier().equals(productFeatureIdentifier)) {
				productFeature = pf;
				break;
			}
		}
		return productFeature;
	}

	@Override
	public final boolean addCategory(String category)
	{
		Objects.requireNonNull(category, "category must not be null");
		return categories.add(category);
	}

	@Override
	public final boolean removeCategory(String category)
	{
		Objects.requireNonNull(category, "category must not be null");
		return categories.remove(category);
	}

	@Override
	public final Iterable<String> getCategories()
	{
		return Iterables.of(categories);
	}

	@Override
	public int compareTo(PersistentProduct other)
	{
		return this.name.compareTo(other.name);
	}


}
