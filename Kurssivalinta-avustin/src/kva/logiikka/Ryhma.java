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
package kva.logiikka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javafx.collections.SetChangeListener;
import kva.logiikka.lataus.LuotavaRyhma;
import kva.logiikka.tapahtumat.ValintaKuuntelija;

/**Esitys kurssitarjottimeen kuuluvasta ryhmästä.
 * <p>
 * Moduulia itseään kuvaavat tiedot, esimerkiksi onko moduuli pakollinen, säilötään 
 * luokassa {@link kva.logiikka.Moduuli}, ja ne saa selville {@link #getModuuli()}-metodin 
 * kautta. Ryhmää itseään koskevat tiedot, kuten sijainti kurssitarjottimessa, säilötään 
 * tähän luokkaan.
 * <p>
 * {@link kva.logiikka.Kurssitarjotin} luo omat {@code Ryhmansa} sille annettujen 
 * tietojen pohjalta. {@code Ryhmien} sisältämiä tietoja ei voi muuttaa jälkikäteen.
 *
 * @author Väinö Viinikka
 */
public class Ryhma {
    
    private Kurssitarjotin tarjotin;
    private Moduuli moduuli;
    private String ryhmakoodi;
    private ArrayList<PalkinTunniste> sijainnit;
    private Collection<ValintaKuuntelija> valintaKuuntelijat;

    /**Luo uuden {@code Ryhman} annettujen tietojen perusteella.
     * <p>
     * Tiedot ryhmäkoodista, sijainneista kurssitarjottimessa sekä muut ryhmälle yksilölliset 
     * tiedot saadaan parametrina annetulta {@code LuotavaltaRyhmalta}.
     * 
     * @param pohja sisältää tiedot sijainneista ym. ryhmäkohtaisista asioista
     * @param moduuli sisältää tiedot {@code Ryhman} kuvaamasta opintokokonaisuudesta
     * @param tarjotin {@code Kurssitarjotin}, johon {@code Ryhma} kuuluu
     * @throws java.lang.NullPointerException jos jokin parametreista on {@code null}
     * @throws java.lang.IllegalArgumentException jos {@code moduuli} ja {@code pohja} 
     *         palauttavat eri ryhmäkoodit
     */
    public Ryhma(LuotavaRyhma pohja, Moduuli moduuli, Kurssitarjotin tarjotin) {
        Objects.requireNonNull(pohja, "Pohja ei saa olla null.");
        Objects.requireNonNull(moduuli, "Moduuli ei saa olla null.");
        Objects.requireNonNull(tarjotin, "Tarjotin ei saa olla null.");
        if(!pohja.getKurssikoodi().equals(moduuli.getKoodi())) {
            throw new IllegalArgumentException("Ryhmän kurssikoodit ovat ristiriitaiset: LuotavanRyhman koodi on \"" + 
                    pohja.getKurssikoodi() + "\" ja Moduulin \"" + moduuli.getKoodi() + "\".");
        }
        
        ryhmakoodi = pohja.getRyhmakoodi();
        sijainnit = pohja.getSijainnit();
        
        this.moduuli = moduuli;
        this.tarjotin = tarjotin;
        
        valintaKuuntelijat = new ArrayList<>();
        SetChangeListener<Ryhma> kuuntelija = (muutos) -> {
            if(muutos.wasAdded()) {
                if(this.equals(muutos.getElementAdded())) {
                    valintaKuuntelijat.forEach((vk) -> vk.valittu(this));
                } else if(this.getModuuli().equals(muutos.getElementAdded().getModuuli())) {
                    valintaKuuntelijat.forEach((vk) -> vk.valittuMuualta(this));
                }
            } else {
                if(this.equals(muutos.getElementRemoved())) {
                    valintaKuuntelijat.forEach((vk) -> vk.valintaPoistettu(this));
                } else if(this.getModuuli().equals(muutos.getElementRemoved().getModuuli())) {
                    valintaKuuntelijat.forEach((vk) -> vk.valintaPoistettuMuualta(this));
                }
            }
        };
        tarjotin.getValitutRyhmat().addListener(kuuntelija);
    }
    
    /**Palauttaa {@code Moduulin}, joka sisältää tiedot ryhmän kuvaamasta opintokokonaisuudesta.
     * 
     * @return {@code Ryhman Moduuli}
     */
    public Moduuli getModuuli() {
        return moduuli;
    }
    
    /**Palauttaa {@code Kurssitarjottimen}, johon {@code Ryhma} kuuluu.
     * 
     * @return {@code Kurssitarjotin}, joka sisältää {@code Ryhman}.
     */
    public Kurssitarjotin getTarjotin() {
        return tarjotin;
    }
    
