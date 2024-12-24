import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class GornerTableCellRenderer implements TableCellRenderer {
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private JCheckBox checkBox = new JCheckBox();
    private String needle = null;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    public GornerTableCellRenderer() {
        // Показывать только 5 знаков после запятой
        formatter.setMaximumFractionDigits(5);

        // Не использовать группировку (т.е. не отделять тысячи ни запятыми, ни пробелами),
        // т.е. показывать число как "1000", а не "1 000" или "1,000"
        formatter.setGroupingUsed(false);

        // Установить в качестве разделителя дробной части точку, а не запятую.
        // По умолчанию, в региональных настройках Россия/Беларусь дробная часть отделяется запятой
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();

        // Меняем символ дробной части на точку
        dottedDouble.setDecimalSeparator('.');

        // Применяем измененные символы форматирования
        formatter.setDecimalFormatSymbols(dottedDouble);

        // Разместить надпись внутри панели
        panel.add(label);

        // Установить выравнивание надписи по левому краю панели
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (value instanceof Boolean){
            checkBox.setSelected((Boolean) value);
            if (isSelected) {
                checkBox.setBackground(Color.CYAN); // Например, если выбрана, фон голубой
            } else {
                checkBox.setBackground(Color.WHITE); // По умолчанию белый
            }
            return  checkBox;
        }
        else{
            // Преобразуем значение из ячейки (value) в строку с использованием настроенного форматировщика
            String formattedDouble = formatter.format(value);

            // Устанавливаем текст на метке (label) равным отформатированному значению
            label.setText(formattedDouble);

            // Проверяем, если столбец = 1 (второй столбец), иголка не null, и иголка совпадает со значением в ячейке
            if (col != 2 && needle != null && needle.equals(formattedDouble)) {
                // Если условие выполнено, изменяем фон на красный
                panel.setBackground(Color.RED);
            } else {
                // Если условие не выполнено, фон остается белым
                panel.setBackground(Color.WHITE);
            }

            // Возвращаем панель, которая будет отображать ячейку таблицы
            return panel;
        }


    }

    public void setNeedle(String needle) {
        // Устанавливаем значение иголки для поиска
        this.needle = needle;
    }




}
