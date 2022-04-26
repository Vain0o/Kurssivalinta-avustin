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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import kva.logiikka.lataus.LuotavaRyhma;

/**Säiliöluokka kurssitarjottimen {@code Ryhmille}.
 * <p>
 * Luokka sisältää toiminnallisuuden {@link kva.logiikka.Ryhma}-olioiden merkitsemiseen 
 * valituksi tai ei-valituksi, sekä näiden toimenpiteiden kuuntelulle. Se sisältää 
 * myös tiedot {@code Ryhmien} {@link kva.logiikka.Moduuli}-olioista sekä järjestyksestä, 
 * jossa käyttöliittymän tulisi esittää periodit ja palkit.
 * <p>
 * {@code Kurssitarjottimessa} ei voi valita samaan aikaan useampaa {@code Ryhmaa}, 
 * joilla on samat {@code Moduulit}, eikä useampaa ryhmaa, jotka sijaitsevat samassa 
 * palkissa. Kun käyttäjä valitsee {@code Ryhman}, mahdollisten päällekkäisten {@code Ryhmien} 
 * valinta poistetaan.
 *
 * @author Väinö Viinikka
 */
public class Kurssitarjotin {
    
    private Set<Ryhma> ryhmat;
    private Set<Moduuli> moduulit;
    private List<PalkinTunniste> mahdollisetPalkit;
    private final ObservableSet<Ryhma> valitutRyhmat;
    
    /**Luo uuden {@code Kurssitarjottimen}.
     * <p>
     * Konstruktori on tarkoitettu ensisijaisesti kutsuttavaksi {@link kva.logiikka.Sovelluslogiikka}sta, 
     * mutta sen luomiselle muualla ei ole periaatteellista estettä.
     * 
     * @param ryhmat lista {@link kva.logiikka.lataus.LuotavaRyhma}-olioista, joiden 
     *        perusteella {@code Kurssitarjotin} luo {@code Ryhmansa}
     * @param moduulit lista {@code Ryhmien Moduuleista}
     * @param periodienJarjestys kertoo periodien keskinäisen järjestyksen.
     * @throws java.lang.IllegalArgumentException jos {@code moduulit} ei sisällä 
     *         moduuleja kaikille {@code ryhmat}-listalla esiintyville ryhmille
     * @throws java.lang.IllegalArgumentException jos {@code periodienJarjestys} 
     *         ei sisällä kaikkia oppilaitoksen ja periodin yhdistelmiä, jotka esiintyvät 
     *         {@code ryhmien} alkioiden mahdollisissa sijainneissa
     * @throws java.lang.NullPointerException jos jokin parametreistä on arvoltaan 
     *         {@code null} tai sisältää {@code null}-arvon
     */
    public Kurssitarjotin(Collection<LuotavaRyhma> ryhmat, Collection<Moduuli> moduulit, List<PeriodinTunniste> periodienJarjestys) {
        this.ryhmat = new HashSet<>();
        this.moduulit = new HashSet<>(moduulit);
        this.mahdollisetPalkit = new ArrayList<>();
        this.valitutRyhmat = FXCollections.observableSet(new HashSet<>());
        
        valitutRyhmat.addListener((SetChangeListener.Change<? extends Ryhma> change) -> {
            if(change.wasAdded()) {
                HashSet<Ryhma> poistettavat = new HashSet<>();
                valitutRyhmat.stream()
                        .filter((ryhma) -> {
                            if(ryhma.getModuuli().equals(change.getElementAdded().getModuuli())) {
                                return true;
                            }
                            return change.getElementAdded().getSijainnit().stream()
                                    .anyMatch((tunniste) -> (ryhma.getSijainnit().contains(tunniste)));
                        })
                        .filter((ryhma) -> !change.getElementAdded().equals(ryhma))
                        .forEach((ryhma) -> poistettavat.add(ryhma));
                valitutRyhmat.removeAll(poistettavat);
            }
        });
        
        HashMap<String, Moduuli> etsintaaVarten = new HashMap<>();
        moduulit.forEach((moduuli) -> etsintaaVarten.put(moduuli.getKoodi(), moduuli));
        
        ryhmat.forEach((ryhmanPohja) -> {
            if(!etsintaaVarten.containsKey(ryhmanPohja.getKurssikoodi())) {
                throw new IllegalArgumentException("Kurssikoodille \"" + ryhmanPohja.getKurssikoodi() + "\" ei löydy Moduulia.");
            }
            this.ryhmat.add(new Ryhma(ryhmanPohja, etsintaaVarten.get(ryhmanPohja.getKurssikoodi()), this));
            ryhmanPohja.getSijainnit().stream()
                    .filter((palkki) -> !mahdollisetPalkit.contains(palkki))
                    .forEach((palkki) -> mahdollisetPalkit.add(palkki));
        });
        
        Comparator<PalkinTunniste> vertailija = Comparator.comparing((sijainti) -> {
            PeriodinTunniste kokeilu = new PeriodinTunniste(sijainti.getOppilaitos(), sijainti.getPeriodi());
            int tulos = periodienJarjestys.indexOf(kokeilu);
            if(tulos == -1) {
                throw new IllegalArgumentException("Periodin " + kokeilu.toString() + " paikkaa ei kerrottu.");
            }
            return tulos;
        });
        Comparator<PalkinTunniste> vertailija2 = vertailija.thenComparing(PalkinTunniste::getJarjestysluku);
        mahdollisetPalkit.sort(vertailija2);
    }
    
    /**Palauttaa kaikki {@code Kurssitarjottimen Ryhmat}.
     * <p>
     * Metodia kannattaa käyttää yhdessä Stream-API:n kanssa esimerkikisi tiettyjen 
     * {@code Ryhmien} hakemiseen.
     * 
     * @return kokoelma {@code Ryhma}-olioista
     */
    public Set<Ryhma> getKaikkiRyhmat() {
        return new HashSet<>(ryhmat);
    }
    
    /**Palauttaa tällä hetkellä valituiksi merkityt {@code Ryhmat} {@link javafx.collections.ObservableSet}-muodossa.
     * <p>
     * Kursseja voi merkitä valituiksi tai ei-valituiksi yhtä hyvin komennoilla 
     * {@code getValitutRyhmat().add()} ja {@code getValitutRyhmat().remove()} kuin 
     * {@code Ryhman} metodin {@code setOnValittu()} kautta.
     * 
     * @return listaus valituista {@code Ryhmista}
     */
    public ObservableSet<Ryhma> getValitutRyhmat() {
        return valitutRyhmat;
    }
    
    /**Palauttaa listan {@code Kurssitarjottimen Moduulien} mahdollisista sijainneista.
     * <p>
     * Lista palautetaan siinä järjestyksessä, jossa palkit tulee käyttöliittymässä 
     * esittää.
     * 
     * @return lista mahdollisia sijainteja kuvaavista {@link kva.logiikka.PalkinTunniste}ista.
     */
    public List<PalkinTunniste> getMahdollisetPalkit() {
        return new ArrayList<>(mahdollisetPalkit);
    }
    
    /**Palauttaa listan {@code Kurssitarjottimen Ryhmien} mahdollisista periodeista.
     * <p>
     * Lista palautetaan siinä järjestyksessä, jossa periodit tulee käyttöliittymässä 
     * esittää.
     * 
     * @return lista mahdollisia sijainteja kuvaavista {@link kva.logiikka.PeriodinTunniste}
     */
    public List<PeriodinTunniste> getMahdollisetPeriodit() {
        return mahdollisetPalkit.stream()
                .map((palkki) -> palkki.getPeriodinTunniste())
                .distinct()
                .collect(Collectors.toList());
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
