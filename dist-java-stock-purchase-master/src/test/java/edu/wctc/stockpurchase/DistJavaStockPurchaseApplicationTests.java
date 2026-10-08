package edu.wctc.stockpurchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wctc.stockpurchase.controller.StockPurchaseController;
import edu.wctc.stockpurchase.entity.StockPurchase;
import edu.wctc.stockpurchase.repo.StockPurchaseRepository;
import edu.wctc.stockpurchase.service.StockPurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DistJavaStockPurchaseApplicationTests {

    private MockMvc mockMvc;
    private StockPurchaseRepository stockPurchaseRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        stockPurchaseRepository = mock(StockPurchaseRepository.class);
        objectMapper = new ObjectMapper().findAndRegisterModules();

        StockPurchaseService stockPurchaseService = new StockPurchaseService(stockPurchaseRepository, objectMapper);
        StockPurchaseController controller = new StockPurchaseController(stockPurchaseService, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldGetAllStockPurchases() throws Exception {
        StockPurchase purchase = new StockPurchase();
        purchase.setId(1);
        purchase.setSymbol("CCL");
        purchase.setPricePerShare(25.44);
        purchase.setShares(100);
        purchase.setPurchaseDate(LocalDateTime.of(2021, 9, 25, 0, 0));

        when(stockPurchaseRepository.findAll()).thenReturn(List.of(purchase));

        mockMvc.perform(get("/api/stockpurchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("CCL"));
    }

    @Test
    void shouldCreateUpdatePatchAndDeleteStockPurchase() throws Exception {
        StockPurchase original = new StockPurchase();
        original.setId(1);
        original.setSymbol("CCL");
        original.setPricePerShare(25.44);
        original.setShares(100);
        original.setPurchaseDate(LocalDateTime.of(2021, 9, 25, 0, 0));

        StockPurchase created = new StockPurchase();
        created.setId(7);
        created.setSymbol("GME");
        created.setPricePerShare(182.28);
        created.setShares(7.5);
        created.setPurchaseDate(LocalDateTime.of(2021, 10, 11, 5, 0));

        when(stockPurchaseRepository.save(any(StockPurchase.class))).thenAnswer(invocation -> {
            StockPurchase saved = invocation.getArgument(0);
            saved.setId(7);
            return saved;
        });
        when(stockPurchaseRepository.findById(7)).thenReturn(Optional.of(created));
        when(stockPurchaseRepository.existsById(7)).thenReturn(true);

        mockMvc.perform(post("/api/stockpurchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("GME"));

        created.setSymbol("GME2");
        created.setPricePerShare(190.00);
        when(stockPurchaseRepository.findById(7)).thenReturn(Optional.of(created));

        mockMvc.perform(put("/api/stockpurchases/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("GME2"));

        mockMvc.perform(patch("/api/stockpurchases/7")
                        .contentType("application/json-patch+json")
                        .content("[{\"op\":\"replace\",\"path\":\"/symbol\",\"value\":\"BACX\"},{\"op\":\"replace\",\"path\":\"/shares\",\"value\":35.5}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BACX"))
                .andExpect(jsonPath("$.shares").value(35.5));

        mockMvc.perform(delete("/api/stockpurchases/7"))
                .andExpect(status().isNoContent());
    }
}
