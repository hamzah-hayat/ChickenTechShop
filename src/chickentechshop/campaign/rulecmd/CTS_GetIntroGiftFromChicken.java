package chickentechshop.campaign.rulecmd;

import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import java.util.List;
import java.util.Map;

/**
 * Gives the Intro gift from Chicken (A Beta Core)
 */
public class CTS_GetIntroGiftFromChicken extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
            final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        // Get a Gamma core from Chicken
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        playerCargo.addSpecial(new SpecialItemData("beta_core", null), 1);

        return true;
    }

}