/**Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
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
package kva.logiikka;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import kva.logiikka.lataus.LuotavaRyhma;

/**Säiliöluokka kurssitarjottimen {@link kva.logiikka.Ryhma}-olioille
 *
 * @author Väinö Viinikka
 */
public class Kurssitarjotin {
    
    private Set<Ryhma> ryhmat;
    private Set<Moduuli> moduulit;
    private Set<RyhmanSijainti> mahdollisetSijainnit;
    private ObservableSet<Ryhma> valitutRyhmat;
    
    /**Luo uuden {@code Kurssitarjottimen}.
     * <p>
     * Konstruktori on tarkoitettu ensisijaisesti kutsuttavaksi {@link kva.logiikka.lataus.KurssitarjottimenLataaja}sta, 
     * mutta sen luomiselle itse ei ole periaatteellista estettä.
     * 
     * @param ryhmat lista {@link kva.logiikka.lataus.LuotavaRyhma}-olioista, joiden 
     *        perusteella {@code Kurssitarjotin} luo {@code Ryhmansa}
     * @param moduulit lista {@code Ryhmien Moduuleista}
     * @throws java.lang.IllegalArgumentException jos {@code moduulit} ei sisällä 
     *         moduuleja kaikille {@code ryhmat}-listalla esiintyville ryhmille
     * @throws java.lang.NullPointerException jos {@code ryhmat} tai {@code moduulit} 
     *         on arvoltaan {@code null} tai sisältää {@code null}-arvon
     */
    public Kurssitarjotin(Collection<LuotavaRyhma> ryhmat, Collection<Moduuli> moduulit) {
        this.ryhmat = new HashSet<>();
        this.moduulit = new HashSet<>(moduulit);
        this.mahdollisetSijainnit = new HashSet<>();
        this.valitutRyhmat = FXCollections.observableSet(new HashSet<>());
        
        HashMap<String, Moduuli> etsintaaVarten = new HashMap<>();
        moduulit.forEach((moduuli) -> etsintaaVarten.put(moduuli.getKoodi(), moduuli));
        
        ryhmat.forEach((ryhmanPohja) -> {
            if(!etsintaaVarten.containsKey(ryhmanPohja.getKurssikoodi())) {
                throw new IllegalArgumentException("Kurssikoodille \"" + ryhmanPohja.getKurssikoodi() + "\" ei löydy Moduulia.");
            }
            this.ryhmat.add(new Ryhma(ryhmanPohja, etsintaaVarten.get(ryhmanPohja.getKurssikoodi()), this));
            mahdollisetSijainnit.addAll(ryhmanPohja.getSijainnit());
        });
    }
    
    /**Palauttaa tällä hetkellä valituiksi merkityt {@code Ryhmat} {@link javafx.collections.ObservableSet}-muodossa.
     * <p>
     * Kursseja voi merkitä valituiksi tai ei-valituiksi yhtä hyvin komennoilla 
     * {@code getValitutRyhmat().add()} ja {@code getValitutRyhmat().remove()} kuin 
     * {@code Ryhman} metodin {@code setOnValittu()} kautta.
     * 
     * @return Listaus valituista {@code Ryhmista}
     */
    public ObservableSet<Ryhma> getValitutRyhmat() {
        return valitutRyhmat;
    }
    
    /**Palauttaa sen {@code Kurssitarjottimen Moduulin}, jolla on annettu koodi.
     * 
     * @param koodi halutun moduulin koodi, esimerkiksi ENA05.
     * @return haluttua kurssia kuvaava {@code Moduuli}-olio, tai {@code null} jos 
     *         haluttua moduulia ei ole
     */
    public Moduuli haeModuuli(String koodi) {
        //Moduulien haku voidaan muuttaa tehtäväksi HashMapin avulla, jos se osoittautuu tarpeelliseksi.
        return moduulit.stream()
                .filter((moduuli) -> moduuli.getKoodi().equals(koodi))
                .findAny()
                .orElse(null);
    }
}
