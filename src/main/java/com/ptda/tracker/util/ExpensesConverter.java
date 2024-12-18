package com.ptda.tracker.util;

import com.ptda.tracker.dto.ExpenseImportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesConverter {
    private List<String[]> rawData;
    private List<ExpenseImportDto> processedExpenses = new ArrayList<>();
    private Map<String, String> columnMapping = new HashMap<>();
    private Map<String, String> categoryMapping = new HashMap<>();
    private String dateFormat = "dd-MM-yyyy";

    private void processRawData() {
        processedExpenses.clear();
        for (String[] row : rawData) {
            ExpenseImportDto dto = new ExpenseImportDto(
                    columnMapping.getOrDefault("Title", "") != "" ? row[getIndex("Title")] : "",
                    columnMapping.getOrDefault("Description", "") != "" ? row[getIndex("Description")] : "",
                    columnMapping.getOrDefault("Amount", "") != "" ? row[getIndex("Amount")] : "",
                    columnMapping.getOrDefault("Date", "") != "" ? parseDate(row[getIndex("Date")]) : "",
                    columnMapping.getOrDefault("Category", "") != "" ? row[getIndex("Category")] : ""
            );
            processedExpenses.add(dto);
        }
    }

    private int getIndex(String field) {
        return Integer.parseInt(columnMapping.get(field));
    }

    private String parseDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format(new SimpleDateFormat(dateFormat).parse(date));
        } catch (Exception e) {
            return date;
        }
    }

    private void applyCategoryMapping() {
        for (ExpenseImportDto dto : processedExpenses) {
            if (categoryMapping.containsKey(dto.getCategory())) {
                dto.setCategory(categoryMapping.get(dto.getCategory()));
            }
        }
    }
}
