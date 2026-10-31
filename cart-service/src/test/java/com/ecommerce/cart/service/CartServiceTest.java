package com.ecommerce.cart.service;

import com.ecommerce.cart.command.AddCartItemCommand;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.exception.CartNotFoundException;
import com.ecommerce.cart.mapper.CartItemMapper;
import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.SelectedConfiguration;
import com.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest {
    private CartRepository cartRepository;
    private CartMapper cartMapper;
    private CartItemMapper cartItemMapper;
    private CartService cartService;

    @BeforeEach
    void setup() {
        this.cartRepository = Mockito.mock(CartRepository.class);
        this.cartItemMapper = Mappers.getMapper(CartItemMapper.class);
        this.cartMapper = Mappers.getMapper(CartMapper.class);
        ReflectionTestUtils.setField(cartMapper, "cartItemMapper", cartItemMapper);
        this.cartService = new CartService(cartRepository, cartMapper, cartItemMapper);
    }

    @Test
    void createCart_correctData_cartReturned() {
        // GIVEN
        Long cartId = 1L;
        Cart savedCart = new Cart(cartId, new ArrayList<>());
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        // WHEN
        CartDto result = cartService.createCart();

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(cartId, result.id()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertTrue(result.items().isEmpty()),
                () -> Assertions.assertEquals(BigDecimal.ZERO, result.totalPrice())
        );
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addProductToCart_newItem_itemAddedToCart() {
        // GIVEN
        Long cartId = 1L;
        Long productId = 10L;
        Cart cart = new Cart(cartId, new ArrayList<>());
        SelectedConfiguration configuration = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        AddCartItemCommand command = new AddCartItemCommand(
                productId,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(configuration)
        );
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        CartDto result = cartService.addProductToCart(cartId, command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(cartId, result.id()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertEquals(1, result.items().size()),
                () -> Assertions.assertEquals(productId, result.items().getFirst().productId()),
                () -> Assertions.assertEquals("test_product_name", result.items().getFirst().productName()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(2, result.items().getFirst().quantity()),
                () -> Assertions.assertNotNull(result.items().getFirst().configurations()),
                () -> Assertions.assertEquals(1, result.items().getFirst().configurations().size()),
                () -> Assertions.assertEquals("test_category", result.items().getFirst().configurations().getFirst().getCategory()),
                () -> Assertions.assertEquals("test_value", result.items().getFirst().configurations().getFirst().getValue()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.items().getFirst().configurations().getFirst().getAdditionalPrice()),
                () -> Assertions.assertEquals(new BigDecimal("2200.00"), result.totalPrice())
        );
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addProductToCart_existingItemWithSameConfigurations_quantityIncreased() {
        // GIVEN
        Long cartId = 1L;
        Long productId = 10L;
        SelectedConfiguration existingConfig = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        CartItem existingItem = new CartItem(
                100L,
                cartId,
                productId,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(existingConfig)
        );
        List<CartItem> items = new ArrayList<>();
        items.add(existingItem);
        Cart cart = new Cart(cartId, items);
        SelectedConfiguration newConfig = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        AddCartItemCommand command = new AddCartItemCommand(
                productId,
                "test_product_name",
                new BigDecimal("1000.00"),
                3,
                List.of(newConfig)
        );
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        CartDto result = cartService.addProductToCart(cartId, command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(cartId, result.id()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertEquals(1, result.items().size()),
                () -> Assertions.assertEquals(productId, result.items().getFirst().productId()),
                () -> Assertions.assertEquals("test_product_name", result.items().getFirst().productName()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(5, result.items().getFirst().quantity()), // 2 + 3 = 5
                () -> Assertions.assertEquals(1, result.items().getFirst().configurations().size()),
                () -> Assertions.assertEquals(new BigDecimal("5500.00"), result.totalPrice())
        );
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addProductToCart_cartDoesNotExist_cartNotFoundExceptionThrown() {
        // GIVEN
        Long nonExistingCartId = 999L;
        AddCartItemCommand command = new AddCartItemCommand(
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                1,
                List.of()
        );
        when(cartRepository.findById(nonExistingCartId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(CartNotFoundException.class)
                .isThrownBy(() -> cartService.addProductToCart(nonExistingCartId, command))
                .extracting(CartNotFoundException::getMessage)
                .isEqualTo("Cart with ID: 999 was not found");
        verify(cartRepository, times(1)).findById(nonExistingCartId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartById_cartExists_cartReturned() {
        // GIVEN
        Long cartId = 1L;
        SelectedConfiguration config = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        CartItem item = new CartItem(
                100L,
                cartId,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        Cart cart = new Cart(cartId, List.of(item));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // WHEN
        CartDto result = cartService.getCartById(cartId);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(cartId, result.id()),
                () -> Assertions.assertEquals(new BigDecimal("2200.00"), result.totalPrice()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertEquals(1, result.items().size()),
                () -> Assertions.assertEquals(10L, result.items().getFirst().productId()),
                () -> Assertions.assertEquals("test_product_name", result.items().getFirst().productName()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(2, result.items().getFirst().quantity()),
                () -> Assertions.assertEquals(1, result.items().getFirst().configurations().size()),
                () -> Assertions.assertEquals("test_category", result.items().getFirst().configurations().getFirst().getCategory()),
                () -> Assertions.assertEquals("test_value", result.items().getFirst().configurations().getFirst().getValue()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.items().getFirst().configurations().getFirst().getAdditionalPrice())
        );
        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void getCartById_cartDoesNotExist_cartNotFoundExceptionThrown() {
        // GIVEN
        Long nonExistingCartId = 999L;
        when(cartRepository.findById(nonExistingCartId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(CartNotFoundException.class)
                .isThrownBy(() -> cartService.getCartById(nonExistingCartId))
                .extracting(CartNotFoundException::getMessage)
                .isEqualTo("Cart with ID: 999 was not found");

        verify(cartRepository, times(1)).findById(nonExistingCartId);
    }
}
