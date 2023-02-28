package chickentechshop.campaign.rulescmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/**
 * Checks the player level
 */
public class CTS_CheckPlayerLevel extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params,
            final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        final int level = params.get(0).getInt(memoryMap);

        return Global.getSector().getCharacterData().getPerson().getStats().getLevel() >= level;

    }

}