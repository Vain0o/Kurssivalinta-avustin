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
package kva.logiikka.tapahtumat;

import kva.logiikka.Kurssitarjotin;
import kva.logiikka.Ryhma;

/**{@code Ryhman} valintatilan muutosta kuvaava tapahtuma.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.tapahtumat.ValintaKuuntelija
 * @see kva.logiikka.tapahtumat.ValintaTapahtuma.TapahtumaTyyppi
 * @see kva.logiikka.Ryhma
 */
public class ValintaTapahtuma {
    
    private final Ryhma kohde;
    private final TapahtumaTyyppi tyyppi;
    
    /**Luo uuden {@code ValintaTapahtuman}.
     * 
     * @param kohde {@code Ryhma}, jonka valintatapahtumasta on kyse
     * @param tyyppi kertoo, millainen tapahtuma oli kyseessa
     */
    public ValintaTapahtuma(Ryhma kohde, TapahtumaTyyppi tyyppi) {
        this.kohde = kohde;
        this.tyyppi = tyyppi;
    }

    /**{@code Ryhma}, jonka valintatapahtumasta on kyse.
     * 
     * @return kyseessä oleva {@code Ryhma}
     */
    public Ryhma getKohde() {
        return kohde;
    }

    /**Kertoo, millainen tapahtuma oli kyseessä.
     * 
     * @return tapahtuman tyyppi.
     */
    public TapahtumaTyyppi getTyyppi() {
        return tyyppi;
    }

    /**{@code Kurssitarjotin}, jonka {@code Ryhman} valintatapahtumasta on kyse.
     * 
     * @return {@code Ryhman Kurssitarjotin}
     */
    public Kurssitarjotin getTarjotin() {
        return kohde.getTarjotin();
    }
    
    /**Esitys {@code ValintaTapahtuman} mahdollisista tyypeistä.
     * 
     * @author Väinö Viinikka
     * @see kva.logiikka.tapahtumat.ValintaTapahtuma
     * @see kva.logiikka.Ryhma
     */
    public enum TapahtumaTyyppi {
        
        /**Kertoo, että {@code Ryhma} on valittu.
         * 
         */
        VALITTU, 
        
        /**Kertoo, että {@code Ryhman} valinta on poistettu.
         * 
         */
        VALINTA_POISTETTU, 
        
        /**Kertoo, että samassa {@code Kurssitarjottimessa} on valittu {@code Ryhma}, 
         * jolla on sama {@code Moduuli}, kuin kyseessä olevalla ryhmalla.
         * 
         */
        VALITTU_MUUALTA, 
        
        /**Kertoo, että samassa {@code Kurssitarjottimessa} ei ole enää valittuna 
         * {@code Ryhmaa}, jonka {@code Moduuli} olisi sama, kuin kyseessä olevan {@code Ryhman}.
         * 
         */
        VALINTA_POISTETTU_MUUALTA;
    }
}
