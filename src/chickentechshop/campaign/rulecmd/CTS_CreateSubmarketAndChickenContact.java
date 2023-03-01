package chickentechshop.campaign.rulecmd;

import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import chickentechshop.campaign.intel.TechMarketContact;
import chickentechshop.campaign.intel.missions.chicken.ChickenQuestUtils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.List;
import java.util.Map;

/**
 * Creates the character contact for Chicken
 * Also creates the Submarket wherever he is
 */
public class CTS_CreateSubmarketAndChickenContact extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
            final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        // Add Chicken Submarket to wherever Chicken is
        MarketAPI market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();
        market.addSubmarket("chicken_market");

        // Add Chicken as a contact
        PersonAPI chicken = Global.getSector().getImportantPeople().getPerson(ChickenQuestUtils.PERSON_CHICKEN);
        BaseMissionHub.set(chicken, new BaseMissionHub(chicken));
        chicken.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
        TechMarketContact intel = new TechMarketContact(chicken, market);
        Global.getSector().getIntelManager().addIntel(intel, false, dialog.getTextPanel());

        return true;
    }

}