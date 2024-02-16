// package chickentechshop.campaign.rulecmd;

// import com.fs.starfarer.api.Global;
// import com.fs.starfarer.api.campaign.CargoAPI;
// import com.fs.starfarer.api.campaign.CargoPickerListener;
// import com.fs.starfarer.api.campaign.CargoStackAPI;
// import com.fs.starfarer.api.campaign.FactionAPI;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import com.fs.starfarer.api.campaign.InteractionDialogAPI;
// import com.fs.starfarer.api.campaign.RepLevel;
// import com.fs.starfarer.api.campaign.SpecialItemData;
// import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
// import com.fs.starfarer.api.campaign.TextPanelAPI;
// import com.fs.starfarer.api.campaign.econ.MarketAPI;
// import com.fs.starfarer.api.campaign.rules.MemKeys;
// import com.fs.starfarer.api.campaign.rules.MemoryAPI;
// import com.fs.starfarer.api.characters.PersonAPI;
// import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
// import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
// import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
// import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
// import com.fs.starfarer.api.loading.WeaponSpecAPI;
// import com.fs.starfarer.api.ui.TooltipMakerAPI;
// import com.fs.starfarer.api.util.Misc;
// import com.fs.starfarer.api.util.Misc.Token;
// import com.fs.starfarer.api.util.WeightedRandomPicker;
// import java.awt.Color;
// import org.apache.log4j.Logger;

// public class CTS_OpenOmegaShop extends BaseCommandPlugin {

//     public static final int smallOmegaCost = 5;
//     public static final int mediumOmegaCost = 10;
//     public static final int largeOmegaCost = 15;

//     protected List<String> omegaBlueprintIDs = new ArrayList<>();

//     public static Logger log = Global.getLogger(CTS_OpenOmegaShop.class);

//     @Override
//     public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
//             final Map<String, MemoryAPI> memoryMap) {
//         return true;
//     }

//     // Get all not yet known omega blueprints
//     public List<String> createOmageBlueprintList() {
//         List<String> omegaBlueprints = new ArrayList<>();
//         List<WeaponSpecAPI> weaponSpecs = Global.getSettings().getAllWeaponSpecs();

//         for (WeaponSpecAPI spec : weaponSpecs) {
//             // Check if this is not a blueprint that should drop etc
//             if (!spec.hasTag("omega")) {
//                 continue;
//             }
//             // Check if player already knows this weapon?
//             if (Global.getSector().getPlayerFaction().knowsWeapon(spec.getWeaponId())) {
//                 continue;
//             }
//             // Add the data to our picker
//             omegaBlueprints.add(spec.getWeaponId());
//         }
//         return omegaBlueprints;
//     }

//     // @Override
//     // protected boolean callAction(String action, String ruleId,
//     // InteractionDialogAPI dialog, List<Token> params,
//     // Map<String, MemoryAPI> memoryMap) {
//     // if ("showBlueprintPackages".equals(action)) {
//     // selectBlueprintPackages(dialog, memoryMap);
//     // return true;
//     // } else if ("showPerson".equals(action)) {
//     // dialog.getVisualPanel().showPersonInfo(getPerson(), true);
//     // return true;
//     // }
//     // return false;
//     // }

//     protected void updateInteractionDataImpl() {
//         set("$cts_omegaBuy_ref", this);

//         set("$cts_bpbuy_count", blueprintPackageIDs.size());
//         set("$cts_bpbuy_manOrWoman", getPerson().getManOrWoman());
//         set("$cts_bpbuy_rank", getPerson().getRank().toLowerCase());
//         set("$cts_bpbuy_rankAOrAn", getPerson().getRankArticle());
//         set("$cts_bpbuy_hisOrHer", getPerson().getHisOrHer());
//     }

//     protected void selectBlueprintPackages(final InteractionDialogAPI dialog, final Map<String, MemoryAPI> memoryMap) {

//         CargoAPI copy = Global.getFactory().createCargo(false);
//         for (String bppackage : blueprintPackageIDs) {
//             copy.addSpecial(new SpecialItemData(bppackage, null), 1);
//         }

//         final CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
//         final TextPanelAPI text = dialog.getTextPanel();

//         final float width = 310f;
//         dialog.showCargoPickerDialog("Select blueprints to purchase",
//                 Misc.ucFirst("confirm"),
//                 Misc.ucFirst("cancel"),
//                 true, width, copy, new CargoPickerListener() {
//                     public void pickedCargo(CargoAPI cargo) {
//                         cargo.sort();
//                         float cost = computeValue(cargo);

//                         if (cost > 0 && cost < playerCargo.getCredits().get()) {
//                             playerCargo.getCredits().subtract(cost);
//                             AddRemoveCommodity.addCreditsLossText((int) cost, text);
//                             for (CargoStackAPI stack : cargo.getStacksCopy()) {
//                                 playerCargo.addItems(stack.getType(), stack.getData(), stack.getSize());
//                                 AddRemoveCommodity.addStackGainText(stack, text, false);
//                             }
//                             memoryMap.get(MemKeys.LOCAL).set("$option", "contact_accept", 0);
//                             FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
//                         }
//                     }

//                     @Override
//                     public void cancelledCargoSelection() {
//                     }

//                     @Override
//                     public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp,
//                             boolean pickedUpFromSource, CargoAPI combined) {

//                         float cost = computeValue(combined);
//                         float credits = playerCargo.getCredits().get();

//                         float pad = 3f;
//                         float opad = 10f;
//                         Color h = Misc.getHighlightColor();

//                         FactionAPI faction = getPerson().getFaction();
//                         panel.setParaOrbitronLarge();
//                         panel.addPara(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor(), opad);
//                         panel.setParaFontDefault();

//                         panel.addImage(faction.getLogo(), width * 1f, pad);

//                         String str = "Purchase cost: %s";
//                         panel.addPara(str, opad, cost <= credits ? h : Misc.getNegativeHighlightColor(),
//                                 Misc.getDGSCredits(cost));
//                         str = "You have %s";
//                         panel.addPara(str, pad, h, Misc.getDGSCredits(credits));
//                     }
//                 });
//     }

//     protected float computeValue(CargoAPI cargo) {
//         float cost = 0;
//         for (CargoStackAPI stack : cargo.getStacksCopy()) {
//             SpecialItemSpecAPI item = stack.getSpecialItemSpecIfSpecial();
//             if (item != null) {
//                 cost += item.getBasePrice();
//             }
//         }
//         return cost;
//     }
// }