package com.example.loginapp.model.interator;

import com.example.loginapp.model.entity.Order;
import com.example.loginapp.model.entity.OrderProduct;
import com.example.loginapp.model.entity.OrderStatus;
import com.example.loginapp.model.entity.Voucher;
import com.example.loginapp.model.listener.PaymentOptionListener;
import com.example.loginapp.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PaymentOptionInterator {

    private final PaymentOptionListener listener;

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public PaymentOptionInterator(PaymentOptionListener listener) {
        this.listener = listener;
    }

    public void createOrder(Order order, boolean isSaveDeliveryAddress, boolean isProductsFroShoppingCart) {

        String uid = user.getUid();

        if (isSaveDeliveryAddress)
            Constant.deliveryAddressRef
                    .child(uid)
                    .child(order.getDeliveryAddress().getDeliveryAddressId())
                    .setValue(order.getDeliveryAddress());

        if (isProductsFroShoppingCart)
            for (OrderProduct product : order.getOrderProducts())
                Constant.cartRef
                        .child(uid)
                        .child(String.valueOf(product.getId()))
                        .removeValue();

        Voucher voucher  = order.getVoucher();
        if (voucher != null)
            Constant.myVoucherRef.child(uid).child(voucher.getVoucherCode()).removeValue();

        order.setOrderId("SA" + System.currentTimeMillis());
        order.setOrderDate(System.currentTimeMillis());
        order.setOrderStatus(OrderStatus.Processing);

        Constant.orderRef
                .child(uid)
                .child(order.getOrderId())
                .setValue(order)
                .addOnCompleteListener(task -> listener.goOrderSuccessScreen())
                .addOnFailureListener(task -> listener.onMessage("An error occurred, Please try again"));
    }
}
