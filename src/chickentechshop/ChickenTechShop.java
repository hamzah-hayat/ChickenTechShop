package chickentechshop;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import chickentechshop.campaign.submarkets.TechMarket;

public class ChickenTechShop extends BaseModPlugin {

    public void onGameLoad(boolean newGame) {
        TechMarket.clearSubmarketCache();

        MarketAPI market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();

        market.addSubmarket("chicken_market");
    }
}
