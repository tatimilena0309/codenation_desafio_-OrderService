package br.com.codenation.service;

import java.util.*;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

    private ProductRepository productRepository = new ProductRepositoryImpl();
    private static Double DESCONTO = 0.8;

    /**
     * Calculate the sum of all OrderItems
     */
    @Override
    public Double calculateOrderValue(List<OrderItem> items) {
        Double total = 0.0;
        Map<Long, List<Product>> produtosMap = findProductsById(items.stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(Product::getId));

        for (OrderItem item : items) {
            Product product = produtosMap.get(item.getProductId()).get(0);
            total += product.getIsSale() ?
                    product.getValue() * DESCONTO * item.getQuantity() :
                    product.getValue() * item.getQuantity();
        }
        return total;
    }

    /**
     * Map from idProduct List to Product Set
     */
    @Override
    public Set<Product> findProductsById(List<Long> ids) {
        return ids.stream()
                .filter(id -> (this.productRepository.findById(id)).isPresent())
                .map(id -> this.productRepository.findById(id).get())
                .collect(Collectors.toSet());

    }

    /**
     * Calculate the sum of all Orders(List<OrderIten>)
     */
    @Override
    public Double calculateMultipleOrders(List<List<OrderItem>> orders) {

        return orders.stream()
                .mapToDouble(this::calculateOrderValue)
                .sum();
    }

    /**
     * Group products using isSale attribute as the map key
     */
    @Override
    public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
        return findProductsById(productIds).stream()
                .collect(Collectors.groupingBy(Product::getIsSale));
    }
}