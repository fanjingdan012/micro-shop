package com.fjd.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderProductLine {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SALESORDERID", nullable = true)
    private SalesOrder salesOrder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_Id", nullable = true)
    private SKU sku;

    private BigDecimal quantity;
}
