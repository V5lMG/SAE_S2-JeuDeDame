module iut.sae.jeudedame {
    requires javafx.controls;
    requires javafx.fxml;

    exports fr.iutrodez.jeudedame;
    opens fr.iutrodez.jeudedame to javafx.fxml;
    exports fr.iutrodez.jeudedame.controleur;
    opens fr.iutrodez.jeudedame.controleur to javafx.fxml;
    exports fr.iutrodez.jeudedame.modele;
    opens fr.iutrodez.jeudedame.modele to javafx.fxml;
    exports fr.iutrodez.jeudedame.vue;
    opens fr.iutrodez.jeudedame.vue to javafx.fxml;
}
