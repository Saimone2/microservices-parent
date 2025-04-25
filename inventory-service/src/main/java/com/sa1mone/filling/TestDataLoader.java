package com.sa1mone.filling;

import com.sa1mone.dto.ProductDTO;
import com.sa1mone.entity.Warehouse;
import com.sa1mone.request.InventoryRequest;
import com.sa1mone.request.WarehouseRequest;
import com.sa1mone.service.InventoryService;
import com.sa1mone.service.WarehouseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDataLoader implements CommandLineRunner {

    private final InventoryService inventoryService;
    private final WarehouseService warehouseService;

    public TestDataLoader(InventoryService inventoryService, WarehouseService warehouseService) {
        this.inventoryService = inventoryService;
        this.warehouseService = warehouseService;
    }

    @Override
    public void run(String... args) {
        if (warehouseService.isWarehouseTableEmpty()) {
            List<WarehouseRequest> testWarehouseRequests = List.of(
                    new WarehouseRequest("Warehouse A", "Kyiv, Ukraine"),
                    new WarehouseRequest("Warehouse B", "Lviv, Ukraine"),
                    new WarehouseRequest("Warehouse C", "Odessa, Ukraine"),
                    new WarehouseRequest("Warehouse D", "Dnipro, Ukraine")
            );
            testWarehouseRequests.forEach(warehouseService::addWarehouse);

            if (inventoryService.isInventoryTableEmpty()) {
                List<InventoryRequest> testInventory = getInventoryRequests();
                testInventory.forEach(inventoryService::createLeftovers);
            }
        }
    }

    private List<InventoryRequest> getInventoryRequests() {
        List<ProductDTO> testProducts = inventoryService.getTestProducts();
        List<Warehouse> testWarehouses = warehouseService.getAllWarehouses();

        return List.of(
                new InventoryRequest(testProducts.get(0).getId(), testWarehouses.get(0).getId(), 5),
                new InventoryRequest(testProducts.get(0).getId(), testWarehouses.get(2).getId(), 9),
                new InventoryRequest(testProducts.get(1).getId(), testWarehouses.get(1).getId(), 8),
                new InventoryRequest(testProducts.get(2).getId(), testWarehouses.get(1).getId(), 12),
                new InventoryRequest(testProducts.get(3).getId(), testWarehouses.get(2).getId(), 6)
        );
    }
}