package chickentechshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;

import chickentechshop.campaign.intel.TechMarketContact;
import chickentechshop.campaign.intel.missions.chicken.ChickenQuestUtils;

public class ChickenTechShop extends BaseModPlugin {

    public static Logger log = Global.getLogger(ChickenTechShop.class);

    final static int LEVEL_REQ = 10;

    // Inital setup, create chicken at the correct location
    // Or dont if he already exists
    // Choices are (in order)
    // 1. Prism Starport
    // 2. Nova Maxios
    // 3. Random Independent world/station/market
    // 4. PANIC PICK ANYWHERE AHHHHHHHH (Just pick any market)
    // 5. Its over :'( (Dont Spawn)
    public void chickenInitialSetup() {
        Random random = new Random();

        // Choose our start location for Chicken
        // Prisim Starport
        MarketAPI market;
        SectorEntityToken entity;
        entity = Global.getSector().getEntityById("nex_prismFreeport");
        if (entity != null) {
            market = entity.getMarket();
            ChickenQuestUtils.createChicken(market);
            return;
        }

        // Nova Maxios
        entity = Global.getSector().getEntityById("new_maxios");
        if (entity != null) {
            market = entity.getMarket();
            ChickenQuestUtils.createChicken(market);
            return;
        }

        // An Indepedent/Any! Market
        List<MarketAPI> allMarkets = Global.getSector().getEconomy().getMarketsCopy();
        List<MarketAPI> allIndependentMarkets = new ArrayList<MarketAPI>();

        for (MarketAPI m : allMarkets) {
            if (m.getFactionId() == Factions.INDEPENDENT) {
                allIndependentMarkets.add(m);
            }
        }

        if (allIndependentMarkets.size() > 0) {
            int randomMarket = random.nextInt(allIndependentMarkets.size());
            if (allIndependentMarkets.get(randomMarket) != null) {
                ChickenQuestUtils.createChicken(allIndependentMarkets.get(randomMarket));
                return;
            }
        } else {
            int randomMarket = random.nextInt(allMarkets.size());
            if (allMarkets.get(randomMarket) != null) {
                ChickenQuestUtils.createChicken(allMarkets.get(randomMarket));
                return;
            }
        }

        // If we reach this point, all hope is lost
        log.error("Could not find a valid market to spawn Chicken at!");

    }

    @Override
    public void onGameLoad(boolean newGame) {

        MarketAPI market = Global.getSector().getImportantPeople().getPerson(ChickenQuestUtils.PERSON_CHICKEN)
                .getMarket();
        final SectorAPI sector = Global.getSector();

        if (sector != null && sector.getListenerManager() != null) {
            if (market != null) {
                if (!market.hasCondition(Conditions.ABANDONED_STATION)
                        || !market.hasCondition(Conditions.DECIVILIZED)) {
                    sector.addTransientListener(new ChickenIntroCheck());
                }
            }
        }
    }

    @Override
    public void onNewGameAfterTimePass() {
        chickenInitialSetup();
        final SectorAPI sector = Global.getSector();
        MarketAPI market = Global.getSector().getImportantPeople().getPerson(ChickenQuestUtils.PERSON_CHICKEN)
                .getMarket();
        if (market == null) {
            sector.removeListener(new ChickenIntroCheck());
        }
        if (Global.getSector().getIntelManager().hasIntelOfClass(TechMarketContact.class)) {
            sector.removeListener(new ChickenIntroCheck());
        }
    }

    public class ChickenIntroCheck extends BaseCampaignEventListener {
        public ChickenIntroCheck() {
            super(false);
        }

        @Override
        public void reportPlayerClosedMarket(final MarketAPI market) {
            MarketAPI chickenMarket = ChickenQuestUtils.getChickenMarket().getPrimaryEntity().getMarket();
            if (Global.getSector().getCharacterData().getPerson().getStats().getLevel() >= LEVEL_REQ) {
                if (!Global.getSector().getIntelManager().hasIntelOfClass(TechMarketContact.class)) {
                    BreadcrumbIntel intel = new BreadcrumbIntel(Global.getSector().getPlayerFleet(),
                            ChickenQuestUtils.getChickenMarket().getPrimaryEntity());
                    intel.setTitle("A Message from a friend");
                    intel.setText(
                            "You recieve a message from someone named \"Chicken\" who claims he has something for you on "
                                    + chickenMarket.getName() + " in the " + chickenMarket.getStarSystem().getName());
                    Global.getSector().getIntelManager().addIntel(intel, false);
                    Global.getSector().removeListener(this);
                }
            }
        }
    }
}
