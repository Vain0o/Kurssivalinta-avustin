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

import java.util.Optional;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
        KurssitarjottimenLataaja lataaja = new WebEngineLataaja();
        //KurssitarjottimenLataaja lataaja = new TestiLataaja();
        
        Sovelluslogiikka logiikka = new Sovelluslogiikka(lataaja);
        Kayttoliittyma kayttis = new Kayttoliittyma(logiikka);
        kayttis.luo(primaryStage);
        
        naytaLaillisetIlmoitukset();
    }

    /**Sovelluksen pääohjelma, jonka ainoa tehtävä on kutsua metodia {@code Application.launch()}.
     * 
     * @param args komentorivin argumentit
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private void naytaLaillisetIlmoitukset() {
        String teksti = "Kurssivalinta-avustin 1.0 Copyright (C) 2022 Väinö Viinikka\n\n" +
                "Tämän ohjelman käyttö, muokkaus ja levittäminen on sallittu\n" + 
                "GNU General Public Licensen version 3 tai myöhempien\n" + 
                "versioiden ehdoilla. OHJELMALLA EI OLE MINKÄÄNLAISTA TAKUUTA.\n\n\n\n";
        ButtonType linkki = new ButtonType("Lisätetoja");
        Alert ikkuna = new Alert(Alert.AlertType.NONE, teksti, ButtonType.OK, linkki);
        ikkuna.setTitle("Kurssivalinta-avustin");
        
        Optional<ButtonType> tulos = ikkuna.showAndWait();
        if(tulos.isPresent() && tulos.get().equals(linkki)) {
            super.getHostServices().showDocument("http://www.gnu.org/licenses/gpl-3.0.html");
        }
    }
}
