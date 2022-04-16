/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2022 Väinö Viinikka
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
import kva.logiikka.lataus.KurssitarjottimenLataaja;
import kva.logiikka.lataus.TestiLataaja;//Tätä ei poisteta, sillä sitä voi tarvita testaamiseen.
import kva.logiikka.lataus.WebEngineLataaja;
import kva.ui.Kayttoliittyma;

/**Kurssivalinta-avustimen pääluokka, joka alustaa käyttöliittymän ja sovelluslogiikan
 * ja käynnistää sovelluksen.
 * <p>
 * Luokka ei sisällä sovelluksen varsinaista toteutusta. Se ainoastaan luo ilmetymät
 * luokista {@link kva.ui.Kayttoliittyma}, {@link kva.logiikka.Sovelluslogiikka} sekä 
 * {@link kva.logiikka.lataus.KurssitarjottimenLataaja} ja käynnistää sovelluksen 
 * {@code Kayttoliittyman} metodilla {@code luo}.
 * <p>
 * Tämänhetkinen toteutus käyttää {@code KurssitarjottimenLataajana} {@link kva.logiikka.lataus.TestiLataaja}a.
 *
 * @author Väinö Viinikka
 */
public class KurssivalintaAvustin extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        //KurssitarjottimenLataaja lataaja = new WebEngineLataaja();
        KurssitarjottimenLataaja lataaja = new TestiLataaja();
        Sovelluslogiikka logiikka = new Sovelluslogiikka(lataaja);
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
