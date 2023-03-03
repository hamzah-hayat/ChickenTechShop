package chickentechshop.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;

import chickentechshop.campaign.intel.missions.chicken.ChickenQuestUtils;
import java.util.List;
import java.util.Map;

/**
 * Makes sure Chicken is here
 */
public class CTS_IsChickenHere extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
            final Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = ChickenQuestUtils.getChickenMarket();
        if (Global.getSector().getPlayerPerson().getMarket().getId() == market.getId()) {
            return true;
        }

        return false;
    }

}