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
import java.util.HashSet;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import kva.logiikka.Moduuli;
import kva.logiikka.PalkinTunniste;
import kva.logiikka.PeriodinTunniste;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**Toteuttaa {@code KurssitarjottimenLataajan}, jossa {@code Kurssitarjottimen} tiedot 
 * ladataan suoraan Wilmasta.
 * <p>
 * Tiedot luetaan verkkosivujen HTML-lähdekoodista, joka saavutetaan luokan {@link javafx.scene.web.WebEngine} 
 * avulla.
 *
 * @author Väinö Viinikka
 * @since Kurssivalinta-avustin 1.0
 */
public class WebEngineLataaja extends KurssitarjottimenLataaja {
    
    private WebEngine moottori;
    private String perusOsoite;
    private HashMap<PeriodinTunniste, String> linkit = new HashMap<>();
    
    private ChangeListener<Worker.State> kuuntelija;
    private PeriodinTunniste nykyinenPeriodi;

    /**{@inheritDoc}
     * 
     * @param data taulukko, jonka solussa 0 on {@code WebEngine}, jolla on kirjauduttu 
     *        Wilmaan siten, että opiskelijan etusivu on auki.
     */
    @Override
    public void lataaPeriodienTunnisteet(Object[] data) {
        moottori = (WebEngine) data[0];
        
        try {
            perusOsoite = perusOsoite(moottori.getLocation());
        } catch(LataajaPoikkeus poikkeus) {
            lahetaVirhe(poikkeus);
            return;
        }
        
        kuuntelija = (tarkkailtava, vanhaArvo, uusiArvo) -> {
            switch (uusiArvo) {
                case SUCCEEDED:
                    try {
                        luePeriodinTunnisteet(moottori.getDocument().getElementById("own-schools"));
                        luePeriodinTunnisteet(moottori.getDocument().getElementById("ext-schools"));
                    } catch(Throwable t) {
                        lahetaVirhe(t);
                        moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                        return;
                    }
                    lahetaPeriodinTunnisteet();
                    moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                    break;
                case FAILED:
                    lahetaVirhe(moottori.getLoadWorker().getException());
                    moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                    break;
                case CANCELLED:
                    lahetaVirhe(new LataajaPoikkeus("Lataus keskeytettiin."));
                    moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                    break;
            }
        };
        moottori.getLoadWorker().stateProperty().addListener(kuuntelija);
        
        moottori.load(perusOsoite + "selection/view?");
    }

    @Override
    public void lataaKurssitarjotin(List<PeriodinTunniste> periodit) {
        kuuntelija = (tarkkailtava, vanhaArvo, uusiArvo) -> {switch (uusiArvo) {
                case SUCCEEDED:
                    try {
                        luePeriodi(moottori.getDocument());
                    } catch(Throwable t) {
                        lahetaVirhe(t);
                        moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                        return;
                    }
                    if(!periodit.isEmpty()) {
                        nykyinenPeriodi = periodit.remove(0);
                        moottori.load(perusOsoite + "selection/" + linkit.get(nykyinenPeriodi));
                    } else {
                        moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                        lahetaKurssitarjotin();
                    }
                    break;
                case FAILED:
                    lahetaVirhe(moottori.getLoadWorker().getException());
                    moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                    break;
                case CANCELLED:
                    if(vanhaArvo == Worker.State.SUCCEEDED) {
                        break;
                    }
                    lahetaVirhe(new LataajaPoikkeus("Lataus keskeytettiin."));
                    moottori.getLoadWorker().stateProperty().removeListener(kuuntelija);
                    break;
            }
        };
        moottori.getLoadWorker().stateProperty().addListener(kuuntelija);
        
        if(!periodit.isEmpty()) {
            nykyinenPeriodi = periodit.remove(0);
            moottori.load(perusOsoite + "selection/" + linkit.get(nykyinenPeriodi));
        }
    }
    
    /**Palauttaa Wilman opiskelijan etusivun URL-osoitteen.
     * <p>
     * Jos käyttäjä on surffannut sivulla ennen periodien lataus -painikkeen painamista, 
     * etusivun osoite saadaan myöhempää käyttöä varten metodin avulla.
     * 
     * @param URL {@code WebEnginen} nykyisen sivun osoite
     * @return opiskelijan etusivun osoite
     * @throws kva.logiikka.lataus.KurssitarjottimenLataaja.LataajaPoikkeus jos WebEnginellä 
     *         ei ole kirjauduttu Wilmaan
     */
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
    
