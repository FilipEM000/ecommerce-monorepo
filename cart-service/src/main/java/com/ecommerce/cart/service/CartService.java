package com.ecommerce.cart.service;

import com.ecommerce.cart.command.AddCartItemCommand;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.exception.CartNotFoundException;
import com.ecommerce.cart.mapper.CartItemMapper;
import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Transactional(readOnly = true)
    public CartDto getCartById(Long id) {
        log.info("Process of fetching cart with ID: {} started", id);
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartDto createCart() {
        log.info("Process of creating cart started");
        Cart cart = new Cart();
        Cart saved = cartRepository.save(cart);
        log.info("Process of creating cart completed successfully");
        return cartMapper.toDto(saved);
    }

    @Transactional
    public CartDto addProductToCart(Long id, AddCartItemCommand addCartItemCommand) {
        log.info("Process of adding the product to the cart started");
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.hasSameProductAndConfigurations(addCartItemCommand.productId(), addCartItemCommand.configurations()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(addCartItemCommand.quantity());
        } else {
            CartItem newItem = cartItemMapper.toEntity(addCartItemCommand);
            cart.addItem(newItem);
        }
        Cart saved = cartRepository.save(cart);
        log.info("Product successfully added to cart with ID: {}", id);
        return cartMapper.toDto(saved);
    }
}
