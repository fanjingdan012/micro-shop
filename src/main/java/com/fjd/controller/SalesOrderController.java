//package com.fjd.controller;
//
//import com.fjd.model.SalesOrder;
//import com.fjd.service.BOService;
//import com.fjd.service.SalesOrderService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//@Controller
//@RequestMapping(path="/SalesOrders")
//public class SalesOrderController {
//    private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderController.class);
//
//    @Autowired
//    private SalesOrderService salesOrderService;
//    @ApiOperation(value = "Get a sales order.", notes = "Get a sales order by id.", response = SalesOrder.class)
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "Find the sales order.") })
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    public ResponseEntity<SalesOrder> get(
//            @ApiParam(name = "id", value = "Sales order id.", required = true) @PathVariable long id,
//            @ApiParam(name = "expand", value = "Expanding related objects.", required = false) @RequestParam(required = false) String expand,
//            @ApiParam(name = "select", value = "Selecting a set of fields.", required = false) @RequestParam(required = false) String select) {
//        LOGGER.info("Get sales order by id {}.", id);
//
//        SalesOrder salesOrder = salesOrderService.loadAndConvertRO(id, expand, select);
//
//        return new ResponseEntity<>(salesOrder, HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "Get a list of sales  orders.", notes = "Get a list of sales orders with paging (optional).", response = SalesOrder[].class)
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "List of sales orders or emtpy list.") })
//    @RequestMapping(method = RequestMethod.GET)
//    public ResponseEntity<SalesOrder[]> list(
//            @ApiParam(name = "filter", value = "Filtering conditions.", required = false) @RequestParam(required = false) String filter,
//            @ApiParam(name = "expand", value = "Expand related objects.", required = false) @RequestParam(required = false) String expand,
//            @ApiParam(name = "select", value = "Selecting a set of fields.", required = false) @RequestParam(required = false) String select,
//            @ApiParam(name = "orderby", value = "Sequence of selected records.", required = false) @RequestParam(required = false) String orderby,
//            @ApiParam(name = "limit", value = "Maximum number of items allowed to be fetched.", required = false) @RequestParam(required = false) Integer limit,
//            @ApiParam(name = "offset", value = "Number of skipped items in the result set.", required = false) @RequestParam(required = false) Integer offset) {
//        LOGGER.info("List sales order {} {}.", filter, expand);
//
//        SalesOrder[] salesOrders = salesOrderService.queryAndConvertROs(filter, expand, select, orderby, limit, offset)
//                .toArray(new SalesOrder[] {});
//
//        return new ResponseEntity<>(salesOrders, HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "Create a sales order.", notes = "Create a sales order using the given data.", response = Long.class)
//    @ApiResponses(value = { @ApiResponse(code = 201, message = "The id of the newly created sales order.") })
//    @RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<Long> create(
//            @ApiParam(name = "salesOrder", value = "JSON data of the sales order to be created.", required = true) @RequestBody SalesOrder salesOrder) {
//        LOGGER.info("Create a sales order {}.", salesOrder.toString());
//
//        Long id = salesOrderService.createBO(salesOrder);
//
//        return new ResponseEntity<>(id, HttpStatus.CREATED);
//    }
//
//    @ApiOperation(value = "Update a sales order.", notes = "Update a sales order using the given data.", response = Void.class)
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "Update successfully.") })
//    @RequestMapping(value = "/{id}", method = { RequestMethod.PATCH })
//    public ResponseEntity<Void> update(
//            @ApiParam(name = "id", value = "Sales order id.", required = true) @PathVariable long id,
//            @ApiParam(name = "salesOrder", value = "Update data in the JSON format.") @RequestBody SalesOrder salesOrder) {
//        LOGGER.info("Update a sales order {}.", id);
//
//        salesOrderService.updateBO(id, salesOrder);
//
//        return new ResponseEntity<>((Void) null, HttpStatus.OK);
//    }
//
//    // @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity<Void> delete(long id) {
//
//        return null;
//    }
//
//    @ApiOperation(value = "Get the count of the matching sales orders.", notes = "Get the count of the matching sales orders.", response = Integer.class)
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "Count number of the matching sales orders.") })
//    @RequestMapping(value = "/count", method = RequestMethod.GET)
//    public ResponseEntity<Integer> count(
//            @ApiParam(name = "filter", value = "Filtering conditions.", required = false) @RequestParam(required = false) String filter) {
//        LOGGER.info("Count sales order with filter: {}", filter);
//
//        Long number = salesOrderService.countBOs(filter);
//
//        return new ResponseEntity<>(number.intValue(), HttpStatus.OK);
//    }
//}
