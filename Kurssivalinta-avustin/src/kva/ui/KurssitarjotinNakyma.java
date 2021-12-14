/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2021 Väinö viinikka
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

/**Toteuttaa {@code Nakyman}, jossa käyttäjä valitsee kurssit.
 *
 * @author Väinö Viinikka
 */
public class KurssitarjotinNakyma extends Nakyma {

    /**Luo uuden kurssivalintanäkymän.
     * 
     * @param otsikko {@code Nakyman} kuvastaman välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code Nakyma} kuuluu
     */
    public KurssitarjotinNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
    }

    @Override
    public Node luoSisalto() {
        Button btn = new Button();
        btn.setText("Tuo kurssit Wilmasta.");
        btn.setOnAction((ActionEvent event) -> {
            System.out.println("Kurssien tuontia ei ole toteutettu vielä.");
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        return root;
    }
    
}
