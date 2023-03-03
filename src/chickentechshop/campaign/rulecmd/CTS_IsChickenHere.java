package chickentechshop.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;

import chickentechshop.campaign.intel.missions.chicken.ChickenQuestUtils;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Makes sure Chicken is here
 */
public class CTS_IsChickenHere extends BaseCommandPlugin {

    public static Logger log = Global.getLogger(CTS_IsChickenHere.class);

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
            final Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = ChickenQuestUtils.getChickenMarket();

        String playerLocation = (String) memoryMap.get(MemKeys.LOCAL).get("$id");
        String chickenLocation = market.getId();

        if (playerLocation.equals(chickenLocation)) {
            return true;
        }

        // Random sector check
        playerLocation = "market_" + playerLocation;
        if (playerLocation.equals(chickenLocation)) {
            return true;
        }

        return false;
    }

}