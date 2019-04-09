package com.fjd.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SalesOrder extends BusinessObject {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy(value = "lineNumber")
    private List<OrderProductLine> productLines = new ArrayList<OrderProductLine>();
}
