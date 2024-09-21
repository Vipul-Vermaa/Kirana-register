package controller;

import com.example.kirana.Controller.TransactionController;
import com.example.kirana.Model.TransactionModel;
import com.example.kirana.Service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionModel transaction;


    @BeforeEach
    public void setup() {
        transaction = new TransactionModel();
        transaction.setId("1");
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setType(TransactionModel.TransactionType.CREDIT);
        transaction.setCurrency("INR");
        transaction.setTimestamp(LocalDateTime.now());
    }

    // Test for addTransaction API
    @Test
    public void testAddTransaction() throws Exception {
        when(transactionService.addTransaction(any(BigDecimal.class), anyString(), anyString())).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions/addtransaction")
                        .param("amount", "1000")
                        .param("type", "credit")
                        .param("currency", "USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(transaction)));
    }

    // Test for invalid transaction type
    @Test
    public void testAddTransactionInvalidType() throws Exception {
        mockMvc.perform(post("/api/transactions/addtransaction")
                        .param("amount", "1000")
                        .param("type", "invalid")
                        .param("currency", "USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    // Test for generateReport API (monthly report)
    @Test
    public void testGenerateMonthlyReport() throws Exception {
        List<TransactionModel> transactions = Arrays.asList(transaction);
        when(transactionService.getReports(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions/reports")
                        .param("type", "monthly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));
    }
    // Test for invalid report type
    @Test
    public void testGenerateReportInvalidType() throws Exception {
        mockMvc.perform(get("/api/transactions/reports")
                        .param("type", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}