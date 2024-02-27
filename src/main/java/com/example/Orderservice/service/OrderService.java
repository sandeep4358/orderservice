package com.example.Orderservice.service;

import com.example.Orderservice.dto.InventoryResponse;
import com.example.Orderservice.dto.OrderLineItemsDto;
import com.example.Orderservice.dto.OrderNotificationEvent;
import com.example.Orderservice.dto.OrderRequest;
import com.example.Orderservice.model.Order;
import com.example.Orderservice.model.OrderLineItems;
import com.example.Orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

  //  private final KafkaTemplate<String,Object> kafkaTemplate ;

    @Autowired
    private WebClient.Builder webClientBuilder;

//    // http://localhost:8082/api/inventory?skuCode=iphone-13&skuCode=iphone13-red
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String>  skuCodelist = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        //call Inventory Service, and place order if product is in stock.

        InventoryResponse[] inventoryResponsArray = webClientBuilder.build().get()
                //.uri("http://192.168.0.102:8026/inventory-service/api/inventory/isInStock", //comment for using eureka registory for the service
                .uri("http://inventory-service/api/inventory/isInStock",  // this uri is the eureka service registry uri; i am not using any ip just the service name.
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodelist).build())
                .retrieve().bodyToMono(InventoryResponse[].class)
                .block();


       boolean allProductInStock = Arrays.stream(inventoryResponsArray)
               .allMatch(InventoryResponse::isInStock);
        log.info("Response from the inventory service is :: {} ",allProductInStock);
       if(allProductInStock){
           orderRepository.save(order);
            //notification sent to the notification server
           //kafkaTemplate.send("orderNotification",OrderNotificationEvent.builder().orderId(order.getOrderNumber()).build());

       }else{
           throw  new IllegalArgumentException("Product is not in stock, please try again later");
       }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

       // OrderLineItems.builder().price()n

        return orderLineItems;
    }
}