    /**Lukee Wilmasta periodien tunnisteet ja lisää ne {@code KurssitarjottimenLataajalle}.
     * <p>
     * Metodia kutsutaan Wilman "selection/view?"-sivun elementeille, joiden id:t 
     * ovat "own-schools" ja "ext-schools".
     * 
     * @param e Kurssivalintasivun elementti, joka sisältää vaihtoehtoiset periodit.
     */
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
    
    /**Lukee tietyn periodin {@code Ryhmat} ja {@code Moduulit} sekä lisää ne {@code KurssitarjottimenLataajalle}.
     * <p>
     * Metodia kutsutaan kutakin valittua periodia kuvaavalle dokumentille, jonka 
     * osoite on [opiskelijan etusivun osoite] + "selection/" + [periodikohtainen merkkijono].
     * 
     * @param d periodia kuvaavan sivun lähdekoodin sisältävä HTML-dokumentti
     */
    private void luePeriodi(Document d) {
        Element paalohko = d.getElementById("main-tray-parent");
        Element tarjotinSailio = (Element) paalohko.getElementsByTagName("div").item(0);
        Element taulukkoSailio = (Element) tarjotinSailio.getElementsByTagName("div").item(1);
        Element tarjotin = (Element) taulukkoSailio.getElementsByTagName("ul").item(0);
        
        HashSet<String> kaytetytPalkkienNimet = new HashSet<>();
        
        for(int i = 0; i < tarjotin.getElementsByTagName("li").getLength(); i = i + 2) {
            Element palkkiSailio = (Element) tarjotin.getElementsByTagName("li").item(i);
            Element otsikko = (Element) palkkiSailio.getElementsByTagName("span").item(0);
            
            String palkinNimi = otsikko.getTextContent().trim();
            if(kaytetytPalkkienNimet.contains(palkinNimi)) {
                palkinNimi = " " + palkinNimi;
            }
            kaytetytPalkkienNimet.add(palkinNimi);
            PalkinTunniste palkki = new PalkinTunniste(nykyinenPeriodi, palkinNimi, i);
            
            Element ryhmienSailio = (Element) palkkiSailio.getElementsByTagName("li").item(0);
            for(int a = 0; a < ryhmienSailio.getElementsByTagName("a").getLength(); a++) {
                Element ryhma = (Element) ryhmienSailio.getElementsByTagName("a").item(a);
                if(ryhma.getAttribute("title").contains("Tämä kurssi on jo suoritettu.")) {
                    break;
                }
                String ryhmakoodi = ryhma.getTextContent();
                if(ryhmakoodi.split("[.]").length != 2) {
                    break;
                }
                if(!lisaaSijainti(ryhmakoodi, palkki)) {
                    LuotavaRyhma uusiRyhma = new LuotavaRyhma(ryhmakoodi);
                    uusiRyhma.lisaaSijainti(palkki);
                    lisaaRyhma(uusiRyhma);
                    
                    String kurssikoodi = ryhmakoodi.split("[.]")[0];
                    if(!onModuulia(kurssikoodi)) {
                        Moduuli.Tyyppi uusiTyyppi;
                        switch(ryhma.getAttribute("class")) {
                            case " kB123A25D_10026-off":
                            case " kB123A25D_10024-off":
                            case " kB123A25D_10024-off disa":
                                uusiTyyppi = Moduuli.Tyyppi.PAKOLLINEN;
                                break;
                            case " kB123A25D_10027-off":
                            case " kB123A25D_10023-off":
                            case " kB123A25D_10023-off disa":
                                uusiTyyppi = Moduuli.Tyyppi.VALTAKUNNALLINEN_SYVENTAVA;
                                break;
                            case " kB123A25D_159-off":
                            case " kB123A25D_10025-off":
                            case " kB123A25D_10025-off disa":
                            default:
                                uusiTyyppi = Moduuli.Tyyppi.SOVELTAVA;
                        }
                        lisaaModuuli(new Moduuli(kurssikoodi, uusiTyyppi));
                    }
                }
            }
        }
    }
}
