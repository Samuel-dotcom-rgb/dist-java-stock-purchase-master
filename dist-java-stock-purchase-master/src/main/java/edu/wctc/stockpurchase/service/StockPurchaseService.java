package edu.wctc.stockpurchase.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import edu.wctc.stockpurchase.entity.StockPurchase;
import edu.wctc.stockpurchase.repo.StockPurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockPurchaseService {
    private final StockPurchaseRepository stockPurchaseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public StockPurchaseService(StockPurchaseRepository stockPurchaseRepository, ObjectMapper objectMapper) {
        this.stockPurchaseRepository = stockPurchaseRepository;
        this.objectMapper = objectMapper;
    }

    public List<StockPurchase> findAll() {
        List<StockPurchase> purchases = new ArrayList<>();
        stockPurchaseRepository.findAll().forEach(purchases::add);
        return purchases;
    }

    public Optional<StockPurchase> findById(Integer id) {
        return stockPurchaseRepository.findById(id);
    }

    public StockPurchase save(StockPurchase stockPurchase) {
        return stockPurchaseRepository.save(stockPurchase);
    }

    public StockPurchase update(Integer id, StockPurchase stockPurchase) {
        StockPurchase existing = stockPurchaseRepository.findById(id)
                .orElse(new StockPurchase());

        existing.setId(id);
        existing.setSymbol(stockPurchase.getSymbol());
        existing.setPricePerShare(stockPurchase.getPricePerShare());
        existing.setShares(stockPurchase.getShares());
        existing.setPurchaseDate(stockPurchase.getPurchaseDate());

        return stockPurchaseRepository.save(existing);
    }

    public StockPurchase patch(Integer id, JsonPatch jsonPatch) {
        StockPurchase stockPurchase = stockPurchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock purchase not found with id: " + id));

        try {
            var updatedNode = jsonPatch.apply(objectMapper.valueToTree(stockPurchase));
            StockPurchase patched = objectMapper.treeToValue(updatedNode, StockPurchase.class);
            patched.setId(id);
            return stockPurchaseRepository.save(patched);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to apply patch to stock purchase with id: " + id, e);
        }
    }

    public void deleteById(Integer id) {
        if (stockPurchaseRepository.existsById(id)) {
            stockPurchaseRepository.deleteById(id);
        }
    }
}
