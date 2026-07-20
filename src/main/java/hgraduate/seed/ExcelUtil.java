package hgraduate.seed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

// POI cell readers for seed import — DataFormatter is used for String columns because some columns
// (e.g. graduation_rules.operator/value) hold numeric-looking values in the xlsx itself; direct typed
// getters are used for columns that are always numeric/boolean in the seed data.
final class ExcelUtil {

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private ExcelUtil() {
    }

    static List<Row> dataRows(Sheet sheet) {
        List<Row> rows = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows;
    }

    static String getString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        String value = DATA_FORMATTER.formatCellValue(cell);
        return value.isEmpty() ? null : value;
    }

    static Long getLong(Row row, int col) {
        Double value = getDouble(row, col);
        return value == null ? null : (long) (double) value;
    }

    static Integer getInteger(Row row, int col) {
        Long value = getLong(row, col);
        return value == null ? null : value.intValue();
    }

    // 빈 셀이 BLANK가 아니라 빈 문자열의 STRING 셀로 저장된 경우가 있어(예: curriculum_versions.effective_to),
    // 셀 타입과 무관하게 문자열로 먼저 읽어 비어있으면 null, 아니면 숫자로 파싱한다.
    static Double getDouble(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        String value = getString(row, col);
        return value == null ? null : Double.parseDouble(value.trim());
    }

    static BigDecimal getBigDecimal(Row row, int col) {
        Double value = getDouble(row, col);
        return value == null ? null : BigDecimal.valueOf(value);
    }

    static boolean getBoolean(Row row, int col) {
        Cell cell = row.getCell(col);
        return cell != null && cell.getBooleanCellValue();
    }

    static LocalDateTime getLocalDateTime(Row row, int col) {
        String value = getString(row, col);
        return value == null ? null : LocalDateTime.parse(value);
    }
}
