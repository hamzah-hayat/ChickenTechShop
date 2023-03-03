package chickentechshop;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
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
    // 3. Random indepedent world
    // 4. Random Hegemony world
    // 5. PANIC PICK ANYWHERE AHHHHHHHH (Just pick any planet that isnt decivilised)
    // 6. Its over :'( (Dont Spawn)
    public void chickenInitialSetup() {

        // Choose our start location for Chicken
        // Prisim Starport
        MarketAPI market;
        market = Global.getSector().getEntityById("nex_prismFreeport").getMarket();
        if (market != null) {
            ChickenQuestUtils.createChicken(market);
            return;
        }

        // Nova Maxios
        market = Global.getSector().getEntityById("novamaxios").getMarket();
        if (market != null) {
            ChickenQuestUtils.createChicken(market);
            return;
        }
    }

    @Override
    public void onNewGame() {
        chickenInitialSetup();
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
            if (Global.getSector().getCharacterData().getPerson().getStats().getLevel() >= LEVEL_REQ) {
                if (!Global.getSector().getIntelManager().hasIntelOfClass(TechMarketContact.class)) {
                    BreadcrumbIntel intel = new BreadcrumbIntel(ChickenQuestUtils.getChickenMarket().getPrimaryEntity(),
                            ChickenQuestUtils.getChickenMarket().getPrimaryEntity());
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
