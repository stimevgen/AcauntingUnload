package DTO;

import java.util.List;
import java.util.Map;

public class AnswerDTO {
    private final List<Map<Class<?>, String>> column;
    private final List<List<?>> row;

    public AnswerDTO(List<Map<Class<?>, String>> column, List<List<?>> row) {
        this.column = column;
        this.row = row;
    }

    public List<Map<Class<?>, String>> getColumn() {
        return column;
    }

    public List<List<?>> getRow() {
        return row;
    }
}
