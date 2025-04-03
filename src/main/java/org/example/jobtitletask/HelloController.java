package org.example.jobtitletask;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelloController {

    @FXML private Label welcomeText;
    @FXML private ComboBox<String> languageSelector;
    @FXML private ListView<String> employeeList;
    @FXML private TextField keyNameField;
    @FXML private TextField translationField;
    @FXML private Button saveButton;

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/my_new_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Kuusamo2013!";

    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        languageSelector.getItems().addAll("English", "Spanish", "French", "汉语");
        languageSelector.setValue("English");
        languageSelector.setOnAction(event -> changeLanguage());
        changeLanguage();
    }

    @FXML
    private void changeLanguage() {
        String selectedLang = languageSelector.getValue();
        Locale locale = switch (selectedLang) {
            case "English" -> new Locale("en", "US");
            case "Spanish" -> new Locale("es", "ES");
            case "French" -> new Locale("fr", "FR");
            case "汉语" -> new Locale("zh", "CN");
            default -> new Locale("en", "US");
        };

        bundle = ResourceBundle.getBundle("messages", locale);

        // Update UI labels
        welcomeText.setText(bundle.getString("title"));
        keyNameField.setPromptText(bundle.getString("jobTitleKey"));
        translationField.setPromptText(bundle.getString("translation"));
        saveButton.setText(bundle.getString("save"));

        // Load data from DB
        fetchLocalizedData(locale.getLanguage());
    }

    private void fetchLocalizedData(String languageCode) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT key_name, translation_text FROM translations WHERE language_code=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, languageCode);
            ResultSet rs = stmt.executeQuery();

            employeeList.getItems().clear();
            while (rs.next()) {
                String keyName = rs.getString("key_name");
                String translation = rs.getString("translation_text");
                employeeList.getItems().add(keyName + ": " + translation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveTranslation() {
        String keyName = keyNameField.getText();
        String translation = translationField.getText();
        String languageCode = getLanguageCode(languageSelector.getValue());

        if (keyName.isEmpty() || translation.isEmpty()) {
            System.out.println("Key or translation is empty.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = "INSERT INTO translations (key_name, language_code, translation_text) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, keyName);
            stmt.setString(2, languageCode);
            stmt.setString(3, translation);
            stmt.executeUpdate();

            System.out.println("Saved successfully.");

            keyNameField.clear();
            translationField.clear();
            fetchLocalizedData(languageCode);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getLanguageCode(String language) {
        return switch(language){
            case "English" -> "en";
            case "Spanish" -> "es";
            case "French" -> "fr";
            case "汉语" -> "zh";
            default -> "en";
        };
    }
}