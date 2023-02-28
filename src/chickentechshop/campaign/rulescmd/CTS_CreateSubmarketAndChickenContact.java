package chickentechshop.campaign.rulescmd;

import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

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
        ChickenQuestUtils.createChicken(market);

        // Add Chicken as a contact

        return true;
    }

}