package chickentechshop.campaign.intel.missions.chicken;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Voices;

public class ChickenQuestUtils {
		
	public static final String PERSON_CHICKEN = "chicken";
	
	public static void createChicken(MarketAPI market) {
		PersonAPI person = Global.getFactory().createPerson();
		person.setId(PERSON_CHICKEN);
		person.setImportance(PersonImportance.HIGH);
		person.setVoice(Voices.SPACER);
		person.setFaction(Factions.INDEPENDENT);
		person.setGender(FullName.Gender.MALE);
		person.setRankId(Ranks.SPECIAL_AGENT);
		person.setPostId(Ranks.POST_SPECIAL_AGENT);
		person.getName().setFirst("Chicken");
		person.getName().setLast("");
		person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "chicken"));
		person.addTag("chicken");
		Global.getSector().getImportantPeople().addPerson(person);
		market.addPerson(person);
	}
}