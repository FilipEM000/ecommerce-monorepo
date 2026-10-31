package com.ecommerce.cart.controller;

import com.ecommerce.cart.command.AddCartItemCommand;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.dto.CartItemDto;
import com.ecommerce.cart.exception.CartNotFoundException;
import com.ecommerce.cart.model.SelectedConfiguration;
import com.ecommerce.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @Test
    void create_correctData_cartReturned() throws Exception {
        // GIVEN
        Long cartId = 1L;
        CartDto cartDto = new CartDto(cartId, List.of(), BigDecimal.ZERO);
        when(cartService.createCart()).thenReturn(cartDto);

        // WHEN & THEN
        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalPrice").value(0));
        verify(cartService, times(1)).createCart();
    }

    @Test
    void addItem_correctData_cartReturned() throws Exception {
        // GIVEN
        Long cartId = 1L;
        SelectedConfiguration config = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        AddCartItemCommand command = new AddCartItemCommand(
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        CartItemDto itemDto = new CartItemDto(
                100L,
                cartId,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        CartDto responseCart = new CartDto(cartId, List.of(itemDto), new BigDecimal("2200.00"));
        when(cartService.addProductToCart(eq(cartId), any(AddCartItemCommand.class))).thenReturn(responseCart);

        // WHEN & THEN
        mockMvc.perform(post("/carts/{id}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items[0].id").value(100))
                .andExpect(jsonPath("$.items[0].cartId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].productName").value("test_product_name"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.items[0].configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.items[0].configurations[0].additionalPrice").value(100.00))
                .andExpect(jsonPath("$.totalPrice").value(2200.00));
        verify(cartService, times(1)).addProductToCart(eq(cartId), any(AddCartItemCommand.class));
    }

    @Test
    void addItem_cartNotFound_notFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingCartId = 999L;
        AddCartItemCommand command = new AddCartItemCommand(
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                1,
                List.of()
        );
        when(cartService.addProductToCart(eq(nonExistingCartId), any(AddCartItemCommand.class)))
                .thenThrow(new CartNotFoundException(nonExistingCartId));

        // WHEN & THEN
        mockMvc.perform(post("/carts/{id}/items", nonExistingCartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart with ID: 999 was not found"))
                .andExpect(jsonPath("$.status").value(404));
        verify(cartService, times(1)).addProductToCart(eq(nonExistingCartId), any(AddCartItemCommand.class));
    }

    @Test
    void getCartById_cartExists_cartReturned() throws Exception {
        // GIVEN
        Long cartId = 1L;
        SelectedConfiguration config = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        CartItemDto itemDto = new CartItemDto(
                100L,
                cartId,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        CartDto cartDto = new CartDto(cartId, List.of(itemDto), new BigDecimal("2200.00"));

        when(cartService.getCartById(cartId)).thenReturn(cartDto);

        // WHEN & THEN
        mockMvc.perform(get("/carts/{id}", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(2200.00))
                .andExpect(jsonPath("$.items[0].id").value(100))
                .andExpect(jsonPath("$.items[0].cartId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].productName").value("test_product_name"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.items[0].configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.items[0].configurations[0].additionalPrice").value(100.00));

        verify(cartService, times(1)).getCartById(cartId);
    }

    @Test
    void getCartById_cartNotFound_notFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingCartId = 999L;
        when(cartService.getCartById(nonExistingCartId))
                .thenThrow(new CartNotFoundException(nonExistingCartId));

        // WHEN & THEN
        mockMvc.perform(get("/carts/{id}", nonExistingCartId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart with ID: 999 was not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(cartService, times(1)).getCartById(nonExistingCartId);
    }
}
