/* Kurssivalinta-avustin  – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
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
package kva.logiikka.lataus;

import java.util.HashMap;
import java.util.List;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import kva.logiikka.PeriodinTunniste;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Väinö Viinikka
 */
public class WebEngineLataaja extends KurssitarjottimenLataaja {
    
    private WebEngine moottori;
    private String perusOsoite;
    private HashMap<PeriodinTunniste, String> linkit = new HashMap<>();

    @Override
    public void lataaPeriodienTunnisteet(Object[] data) {
        //https://helsinki.inschool.fi/!02617820/
        //https://helsinki.inschool.fi/!02617820/selection/view?
        moottori = (WebEngine) data[0];
        
        try {
            perusOsoite = perusOsoite(moottori.getLocation());
        } catch(LataajaPoikkeus poikkeus) {
            lahetaVirhe(poikkeus);
            return;
        }
        
        moottori.getLoadWorker().stateProperty().addListener((tarkkailtava, vanhaArvo, uusiArvo) -> {
            if(uusiArvo == Worker.State.SUCCEEDED) {
                luePeriodinTunnisteet(moottori.getDocument().getElementById("own-schools"));
                luePeriodinTunnisteet(moottori.getDocument().getElementById("ext-schools"));
                lahetaPeriodinTunnisteet();
            }
        });
        
        moottori.load(perusOsoite + "selection/view?");
    }

    @Override
    public void lataaKurssitarjotin(List<PeriodinTunniste> periodit) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private String perusOsoite(String URL) throws LataajaPoikkeus {
        int kauttaviivaLaskuri = 0;
        int merkkiLaskuri = 0;
        for(char c : moottori.getLocation().toCharArray()) {
            merkkiLaskuri++;
            if(c == '/') {
                kauttaviivaLaskuri++;
                if(kauttaviivaLaskuri >= 4) {
                    return moottori.getLocation().substring(0, merkkiLaskuri);
                }
            }
        }
        throw new LataajaPoikkeus("WebEnginellä ei ole kirjauduttu Wilmaan. Osoite: " + URL);
    }
    
    private void luePeriodinTunnisteet(Element e) {
        for(int i = 0; i < e.getElementsByTagName("h4").getLength(); i++) {
            Node otsikkoSolmu = e.getElementsByTagName("h4").item(i);
            String oppilaitoksenNimi = otsikkoSolmu.getTextContent();
            
            Element lohko = (Element) e.getElementsByTagName("ul").item(i);
            for(int a = 0; a < lohko.getElementsByTagName("li").getLength(); a++) {
                Element elementti = (Element) lohko.getElementsByTagName("li").item(a);
                Element linkki = (Element) elementti.getElementsByTagName("a").item(0);
                String periodinNimi = linkki.getTextContent().trim();
                PeriodinTunniste tunniste = new PeriodinTunniste(oppilaitoksenNimi, periodinNimi);
                linkit.put(tunniste, linkki.getAttribute("href"));
                lisaaPeriodinTunniste(tunniste);
            }
        }
    }
}
