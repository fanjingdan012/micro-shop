package com.fjd.ro;


import com.fjd.model.BusinessObject;
import com.fjd.model.OrderProductLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.dozer.Mapping;

import javax.validation.Valid;
import java.util.List;

@ApiModel(value = "SalesOrder", description = "Sales Order Model.")
public class SalesOrder extends BusinessObject {
    @ApiModelProperty(value = "Unique idendifier of sales order.", readOnly = true)
    // @APIValidation(readOnly = true)
    private Long id;


    @ApiModelProperty(value = "Product line for sales order.")
    @Mapping("productLines")
    @Valid
    private List<OrderProductLine> productLines;
}
