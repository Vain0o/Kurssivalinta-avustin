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

import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**Toteuttaa {@code Nakyman}, jossa käyttäjä valitsee aineet, joiden kursseja hänelle 
 * näytetään.
 * <p>
 * Näkymässä käyttäjä voi valita ensin suomen kielen, matematiikan ja ruotsin kielen 
 * oppimäärät sekä katsomusaineen {@code RadioButton}-painikkeilla. Tämän jälkeen 
 * hän valitsee muut opiskelemansa kielet oppimäärittäin  {@code CheckBox}-painikkeilla.
 * Lopussa on lisäksi toiminnallisuus piilotettavien ja epäkiinnostavien aineiden 
 * lisäämiseen ja poistamiseen manuaalisesti. Näitä työkaluja voi käyttää esimerkiksi 
 * silloin, jos sattui vahingossa klikkaamaan kurssitarjottimesta "piilota aine".
 * <p>
 * Kaikki muutokset päivitetään {@code Kayttoliittyman Asetukset}-olion kokoelmiin. 
 * {@code Asetuksiin} tehtävät muutokset päivittyvät automaattisesti lopun manuaalisiin 
 * työkaluihin, mutteivät {@code CheckBoxeihin} eivätkä {@code RadioButtoneihin}.
 *
 * @author Väinö Viinikka
 * @see Asetukset
 */
public class AsetusNakyma extends Nakyma {
    
