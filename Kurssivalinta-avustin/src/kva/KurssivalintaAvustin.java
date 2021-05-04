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
package kva;

import javafx.application.Application;
import javafx.stage.Stage;
import kva.logiikka.Sovelluslogiikka;
import kva.ui.Kayttoliittyma;

/**Kurssivalinta-avustimen pääluokka, joka alustaa käyttöliittymän ja sovelluslogiikan
 * ja käynnistää sovelluksen.
 * <p>
 * Luokka ei sisällä sovelluksen varsinaista toteutusta. Se ainoastaan luo ilmetymät
 * luokista {@link kva.ui.Kayttoliittyma} ja {@link kva.logiikka.Sovelluslogiikka} 
 * ja käynnistää sovelluksen {@code Kayttoliittyman} metodilla {@code luo}.
 *
 * @author Väinö Viinikka
 */
public class KurssivalintaAvustin extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Sovelluslogiikka logiikka = new Sovelluslogiikka();
        Kayttoliittyma kayttis = new Kayttoliittyma(logiikka);
        kayttis.luo(primaryStage);
    }

    /**Sovelluksen pääohjelma, jonka ainoa tehtävä on kutsua metodia {@code Application.launch()}.
     * 
     * @param args komentorivin argumentit
     */
    public static void main(String[] args) {
        launch(args);
    }
}
