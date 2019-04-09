package com.fjd.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SKU", description = "SKU Model")
public class SKU {
    @ApiModelProperty(value = "A unique idendifier of sku", readOnly = true)
    //@APIValidation(readOnly = true)
    private Long id;
}
