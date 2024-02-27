package com.example.Orderservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderNotificationEvent {
    private String orderId;
}
