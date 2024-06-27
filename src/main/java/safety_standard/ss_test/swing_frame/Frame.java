package safety_standard.ss_test.swing_frame;

import safety_standard.ss_test.csv.CsvWriter;
import safety_standard.ss_test.dto.Category;
import safety_standard.ss_test.entities.DocumentEntity;
import safety_standard.ss_test.service.DocumentService;
import safety_standard.ss_test.web_parser.Parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

//Окно программы, написанной на Swing
public class Frame extends JFrame {

    private JTextField documentField;
    private JTextField urlField;
    private JButton browseButton;
    private JButton saveButton;
    private JButton uploadButton;

    private DocumentService documentService = new DocumentService();

    public Frame() {
        setTitle("Выгрузка категорий документов");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadInitialValues();
    }

    private void initComponents() {
        // Панель для полей ввода
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Первая строка
        JLabel documentLabel = new JLabel("Путь документа:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(documentLabel, gbc);

        documentField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(documentField, gbc);

        browseButton = new JButton("Путь...");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(browseButton, gbc);

        // Вторая строка
        JLabel urlLabel = new JLabel("URL:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(urlLabel, gbc);

        urlField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(urlField, gbc);

        // Третья строка
        saveButton = new JButton("Сохранить параметры");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        uploadButton = new JButton("Выгрузить документ");
        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(uploadButton, gbc);

        add(panel, BorderLayout.CENTER);

        // Добавляем регистраторы событий
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveParameters();
            }
        });

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadDocument();
            }
        });
    }

    // Загрузка значений URL и PATH из БД
    private void loadInitialValues() {
        documentField.setText(documentService.getPath());
        urlField.setText(documentService.getUrl());
    }

    // Возможность выбрать путь в проводнике Windows
    private void chooseDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            documentField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    // Сохранение URL и PATH в БД
    private void saveParameters() {
        if (documentField.getText().isEmpty() || urlField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Поля параметров должны быть заполнены!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            // Логика для сохранения параметров
            documentService.saveParameters(urlField.getText(), documentField.getText());

            JOptionPane.showMessageDialog(this, "Параметры успешно сохранены!");
        }
    }

    // Выгрузка CSV
    private void uploadDocument() {
        if (documentField.getText().isEmpty() || urlField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Поля параметров должны быть заполнены!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            // Логика для выгрузки документа
            java.util.List<DocumentEntity> listOfDoc = new ArrayList<>();
            Parser parser = new Parser();

            Map<String, String> mapOfLinks = parser.checkLinkForDocuments(urlField.getText());
            for(String category: mapOfLinks.keySet()) {
                parser.checkDocuments(mapOfLinks.get(category), category, listOfDoc);
            }

            if(listOfDoc.size()==0) {
                JOptionPane.showMessageDialog(this, "По введенному URL ничего не найдено!");
            }
            else {
                DocumentService documentService = new DocumentService();
                documentService.saveListOfDocWithCheckExists(listOfDoc);

                java.util.List<Category> categoryList = new ArrayList<>();
                String pathOfDoc = documentField.getText() + "\\vse-dokumenty.csv";
                Path filePath = Paths.get(pathOfDoc);
                if(Files.exists(filePath)) {
                    try {
                        BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
                        Instant dateDoc = attr.creationTime().toInstant();

                        categoryList = documentService.categoryWithAddition(dateDoc);
                        CsvWriter.writeCsv(categoryList, pathOfDoc, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    categoryList = documentService.category();
                    CsvWriter.writeCsv(categoryList, pathOfDoc, false);
                }
                JOptionPane.showMessageDialog(this, "Документ выгружен успешно!");
            }
        }
    }
}
