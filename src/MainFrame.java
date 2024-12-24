import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MainFrame extends JFrame {
    // Константы с исходным размером окна приложения
     private static final int WIDTH = 700;
     private static final int HEIGHT = 500;

    // Массив коэффициентов многочлена
    private Double[] coefficients;
    private JFileChooser fileChooser = null;




    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    private JMenuItem aboutMenuItem;

    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;

    private Box hBoxResult;

    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();
    private GornerTableModel data;

    public MainFrame(Double[] coefficients) {
        super("Табулирование многочлена на отрезке по схеме Горнера");
        this.coefficients = coefficients;
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);

        // Создать меню
        JMenuBar menuBar = new JMenuBar();
        // Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar);
        // Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        // Добавить его в главное меню
        menuBar.add(fileMenu);
        // Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
        // Добавить его в главное меню
        menuBar.add(tableMenu);
        //Создать пункт меню"О программе"
        JMenu helpMenu = new JMenu("Справка");
        menuBar.add(helpMenu);

        // Создать новое "действие" по сохранению в текстовый файл

        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    saveToTextFile(fileChooser.getSelectedFile());
                }
            }
        };
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        saveToTextMenuItem.setEnabled(false);


        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    // Если экземпляр диалогового окна // "Открыть файл" ещѐ не создан, // то создать его
                    fileChooser = new JFileChooser();
                    // и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) ;
                // Если результат его показа успешный, // сохранить данные в двоичный файл
                saveToGraphicsFile(fileChooser.getSelectedFile());
            }
        };

        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
        saveToGraphicsMenuItem.setEnabled(false);
        // Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена"){
            public void actionPerformed(ActionEvent event){
                // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска", "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
                // Обновить таблицу
                getContentPane().repaint();
            }
        };
        searchValueMenuItem = tableMenu.add(searchValueAction);
        searchValueMenuItem.setEnabled(false);


        aboutMenuItem = new JMenuItem("О программе");
        // Добавляем обработчик действия для "О программе"

        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Открытие диалогового окна с информацией о программе
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Автор: Роик Варвара  \nГруппа: 9",
                        "О программе",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutMenuItem);

        // Создать область с полями ввода для границ отрезка и шага
// Создать подпись для ввода левой границы отрезка
        JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
// Создать текстовое поле для ввода значения длиной в 10 символов
// со значением по умолчанию 0.0
        textFieldFrom = new JTextField("0.0", 10);
// Установить максимальный размер равный предпочтительному, чтобы
// предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());

// Создать подпись для ввода левой границы отрезка
        JLabel labelForTo = new JLabel("до:");
// Создать текстовое поле для ввода значения длиной в 10 символов
// со значением по умолчанию 1.0
        textFieldTo = new JTextField("1.0", 10);
// Установить максимальный размер равный предпочтительному, чтобы
// предотвратить увеличение размера поля ввода
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());

// Создать подпись для ввода шага табулирования
        JLabel labelForStep = new JLabel("с шагом:");
// Создать текстовое поле для ввода значения длиной в 10 символов
// со значением по умолчанию 1.0
        textFieldStep = new JTextField("0.1", 10);
// Установить максимальный размер равный предпочтительному, чтобы
// предотвратить увеличение размера поля ввода
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());

// Создать контейнер 1 типа "коробка с горизонтальной укладкой"
        Box hboxRange = Box.createHorizontalBox();
// Задать для контейнера тип рамки "объѐмная"
        hboxRange.setBorder(BorderFactory.createBevelBorder(1));

// Добавить "клей" C1-H1
        hboxRange.add(Box.createHorizontalGlue());
// Добавить подпись "От"
        hboxRange.add(labelForFrom);
// Добавить "распорку" C1-H2
        hboxRange.add(Box.createHorizontalStrut(10));
// Добавить поле ввода "От"
        hboxRange.add(textFieldFrom);
// Добавить "распорку" C1-H3
        hboxRange.add(Box.createHorizontalStrut(20));
// Добавить подпись "До"
        hboxRange.add(labelForTo);
// Добавить "распорку" C1-H4
        hboxRange.add(Box.createHorizontalStrut(10));
// Добавить поле ввода "До"
        hboxRange.add(textFieldTo);
// Добавить "распорку" C1-H5
        hboxRange.add(Box.createHorizontalStrut(20));
// Добавить подпись "с шагом"
        hboxRange.add(labelForStep);
// Добавить "распорку" C1-H6
        hboxRange.add(Box.createHorizontalStrut(10));
// Добавить поле для ввода шага табулирования
        hboxRange.add(textFieldStep);
// Добавить "клей" C1-H7
        hboxRange.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному
        // минимальному, чтобы при компоновке область совсем не сдавили
        hboxRange.setPreferredSize(new Dimension(new Double(hboxRange.getMaximumSize().getWidth()).intValue(),
                new Double(hboxRange.getMinimumSize().getHeight()).intValue()*2));
        // Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hboxRange, BorderLayout.NORTH);



        // Создать кнопку "Вычислить"

        JButton buttonCalc = new JButton("Вычислить");
// Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    // Считать значения начала и конца отрезка, шага
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());

                    // На основе считанных данных создать новый экземпляр модели таблицы
                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);

                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);

                    // Установить в качестве визуализатора ячеек для класса Double разработанный визуализатор
                    //table.setDefaultRenderer(Double.class, renderer);
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        table.getColumnModel().getColumn(col).setCellRenderer(renderer);
                    }

                    // Установить размер строки таблицы в 30 пикселов
                    table.setRowHeight(30);

                    // Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();

                    // Добавить в hBoxResult таблицу, "обѐрнутую" в панель с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));

                    // Обновить область содержания главного окна
                    getContentPane().validate();

                    // Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать сообщение об ошибке
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");
        // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev){
                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                // Удалить все вложенные элементы контейнера
                hBoxResult.removeAll();
                hBoxResult.add(new JPanel());
                // Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
                // Обновить область содержания главного окна
                getContentPane().validate();
            }
        });

        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному минимальному, чтобы при // компоновке окна область совсем не сдавили
        hboxButtons.setPreferredSize(new Dimension(new
                Double(hboxButtons.getMaximumSize().getWidth()).intValue(), new
                Double(hboxButtons.getMinimumSize().getHeight()).intValue()*2));
        // Разместить контейнер с кнопками в нижней (южной) области граничной компоновки
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
        // Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        getContentPane().add(hBoxResult, BorderLayout.CENTER);



    }



    protected void saveToGraphicsFile(File selectedFile) {
        try {
            // Создать новый байтовый поток вывода, направленный в указанный файл
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));

            // Записать в поток вывода попарно значение X в точке, значение многочлена в точке
            for (int i = 0; i<data.getRowCount(); i++) {
                out.writeDouble((Double)data.getValueAt(i,0));
                out.writeDouble((Double)data.getValueAt(i,1));
            }

            // Закрыть поток вывода
            out.close();
        } catch (Exception e) {
            // Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,
            // так как мы файл создаѐм, а не открываем для чтения
        }
    }

    protected void saveToTextFile(File selectedFile) {
        try {
            PrintStream out = new PrintStream(selectedFile);
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for (int i = 0; i < coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" + (coefficients.length - i - 1));
                if (i != coefficients.length - 1) out.print(" + ");
            }
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " + data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");
            for (int i = 0; i < data.getRowCount(); i++) {
                out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));
            }
            out.close();
        } catch (FileNotFoundException e) {
            // Обработка исключений
        }
    }

}
