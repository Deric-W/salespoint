package org.salespointframework.core.product;

import org.salespointframework.core.money.Money;
import org.salespointframework.core.product.features.ProductFeatureType;

public interface ProductType {
	String getProductIdentifier();
	Money getPrice();
	Iterable<ProductFeatureType> getProductFeatureTypes(); // TODO umbenennen, je nachdem wie die Klasse heißen wird

}
