package legend.game.modding.coremod.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import legend.game.scripting.PartyFlagCategory;

public class PartyFlagConfigEntry extends EnumConfigEntry<PartyFlagCategory> {
  public PartyFlagConfigEntry() {
    super(PartyFlagCategory.class, PartyFlagCategory.STORY, ConfigStorageLocation.CAMPAIGN, ConfigCategory.PARTY);
  }
}
