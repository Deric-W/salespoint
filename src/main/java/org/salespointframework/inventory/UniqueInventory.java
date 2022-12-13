/*
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.salespointframework.inventory;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.core.SalespointRepository;
import org.salespointframework.inventory.InventoryItem.InventoryItemIdentifier;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.Assert;

/**
 * A {@link UniqueInventory} manages {@link UniqueInventoryItem}s, i.e. only a single {@link InventoryItem} can exist
 * per {@link Product}. This simplifies the lookup of the quantity available for a given {@link Product} to the lookup
 * of a single {@link UniqueInventoryItem}.
 * <p>
 * Also, on {@link Order} completion, a {@link UniqueInventoryItem} can be updated and get its {@link Quantity} deducted
 * based on the quantity of products ordered.
 *
 * @author Oliver Drotbohm
 * @since 7.2
 * @see MultiInventoryItem
 */
public interface UniqueInventory<T extends UniqueInventoryItem>
		extends Inventory<T>, SalespointRepository<T, InventoryItemIdentifier> {

	/**
	 * Returns the {@link InventoryItem} for the given {@link ProductIdentifier}.
	 *
	 * @param productIdentifier must not be {@literal null}.
	 * @return
	 */
	@Query("""
			select i from #{#entityName} i
			 where i.product.id = ?1
			""")
	Optional<T> findByProductIdentifier(ProductIdentifier productIdentifier);

	/**
	 * Returns the {@link InventoryItem} for the given {@link Product}.
	 *
	 * @param product must not be {@literal null}.
	 * @return
	 */
	default Optional<T> findByProduct(Product product) {
		return findByProductIdentifier(product.getId());
	}

	/**
	 * Returns whether the {@link UniqueInventoryItem} associated with the
	 * {@link ProductIdentifier} is available in exactly or more of the
	 * given {@link Quantity}.
	 * 
	 * @param productIdentifier must not be {@literal null}
	 * @param quantity          must not be {@literal null}
	 * @return
	 */
	default boolean hasSufficientQuantity(ProductIdentifier productIdentifier, Quantity quantity) {
		Assert.notNull(productIdentifier, "ProductIdentifier must not be null!");
		Assert.notNull(quantity, "Quantity must not be null!");

		var item = this.findByProductIdentifier(productIdentifier);

		return item.map(it -> it.hasSufficientQuantity(quantity))
				.orElseGet(quantity::isZeroOrNegative);
	}

	/**
	 * Returns whether the {@link UniqueInventory} contains enough stock to satisfy
	 * the {@link OrderLine}s of the given {@link Order}.
	 * <p>
	 * If there are multiple {@link OrderLine}s per {@link Product} their
	 * quantities are added together.
	 * 
	 * @param order must not be {@literal null}
	 * @return
	 */
	default boolean hasSufficientQuantity(Order order) {
		Map<ProductIdentifier, Quantity> quantities = order
				.getOrderLines()
				.stream()
				.collect(Collectors.groupingByConcurrent(OrderLine::getProductIdentifier,
						Collectors.reducing(Quantity.NONE, OrderLine::getQuantity, Quantity::add)));

		return quantities.entrySet().stream()
				.allMatch(entry -> this.hasSufficientQuantity(entry.getKey(), entry.getValue()));
	}
}
