module com.example.dungeaoncrawler {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dungeaoncrawler to javafx.fxml;
    exports com.example.dungeaoncrawler;
    exports com.example.dungeaoncrawler.logic.map;
    opens com.example.dungeaoncrawler.logic.map to javafx.fxml;
    exports com.example.dungeaoncrawler.utils;
    opens com.example.dungeaoncrawler.utils to javafx.fxml;
    exports com.example.dungeaoncrawler.logic.fight;
    opens com.example.dungeaoncrawler.logic.fight to javafx.fxml;
}