    /**Palauttaa ryhmän ryhmäkoodin, joka sisältää kurssikoodin sekä ryhmätunnisteen, 
     * esimerkiksi HI02.4.
     * 
     * @return ryhmäkoodi
     */
    public String getKoodi() {
        return ryhmakoodi;
    }

    /**Palauttaa samaa kurssia koskevia {@code Ryhmia} toisistaan erottavan tunnuksen.
     * <p>
     * Esimerkiksi ryhmäkoodin "FY05.2" ryhmätunniste olisi "2". Nimeä ei palauteta 
     * kokonaislukumuodossa, koska ainakin Otaniemen lukiossa on käytössä myös kirjaimia 
     * sisältäviä ryhmänimiä, kuten MAA03.D1.
     * 
     * @return ryhmäkoodin pistettä seuraava osa
     */
    public String getRyhmatunniste() {
        String[] arr = ryhmakoodi.split("[.]");
        return arr[arr.length - 1];
    }

    /**Palauttaa listan {@code RyhmanSijainneista}, jotka kuvaavat {@code Ryhman} 
     * sijainteja kurssitarjottimessa.
     * <p>
     * Sijainteja ei voi lisätä tai poistaa palautetun listan kautta.
     * 
     * @return lista sijainteja kuvaavista {@code PalkinTunniste}-olioista
     */
    public ArrayList<PalkinTunniste> getSijainnit() {
        return new ArrayList<>(sijainnit);
    }
    
    /**Kertoo, onko kyseinen ryhmä valittu vai ei.
     * 
     * @return {@code true}, jos {@code Ryhma} on valittu, muuten {@code false}
     */
    public boolean OnValittu() {
        return tarjotin.getValitutRyhmat().contains(this);
    }

    /**Parametrin arvolla {@code true} merkitsee {@code Ryhman} valituksi. Muussa 
     * tapauksessa poistaa {@code Ryhman} valinnan.
     * 
     * @param onValittu määrittää, valitaanko {@code Ryhma} vai poistetaanko sen 
     *        valinta
     */
    public void setOnValittu(boolean onValittu) {
        if(onValittu) {
            tarjotin.getValitutRyhmat().add(this);
        } else {
            tarjotin.getValitutRyhmat().remove(this);
        } 
    }

    /**Kertoo, onko samalta kurssitarjottimelta valittu jokin muu {@code Ryhma}, jolla 
     * on sama {@code Moduuli}.
     * <p>
     * {@code Kurssitarjotin} ei salli useamman samamoduulisen {@code Ryhman} valintaa 
     * samaan aikaan. 
     * 
     * @return {@code true}, jos moduuli on valittu muualta, muuten {@code false}
     */
    public boolean onValittuMuualta() {
        return tarjotin.getValitutRyhmat().stream()
                .filter((ryhma) -> ryhma.getModuuli().equals(getModuuli()))
                .anyMatch((ryhma) -> !ryhma.equals(this));
    }
    
    /**Lisää {@code Ryhmalle} uuden {@code ValintaKuuntelijan}.
     * <p>
     * Sama {@code ValintaKuuntelija} voidaan lisätä {@code Ryhmalle} useita kertoja. 
     * Tällöin sen asiaankuuluvia metodeita kutsutaan useita kertoja kunkin tapahtuman 
     * yhteydessä.
     * 
     * @param kuuntelija lisättävä {@code ValintaKuuntelija}
     */
    public void lisaaValintaKuuntelija(ValintaKuuntelija kuuntelija) {
        valintaKuuntelijat.add(kuuntelija);
    }
    
    /**Poistaa {@code Ryhmalta} annetun {@code ValintaKuuntelijan}.
     * <p>
     * Jos {@code Ryhmalla} ei alun perinkään ollut kyseistä {@code ValintaKuuntelijaa}, 
     * mitään ei tapahdu. Sama kuuntelija voidaan lisätä {@code Ryhmalle} useita 
     * kertoja. Tällaisissa tapauksissa tämä metodi poistaa vain yhden duplikaateista.
     * 
     * @param kuuntelija poistettava {@code ValintaKuuntelija}
     */
    public void poistaValintaKuuntelija(ValintaKuuntelija kuuntelija) {
        valintaKuuntelijat.remove(kuuntelija);
    }

    @Override
    public String toString() {
        return "Ryhma{ryhmakoodi=" + ryhmakoodi + ", moduuli=" + moduuli + ", sijainnit=" + sijainnit + '}';
    }
}
