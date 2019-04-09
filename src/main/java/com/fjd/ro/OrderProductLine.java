package com.fjd.ro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fjd.model.SKU;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.dozer.Mapping;

import javax.persistence.*;
import javax.validation.Valid;
import java.math.BigDecimal;

@ApiModel(value = "SalesOrderProductLine", description = "Product line of sales order.")
public class OrderProductLine {
    @ApiModelProperty(value = "Unique idendifier of sales order product line.")
    private Long id;

    @Mapping("salesOrder.id")
    @JsonIgnore
    private Long orderId;

    @ApiModelProperty(value = "SKU.")
    //TODO
    //@GroupMapping(srcFields = { "id", "name", "code" }, destFields = { "sku.id", "skuName", "sku.code" })
    private SKU sku;


    @ApiModelProperty(value = "SKU quantity under sales UoM.", example = "12.345")
    @Valid
    private BigDecimal quantity;
}
