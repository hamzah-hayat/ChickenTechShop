package chickentechshop.campaign.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import chickentechshop.campaign.intel.missions.chicken.ChickenQuestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class TechMarket extends BaseSubmarketPlugin {

    public static RepLevel MIN_STANDING = RepLevel.VENGEFUL;
    public static Logger log = Global.getLogger(TechMarket.class);

    private int techMarketLevel = 1;
    private int currentCredits = 0;
    private int[] levelCosts = { 100000, 150000, 200000, 250000 };

    public int getTechMarketLevel() {
        return techMarketLevel;
    }

    // Tech Market can be 1 to 5 inclusive
    public void setTechMarketLevel(int newLevel) {
        if (newLevel < 1) {
            techMarketLevel = 1;
        } else if (newLevel > 5) {
            techMarketLevel = 5;
        } else {
            techMarketLevel = newLevel;
        }
    }

    // Adding credits is the main way we increase our TechLevel
    public void addCreditsToTechMarket(int credits) {
        // The max level is 5
        if (techMarketLevel >= 5) {
            return;
        }

        currentCredits += credits;
        if (currentCredits >= levelCosts[getTechMarketLevel() - 1]) {
            currentCredits -= levelCosts[getTechMarketLevel() - 1];
            setTechMarketLevel(getTechMarketLevel() + 1);
            updateCargoForce();
        }
    }

    public String ToNextLevelCreditsString() {
        return currentCredits + "/" + levelCosts[getTechMarketLevel() - 1];
    }

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

    // Force update the Marketplace
    public void updateCargoForce() {
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
        HashMap<String, Boolean> specialItemsList = new HashMap<String, Boolean>();
        Random random = new Random();

        // Get all the items to add via tags or hardcoded ids
        for (SpecialItemSpecAPI spec : Global.getSettings().getAllSpecialItemSpecs()) {
            final String[] TagsToAdd = { "nanoforge", "hist3t" };

            // Hardcoded for DIY planets atm, as they dont have any tags
            final String[] ItemIDsToAdd = { "atmo_mineralizer", "atmo_sublimator", "solar_reflector",
                    "tectonic_attenuator", "weather_core", "climate_sculptor", "gravity_oscillator", "rad_remover" };

            for (String itemTag : spec.getTags()) {
                if (Arrays.asList(TagsToAdd).contains(itemTag) || Arrays.asList(ItemIDsToAdd).contains(spec.getId())) {
                    specialItemsList.put(spec.getId(), true);
                }
            }

            if (Arrays.asList(ItemIDsToAdd).contains(spec.getId())) {
                specialItemsList.put(spec.getId(), true);
            }

        }

        // Now Pick based on the techMarketLevel
        // Take random 20% of total items per market level
        // Quantity is random number from 1 to market level
        // Make our random picker list
        WeightedRandomPicker<String> randomPicker = new WeightedRandomPicker<>(itemGenRandom);
        for (HashMap.Entry<String, Boolean> item : specialItemsList.entrySet()) {
            randomPicker.add(item.getKey());
        }

        // Then add the items
        for (int i = 0; i < (randomPicker.getTotal() / 5) * techMarketLevel; i++) {
            if (randomPicker.isEmpty())
                break;

            String itemID = randomPicker.pickAndRemove();
            // Guaranteed to get at least 1, more based on tech level
            int quantity = random.nextInt(techMarketLevel) + 1;
            log.info("Trying to add " + itemID + " with quantity " + quantity);
            cargo.addSpecial(new SpecialItemData(itemID, null), quantity);
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
        RepLevel chicken_repLevel = Global.getSector().getImportantPeople().getPerson(ChickenQuestUtils.PERSON_CHICKEN)
                .getRelToPlayer().getLevel();
        float mult;
        switch (chicken_repLevel) {
            case NEUTRAL:
                mult = 0.5f;
                break;
            case FAVORABLE:
                mult = 0.4f;
                break;
            case WELCOMING:
                mult = 0.3f;
                break;
            case FRIENDLY:
                mult = 0.2f;
                break;
            case COOPERATIVE:
                mult = 0.1f;
                break;
            default:
                mult = 0.5f;
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
        return "The Current Level of this market is " + techMarketLevel + ". The next level will be reached in "
                + ToNextLevelCreditsString() + " credits";
    }

    @Override
    public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
        Highlights highlights = new Highlights();
        highlights.append(techMarketLevel + "", Misc.getHighlightColor());
        highlights.append(ToNextLevelCreditsString() + " credits" + "", Misc.getHighlightColor());
        return highlights;
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