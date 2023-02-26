package chickentechshop.campaign.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.util.Highlights;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

public class TechMarket extends BaseSubmarketPlugin {

    public static RepLevel MIN_STANDING = RepLevel.VENGEFUL;

    public static Logger log = Global.getLogger(TechMarket.class);

    static Exception failedLoad = null;
    protected static Set<SubmarketAPI> cachedSubmarkets = null;

    @Override
    public void updateCargoPrePlayerInteraction() {
        // log.info("Days since update: " + sinceLastCargoUpdate);
        if (sinceLastCargoUpdate < 3)
            return;
        sinceLastCargoUpdate = 0f;

        CargoAPI cargo = getCargo();

        // clear inventory
        for (CargoStackAPI s : cargo.getStacksCopy()) {
            float qty = s.getSize();
            cargo.removeItems(s.getType(), s.getData(), qty);
        }
        cargo.removeEmptyStacks();
        // addShips();
        // addWings();
        // addWeapons();
        addSpecialTech();
        cargo.sort();
    }

    // addSpecialTech adds tech items to shop, such as colony items, AI cores and
    // blueprints
    // For now, just getting basic items to work!
    protected void addSpecialTech() {
        CargoAPI cargo = getCargo();

        for (SpecialItemSpecAPI spec : Global.getSettings().getAllSpecialItemSpecs()) {
            final String[] dontAdd = { "fighter_bp", "industry_bp", "modspec", "ship_bp", "weapon_bp",
                    "nex_factionSetupItem" };
            if (!Arrays.asList(dontAdd).contains(spec.getId())) {
                log.info("Trying to add " + spec.getId());
                cargo.addSpecial(new SpecialItemData(spec.getId(), null), 3f);
            }
        }
    }

    protected void addWeapons() {
        CargoAPI cargo = getCargo();
        List<String> weaponIds = Global.getSector().getAllWeaponIds();

        for (String weaponId : weaponIds) {
            cargo.addWeapons(weaponId, 10);
        }
    }

    protected void addWings() {
        CargoAPI cargo = getCargo();
        for (FighterWingSpecAPI spec : Global.getSettings().getAllFighterWingSpecs()) {
            cargo.addItems(CargoAPI.CargoItemType.FIGHTER_CHIP, spec.getId(), 5);
        }
    }

    protected void addShips() {

        // CargoAPI cargo = getCargo();
        // FleetDataAPI data = cargo.getMothballedShips();
    }

    // call this on game load
    public static void clearSubmarketCache() {
        cachedSubmarkets = null;
    }

    public static void cacheSubmarketsIfNeeded() {
        if (cachedSubmarkets == null) {
            cachedSubmarkets = new HashSet<>();
            for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                if (market.hasSubmarket("chicken_market")) {
                    cachedSubmarkets.add(market.getSubmarket("chicken_market"));
                }
            }
        }
    }

    // ==========================================================================
    // ==========================================================================

    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL)
            return true;
        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL)
            return true;
        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL)
            return true;
        return false;
    }

    @Override
    public float getTariff() {
        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
        float mult = 1f;
        switch (level) {
            case NEUTRAL:
                mult = 1f;
                break;
            case FAVORABLE:
                mult = 0.9f;
                break;
            case WELCOMING:
                mult = 0.75f;
                break;
            case FRIENDLY:
                mult = 0.65f;
                break;
            case COOPERATIVE:
                mult = 0.5f;
                break;
            default:
                mult = 1f;
        }
        return mult;
    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
    }

    @Override
    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        return "No sales/returns";
    }

    @Override
    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        return "No sales/returns";
    }

    @Override
    public String getTooltipAppendix(CoreUIAPI ui) {
        return null;
    }

    @Override
    public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
        return null;
    }

    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
        return level.isAtWorst(MIN_STANDING);
    }

    @Override
    public boolean isBlackMarket() {
        return false;
    }

}