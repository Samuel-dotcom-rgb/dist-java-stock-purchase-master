package edu.wctc.stockpurchase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import edu.wctc.stockpurchase.entity.StockPurchase;
import edu.wctc.stockpurchase.service.StockPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockpurchases")
public class StockPurchaseController {

    private final StockPurchaseService stockPurchaseService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StockPurchaseController(StockPurchaseService stockPurchaseService, ObjectMapper objectMapper) {
        this.stockPurchaseService = stockPurchaseService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<StockPurchase> getAllStockPurchases() {
        return stockPurchaseService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockPurchase> getStockPurchaseById(@PathVariable Integer id) {
        return stockPurchaseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockPurchase> createStockPurchase(@RequestBody StockPurchase stockPurchase) {
        StockPurchase saved = stockPurchaseService.save(stockPurchase);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping
    public ResponseEntity<StockPurchase> replaceStockPurchase(@RequestBody StockPurchase stockPurchase) {
        if (stockPurchase.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(stockPurchaseService.update(stockPurchase.getId(), stockPurchase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockPurchase> replaceStockPurchase(@PathVariable Integer id,
                                                            @RequestBody StockPurchase stockPurchase) {
        stockPurchase.setId(id);
        return ResponseEntity.ok(stockPurchaseService.update(id, stockPurchase));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<StockPurchase> patchStockPurchase(@PathVariable Integer id,
                                                           @RequestBody String patchDocument) {
        try {
            JsonPatch jsonPatch = JsonPatch.fromJson(objectMapper.readTree(patchDocument));
            StockPurchase updated = stockPurchaseService.patch(id, jsonPatch);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockPurchase(@PathVariable Integer id) {
        stockPurchaseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
