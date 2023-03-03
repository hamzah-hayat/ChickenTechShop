package chickentechshop.campaign.intel.missions.chicken;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.SpySatDeployment;
import com.fs.starfarer.api.util.Misc;

import chickentechshop.campaign.submarkets.TechMarket;

public class CTS_SpySatDeployment extends SpySatDeployment {

	private String makeFunnyInsult() {

		String[] adjective = { "silly", "crazy", "huge", "absolute", "foolish", "giant" };
		String[] noun = { "banana", "donkey", "muppet", "fool", "donut" };
		Random random = new Random();
		int adjectiveIndex = random.nextInt(adjective.length);
		int nounIndex = random.nextInt(noun.length);

		return adjective[adjectiveIndex] + " " + noun[nounIndex];
	}

	protected void updateInteractionDataImpl() {
		set("$ssat_barEvent", isBarEvent());
		set("$ssat_underworld", getPerson().hasTag(Tags.CONTACT_UNDERWORLD));
		set("$ssat_manOrWoman", getPerson().getManOrWoman());
		set("$ssat_reward", Misc.getWithDGS(getCreditsReward()));

		set("$ssat_personName", getPerson().getNameString());
		set("$ssat_systemName", market.getStarSystem().getNameWithLowercaseTypeShort());
		set("$ssat_marketName", market.getName());
		set("$ssat_marketOnOrAt", market.getOnOrAt());
		set("$ssat_dist", getDistanceLY(market));

		// Funny Insult
		set("$cts_ssat_insult", makeFunnyInsult());
	}

	@Override
	protected void notifyEnding() {
		super.notifyEnding();

		// Add to Chickens Tech market Level
		MarketAPI market = ChickenQuestUtils.getChickenMarket();
		TechMarket submarket = (TechMarket) market.getSubmarket("chicken_market").getPlugin();
		submarket.addCreditsToTechMarket(creditReward);
	}
}