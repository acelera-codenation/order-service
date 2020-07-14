package br.com.codenation.service;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private ProductRepository productRepository = new ProductRepositoryImpl();

    private Double calculateItemPrice(OrderItem item) {
        Optional<Product> product = productRepository.findById(item.getProductId());
        if (product.isPresent()) {
            double discount = product.get().getIsSale() ? 0.8 : 1.0;
            return item.getQuantity() * (product.get().getValue() * discount);
        } else {
            return 0.0;
        }
    }

    /**
     * Calculate the sum of all OrderItems
     */
    @Override
    public Double calculateOrderValue(List<OrderItem> items) {
        return items.stream().mapToDouble(this::calculateItemPrice).sum();
    }

    /**
     * Map from idProduct List to Product Set
     */
    @Override
    public Set<Product> findProductsById(List<Long> ids) {
        return productRepository.findAll()
                .stream().filter(item -> ids.contains(item.getId()))
                .collect(Collectors.toSet());
    }

    /**
     * Calculate the sum of all Orders(List<OrderIten>)
     */
    @Override
    public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
        return orders.stream().mapToDouble(this::calculateOrderValue).sum();
    }

    /**
     * Group products using isSale attribute as the map key
     */
    @Override
    public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
        Set<Product> products = findProductsById(productIds);
        return products.stream().collect(Collectors.groupingBy(Product::getIsSale));
    }

}