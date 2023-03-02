package chickentechshop;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;

import chickentechshop.campaign.intel.TechMarketContact;
import chickentechshop.campaign.intel.missions.chicken.ChickenQuestUtils;

public class ChickenTechShop extends BaseModPlugin {

    final static int LEVEL_REQ = 10;

    public void onGameLoad(boolean newGame) {

        MarketAPI market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();
        final SectorAPI sector = Global.getSector();

        ChickenQuestUtils.createChicken(market);

        if (sector != null && sector.getListenerManager() != null) {
            if (market != null) {
                if (!market.hasCondition(Conditions.ABANDONED_STATION)) {
                    sector.addTransientListener(new ChickenIntroCheck());
                }
            }
        }
    }

    @Override
    public void onNewGameAfterTimePass() {
        final SectorAPI sector = Global.getSector();
        MarketAPI market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();
        if (market == null) {
            sector.removeListener(new ChickenIntroCheck());
        }
        if (Global.getSector().getIntelManager().hasIntelOfClass(TechMarketContact.class)) {
            sector.removeListener(new ChickenIntroCheck());
        }
    }

    public static class ChickenIntroCheck extends BaseCampaignEventListener {
        public ChickenIntroCheck() {
            super(false);
        }

        @Override
        public void reportPlayerClosedMarket(final MarketAPI market) {
            if (Global.getSector().getCharacterData().getPerson().getStats().getLevel() >= LEVEL_REQ) {
                if (!Global.getSector().getIntelManager().hasIntelOfClass(TechMarketContact.class)) {
                    BreadcrumbIntel intel = new BreadcrumbIntel(Global.getSector().getEntityById("nex_prismFreeport"),
                            Global.getSector().getEntityById("nex_prismFreeport"));
                    intel.setTitle("A Message for you");
                    intel.setText(
                            "You have recieved a message from someone nammed \"Chicken\" who claims he can help you.");
                    Global.getSector().getIntelManager().addIntel(intel, false);
                    Global.getSector().removeListener(this);
                }
            }
        }
    }
}
