package chickentechshop.campaign.intel.missions.chicken;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.missions.SmugglingMission;
import chickentechshop.campaign.submarkets.TechMarket;

public class CTS_SmugglingMission extends SmugglingMission {

	@Override
	protected void notifyEnding() {
		super.notifyEnding();

		// Add to Chickens Tech market Level
		MarketAPI market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();
		TechMarket submarket = (TechMarket) market.getSubmarket("chicken_market").getPlugin();
		submarket.addCreditsToTechMarket(creditReward);
	}
}