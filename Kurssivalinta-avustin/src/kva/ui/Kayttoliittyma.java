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
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kva.logiikka.Sovelluslogiikka;

/**JavaFX-kirjastojen avulla luotu urssivalinta-avustimen käyttöliittymä
 * <p>
 * Käyttöliittymä-ikkuna luodaan metodissa {@code luo} annettuun {@code Stage}-olioon. 
 * Ikkuna sisältää {@link kva.ui.AsetusNakyma}n ja {@link kva.ui.KurssitarjotinNakyma}n 
 * {@link javafx.scene.control.TabPane}n välilehdillä. Näkymien varsinainen toteutus 
 * on kyseisissä luokissa.
 *
 * @author Väinö Viinikka
 */
public class Kayttoliittyma {
    
    /**Sisältää tiedon käyttöliittymän käyttämästä sovelluslogiikasta.*/
    private Sovelluslogiikka logiikka;

    /**Luo uuden käyttöliittymän, joka käyttää parametrina annettua sovelluslogiikkaa.
     * <p>
     * Konstruktori ei vielä luo uutta, käyttäjälle näytettävää ikkunaa, vaan se
     * tehdään vasta metodissa {@code luo}.
     * 
     * @param logiikka {@code Sovelluslogiikka}, joka vastaa ohjeman varsinaisesta toteutuksesta
     */
    public Kayttoliittyma(Sovelluslogiikka logiikka) {
        this.logiikka = logiikka;
    }
    
    /**Luo käyttöliittymäikkunan ja sen sisällön annettuun {@link javafx.stage.Stage}en.
     * <p>
     * Metodia tulisi kutsua JavaFX:n luomassa säikeessä. Suositeltava tapa käyttää
     * metodia on kutsua sitä luokan {@link javafx.application.Application} alaluokan
     * metodissa {@Start}.
     * 
     * @param ikkuna {@code Stage}, johon käyttöliittymä luodaan
     */
    public void luo(Stage ikkuna) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        ikkuna.setTitle("Hello World!");
        ikkuna.setScene(scene);
        ikkuna.show();
    }
}
