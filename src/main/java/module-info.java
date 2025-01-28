module org.aouessar.chessgame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;

    opens org.aouessar.chessgame to javafx.fxml;
    exports org.aouessar.chessgame;
    exports org.aouessar.chessgame.piece.factory;
    exports org.aouessar.chessgame.domain;
    opens org.aouessar.chessgame.domain to javafx.fxml;
    exports org.aouessar.chessgame.piece;
}