/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2021 Väinö Viinikka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kva.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import kva.logiikka.Sovelluslogiikka;

/**Toteuttaa {@link kva.ui.Nakyma}n, jossa käyttäjä valitsee aineet, joiden kursseja 
 * hänelle näytetään.
 *
 * @author Väinö Viinikka
 */
public class AsetusNakyma extends Nakyma {

    public AsetusNakyma(String otsikko, Sovelluslogiikka logiikka) {
        super(otsikko, logiikka);
    }

    @Override
    public Node luoSisalto() {
        Button btn = new Button();
        btn.setText("Valitse pitkä matikka!");
        btn.setOnAction((ActionEvent event) -> {
            System.out.println("Aineiden valintaa ei ole toteutettu vielä.");
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        return root;
    }
    
}
