package chickentechshop.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;

import chickentechshop.campaign.submarkets.TechMarket;

import java.util.List;
import java.util.Map;

/**
 * Checks the player level
 */
public class CTS_ResetTechMarketInventory extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
            final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }
        MarketAPI market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();
        TechMarket submarket = (TechMarket) market.getSubmarket("chicken_market").getPlugin();
        submarket.updateCargoForce();

        return true;
    }

}