    /**Luo uuden asetusnäkymän.
     * 
     * @param otsikko {@code Nakyman} kuvastaman välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code Nakyma} kuuluu
     */
    public AsetusNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
    }

    @Override
    public Node luoSisalto() {
        VBox asettelu = new VBox();
        asettelu.setSpacing(10);
        asettelu.setPadding(new Insets(10, 0, 10, 5));
        
        asettelu.getChildren().add(new Label("Täytä tähän ainevalintasi, jotta sinulle " + 
                "ei näytetä kursseja, joita et tule käymään."));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse, opiskeletko suomea äidinkielenä vai toisena kielenä."));
        asettelu.getChildren().addAll(luoRadioButtonit(new String[] {"Äidinkielenä (ÄI)", "Toisena kielenä (S2)"}, 
                new String[] {"ÄI", "S2"}, "ÄI"));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse matematiikan oppimäärä."));
        asettelu.getChildren().addAll(luoRadioButtonit(new String[] {"Pitkä (MAA)", "Lyhyt (MAB)"}, 
                new String[] {"MAA", "MAB"}, "MAA"));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse ruotsin oppimäärä."));
        asettelu.getChildren().addAll(luoRadioButtonit(new String[] {"Pitkä (RUA)", "Keskipitkä (RUB)"}, 
                new String[] {"RUA", "RUB"}, "RUB"));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse kasomusaine."));
        asettelu.getChildren().addAll(luoRadioButtonit(new String[] {"Evankelis-luterilaisuus (UE)", "Ortodoksisuus (UO)", 
                "Katolilaisuus (UK)", "Islam (UI)", "Juutalaisuus (UJ)", "Elämänkatsomustieto (ET)", "Muu uskonto (UX)"}, 
                new String[] {"UE", "UO", "UK", "UI", "UJ", "ET", "UX"}, "UE"));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse opiskelemasi pitkän oppimäärän kielet."));
        ArrayList<CheckBox> pitkatKielet = luoCheckBoxit(new String[] {"Englanti (ENA)", "Saksa (SAA)", "Ranska (RAA)", 
                "Espanja (EAA)", "Venäjä (VEA)"}, new String[] {"ENA", "SAA", "RAA", "EAA", "VEA"});
        pitkatKielet.get(0).setSelected(true);
        asettelu.getChildren().addAll(pitkatKielet);
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse opiskelemasi B2-oppimäärän kielet."));
        asettelu.getChildren().addAll(luoCheckBoxit(new String[] {"Saksa (SAB2)", "Ranska (RAB2)", "Espanja (EAB2)", "Venäjä (VEB2)"}, 
                new String[] {"SAB2", "RAB2", "EAB2", "VEB2"}));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Valitse opiskelemasi B3-oppimäärän kielet."));
        asettelu.getChildren().addAll(luoCheckBoxit(new String[] {"Saksa (SAB3)", "Ranska (RAB3)", "Espanja (EAB3)", 
                "Italia (IAB3)", "Venäjä (VEB3)", "Kiina (KIB3)", "Japani (JPB3)", "Englanti (ENB3)"}, 
                new String[] {"SAB3", "RAB3", "EAB3", "IAB3", "VEB3", "KIB3", "JPB3", "ENB3"}));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Tässä voit vielä lisätä ja poistaa sellaisten aineiden koodeja, joita et halua"
                + "\nnäytettävän kurssitarjottimessa."));
        asettelu.getChildren().add(luoEsitys(getKayttoliittyma().getAsetukset().piilotetutAineet, "p"));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Tässä voit lisätä ja poistaa sellaisten aineiden koodeja, joista haluat näytettävän"
                + "\nkurssitarjottimessa vain pakolliset kurssit."));
        asettelu.getChildren().add(luoEsitys(getKayttoliittyma().getAsetukset().epakiinnostavatAineet, "e"));
        asettelu.getChildren().add(new Separator());
        
        asettelu.getChildren().add(new Label("Tässä voit lisätä ja poistaa sellaisten yksittäisten kurssien koodeja, joita et" 
                + "\nhalua näytettävän kurssitarjottimessa."));
        asettelu.getChildren().add(luoEsitys(getKayttoliittyma().getAsetukset().piilotetutModuulit, "k"));
        
        ScrollPane pohja = new ScrollPane();
        pohja.setContent(asettelu);
        
        pohja.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setMaxWidth(450);
        
        return new StackPane(pohja);
    }
    
    /**Luo {@code RadioButtonit} aineen oppimäärien valintaan tms. ja alustaa niiden kuuntelun.
     * <p>
     * Jokaisesta {@code vaihtoehdot}-taulukon alkiosta luodaan {@code RadioButton}, 
     * jolle alustetaan kuuntelu siten, että {@code ainekoodit}-taulukossa samalla 
     * indeksillä oleva merkkijono merkitään asetuksissa piilotetuksi aineeksi täsmälleen 
     * silloin, kun kyseistä {@code RadioButtonia} ei ole valittu. {@code RadioButtonit} 
     * järjestetään samaan {@code ToggleGroupiin}.
     * 
     * @param vaihtoehdot {@code RadioButtonien} tekstit
     * @param ainekoodit niiden kouluaineiden koodit, joiden statusta {@code RadioButtoneilla} 
     *                   halutaan muuttaa
     * @param oletusarvo Se {@code RadioButton}, jonka ainekoodi on sama, kuin {@code oletusarvo} 
     *                   asetetaan automaattisesti valituksi. Jos mikään ainekoodeista 
     *                   ei ole sama kuin oletusarvo, mitään {@code RadioButtonia} 
     *                   ei valita.
     * @return luodut {@code RadioButtonit} samassa järjestyksessä, kuin missä ne 
     *         ovat {@code vaihtoehdot}-taulukossa
     * @exception java.lang.ArrayIndexOutOfBoundsException jos {@code ainekoodit} 
     *            on lyhyempi kuin {@code vaihtoehdot}
     */
    private ArrayList<RadioButton> luoRadioButtonit(String[] vaihtoehdot, String[] ainekoodit, String oletusarvo) {
        ArrayList<RadioButton> napit = new ArrayList<>();
        ToggleGroup ryhma = new ToggleGroup();
        
        for(int i = 0; i < vaihtoehdot.length; i++) {
            String teksti = vaihtoehdot[i];
            String ainekoodi = ainekoodit[i];
            
            RadioButton nappi = new RadioButton(teksti);
            nappi.setToggleGroup(ryhma);
            alustaKuuntelu(nappi.selectedProperty(), ainekoodi);
            if(oletusarvo.equals(ainekoodi)) {
                nappi.setSelected(true);
            }
            
            napit.add(nappi);
        }
        
        return napit;
    }
    
    /**Luo {@code CheckBoxit} kielten tms. valintaan ja alustaa niiden kuuuntelun.
     * <p>
     * Jokaisesta {@code vaihtoehdot}-taulukon alkiosta luodaan {@code CheckBox}, 
     * jolle alustetaan kuuntelu siten, että {@code ainekoodit}-taulukossa samalla 
     * indeksillä oleva merkkijono merkitään asetuksissa piilotetuksi aineeksi täsmälleen 
     * silloin, kun kyseistä {@code CheckBoxia} ei ole valittu.
     * 
     * @param vaihtoehdot {@code CheckBoxien} tekstit
     * @param ainekoodit niiden kouluaineiden koodit, joiden statusta {@code CheckBoxeilla} 
     *                   halutaan muuttaa
     * @return luodut {@code CheckBoxit} samassa järjestyksessä, kuin missä ne ovat 
     *                {@code vaihtoehdot}-taulukossa
     * @exception java.lang.ArrayIndexOutOfBoundsException jos {@code ainekoodit} 
     *            on lyhyempi kuin {@code vaihtoehdot}
     */
    private ArrayList<CheckBox> luoCheckBoxit(String[] vaihtoehdot, String[] ainekoodit) {
        ArrayList<CheckBox> napit = new ArrayList<>();
        
        for(int i = 0; i < vaihtoehdot.length; i++) {
            String teksti = vaihtoehdot[i];
            String ainekoodi = ainekoodit[i];
            
            CheckBox nappi = new CheckBox(teksti);
            nappi.setAllowIndeterminate(false);
            alustaKuuntelu(nappi.selectedProperty(), ainekoodi);
            
            napit.add(nappi);
        }
        
        return napit;
    }
    
    /**Alustaa valinnan kuuntelun käyttöliittymäkomponentin {@code SelectedPropertylle}.
     * <p>
     * Metodi lisää parametrina annetulle propertylle kuuntelijan, joka varmistaa, 
     * että parametrina annettu ainekoodi kuuluu {@code Kayttoliittyman} asetuksissa 
     * piilotettuihin aineisiin täsmälleen silloin, kun propertyn arvo on {@code false}. 
     * Samalla metodi varmistaa, että alkutilanne on oikein lisäämällä tai poistamalla 
     * ainekoodin tarvittaessa piilotetuista aineista.
     * 
     * @param nayttaminen {@code BooleanProperty}, joka kertoo, pitäisikö {@code ainekoodin} 
     *                    kurssien olla näkyvissä vai ei.
     * @param ainekoodi sen kouluaineen koodi, jonka kurssit halutaan näyttää tai 
     *                  piilottaa.
     */
    private void alustaKuuntelu(BooleanProperty nayttaminen, String ainekoodi) {
        nayttaminen.addListener((obs, vanhaArvo, uusiArvo) -> {
            setNaytetaanko(ainekoodi, uusiArvo);
        });
        setNaytetaanko(ainekoodi, nayttaminen.getValue());
    }
    
    /**Määrittää, näytetäänkö annettu aine kurssitarjottimessa.
     * <p>
     * Jos {@code naytetaanko} on {@code false}, {@code aine} lisätään {@code Asetuksissa} 
     * piilotettavien aineiden listalle. Jos se on {@code true}, {@code aine} poistetaan 
     * sieltä.
     * 
     * @param aine sen aineen koodi, jonka näkyvyyttä muutetaan
     * @param naytetaanko kertoo, piiloteteaanko vai näytetäänkö aine
     */
    private void setNaytetaanko(String aine, boolean naytetaanko) {
        if (naytetaanko) {
            getKayttoliittyma().getAsetukset().piilotetutAineet.remove(aine);
        } else {
            getKayttoliittyma().getAsetukset().piilotetutAineet.add(aine);
        }
    }
    
    /**Luo tarkkailtavasta kokoelmasta esityksen, jossa alkioiden lisääminen ja poistaminen 
     * on mahdollista.
     * <p>
     * Kokoelman esitys sisältää listan tarkkailtavan kokoelman elementeistä ja tekstikentän 
     * uusien elementtien lisäämiseen, sekä poistonapit elementtien poistamiseen. 
     * Listanäkymä päivittyy automaattisesti, kun kokoelmaan lisätään tai siitä poistetaan 
     * elementtejä muilla tavoin.
     * <p>
     * Esitys{@code Regionin} alkioissa käytetään ID:itä, jotka saadaan lisäämällä 
     * parametrina annettuun etuliitteeseen kokoelman alkion teksti. Etuliitettä voi 
     * käyttää estämään eri alkioiden sekoittuminen.
     * 
     * @param tarkkailtava kokoelma, jota esitys kuvaa
     * @param etuliite merkkijono, joka lisätään kokoelman sisältämien alkioiden ID:iden
     *                 alkuun
     * @return komponentti, joka sisältää esitysnäkymän.
     */
    private Region luoEsitys(ObservableSet<String> tarkkailtava, String etuliite) {
        FlowPane listaus = new FlowPane();
        TextField tekstikentta = new TextField();
        Button nappi = new Button("Lisää");
        listaus.setHgap(10);
        listaus.setVgap(2);
        
        EventHandler<ActionEvent> kasittelija = (e) -> {
            tarkkailtava.add(tekstikentta.getText());
            tekstikentta.clear();
        };
        tekstikentta.setOnAction(kasittelija);
        nappi.setOnAction(kasittelija);
        
        SetChangeListener<String> kuuntelija = (muutos) -> {
            if(muutos.wasAdded()) {
                listaus.getChildren().add(luoEsitysNappi(muutos.getElementAdded(), etuliite, tarkkailtava));
            } else {
                listaus.getChildren().remove(listaus.lookup("#" + etuliite + muutos.getElementRemoved()));
            }
        };
        tarkkailtava.addListener(kuuntelija);
        
        tarkkailtava.forEach((jasen) -> {
            listaus.getChildren().add(luoEsitysNappi(jasen, etuliite, tarkkailtava));
        });
        
        BorderPane pohja = new BorderPane();
        pohja.setCenter(listaus);
        pohja.setBottom(new HBox(tekstikentta, nappi));
        return pohja;
    }
    
    /**Luo {@code luoEsitys()}-metodin käyttämän elementin, joka sisältää merkkijonon 
     * ja napin sen poistamiselle kokoelmasta.
     * <p>
     * Palautettavan {@code Regionin} ID on {@code etuliite + teksti}. Etuliitteitä 
     * kannattaa käyttää estämään eri käyttöliittymäkomponenttien sekoittuminen keskenään.
     * 
     * @param teksti esitysnapissa näytettävä teksti
     * @param etuliite esitysnapin ID:hen lisättävä merkkijono
     * @param tarkkailtava Kun poistonappia klikataan, {@code teksti} poistetaan kokoelmasta.
     * @return luotu esitysnappi annetulla tekstillä ja muilla tiedoilla
     */
    private Region luoEsitysNappi(String teksti, String etuliite, Collection<String> tarkkailtava) {
        Button poistonappi = new Button("x");
        poistonappi.setStyle("-fx-background-color: #ff0000; ");
        poistonappi.setPadding(new Insets(0, 3, 0, 3));
        poistonappi.setOnAction((ev) -> tarkkailtava.remove(teksti));
        
        HBox pohja = new HBox(new Label(teksti));
        pohja.getChildren().add(poistonappi);
        pohja.setId(etuliite + teksti);
        
        return pohja;
    }
}
