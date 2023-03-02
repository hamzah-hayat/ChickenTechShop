package chickentechshop.campaign.intel.missions.chicken;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import java.awt.Color;

public class CTS_BlueprintPackagePurchase extends HubMissionWithBarEvent {

    public static final float COST_MULT = 1.5f;

    protected List<String> blueprintPackageIDs = new ArrayList<>();
    protected int price;

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {

        PersonAPI person = getPerson();
        if (person == null)
            return false;
        MarketAPI market = person.getMarket();
        if (market == null)
            return false;

        if (!setPersonMissionRef(person, "$nex_omegaWep_ref")) {
            return false;
        }

        blueprintPackageIDs.clear();
        blueprintPackageIDs.addAll(createBlueprintPackagesList());
        if (blueprintPackageIDs.isEmpty()) {
            return false;
        }

        return true;
    }

    // Get a number of Blueprint packages
    public List<String> createBlueprintPackagesList() {
        List<String> bpPackageIDs = new ArrayList<>();
        int count;

        RepLevel rep = getPerson().getRelToPlayer().getLevel();
        switch (rep) {
            case COOPERATIVE:
                count = 3;
                break;
            case FRIENDLY:
                count = 2;
                break;
            case WELCOMING:
                count = 1;
                break;
            default:
                count = 1;
                break;
        }

        WeightedRandomPicker<SpecialItemSpecAPI> picker = new WeightedRandomPicker<>(genRandom);
        for (SpecialItemSpecAPI spec : Global.getSettings().getAllSpecialItemSpecs()) {
            final String TagsToAdd = "package_bp";
            final String TagsToNotAdd = "no_drop";

            if (spec.hasTag(TagsToAdd) && !spec.hasTag(TagsToNotAdd)) {
                picker.add(spec);
            }
        }

        for (int i = 0; i < count; i++) {
            if (picker.isEmpty()) {
                break;
            }
            SpecialItemSpecAPI item = picker.pickAndRemove();
            bpPackageIDs.add(item.getId());
        }

        return bpPackageIDs;
    }

    protected void updateInteractionDataImpl() {
        set("$cts_bpbuy_ref2", this);

        set("$cts_bpbuy_count", blueprintPackageIDs.size());
        set("$cts_bpbuy_manOrWoman", getPerson().getManOrWoman());
        set("$cts_bpbuy_rank", getPerson().getRank().toLowerCase());
        set("$cts_bpbuy_rankAOrAn", getPerson().getRankArticle());
        set("$cts_bpbuy_hisOrHer", getPerson().getHisOrHer());
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
            Map<String, MemoryAPI> memoryMap) {
        if ("showBlueprintPackages".equals(action)) {
            selectBlueprintPackages(dialog, memoryMap);
            return true;
        } else if ("showPerson".equals(action)) {
            dialog.getVisualPanel().showPersonInfo(getPerson(), true);
            return true;
        }
        return false;
    }

    protected float computeValue(CargoAPI cargo) {
        float cost = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            SpecialItemSpecAPI item = stack.getSpecialItemSpecIfSpecial();
            if (item != null) {
                cost += item.getBasePrice();
            }
        }
        cost *= COST_MULT;
        return cost;
    }

    protected void selectBlueprintPackages(final InteractionDialogAPI dialog, final Map<String, MemoryAPI> memoryMap) {

        CargoAPI copy = Global.getFactory().createCargo(false);
        for (String bppackage : blueprintPackageIDs) {
            copy.addSpecial(new SpecialItemData(bppackage, null), 1);
        }

        final CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        final TextPanelAPI text = dialog.getTextPanel();

        final float width = 310f;
        dialog.showCargoPickerDialog("Select weapons to purchase",
                Misc.ucFirst("confirm"),
                Misc.ucFirst("cancel"),
                true, width, copy, new CargoPickerListener() {
                    public void pickedCargo(CargoAPI cargo) {
                        cargo.sort();
                        float cost = computeValue(cargo);

                        if (cost > 0 && cost < playerCargo.getCredits().get()) {
                            playerCargo.getCredits().subtract(cost);
                            AddRemoveCommodity.addCreditsLossText((int) cost, text);
                            for (CargoStackAPI stack : cargo.getStacksCopy()) {
                                playerCargo.addItems(stack.getType(), stack.getData(), stack.getSize());
                                AddRemoveCommodity.addStackGainText(stack, text, false);
                            }
                            memoryMap.get(MemKeys.LOCAL).set("$option", "contact_accept", 0);
                            FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
                        }
                    }

                    @Override
                    public void cancelledCargoSelection() {
                    }

                    @Override
                    public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp,
                            boolean pickedUpFromSource, CargoAPI combined) {

                        float cost = computeValue(combined);
                        float credits = playerCargo.getCredits().get();

                        float pad = 3f;
                        float opad = 10f;
                        Color h = Misc.getHighlightColor();

                        FactionAPI faction = getPerson().getFaction();
                        panel.setParaOrbitronLarge();
                        panel.addPara(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor(), opad);
                        panel.setParaFontDefault();

                        panel.addImage(faction.getLogo(), width * 1f, pad);

                        String str = "Purchase cost: %s";
                        panel.addPara(str, opad, cost <= credits ? h : Misc.getNegativeHighlightColor(),
                                Misc.getDGSCredits(cost));
                        str = "You have %s";
                        panel.addPara(str, pad, h, Misc.getDGSCredits(credits));
                    }
                });
    }

    @Override
    public String getBaseName() {
        return "BluePrint Package Purchase"; // not used I don't think
    }

    @Override
    public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        // it's just an transaction immediate transaction handled in rules.csv
        // no intel item etc

        currentStage = new Object(); // so that the abort() assumes the mission was successful
        abort();
    }
}
