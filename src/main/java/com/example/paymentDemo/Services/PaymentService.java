package com.example.paymentDemo.Services;

import com.example.paymentDemo.Repo.PaymentOrderRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentOrderRepo paymentOrderRepo;

    @Transactional
    public PaymentOrder createOrder(User user)
            throws RazorpayException {

        int amount = 49900;

        PaymentOrder order = new PaymentOrder();
        order.setUser(user);
        order.setAmount(amount);
        order.setStatus("CREATED");
        order.setPlanType("PREMIUM");

        paymentOrderRepo.save(order);

        JSONObject request = new JSONObject();

        request.put("amount", amount);
        request.put("currency", "INR");
        request.put(
                "receipt",
                "order_" + order.getId()
        );

        Order razorpayOrder =
                razorpayClient.orders.create(request);

        order.setRazorpayOrderId(
                razorpayOrder.get("id")
        );

        return paymentOrderRepo.save(order);
    }
}