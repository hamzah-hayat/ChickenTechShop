package chickentechshop.campaign.intel.missions.chicken;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.missions.JailbreakMission;
import chickentechshop.campaign.submarkets.TechMarket;

public class CTS_JailbreakMission extends JailbreakMission {

	@Override
	protected void notifyEnding() {
		super.notifyEnding();

		// Add to Chickens Tech market Level
		MarketAPI market = ChickenQuestUtils.getChickenMarket();
		TechMarket submarket = (TechMarket) market.getSubmarket("chicken_market").getPlugin();
		submarket.addCreditsToTechMarket(creditReward);
	}
}